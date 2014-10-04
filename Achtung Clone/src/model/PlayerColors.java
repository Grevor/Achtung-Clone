package model;
import java.awt.Color;


public class PlayerColors {
	public static Color getColor(int id) throws OutOfColorsException {
		switch (id) {
			case 0: return Color.red;
			case 1: return Color.green;
			case 2: return Color.cyan;
			case 3: return new Color(255,180,30);//Color.orange;
			case 4: return new Color(255,100,190);//Color.pink;//Color.blue;
			case 5: return new Color(100,0,255);//Color.magenta;
			
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
