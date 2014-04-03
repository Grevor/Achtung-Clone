import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import org.ejml.data.FixedMatrix2_64F;


public class Snake {
	public static final double DEFAULT_SNAKE_RADIUS = 3;
	private static final double DEFAULT_SPEED = 60.0 / Game.maxFPS;
	private static final double DEFAULT_TURN_SPEED = Math.PI / Game.maxFPS * .6;
	private static final long SNAKE_OPENING_CYCLE_LENGTH = 120;
	private static final long SNAKE_OPENING_TIME = 20;
	private static Random startingTickRandomizer = new Random();
	private FixedMatrix2_64F position, direction;
	private Deque<CollisionData> lastPositions;
	private Color color;
	private double turnSpeed;
	private double snakeRadius;
	private boolean alive;
	private Controler control;
	private long currentTick;
	
	public Snake(Color c, Controler controler) {
		this(c,controler, DEFAULT_SNAKE_RADIUS);
	}
	
	public Snake(Color color, Controler controler, double radius) {
		this.color = color;
		this.control = controler;
		snakeRadius = radius;
	}

	public void respawn(double x, double y) {
		position = new FixedMatrix2_64F(x, y);
		direction = new FixedMatrix2_64F(DEFAULT_SPEED, 0);
		this.alive = true;
		turnSpeed = DEFAULT_TURN_SPEED;
		this.snakeRadius = DEFAULT_SNAKE_RADIUS;
		this.lastPositions = new ArrayDeque<CollisionData>(10);
		this.currentTick = startingTickRandomizer.nextInt((int)SNAKE_OPENING_CYCLE_LENGTH);
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
	
	public void update() {
		checkControler();
		this.lastPositions.add(
				new CollisionData((FixedMatrix2_64F) position.copy(),this.hasHoleThisTick()));
		this.position.a1 += direction.a1;
		this.position.a2 += direction.a2;
		currentTick++;
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
}
