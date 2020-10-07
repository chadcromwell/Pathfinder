/********************************************************************************************************************
Author: Chad Cromwell
Date: December 1st, 2017
Assignment: 2
Program: Squares.java
Description: A class that handles and generates the squares used in the pathfinding program
Methods:
		tick() method - What is executed each frame
		paintComponents() method - Renders everything
		addRectangle() method - Accepts a Terrain ArrayList and then adds Terrain objects to the list. Used in nested for loops (for x and y)
		findPath() method - Finds the path using A* algorithm and simple heuristic that tries to move in a direction that is closer to the end square
		isPath() method - Called when a path is found, gathers all of the parent nodes from the end to start square, stores them in an ArrayList and reverses them.
		reset() method - Resets the animation frames, booleans related to animation and pathfinding, and clears the lists
		createSquares() method - Creates the squares and addes them to the squares ArrayList
********************************************************************************************************************/

import java.awt.*;
import javax.swing.*;
import java.util.*;

public class Squares extends JPanel {

	//Squares variables
	int width; //Width of screen
	int height; //Height of screen
	int divisions; //How many divisions
	int rowHeight; //Width of each row
	int rowWidth; //Height of each row
	int x; //x position of square
	int y; //y position of square
	int nGCost; //New square's g cost
	int nHCost; //New square's h cost
	int curGCost; //Current square's g cost
	int curHCost; //Current square's h cost
	int curFCost; //Curren square's f cost
	int xIndex; //Index in squares
	int yIndex; //Index in squares
	int elementIndex; //Element index of the current square
	int startXIndex; //The starting square's x index
	int startYIndex; //The starting square's y index 
	int endXIndex; //The ending square's x index
	int endYIndex; //The ending square's y index
	int startIndex; //The starting square's element index
	int endIndex; //The ending square's element index
	int curFrame = 0; //The current frame
	int openedDisplayFrame = 0; //The display frame for opened squares, used to animate the opening of squares
	int closedDisplayFrame = 0; //The display frame for closed squares, used to animate the closing of squares
	int pathDisplayFrame = 0; //The display frame for path squares, used to animate the path
	int displayTime = 5; //How quickly the pathfinding animations take, the lower the number the faster it animates. 5 is a good option.
	String selectedType = "open"; //The type of terrain to "paint" squares with, initialized to "open" so all square created are set to "open"

	//Booleans
	boolean animateSearch = false; //Whether or not to animate the search
	boolean animatePath = false;  //Whether or not to animate the path
	boolean openedAnimationDone = false;  //Whether or not the opened squares animation is finished
	boolean closedAnimationDone = false;  //Whether or not the closed squares animation is done
	boolean pathAnimationDone = false;  //Whether or not the closed squares animation is done
	boolean isPath = false;  //Whether or not a path is found
	
	//ArrayLists
	ArrayList<Terrain> squares; //List to hold squares
	ArrayList<Terrain> opened; //List to hold opened squares
	ArrayList<Terrain> closed; //List to hold closed squares
	ArrayList<Terrain> path;  //List to hold path squares
	ArrayList<Integer> openedIndex;  //List to hold opened squares' indices
	ArrayList<Integer> closedIndex;  //List to hold closed squares' indices
	ArrayList<Terrain> openedAnimation; //List to hold opened squares for opened square animation
	ArrayList<Terrain> closedAnimation; //List to hold closed squares for closed square animation

	//Objects
	Terrain start; //Terrain object to hold start square
	Terrain end; //Terrain object to hold end square
	Terrain current; //Terrain object to hold the current square

	//Colours
	Color openColor = new Color(255, 255, 255); //Colour for open terrain, WHITE
	Color grassColor = new Color(0, 200, 0); //Colour for grass terrain, GREEN
	Color swampColor = new Color(0, 100, 0); //Colour for swamp terrain, DARK GREEN
	Color obstacleColor = new Color(50, 40, 60); //Colour for obstacle terrain, OBSIDIAN
	Color startColor = new Color(0, 0, 245); //Colour for start square, BLUE
	Color endColor = new Color(245, 0, 0); //Color for end square, RED
	Color openedColor = new Color(255, 255, 0); //Color for path squares, YELLOW
	Color closedColor = new Color(255, 125, 0); //Color for path squares, ORANGE
	Color pathColor = new Color(0, 200, 200); //Color for path squares, CYAN

