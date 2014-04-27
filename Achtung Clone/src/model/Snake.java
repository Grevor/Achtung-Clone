package model;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Random;

import math.VectorUtilities;

import org.ejml.data.FixedMatrix2_64F;

import controller.Controller;
import core.Game;


public class Snake {
	public static final double DEFAULT_SNAKE_RADIUS = 3;
	private static final double DEFAULT_SPEED = 100.0 / Game.maxFPS;
	private static final double DEFAULT_TURN_SPEED = Math.PI / Game.maxFPS;
	private static final long SNAKE_OPENING_CYCLE_LENGTH = 120;
	private static final long SNAKE_OPENING_TIME = 15;
	private static Random startingTickRandomizer = new Random();
	private FixedMatrix2_64F position, direction;
	private Deque<CollisionData> lastPositions;
	private Color color;
	private double turnSpeed;
	private double snakeRadius;
	private boolean alive;
	private Controller control;
	private long currentTick;
	
	public Snake(Color c, Controller controler) {
		this(c,controler, DEFAULT_SNAKE_RADIUS);
	}
	
	public Snake(Color color, Controller controler, double radius) {
		this.color = color;
		this.control = controler;
		snakeRadius = radius;
		alive = false;
	}

	public void respawn(double x, double y) {
		position = new FixedMatrix2_64F(x, y);
		double angle = startingTickRandomizer.nextDouble() * Math.PI * 2;
		direction = new FixedMatrix2_64F(
				Math.cos(angle) * DEFAULT_SPEED, 
				Math.sin(angle) * DEFAULT_SPEED);
		this.alive = true;
		turnSpeed = DEFAULT_TURN_SPEED;
		this.snakeRadius = DEFAULT_SNAKE_RADIUS;
		this.lastPositions = new ArrayDeque<CollisionData>(10);
		this.currentTick = startingTickRandomizer.nextInt((int)(SNAKE_OPENING_CYCLE_LENGTH - SNAKE_OPENING_TIME)) 
				+ SNAKE_OPENING_TIME;
		//This is to make sure that the snake will be displayed at the very first frame, 
		//to make sure everyone knows their direction at the start of the game.
		//This will be incremented at the first update, and thus the value will once again be inside the specified bounds.
		currentTick--;
	}
	
	public int getColorAsRGB() {
		return color.getRGB();
	}

	public double getSpeed() {
		return VectorUtilities.getLength(direction);
	}

	public void setSpeed(double newSpeed) {
		if(getSpeed() == 0) return;
		else {
			newSpeed /= getSpeed();
			direction.a1 *=newSpeed;
			direction.a2 *= newSpeed;
		}
	}
	
	public void setRadius(double newRadius) {
		if(newRadius <= 0) throw new IllegalArgumentException("Cannot set a non-positive radius.");
		else this.snakeRadius = newRadius;
	}
	
	public void kill() { this.alive = false;}
	public boolean isAlive() {return alive;}
	
	public FixedMatrix2_64F getPosition() {
		return position.copy();
	}
	
	public FixedMatrix2_64F getLastPosition() {
		return lastPositions.peekLast().getPosition().copy();
	}
	
	public CollisionData popCollisionPosition() {
		return lastPositions.poll();
	}
	
	public CollisionData peekCollisionPosition() {
		return lastPositions.peek();
	}
	
	public boolean hasHoleThisTick() {
		return currentTick % SNAKE_OPENING_CYCLE_LENGTH < SNAKE_OPENING_TIME 
				&& !lastPositions.isEmpty();
	}
	
	public Iterator<CollisionData> getLastPositionsIterator() {
		return lastPositions.iterator();
	}
	
	public void update() {
		checkControler();
		currentTick++;
		this.lastPositions.add(
				new CollisionData((FixedMatrix2_64F) position.copy(),this.hasHoleThisTick()));
		this.position.a1 += direction.a1;
		this.position.a2 += direction.a2;
	}

	private void checkControler() {
		if(control.isValid()) {
			if(control.turnClockwise()) {
				direction = VectorUtilities.rotate(direction, turnSpeed);
			} else if(control.turnCounterClockwise()) {
				direction = VectorUtilities.rotate(direction, -turnSpeed);
			}
		}
	}

	public double getRadius() {
		return this.snakeRadius;
	}

	public Color getColor() {
		return color; 
	}
	
	public static double getTurnRadius() {
		return (Math.PI / DEFAULT_TURN_SPEED) * DEFAULT_SPEED / Math.PI;
	}
}
