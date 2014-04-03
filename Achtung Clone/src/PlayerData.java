import java.awt.Color;


public class PlayerData {
	private String name;
	private KeyCode leftKC, rightKC;
	private int score;
	private Controler control;
	private Color color;
	private ScoreUpdateListener scoreListener;
	private NameUpdateListener nameListener;
	
	public PlayerData(String name, Color color, int leftKeyCode, int rightKeyCode) {
		this.name = name;
		this.color = color;
		leftKC = new KeyCode(leftKeyCode);
		rightKC = new KeyCode(rightKeyCode);
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
		nameListener.nameChanged(name);
	}
	
	public String getName() {
		return name;
	}
	
	public KeyCode getLeftKeyCode() {
		return leftKC;
	}
	
	public KeyCode getRightKeyCode() {
		return rightKC;
	}
	
	public boolean bothKeysSet() {
		return leftKC.keyCode != 0 && rightKC.keyCode != 0;
	}
	
	public void incrementScore(int increment) {
		score += increment;
		scoreListener.scoreChanged(score);
	}
	
	public void resetScore() {
		score = 0;
		scoreListener.scoreChanged(score);
	}
	
	public int getScore() { return score; }
	
	public void setControler(Controler c) { control = c; }
	
	public Controler getControler() { return control; }
	
	public void addScoreListener(ScoreUpdateListener l) {
		scoreListener = l;
	}
	
	public void clearScoreUpdateListener(ScoreUpdateListener l) {
		scoreListener = null;
	}
	
	public void setNameListener(NameUpdateListener l) { this.nameListener = l; }
	
	public void clearNameListener() { nameListener = null; }
}
