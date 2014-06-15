package model;
import java.awt.Color;
import java.util.Comparator;

import controller.Controller;


public class PlayerData {
	
	public static class DescendingScoreComparator implements Comparator<PlayerData> {
		public static final DescendingScoreComparator instance = new DescendingScoreComparator();
		@Override
		public int compare(PlayerData o1, PlayerData o2) {
			return o2.score -o1.score;
		}
	}
	
	private String name;
	private int leftKC, rightKC;
	private KeyCode leftKCinterface, rightKCinterface;
	private int score;
	private Controller control;
	private Color color;
	private PlayerDataUpdateListener updateListener;
	
	public PlayerData(String name, Color color, int leftKeyCode, int rightKeyCode) {
		this.name = name;
		this.color = color;
		leftKC = leftKeyCode;
		rightKC = rightKeyCode;
		leftKCinterface = new LeftKeyCode();
		rightKCinterface = new RightKeyCode();
		score = 0;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setName(String name) {
		this.name = name;
		if (updateListener != null) updateListener.nameChanged(name);
	}
	
	public String getName() {
		return name;
	}
	
	public KeyCode getLeftKeyCode() {
		return leftKCinterface;
	}
	
	public KeyCode getRightKeyCode() {
		return rightKCinterface;
	}
	
	private void announceIfActiveStateChanged(boolean wasActive) {
		if (updateListener != null) {
			boolean isActive = isPlayerActivated();
			if (wasActive == false && isActive)
				updateListener.activated();
			else if (wasActive && isActive == false)
				updateListener.deactivated();
		}
	}
	
	public boolean isPlayerActivated() {
		return leftKC != 0 && rightKC != 0;
	}
	
	public void incrementScore(int increment) {
		score += increment;
		if (updateListener != null) updateListener.scoreChanged(score);
	}
	
	public void resetScore() {
		score = 0;
		if (updateListener != null) updateListener.scoreChanged(score);
	}
	
	public int getScore() { return score; }
	
	public void setControler(Controller c) { control = c; }
	
	public Controller getControler() { return control; }
	
	public void addUpdateListener(PlayerDataUpdateListener updateListener) {
		if (this.updateListener != null) {
			throw new Error("This PlayerDataObject already has a update listener.");
		}
		this.updateListener = updateListener;
	}
	
	public void clearUpdateListener() {
		this.updateListener = null;
	}
	
	private class LeftKeyCode implements KeyCode {

		@Override
		public int getKeyCode() {
			return leftKC;
		}

		@Override
		public void setKeyCode(int keyCode) {
			boolean wasActivated = isPlayerActivated();
			leftKC = keyCode;
			announceIfActiveStateChanged(wasActivated);
		}
		
	}
	
	private class RightKeyCode implements KeyCode {

		@Override
		public int getKeyCode() {
			return rightKC;
		}

		@Override
		public void setKeyCode(int keyCode) {
			boolean wasActivated = isPlayerActivated();
			rightKC = keyCode;
			announceIfActiveStateChanged(wasActivated);
		}
		
	}

}
