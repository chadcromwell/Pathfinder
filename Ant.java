/********************************************************************************************************************
Author: Chad Cromwell
Date: December 10th, 2017
Assignment: 2
Program: Ant.java
Description: A class that creates an ant that follows the path that has been found
Methods:
		tick() - What happens each frame
********************************************************************************************************************/

import javax.swing.*;
import java.util.*;

public class Ant extends JPanel {
	private Random rand = new Random(); //Random seed
	private int dir; //Holds an int between 0-7, to determine which direction the ant should move
	private int timer; //Holds the amount of ticks that have passed, used to determine when the ant should change direction
	private int changeDirTime = 200; //How often the ants should change direction, 200 is a good amount. The higher the number, the longer the ants will continue in their chosen direction
	private int randomTimer; //Holds the randomTimer, which is a randomized time limit for how long the ants move in one direction, this uses the changeDirTime to vary the amount each time
	private int xIndex; //Holds the xIndex in squares instead of ant x position
	private int yIndex; //Holds the yIndex in squares instead of any y position
	private int elementIndex; //Holds the element index of the current square the ant is over
	private Terrain currentSquare; //Terrain object that represents the current square the ant is one

	//Finals
	public static final int ANTSIZE = 24; //Size of ant
	private static final int WIDTH = Pathfinder.GRIDWIDTH; //Width of the map
	private static final int HEIGHT = WIDTH; //Height of the map

	//For key presses
	private boolean up; //If moving up
	private boolean down; //If moving down
	private boolean left; //If moving left
	private boolean right; //If moving right
	private boolean canMove = true; //Whether the ant can move in it's current direction
	private boolean food = true; //Whether or not the ant has or is searching for water
	private boolean water; //Whether or not the ant is searching for water
	private boolean searching = true; //Whether or not the ant is searching for resources
	public boolean alive = true; //Whether or not the ant is alive

	//Ant variables
	private double speed = 3; //Ant speed
	private double diagSpeed = 1; //Ant diagonal speed
	int x; //x position
	int y; //y position
	int xMid;
	int yMid;
	int i;
	public String state; //The state of the ant, it can be searching for food, water, or returning food

	//Ant() constructor - Take x and y position
	public Ant(int x, int y) {
		i = 0;
		this.x = x; //Capture x position in ant object
		this.y = y; //Capture y position in ant object
	}

	//tick() - What happens each frame
	public void tick() {
		xMid = x+(ANTSIZE/2);
		yMid = y+(ANTSIZE/2);
		if(i < Pathfinder.squares.path.size()) {
			int targetX = Pathfinder.squares.path.get(i).x+(Pathfinder.squares.rowWidth/2);
			int targetY = Pathfinder.squares.path.get(i).y+(Pathfinder.squares.rowHeight/2);
			if(xMid > targetX-speed) {
				x -= speed;
			}
			if(xMid < targetX+speed) {
				x += speed;
			}
			if(yMid > targetY-speed) {
				y -= speed;
			}
			if(yMid < targetY+speed) {
				y += speed;
			}
			if(xMid <= targetX+speed && xMid >= targetX-speed && yMid <= targetY+speed && yMid >= targetY-speed) {
				i++;
			}
		}
	}
}