	//Level constructor - Accepts an int to determine what level to load
	public Squares(int w, int d) {
		width = w; //Get the width of the map image, assign it to width
		height = width; //Get the height of the map image, assign it to height
		divisions = d; //Capture divisions amount into the square object
		rowHeight = height/divisions; //Calculate the height of the rows
		rowWidth = width/divisions; //Calculate the width of the rows
		squares = new ArrayList<Terrain>(); //Initialize ArrayList comprised of Terrain objects, used for displaying the squares
		opened = new ArrayList<Terrain>(); //Initialize ArrayList comprised of Terrain objects, used for holding open nodes
		closed = new ArrayList<Terrain>(); //Initialize ArrayList comprised of Terrain objects, used for holding closed nodes
		path = new ArrayList<Terrain>(); //Initialize ArrayList comprised of Terrain objects, used for holding closed nodes
		openedAnimation = new ArrayList<Terrain>(); //Initialize ArrayList comprised of Terrain objects, used for holding closed nodes
		closedAnimation = new ArrayList<Terrain>(); //Initialize ArrayList comprised of Terrain objects, used for holding closed nodes
		openedIndex = new ArrayList<Integer>(); //Initialize ArrayList comprised of Terrain objects, used for holding closed nodes
		closedIndex = new ArrayList<Integer>(); //Initialize ArrayList comprised of Terrain objects, used for holding closed nodes
		start = new Terrain(); //Initialize start square Terrain object
		end = new Terrain(); //Initialize end square Terrain object

		//For the number of divisions horizontally and vertically
		createSquares();
		selectedType = "none"; //Set selectedType to none for start of the program
	}

	//tick() method - What is executed each frame
	public void tick() {
		//Iterate through all elements in the square ArrayList
		for(int i = 0; i < squares.size(); i++) {
			//If the mouse is within the current square
			if(Pathfinder.handler.x > squares.get(i).x && Pathfinder.handler.x < squares.get(i).x+squares.get(i).width && Pathfinder.handler.y > squares.get(i).y && Pathfinder.handler.y < squares.get(i).y+squares.get(i).height) {
				squares.get(i).highlighted = true; //Set the current box being hovered over as highlighted
				//If the user clicks
				if(Pathfinder.handler.click) { 
					//If the selectedType is start
					if(selectedType == "start") {
						start = squares.get(i);
						//Iterate through all elements in the square ArrayList
						for(int j = 0; j < squares.size(); j++) {
							//If there is another box that is currently the start square
							if(squares.get(j).type == "start") {
								squares.get(j).type = "open"; //Change it to open terrain, so there can only be one start square
							}
						}
						start.enabled = true; //Start square has been enabled
						startIndex = i; //Save the start square's element index
					}
					//If the selectedType is end
					if(selectedType == "end") {
						end = squares.get(i);
						//Iterate through all elements in the square ArrayList
						for(int j = 0; j < squares.size(); j++) {
							//If there is another square that is currently the end square
							if(squares.get(j).type == "end") {
								squares.get(j).type = "open"; //Change it to open terrain, so there can only be one start square
							}
						}
						end.enabled = true; //End square has been enabled
						endIndex = i; //Save the end square's element index
					}
					//If the selectedType is none
					if(selectedType != "none") {
						squares.get(i).type = selectedType; //Change the square's terrain type to reflect the selectedType

						//If the selectedType is "start"
						if(selectedType == "start") {
							squares.get(i).cost = 0; //Set the cost to 0
						}
						//If the selectedType is "open"
						if(selectedType == "end") {
							squares.get(i).cost = 0; //Set the cost to 0
						}
						//If the selectedType is "open"
						if(selectedType == "open") {
							squares.get(i).cost = 1; //Set the cost to 1
						}
						//If the selectedType is "grass"
						if(selectedType == "grass") {
							squares.get(i).cost = 3; //Set the cost to 3
						}
						//If the selectedType is "swamp"
						if(selectedType == "swamp") {
							squares.get(i).cost = 4; //Set the cost to 4
						}
					}
				}
			}
			//If the mouse isn't in the current square
			if(Pathfinder.handler.x < squares.get(i).x || Pathfinder.handler.x > squares.get(i).x+squares.get(i).width || Pathfinder.handler.y < squares.get(i).y || Pathfinder.handler.y > squares.get(i).y+squares.get(i).height) {
				squares.get(i).highlighted = false; //The square is not highlighted
			}
		}

		//Animation handling
		//If the current frame reached the displayTime
		if(curFrame > displayTime) {
			curFrame = 0; //Set curFrame back to 0
		}
		repaint(); //Repaint the squares
		curFrame++; //Increment the curFrame
	}
	
