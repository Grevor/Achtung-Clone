import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;

import org.ejml.data.FixedMatrix2_64F;


public class Snake {
	private static double DEFAULT_SNAKE_RADIUS = 3;
	private static double DEFAULT_SPEED = 35.0 / Game.maxFPS;
	private static double DEFAULT_TURN_SPEED = Math.PI / Game.maxFPS;
	private FixedMatrix2_64F position, direction;
	private Deque<FixedMatrix2_64F> lastPositions;
	private Color color;
	private double turnSpeed;
	private double snakeRadius;
	private boolean alive;
	private Controler control;
	
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
		this.lastPositions = new ArrayDeque<FixedMatrix2_64F>(10);
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
		return lastPositions.peekLast().copy();
	}
	
	public FixedMatrix2_64F popCollisionPosition() {
		return lastPositions.poll();
	}
	
	public FixedMatrix2_64F peekCollisionPosition() {
		return lastPositions.peek();
	}
	
	public void update() {
		checkControler();
		this.lastPositions.add((FixedMatrix2_64F) position.copy());
		this.position.a1 += direction.a1;
		this.position.a2 += direction.a2;
	}

	private void checkControler() {
		if(control.isValid()) {
			if(control.turnClockwise()) {
				direction = VectorUtilities.rotate(direction, turnSpeed);
			} else if(control.turnCounterClockwise()) {
				direction = VectorUtilities.rotate(direction, turnSpeed);
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
