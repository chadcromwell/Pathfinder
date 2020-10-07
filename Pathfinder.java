/********************************************************************************************************************
Author: Chad Cromwell
Date: December 1st, 2017
Assignment: 2
Program: Pathfinder.java
Description: A program that simulates pathfinding in a 2D 16x16 grid. It allows the user to select the start and end positions, as well as set the terrain type for each square.
Methods:
		start() method - Starts the thread for the program if it isn't already running
		stop() method - Stops the thread for the program if it is running
		tick() method - What happens each frame
		render() method - Renders each frame
		run() method - The program loop
********************************************************************************************************************/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Pathfinder extends Frame implements Runnable {
	//Finals
	private static final int CONTROLSWIDTH = 200;
	public static final int GRIDWIDTH = 800;
	private static final int DIVISIONS = 16; //Split it 16x16
	private static final int WIDTH = GRIDWIDTH+CONTROLSWIDTH; //Window width
	private static final int HEIGHT = GRIDWIDTH+22; //Window height (+22 to account for MacOS window decorations)
	private static final String TITLE = "Pathfinder"; //Window title
	private static final double FPS = 60.0; //Desired FPS

	//Program variables
	public static HandlerClass handler; //HandlerClass objet, handles mouse clicks and movement
	private static JFrame frame; //Frame
	private static JPanel panel; //Panel that goes in frame
	private static JLayeredPane layeredPane; //Panel that goes in frame
	private static JButton findPathButton; //Button for "Find path" - When clicked the path will be calculated
	private static JButton showPathButton; //Button for "Show pathfinding" - When clicked, the path will be calculated but it will show the process
	private static JButton goButton; //Button for "Go!" - When clicked, the ant will move from the start square to the end square
	private static JButton clearButton; //Button for "Clear" - When clicked, everything will be cleared from the display
	private static JCheckBox startBox; //Check box for "Set start location" - When checked, a click will set the square to the starting square
	private static JCheckBox endBox; //Check box for "Set end location" - When checked, a click will set the square to the ending square
	private static JCheckBox openBox; //Check box for "Open terrain" - When checked, a click will set the square to the open terrain
	private static JCheckBox grassBox; //Check box for "Grass terrain" - When checked, a click will set the square to the grass terrain
	private static JCheckBox swampBox; //Check box for "Swamp terrain" - When checked, a click will set the square to the swamp terrain
	private static JCheckBox obstacleBox;  //Check box for "Obstacle terrain" - When checked, a click will set the square to the obstacle terrain
	private static GridBagConstraints gbc; //Holds GridBagConstraints

	//Booleans
	private boolean showPathfinding = false; //Whether or not to show the pathfinding animation
	private boolean showPath = false; //Whether or not to show the path animation
	private boolean isRunning = false; //Boolean to keep track of whether program is running or not

	//Objects
	private Thread thread; //Thread
	public static Squares squares; //Squares object, draws and handles the squares
	public static AntPanel antPanel; //AntPanel object, draws the ant
	private Terrain current; //Current object, holds the current Terrain object

	//FPS variables, initialized in run() method
	private int fps; //Holds the count of the current fps
	private double timer; //Holds current time in milliseconds, used to display FPS
	private long lastTime; //Holds the last time the run method was called
	private double targetTick; //Holds desired FPS
	private double d; //Holds error amount between actual fps and desired fps
	private double interval; //Interval between ticks
	private long now; //Holds the current time for the new frame

	//Program constructor
	public Pathfinder(){
		handler = new HandlerClass(); //Initialize handler
		squares = new Squares(GRIDWIDTH, DIVISIONS); //Initialize squares
		antPanel = new AntPanel(); //Initialize squares
		frame = new JFrame(); //Initialize frame
		frame.setMinimumSize(new Dimension(WIDTH, HEIGHT)); //Set minimum size of the frame, can't get smaller than the content of the window

		//Buttons
		findPathButton = new JButton("Find path"); //Initialize "Find path" button
		findPathButton.setPreferredSize(new Dimension(CONTROLSWIDTH, 40)); //Set the size of the button
		//Add action listener
		findPathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//If start and end squares have been set
				if(squares.start.enabled && squares.end.enabled) {
					findPathButton.setEnabled(false); //Disable the find path button
					showPathButton.setEnabled(true); //Enable the show path button
					goButton.setEnabled(true); //Enable the go button
					showPathfinding = false; //Do not show path finding
					showPath = false; //Do not show the path
					squares.findPath(); //Find the path
					//Disable all check boxes and uncheck them
					startBox.setEnabled(false);
					endBox.setEnabled(false);
					openBox.setEnabled(false);
					grassBox.setEnabled(false);
					swampBox.setEnabled(false);
					obstacleBox.setEnabled(false);
					startBox.setSelected(false);
					endBox.setSelected(false);
					openBox.setSelected(false);
					grassBox.setSelected(false);
					swampBox.setSelected(false);
					obstacleBox.setSelected(false);
					squares.selectedType = "none"; //Set the selectedType to "none", now painting will not occur when a square is clicked
				}
				else {
					JOptionPane.showMessageDialog(null, "You must first choose a start and end square", "Alert", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});

		showPathButton = new JButton("Show pathfinding"); //Initialize "Show pathfinding" button
		showPathButton.setPreferredSize(findPathButton.getPreferredSize()); //Set the size of the button, to be the same as the previous button
		//Add action listener for when the button is clicked
		showPathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPathfinding = true; //Initiate pathfinding, but show it as it works
			}
		});

		goButton = new JButton("Go!"); //Initialize "Go!" button
		goButton.setPreferredSize(showPathButton.getPreferredSize()); //Set the size of the button, to be the same as the previous button
		//Add action listener for when the button is clicked
		goButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPath = true; //Initiate the ant moving
				squares.selectedType = "none"; //Set the selectedType to "none", now painting will not occur when a square is clicked
				antPanel.start(); //Start animating the ant
			}
		});

		clearButton = new JButton("Clear"); //Initialize "Go!" button
		clearButton.setPreferredSize(goButton.getPreferredSize()); //Set the size of the button, to be the same as the previous button
		//Add action listener for when the button is clicked
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findPathButton.setEnabled(true); //Enable the find path button
				showPathfinding = false; //Stop showing pathfinding
				showPath = false; //Stop showing the path
				//Re-enable all the check boxes
				startBox.setEnabled(true);
				endBox.setEnabled(true);
				openBox.setEnabled(true);
				grassBox.setEnabled(true);
				swampBox.setEnabled(true);
				obstacleBox.setEnabled(true);
				antPanel.stop(); //Stop animating the ant
				squares.clear(); //Clear the squares
			}
		});

		//Check boxes
		//Set Start Location box
		startBox = new JCheckBox("Set start location"); //Initialize "set start location" check box
		startBox.setPreferredSize(new Dimension(CONTROLSWIDTH, 50)); //Set check box size
		startBox.setBackground(squares.startColor); //Set the background colour of the check box
		startBox.setForeground(Color.WHITE); //Set the text colour of the check box
		startBox.setOpaque(true); //Make the check box opaque
		//Add a listener for when the check box is interacted with
		startBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) { //When the state of the check box changes
				if(startBox.isSelected()) { //If the check box is checked
					squares.selectedType = "start"; //Set the selectedType to "start", now painting any square that is clicked to that type
					//Set all other check boxes to false, only one check box can be checked at one time
					endBox.setSelected(false);
					openBox.setSelected(false);
					grassBox.setSelected(false);
					swampBox.setSelected(false);
					obstacleBox.setSelected(false);
				}
				//If this checkbox becomes unselected, and others are not selected
				if(!startBox.isSelected() && !endBox.isSelected() && !openBox.isSelected() && !grassBox.isSelected() && !swampBox.isSelected() && !obstacleBox.isSelected()) {
					startBox.setSelected(false); //Uncheck the check box
					squares.selectedType = "none"; //Set the selectedType to "none", now painting will not occur when a square is clicked
				}
			}
		});

		//Set End Location box
		endBox = new JCheckBox("Set end location");
		endBox.setPreferredSize(startBox.getPreferredSize());
		endBox.setBackground(squares.endColor);
		endBox.setForeground(Color.WHITE);
		endBox.setOpaque(true); //Make the check box opaque
		endBox.addItemListener(new ItemListener() { //Add a listener for then the check box is interacted with
			public void itemStateChanged(ItemEvent e) { //When the state of the check box changes
				if(endBox.isSelected()) { //If the check box is checked
					squares.selectedType = "end"; //Set the selectedType to "end", now painting any square that is clicked to that type
					//Set all other check boxes to false, only one check box can be checked at one time
					startBox.setSelected(false);
					openBox.setSelected(false);
					grassBox.setSelected(false);
					swampBox.setSelected(false);
					obstacleBox.setSelected(false);
				}
				//If this checkbox becomes unselected, and others are not selected
				if(!startBox.isSelected() && !endBox.isSelected() && !openBox.isSelected() && !grassBox.isSelected() && !swampBox.isSelected() && !obstacleBox.isSelected()) {
					endBox.setSelected(false); //Uncheck the check box
					squares.selectedType = "none"; //Set the selectedType to "none", now painting will not occur when a square is clicked
				}
			}
		});
		
		//Open Terrain box
		openBox = new JCheckBox("Open terrain");
		openBox.setPreferredSize(endBox.getPreferredSize());
		openBox.setBackground(squares.openColor);
		openBox.setForeground(Color.BLACK);
		openBox.setOpaque(true); //Make the check box opaque
		openBox.addItemListener(new ItemListener() { //Add a listener for then the check box is interacted with
			public void itemStateChanged(ItemEvent e) { //When the state of the check box changes
				if(openBox.isSelected()) { //If the check box is checked
					squares.selectedType = "open"; //Set the selectedType to "open", now painting any square that is clicked to that type
					//Set all other check boxes to false, only one check box can be checked at one time
					startBox.setSelected(false);
					endBox.setSelected(false);
					grassBox.setSelected(false);
					swampBox.setSelected(false);
					obstacleBox.setSelected(false);
				}
				//If this checkbox becomes unselected, and others are not selected
				if(!startBox.isSelected() && !endBox.isSelected() && !openBox.isSelected() && !grassBox.isSelected() && !swampBox.isSelected() && !obstacleBox.isSelected()) {
					openBox.setSelected(false); //Uncheck the check box
					squares.selectedType = "none"; //Set the selectedType to "none", now painting will not occur when a square is clicked
				}
			}
		});

		//Grass Terrain box
		grassBox = new JCheckBox("Grass terrain");
		grassBox.setPreferredSize(openBox.getPreferredSize());
		grassBox.setBackground(squares.grassColor);
		grassBox.setForeground(Color.WHITE);
		grassBox.setOpaque(true); //Make the check box opaque
		grassBox.addItemListener(new ItemListener() { //Add a listener for then the check box is interacted with
			public void itemStateChanged(ItemEvent e) { //When the state of the check box changes
				if(grassBox.isSelected()) { //If the check box is checked
					squares.selectedType = "grass"; //Set the selectedType to "grass", now painting any square that is clicked to that type
					//Set all other check boxes to false, only one check box can be checked at one time
					startBox.setSelected(false);
					endBox.setSelected(false);
					openBox.setSelected(false);
					swampBox.setSelected(false);
					obstacleBox.setSelected(false);
				}
				//If this checkbox becomes unselected, and others are not selected
				if(!startBox.isSelected() && !endBox.isSelected() && !openBox.isSelected() && !grassBox.isSelected() && !swampBox.isSelected() && !obstacleBox.isSelected()) {
					grassBox.setSelected(false); //Uncheck the check box
					squares.selectedType = "none"; //Set the selectedType to "none", now painting will not occur when a square is clicked
				}
			}
		});

		//Swamp Terrain Box
		swampBox = new JCheckBox("Swamp terrain");
		swampBox.setPreferredSize(grassBox.getPreferredSize());
		swampBox.setBackground(squares.swampColor);
		swampBox.setForeground(Color.WHITE);
		swampBox.setOpaque(true); //Make the check box opaque
		swampBox.addItemListener(new ItemListener() { //Add a listener for then the check box is interacted with
			public void itemStateChanged(ItemEvent e) { //When the state of the check box changes
				if(swampBox.isSelected()) { //If the check box is checked
					squares.selectedType = "swamp"; //Set the selectedType to "swamp", now painting any square that is clicked to that type
					//Set all other check boxes to false, only one check box can be checked at one time
					startBox.setSelected(false);
					endBox.setSelected(false);
					openBox.setSelected(false);
					grassBox.setSelected(false);
					obstacleBox.setSelected(false);
				}
				//If this checkbox becomes unselected, and others are not selected
				if(!startBox.isSelected() && !endBox.isSelected() && !openBox.isSelected() && !grassBox.isSelected() && !swampBox.isSelected() && !obstacleBox.isSelected()) {
					swampBox.setSelected(false); //Uncheck the check box
					squares.selectedType = "none"; //Set the selectedType to "none", now painting will not occur when a square is clicked
				}
			}
		});

		//Obstacle Terrain Box
		obstacleBox = new JCheckBox("Obstacle terrain");
		obstacleBox.setPreferredSize(swampBox.getPreferredSize());
		obstacleBox.setBackground(squares.obstacleColor);
		obstacleBox.setForeground(Color.WHITE);
		obstacleBox.setOpaque(true); //Make the check box opaque
		obstacleBox.addItemListener(new ItemListener() { //Add a listener for then the check box is interacted with
			public void itemStateChanged(ItemEvent e) { //When the state of the check box changes
				if(obstacleBox.isSelected()) { //If the check box is checked
					squares.selectedType = "obstacle"; //Set the selectedType to "obstacle", now painting any square that is clicked to that type
					//Set all other check boxes to false, only one check box can be checked at one time
					startBox.setSelected(false);
					endBox.setSelected(false);
					openBox.setSelected(false);
					grassBox.setSelected(false);
					swampBox.setSelected(false);
				}
				//If this checkbox becomes unselected, and others are not selected
				if(!startBox.isSelected() && !endBox.isSelected() && !openBox.isSelected() && !grassBox.isSelected() && !swampBox.isSelected() && !obstacleBox.isSelected()) {
					obstacleBox.setSelected(false); //Uncheck the check box
					squares.selectedType = "none"; //Set the selectedType to "none", now painting will not occur when a square is clicked
				}
			}
		});

		//Initialize panel
		layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, WIDTH, HEIGHT);
		frame.add(layeredPane); //Add the panel to the frame
		panel = new JPanel(new GridBagLayout()); //Create panel, using GridBagLayout
		panel.setBounds(0, 0, WIDTH, HEIGHT);
		antPanel.setBounds(0, 0, WIDTH, HEIGHT);
		antPanel.setOpaque(false);
		panel.addMouseListener(handler); //Add mouse listener to the panel
		panel.addMouseMotionListener(handler); //Add mouse motion listener to the panel
		gbc = new GridBagConstraints(); //Initialize GridBagConstraints

		//Squares, place in top left, taking up 4 wide, 10 high grids
		gbc.fill = GridBagConstraints.BOTH; //Fill horizontally and vertically
		gbc.gridwidth = 4; //4 grids wide
		gbc.gridheight = 11; //10 grids tall
		gbc.weightx = 1; //1 weight in x plane
		gbc.weighty = 1; //1 weight in y plane
		gbc.gridx = 0; //x position 0
		gbc.gridy = 0; //y position 0
		panel.add(squares, gbc); //Add squares to panel with GridBagConstraints

		//Buttons
		//Find Path Button
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 1; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0;  //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 0; //y position 0 
		panel.add(findPathButton, gbc); //Add button to panel with GridBagConstraints

		//Show Pathfinding Button
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 1; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0; //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 1; //y position 1
		panel.add(showPathButton, gbc); //Add button to panel with GridBagConstraints

		//Go! Button
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 1; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0; //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 2; //y position 2
		panel.add(goButton, gbc); //Add button to panel with GridBagConstraints

		//Clear Button
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 1; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0; //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 3; //y position 2
		panel.add(clearButton, gbc); //Add button to panel with GridBagConstraints

		//Start box
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 1; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0; //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 4; //y position 3
		panel.add(startBox, gbc); //Add check box to panel with GridBagConstraints

		//End box
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 1; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0; //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 5; //y position 4
		panel.add(endBox, gbc); //Add check box to panel with GridBagConstraints

		//Open box
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 1; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0; //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 6; //y position 5
		panel.add(openBox, gbc); //Add check box to panel with GridBagConstraints

		//Grass box
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 1; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0; //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 7; //y position 6
		panel.add(grassBox, gbc); //Add check box to panel with GridBagConstraints
	
		//Swamp box
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 1; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0; //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 8; //y position 7
		panel.add(swampBox, gbc); //Add check box to panel with GridBagConstraints
		
		//Obstacle box
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 1; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0; //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 9; //y position 8
		panel.add(obstacleBox, gbc); //Add check box to panel with GridBagConstraints

		layeredPane.add(panel, new Integer(0));
		layeredPane.add(antPanel, new Integer(1));
		frame.setTitle(TITLE); //Add the title to the frame
		frame.setResizable(true); //Window cannot be resized
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit the program when the window is closed
		frame.setLocationRelativeTo(null); //Open the window in the middle
		frame.setVisible(true); //Make the window visible
	}

	//start() method - Starts the thread for the program if it isn't already running
	public synchronized void start() {
		if(isRunning) return; //If the program is already running, exit method
		isRunning = true; //Set boolean to true to show that it is running
		thread = new Thread(this); //Create a new thread
		thread.start(); //Start the thread
	}

	//stop() method - Stops the thread for the program if it is running
	public synchronized void stop() {
		if(!isRunning) return; //If the program is stopped, exit method
		isRunning = false; //Set boolean to false to show that the program is no longer running
		//Attempt to join thread (close the threads, prevent memory leaks)
		try {
			thread.join();
		}
		//If there is an error, print the stack trace for debugging
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	//tick() method - What happens each frame
	private void tick() {
		if(!squares.start.enabled && !squares.end.enabled) {
			showPathButton.setEnabled(false);
			goButton.setEnabled(false);
		}
		squares.tick();
		antPanel.tick();
	}

	//render() method - Renders each frame
	private void render() {
		if(showPathfinding) {
			squares.animateSearch = true;
		}
		if(!showPathfinding) {
			squares.animateSearch = false;
		}
		if(showPath) {
			squares.animatePath = true;
		}
		if(!showPath) {
			squares.animatePath = false;
		}
	}

	//run() method - The program loop
	@Override
	public void run() {
		requestFocus(); //So window is selected when it opens
		fps = 0; //Counts current fps
		timer = System.currentTimeMillis(); //Keep track of current time in milliseconds, used to display FPS
		lastTime = System.nanoTime(); //Keep track of the last time the method was called
		targetTick = FPS; //Set desired FPS
		d = 0; //Varible used to keep track if it is running at desired FPS/used to compensate
		interval = 1000000000/targetTick; //Interval between ticks

		while(isRunning) {
			now = System.nanoTime(); //Capture the time now
			d += (now - lastTime)/interval; //Calculate d
			lastTime = now; //Update lastTime

			//If d is >= 1 we need to render to stay on fps target
			while(d >= 1) {
				tick(); //Call tick method
				render(); //Call render method
				fps++; //Increment fps
				d--; //Decrement d
			}

			//If the difference between the current system time is greater than 1 second than last time check, print the fps, reset fps to 0, and increase timer by 1 second
			if(System.currentTimeMillis() - timer >= 1000) {
				fps = 0; //Set fps to 0
				timer+=1000; //Increase timer by 1 second
			}
		}
		stop(); //Stop the program
	}

	//Main
	public static void main(String[] args) {
		Pathfinder ant = new Pathfinder();
		ant.start(); //Call start method in program object, starts the program
	}
}