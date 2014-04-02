
public class NonRepeatingShortcutKey extends ShortcutKey {
	
	public NonRepeatingShortcutKey(int shortcutKeycode) {
		super(shortcutKeycode);
	}
	
	@Override
	public boolean isPressed() {
		boolean answer = super.isPressed();
		isPressed = false;
		return answer;
	}
}
