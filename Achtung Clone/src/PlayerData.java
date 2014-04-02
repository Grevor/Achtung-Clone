
public class PlayerData {
	private String name;
	private KeyCode leftKC, rightKC;
	public PlayerData(String name, int leftKeyCode, int rightKeyCode) {
		this.name = name;
		leftKC = new KeyCode(leftKeyCode);
		rightKC = new KeyCode(rightKeyCode);
	}
	
	public void setName(String name) {
		this.name = name;
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
}
