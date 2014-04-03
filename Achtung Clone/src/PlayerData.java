
public class PlayerData {
	private String name;
	private KeyCode leftKC, rightKC;
	private int score;
	private Controler control;
	private ScoreUpdateListener scoreListener;
	private NameUpdateListener nameListener;
	public PlayerData(String name, int leftKeyCode, int rightKeyCode) {
		this.name = name;
		leftKC = new KeyCode(leftKeyCode);
		rightKC = new KeyCode(rightKeyCode);
		score = 0;
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
