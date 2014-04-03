package model;
import org.ejml.data.FixedMatrix2_64F;


public class CollisionData {
	private FixedMatrix2_64F position;
	private boolean isHole;
	
	public CollisionData(FixedMatrix2_64F pos, boolean isHole) {
		this.position = pos;
		this.isHole = isHole;
	}
	
	public FixedMatrix2_64F getPosition() { return position;}
	public boolean isHole(){return isHole;}
}
