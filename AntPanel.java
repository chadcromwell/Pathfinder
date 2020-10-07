/********************************************************************************************************************
Author: Chad Cromwell
Date: December 10th, 2017
Assignment: 2
Program: AntPanel.java
Description: A class that handles the animation of the ants
Methods:
		start() method - Initialization of ants, what is first executed
		stop() method - When animation neeeds to stop
		tick() method - What is executed each frame
		paintComponents() method - Renders everything
********************************************************************************************************************/

import java.awt.*;
import javax.swing.*;
import java.util.*;

public class AntPanel extends JPanel {
	public static ArrayList<Ant> antArray; //An ArrayList of Ant objects
	public int startingAnts; //How many ants to start with
	public boolean start = false; //Whether to start animating
	private Color foodColor = new Color(0, 255, 0); //Color of food, GREEN
	private Color waterColor = new Color(0, 255, 255); //Color of water, CYAN
	private Color antColor = Color.BLACK; //Color of ants, BLACK

	//AntPanel() constructor
	public AntPanel() {
		antArray = new ArrayList<Ant>();
	}

	//start() method - Initialization of ants, what is first executed
	public void start() {
		antArray.add(new Ant(Pathfinder.squares.path.get(0).x, Pathfinder.squares.path.get(0).y));
		start = true; //The animation has started
	}

	//stop() method - When animation neeeds to stop
	public void stop() {
		start = false; //The animation is stopped
		antArray.get(0).alive = false;
	}

	//tick() method - What is executed each frame
	public void tick() {
		//If the animation has been started
		if(start) {
			antArray.get(0).tick(); //Tick the ant
			repaint(); //Repaint
		}
	}

	//paintComponents() method - Renders everything
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(antArray.size() > 0) {
			if(start && antArray.get(0).alive) {
				g.setColor(antColor);
				g.fillOval((int)antArray.get(0).x, (int)antArray.get(0).y, Ant.ANTSIZE, Ant.ANTSIZE); //Draw the ant as foodColor GREEN to show that it is carrying the food back home
			}
			if(!antArray.get(0).alive) {
				antArray.remove(0);
			}
		}
	}
}