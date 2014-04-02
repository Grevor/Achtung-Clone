import java.awt.Color;

import org.ejml.alg.dense.mult.MatrixMatrixMult;
import org.ejml.alg.dense.mult.MatrixMultProduct;
import org.ejml.alg.dense.mult.MatrixVectorMult;
import org.ejml.alg.fixed.FixedOps2;
import org.ejml.data.FixedMatrix2_64F;
import org.ejml.ops.MatrixFeatures;


public class Snake {
	private static double DEFAULT_SNAKE_RADIUS = 3;
	private static double DEFAULT_SPEED = 2;
	private static double DEFAULT_TURN_SPEED = 2;
	FixedMatrix2_64F position, direction;
	Color color;
	double turnSpeed;
	double snakeRadius;
	boolean alive = true;
	Controler control;
	
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
	}
	
	public int getColorAsRGB() {
		return color.getRGB();
	}

	public double getSpeed() {
		return Math.sqrt(FixedOps2.dot(direction, direction));
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
	
	public void update() {
		checkControler();
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
}
