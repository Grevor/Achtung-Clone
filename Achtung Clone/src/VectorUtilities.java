import java.awt.Point;

import org.ejml.alg.fixed.FixedOps2;
import org.ejml.data.FixedMatrix2_64F;
import org.ejml.data.FixedMatrix2x2_64F;


public class VectorUtilities {
	public static FixedMatrix2_64F setAngle(FixedMatrix2_64F vector, double angle) {
		FixedMatrix2_64F ret = new FixedMatrix2_64F();
		double deltaAngle = angle - getAngle(vector);
		FixedOps2.mult(new FixedMatrix2x2_64F(Math.cos(deltaAngle), -Math.sin(deltaAngle), 
				Math.sin(deltaAngle), Math.cos(deltaAngle)), vector, ret);
		return ret;
	}

	public static FixedMatrix2_64F rotate(FixedMatrix2_64F vector, double angle) {
		FixedMatrix2_64F ret = new FixedMatrix2_64F();
		FixedOps2.mult(new FixedMatrix2x2_64F(Math.cos(angle), -Math.sin(angle), 
				Math.sin(angle), Math.cos(angle)), vector, ret);
		return ret;
	}

	public static double getAngle(FixedMatrix2_64F vector) {
		return Math.atan2(vector.a2, vector.a1);
	}

	public static Point vectorToPoint(FixedMatrix2_64F vector) {
		int x = (int)Math.round(vector.get(0, 0));
		int y = (int)Math.round(vector.get(0, 1));
		return new Point(x,y);
	}

	public static double getLength(FixedMatrix2_64F vector) {
		return Math.sqrt(FixedOps2.dot(vector, vector));
	}
}