	//paintComponents() method - Renders everything
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//Iterate through every element in the squares ArrayList
		for(int i = 0; i < squares.size(); i++) {
			//If it is a start square
			if(squares.get(i).type == "start") {
				//If it isn't highlighted (not hovered over)
				if(!squares.get(i).highlighted) {
					g.setColor(startColor); //Set the colour to startColor (blue)
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the colour to black
					g.drawRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the black border
				}
				else {
					g.setColor(startColor.brighter()); //Make the colour brighter
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
				}
			}
			//If it is an end square
			if(squares.get(i).type == "end") {
				//If it isn't highlighted (not hovered over)
				if(!squares.get(i).highlighted) {
					g.setColor(endColor); //Set the colour to endColor (red)
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the colour to black
					g.drawRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the black border
				}
				else {
					g.setColor(endColor.brighter()); //Make the colour brighter
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
				}
			}
			//If it is an open square
			if(squares.get(i).type == "open") {
				//If it isn't highlighted (not hovered over)
				if(!squares.get(i).highlighted) {
					g.setColor(openColor); //Set the colour to openColor (white)
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the colour to black
					g.drawRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the black border
				}
				else{
					g.setColor(openColor.brighter()); //Make the colour brighter
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
				}
			}
			//If it is a grass square
			if(squares.get(i).type == "grass") {
				//If it isn't highlighted (not hovered over)
				if(!squares.get(i).highlighted) {
					g.setColor(grassColor); //Set the colour to grassColor (green)
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the colour to black
					g.drawRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the black border
				}
				else {
					g.setColor(grassColor.brighter()); //Make the colour brighter
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
				}
			}
			//If it is a swamp square
			if(squares.get(i).type == "swamp") {
				//If it isn't highlighted (not hovered over)
				if(!squares.get(i).highlighted) {
					g.setColor(swampColor); //Set the color to swampColor (dark green)
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the color to black
					g.drawRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the black border
				}
				else {
					g.setColor(swampColor.brighter()); //Make the colour brighter
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
				}
			}
			//If it is an obsatcle square
			if(squares.get(i).type == "obstacle") {
				//If it isn't highlighted (not hovered over)
				if(!squares.get(i).highlighted) {
					g.setColor(obstacleColor); //Set the color to obstacleColor (obsidian)
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the color to black
					g.drawRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the black border
				}
				else {
					g.setColor(obstacleColor.brighter()); //Make the colour brighter
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
				}
			}
		}

