import java.awt.Color;


public class SnakeColor {
	public static Color getColor(int id) throws OutOfColorsException {
		switch (id) {
			case 0: return Color.red;
			case 1: return Color.blue;
			case 2: return Color.green;
			case 3: return Color.pink;
			case 4: return Color.orange;
			case 5: return Color.cyan;
			default:
				throw new OutOfColorsException();
		}
	}
	
	private static class OutOfColorsException extends Exception {
		
	}
}
