import java.awt.Color;


public class PlayerColors {
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
	
	public static Color getSnakeHeadColor(int id) {
		switch (id) {
		case 0: return Color.white;
		case 1: return Color.white;
		case 2: return Color.white;
		case 3: return Color.white;
		case 4: return Color.white;
		case 5: return Color.white;
		default:
			throw new OutOfColorsException();
	}
	}
	
	private static class OutOfColorsException extends Error {
		
	}
}