		//If the search should be animated
		if(animateSearch) {
			//If openedAnimation has elements
			if(openedAnimation.size() > 0) {
				//For all frames up to openedDisplayFrame
				for(int i = 0; i < openedDisplayFrame; i++) {
					g.setColor(openedColor); //Set the color to openedColor (yellow)
					g.fillRect(openedAnimation.get(i).x, openedAnimation.get(i).y, openedAnimation.get(i).width, openedAnimation.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the color to black
					g.drawRect(openedAnimation.get(i).x, openedAnimation.get(i).y, openedAnimation.get(i).width, openedAnimation.get(i).height); //Draw the black border
				}
				//If the curFrame reaches displayTime
				if(curFrame == displayTime) {
					openedDisplayFrame++; //Increment the openedDisplayFrame, this will show the next opened squares
				}
				//If openedDisplayFrame reaches the number of elements in openedAnimation ArrayList, there's no more squares to animate
				if(openedDisplayFrame == openedAnimation.size()) {
					openedDisplayFrame = openedAnimation.size()-1; //Set the openedDisplayFrame to the max amount, prevents OOB
					openedAnimationDone = true; //Done animating the opened squares
				}
			}
			
			//If closedAnimation has elements
			if(closedAnimation.size() > 0) {
				//For all frames up to closedDisplayFrame
				for(int i = 0; i < closedDisplayFrame; i++) {
					g.setColor(closedColor); //Set the color to closedColor (orange)
					g.fillRect(closedAnimation.get(i).x, closedAnimation.get(i).y, closedAnimation.get(i).width, closedAnimation.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the color to black
					g.drawRect(closedAnimation.get(i).x, closedAnimation.get(i).y, closedAnimation.get(i).width, closedAnimation.get(i).height); //Draw the black border
				}
				//If the curFrame reaches displayTime
				if(curFrame == displayTime) {
					closedDisplayFrame++; //Increment the closedDisplayFrame, this will show the next closed squares
				}
				//If closedDisplayFrame reaches the number of elements in closedAnimation ArrayList, there's no more squares to animate
				if(closedDisplayFrame == closedAnimation.size()) {
					closedDisplayFrame = closedAnimation.size()-1; //Set the closedDisplayFrame to the max amount, prevents OOB
					closedAnimationDone = true; //Done animating the closed squares
				}
			}
		}

		//If the path should be animated
		if(animatePath) {
			//If a path exists
			if(isPath) {
				//For all frames up to pathDisplayFrame
				for(int i = 0; i < pathDisplayFrame; i++) {
					g.setColor(pathColor); //Set the color to pathColor (cyan)
					g.fillRect(path.get(i).x, path.get(i).y, path.get(i).width, path.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the color to black
					g.drawRect(path.get(i).x, path.get(i).y, path.get(i).width, path.get(i).height); //Draw the black border
				}
				//If the curFrame reaches displayTime
				if(curFrame == displayTime) {
					pathDisplayFrame++;
				}
				//If pathDisplayFrame reaches the number of elements in pathAnimation ArrayList, there's no more squares to animate
				if(pathDisplayFrame == path.size()) {
					pathDisplayFrame = path.size()-1; //Set the pathDisplayFrame to the max amount, prevents OOB
					pathAnimationDone = true; //Done animating the path squares
				}
			}
		}
	}

	//addRectangle() method - Accepts a Terrain ArrayList and then adds Terrain objects to the list. Used in nested for loops (for x and y).
	public void addRectangle(ArrayList<Terrain> l){
			l.add(new Terrain(new Rectangle(x*rowWidth, y*rowHeight, rowWidth, rowHeight)));
	}

	//findPath() method - Finds the path using A* algorithm and simple heuristic that tries to move in a direction that is closer to the end square
	public void findPath() {
			reset(); //Call reset method
			opened.add(start); //Add the start square to the opened list
			openedIndex.add((start.x/rowWidth)+((start.y/rowWidth)*divisions)); //Add the element index of the start square to the openedIndex List
			//Do While loop
			do {
				//Sort the opened list by lowest f cost
				Collections.sort(opened, new Comparator<Terrain>() {
					public int compare(Terrain a, Terrain b) {
						if(a.fCost == b.fCost) {
							return 0;
						}
						else {
							return a.fCost - b.fCost;
						}
					}
				});
				current = opened.get(0); //Assign the lowest f cost opened square to the current square
				opened.remove(0); //Remove the current square from the opened List
				closed.add(current); //Add the current square to the closed List
				closedAnimation.add(current); //Add the current square to the closedAnimation List
				closedIndex.add((current.x/rowWidth)+((current.y/rowWidth)*divisions)); //Add the current square's element index to the closedIndex List

				//If the current square is the end square, the path has been found
				if(current.x == end.x && current.y == end.y) {
					isPath(); //Call isPath
					break; //Break out of the loop because the path has been found
				}

				xIndex = current.x/rowWidth; //X index in squares instead of x position
				yIndex = current.y/rowWidth; //Y index in squares instead of y position
				elementIndex = (xIndex)+(yIndex*divisions); //Element index of the current square
				startXIndex = start.x/rowWidth; //Start square's x index
				startYIndex = start.y/rowWidth; //Start square's y index
				endXIndex = end.x/rowWidth; //End square's x index
				endYIndex = end.y/rowWidth; //End square's y index

				//Assign the costs to the squares
				//If the square is in the grid
				if(xIndex >= 0 && xIndex <= Pathfinder.GRIDWIDTH && yIndex >= 0 && yIndex <= Pathfinder.GRIDWIDTH) {

					//If the neighbour is within the window (don't try to parse squares that don't exist), not a closed node, and is traversable
					//Left square
					if(squares.get(elementIndex).x-rowWidth >= 0 && !closed.contains(squares.get(elementIndex-1)) && squares.get(elementIndex-1).type != "obstacle") {
						squares.get(elementIndex-1).gCost = (int)Math.sqrt((Math.abs((xIndex-1)-startXIndex)+Math.abs((yIndex-startYIndex))))+squares.get(elementIndex).cost; //Calculate and assign gCost
						squares.get(elementIndex-1).hCost = (int)Math.sqrt((Math.abs((xIndex-1)-endXIndex)+Math.abs((yIndex-endYIndex))))+squares.get(elementIndex).cost; //Calculate and assign hCost

						nGCost = squares.get(elementIndex-1).gCost; //Calculate the neighbour square's gCost
						nHCost = squares.get(elementIndex-1).hCost; //Calculate the neighbour square's hCost
						curHCost = (int)Math.sqrt((Math.abs(xIndex-endXIndex)+Math.abs((yIndex-endYIndex))))+squares.get(elementIndex).cost; //Calculate the current square's hCost
						
						//If the neighbour's hCost is lower than the current square's hCost (if it's closer to the end square)
						if(nHCost < curHCost || !opened.contains(squares.get(elementIndex-1))) {
							squares.get(elementIndex-1).fCost = nGCost+nHCost; //Assign the neighbour's fCost
							squares.get(elementIndex-1).parent = squares.get(elementIndex); //Update the parent of the neighbour

							//If the opened List does not contain the neighbour
							if(!opened.contains(squares.get(elementIndex-1))){
								opened.add(squares.get(elementIndex-1)); //Add the neighbour to the opened List
								openedAnimation.add(squares.get(elementIndex-1)); //Add the neighbour to the openedAnimation List
								openedIndex.add(elementIndex-1); //Add the neighbour's element index to the openedIndex List
							}
						}
					}

					//Right square
					if(squares.get(elementIndex).x+rowWidth < Pathfinder.GRIDWIDTH && !closed.contains(squares.get(elementIndex+1)) && squares.get(elementIndex+1).type != "obstacle") {
						squares.get(elementIndex+1).gCost = (int)Math.sqrt((Math.abs((xIndex+1)-startXIndex)+Math.abs((yIndex-startYIndex))))+squares.get(elementIndex).cost; //Calculate and assign gCost
						squares.get(elementIndex+1).hCost = (int)Math.sqrt((Math.abs((xIndex+1)-endXIndex)+Math.abs((yIndex-endYIndex))))+squares.get(elementIndex).cost; //Calculate and assign hCost

						nGCost = squares.get(elementIndex+1).gCost; //Calculate the neighbour square's gCost
						nHCost = squares.get(elementIndex+1).hCost; //Calculate the neighbour square's hCost
						curHCost = (int)Math.sqrt((Math.abs(xIndex-endXIndex)+Math.abs((yIndex-endYIndex))))+squares.get(elementIndex).cost; //Calculate the current square's hCost

						//If the neighbour's hCost is lower than the current square's hCost (if it's closer to the end square)
						if(nHCost < curHCost || !opened.contains(squares.get(elementIndex+1))) {
							squares.get(elementIndex+1).fCost = nGCost+nHCost; //Assign the neighbour's fCost
							squares.get(elementIndex+1).parent = squares.get(elementIndex); //Update the parent of the neighbour

							//If the opened List does not contain the neighbour
							if(!opened.contains(squares.get(elementIndex+1))){
								opened.add(squares.get(elementIndex+1)); //Add the neighbour to the opened List
								openedAnimation.add(squares.get(elementIndex+1)); //Add the neighbour to the openedAnimation List
								openedIndex.add(elementIndex+1); //Add the neighbour's element index to the openedIndex List
							}
						}
					}

					//Top square
					if(squares.get(elementIndex).y-rowHeight >= 0 && !closed.contains(squares.get(elementIndex-divisions)) && squares.get(elementIndex-divisions).type != "obstacle") {
						squares.get(elementIndex-divisions).gCost = (int)Math.sqrt((Math.abs(xIndex-startXIndex)+Math.abs(((yIndex-1)-startYIndex))))+squares.get(elementIndex).cost; //Calculate and assign gCost
						squares.get(elementIndex-divisions).hCost = (int)Math.sqrt((Math.abs(xIndex-endXIndex)+Math.abs(((yIndex-1)-endYIndex))))+squares.get(elementIndex).cost; //Calculate and assign hCost

						nGCost = squares.get(elementIndex-divisions).gCost; //Calculate the neighbour square's gCost
						nHCost = squares.get(elementIndex-divisions).hCost; //Calculate the neighbour square's hCost
						curHCost = (int)Math.sqrt((Math.abs(xIndex-endXIndex)+Math.abs((yIndex-endYIndex))))+squares.get(elementIndex).cost; //Calculate the current square's hCost

						//If the neighbour's hCost is lower than the current square's hCost (if it's closer to the end square)
						if(nHCost < curHCost || !opened.contains(squares.get(elementIndex-divisions))) {
							squares.get(elementIndex-divisions).fCost = nGCost+nHCost; //Assign the neighbour's fCost
							squares.get(elementIndex-divisions).parent = squares.get(elementIndex); //Update the parent of the neighbour

							//If the opened List does not contain the neighbour
							if(!opened.contains(squares.get(elementIndex-divisions))){
								opened.add(squares.get(elementIndex-divisions)); //Add the neighbour to the opened List
								openedAnimation.add(squares.get(elementIndex-divisions)); //Add the neighbour to the openedAnimation List
								openedIndex.add(elementIndex-divisions); //Add the neighbour's element index to the openedIndex List
							}
						}
					}

					//Bottom square
					if(squares.get(elementIndex).y+rowHeight < Pathfinder.GRIDWIDTH && !closed.contains(squares.get(elementIndex+divisions)) && squares.get(elementIndex+divisions).type != "obstacle") {
						squares.get(elementIndex+divisions).gCost = (int)Math.sqrt((Math.abs(xIndex-startXIndex)+Math.abs(((yIndex+1)-startYIndex))))+squares.get(elementIndex).cost; //Calculate and assign gCost
						squares.get(elementIndex+divisions).hCost = (int)Math.sqrt((Math.abs(xIndex-endXIndex)+Math.abs(((yIndex+1)-endYIndex))))+squares.get(elementIndex).cost; //Calculate and assign hCost

						nGCost = squares.get(elementIndex+divisions).gCost; //Calculate the neighbour square's gCost
						nHCost = squares.get(elementIndex+divisions).hCost; //Calculate the neighbour square's hCost
						curHCost = (int)Math.sqrt((Math.abs(xIndex-endXIndex)+Math.abs((yIndex-endYIndex))))+squares.get(elementIndex).cost; //Calculate the current square's hCost

						//If the neighbour's hCost is lower than the current square's hCost (if it's closer to the end square)
						if(nHCost < curHCost || !opened.contains(squares.get(elementIndex+divisions))) {
							squares.get(elementIndex+divisions).fCost = nGCost+nHCost; //Assign the neighbour's fCost
							squares.get(elementIndex+divisions).parent = squares.get(elementIndex); //Update the parent of the neighbour

							//If the opened List does not contain the neighbour
							if(!opened.contains(squares.get(elementIndex+divisions))){
								opened.add(squares.get(elementIndex+divisions)); //Add the neighbour to the opened List
								openedAnimation.add(squares.get(elementIndex+divisions)); //Add the neighbour to the openedAnimation List
								openedIndex.add(elementIndex+divisions); //Add the neighbour's element index to the openedIndex List
							}
						}
					}
					
					//Top left square
					if(squares.get(elementIndex).x-rowWidth >= 0 && squares.get(elementIndex).y-rowHeight >= 0 && !closed.contains(squares.get(elementIndex-1-divisions)) && squares.get(elementIndex-1-divisions).type != "obstacle") {
						squares.get(elementIndex-1-divisions).gCost = (int)Math.sqrt((Math.abs((xIndex-1)-startXIndex)+Math.abs(((yIndex-1)-startYIndex))))+squares.get(elementIndex).cost; //Calculate and assign gCost
						squares.get(elementIndex-1-divisions).hCost = (int)Math.sqrt((Math.abs((xIndex-1)-endXIndex)+Math.abs(((yIndex-1)-endYIndex))))+squares.get(elementIndex).cost; //Calculate and assign hCost

						nGCost = squares.get(elementIndex-1-divisions).gCost; //Calculate the neighbour square's gCost
						nHCost = squares.get(elementIndex-1-divisions).hCost; //Calculate the neighbour square's hCost
						curHCost = (int)Math.sqrt((Math.abs(xIndex-endXIndex)+Math.abs((yIndex-endYIndex))))+squares.get(elementIndex).cost; //Calculate the current square's hCost

						//If the neighbour's hCost is lower than the current square's hCost (if it's closer to the end square)
						if(nHCost < curHCost || !opened.contains(squares.get(elementIndex-1-divisions))) {
							squares.get(elementIndex-1-divisions).fCost = nGCost+nHCost; //Assign the neighbour's fCost
							squares.get(elementIndex-1-divisions).parent = squares.get(elementIndex); //Update the parent of the neighbour

							//If the opened List does not contain the neighbour
							if(!opened.contains(squares.get(elementIndex-1-divisions))){
								opened.add(squares.get(elementIndex-1-divisions)); //Add the neighbour to the opened List
								openedAnimation.add(squares.get(elementIndex-1-divisions)); //Add the neighbour to the openedAnimation List
								openedIndex.add(elementIndex-1-divisions); //Add the neighbour's element index to the openedIndex List
							}
						}
					}

					//Bottom left square
					if(squares.get(elementIndex).x-rowWidth >= 0 && squares.get(elementIndex).y+rowHeight < Pathfinder.GRIDWIDTH && !closed.contains(squares.get(elementIndex-1+divisions)) && squares.get(elementIndex-1+divisions).type != "obstacle") {
						squares.get(elementIndex-1+divisions).gCost = (int)Math.sqrt((Math.abs((xIndex-1)-startXIndex)+Math.abs(((yIndex+1)-startYIndex))))+squares.get(elementIndex).cost; //Calculate and assign gCost
						squares.get(elementIndex-1+divisions).hCost = (int)Math.sqrt((Math.abs((xIndex-1)-endXIndex)+Math.abs(((yIndex+1)-endYIndex))))+squares.get(elementIndex).cost; //Calculate and assign hCost

						nGCost = squares.get(elementIndex-1+divisions).gCost; //Calculate the neighbour square's gCost
						nHCost = squares.get(elementIndex-1+divisions).hCost; //Calculate the neighbour square's hCost
						curHCost = (int)Math.sqrt((Math.abs(xIndex-endXIndex)+Math.abs((yIndex-endYIndex))))+squares.get(elementIndex).cost; //Calculate the current square's hCost

						//If the neighbour's hCost is lower than the current square's hCost (if it's closer to the end square)
						if(nHCost < curHCost || !opened.contains(squares.get(elementIndex-1+divisions))) {
							squares.get(elementIndex-1+divisions).fCost = nGCost+nHCost; //Assign the neighbour's fCost
							squares.get(elementIndex-1+divisions).parent = squares.get(elementIndex); //Update the parent of the neighbour

							//If the opened List does not contain the neighbour
							if(!opened.contains(squares.get(elementIndex-1+divisions))){
								opened.add(squares.get(elementIndex-1+divisions)); //Add the neighbour to the opened List
								openedAnimation.add(squares.get(elementIndex-1+divisions)); //Add the neighbour to the openedAnimation List
								openedIndex.add(elementIndex-1+divisions); //Add the neighbour's element index to the openedIndex List
							}
						}
					}

					//Top right square
					if(squares.get(elementIndex).x+rowWidth < Pathfinder.GRIDWIDTH && squares.get(elementIndex).y-rowHeight >= 0 && !closed.contains(squares.get(elementIndex+1-divisions)) && squares.get(elementIndex+1-divisions).type != "obstacle") {
						squares.get(elementIndex+1-divisions).gCost = (int)Math.sqrt((Math.abs((xIndex+1)-startXIndex)+Math.abs(((yIndex-1)-startYIndex))))+squares.get(elementIndex).cost; //Calculate and assign gCost
						squares.get(elementIndex+1-divisions).hCost = (int)Math.sqrt((Math.abs((xIndex+1)-endXIndex)+Math.abs(((yIndex-1)-endYIndex))))+squares.get(elementIndex).cost; //Calculate and assign hCost

						nGCost = squares.get(elementIndex+1-divisions).gCost; //Calculate the neighbour square's gCost
						nHCost = squares.get(elementIndex+1-divisions).hCost; //Calculate the neighbour square's hCost
						curHCost = (int)Math.sqrt((Math.abs(xIndex-endXIndex)+Math.abs((yIndex-endYIndex))))+squares.get(elementIndex).cost; //Calculate the current square's hCost

						//If the neighbour's hCost is lower than the current square's hCost (if it's closer to the end square)
						if(nHCost < curHCost || !opened.contains(squares.get(elementIndex+1-divisions))) {
							squares.get(elementIndex+1-divisions).fCost = nGCost+nHCost; //Assign the neighbour's fCost
							squares.get(elementIndex+1-divisions).parent = squares.get(elementIndex); //Update the parent of the neighbour

							//If the opened List does not contain the neighbour
							if(!opened.contains(squares.get(elementIndex+1-divisions))){
								opened.add(squares.get(elementIndex+1-divisions)); //Add the neighbour to the opened List
								openedAnimation.add(squares.get(elementIndex+1-divisions)); //Add the neighbour to the openedAnimation List
								openedIndex.add(elementIndex+1-divisions); //Add the neighbour's element index to the openedIndex List
							}
						}
					}

					//Bottom right square
					if(squares.get(elementIndex).x+rowWidth < Pathfinder.GRIDWIDTH && squares.get(elementIndex).y+rowHeight < Pathfinder.GRIDWIDTH && !closed.contains(squares.get(elementIndex+1+divisions)) && squares.get(elementIndex+1+divisions).type != "obstacle") {
						squares.get(elementIndex+1+divisions).gCost = (int)Math.sqrt((Math.abs((xIndex+1)-startXIndex)+Math.abs(((yIndex+1)-startYIndex))))+squares.get(elementIndex).cost; //Calculate and assign gCost
						squares.get(elementIndex+1+divisions).hCost = (int)Math.sqrt((Math.abs((xIndex+1)-endXIndex)+Math.abs(((yIndex+1)-endYIndex))))+squares.get(elementIndex).cost; //Calculate and assign hCost

						nGCost = squares.get(elementIndex+1+divisions).gCost; //Calculate the neighbour square's gCost
						nHCost = squares.get(elementIndex+1+divisions).hCost; //Calculate the neighbour square's hCost
						curHCost = (int)Math.sqrt((Math.abs(xIndex-endXIndex)+Math.abs((yIndex-endYIndex))))+squares.get(elementIndex).cost; //Calculate the current square's hCost

						//If the neighbour's hCost is lower than the current square's hCost (if it's closer to the end square)
						if(nHCost < curHCost || !opened.contains(squares.get(elementIndex+1+divisions))) {
							squares.get(elementIndex+1+divisions).fCost = nGCost+nHCost; //Assign the neighbour's fCost
							squares.get(elementIndex+1+divisions).parent = squares.get(elementIndex); //Update the parent of the neighbour

							//If the opened List does not contain the neighbour
							if(!opened.contains(squares.get(elementIndex+1+divisions))){
								opened.add(squares.get(elementIndex+1+divisions)); //Add the neighbour to the opened List
								openedAnimation.add(squares.get(elementIndex+1+divisions)); //Add the neighbour to the openedAnimation List
								openedIndex.add(elementIndex+1+divisions); //Add the neighbour's element index to the openedIndex List
							}
						}
					}
				}
				isPath = false; //Haven't found a path yet
			}
			while(!opened.isEmpty()); //While there are open squares
			closedAnimation.remove(0); //Remove the first closedAnimation square, as this is the start square and we want it to stay blue
	}

	//isPath() method - Called when a path is found, gathers all of the parent nodes from the end to start square, stores them in an ArrayList and reverses them.
	public void isPath() {
		Terrain cur = squares.get(endIndex); //Create a terrain object assign it as the end square because we want to start with the end square
		do{
			cur.isPath = true; //Update the isPath parameter of the square to show it is in the path
			path.add(cur); //Add the current node to the list
			cur = cur.parent; //Set the current node to the parent
		}
		while(cur.parent != null); //While there are parent nodes
		path.add(squares.get(startIndex)); //Add the start square to the path
		Collections.reverse(path); //Reverse the ArrayList so it is drawn from start to end, not end to start
		isPath = true; //isPath is true, we have found a path
	}

	//reset() method - Resets the animation frames, booleans related to animation and pathfinding, and clears the lists
	public void reset() {
		//Reset the animation counters
		curFrame = 0; //Reset curFrame
		openedDisplayFrame = 0; //Reset openedDisplayFrame
		closedDisplayFrame = 0; //Reset closedDisplayFrame
		pathDisplayFrame = 0; //Reset pathDisplayFrame
		isPath = false; //Reset isPath
		openedAnimationDone = false; //Reset openedAnimationDone
		closedAnimationDone = false; //Reset closedAnimationDone
		pathAnimationDone = false; //Reset pathAnimationDone
		openedIndex.clear(); //Clear openedIndex List
		closedIndex.clear(); //Clear closedIndex List
		closedAnimation.clear(); //Clear closedAnimation List
		openedAnimation.clear(); //Clear openedAnimation List
		opened.clear(); //Clear opened List
		closed.clear(); //Clear closed List
		path.clear(); //Clear path List
	}

	//clear() method - Resets the enabled parameters of the start and end squares as well as animation frames. It also clears the squares List and creates a new set of squares (prevents memory leak in the heap space)
	public void clear() {
		start.enabled = false; //Reset start square's enabled parameter
		end.enabled = false; //Reset end square's enabled parameter
		curFrame = 0; //Reset curFrame
		openedDisplayFrame = 0; //Reset openedDisplayFrame
		closedDisplayFrame = 0; //Reset closedDisplayFrame
		pathDisplayFrame = 0; //Reset pathDisplayFrame
		squares.clear(); //Clear squares List
		createSquares();
	}

	//createSquares() method - Creates the squares and addes them to the squares ArrayList
	public void createSquares() {
		for(y = 0; y < divisions; y++) {
			for(x = 0; x < divisions; x++) {
				addRectangle(squares); //Call addRectangle, creating Terrain objects and putting them into the squares ArrayList
			}
		}
	}
}