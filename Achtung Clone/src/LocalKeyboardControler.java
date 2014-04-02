import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class LocalKeyboardControler implements Controler, KeyListener {

	ShortcutKey clockwise;
	ShortcutKey counterClockwise;
	ShortcutKey back;
	ShortcutKey pauseAndSelect;
	
	public LocalKeyboardControler(int clockwiseKeycode, int counterClockwiseKeycode, 
			int pauseSelectKeycode, int backKeycode) {
		this.clockwise = new ShortcutKey(clockwiseKeycode);
		this.counterClockwise = new ShortcutKey(counterClockwiseKeycode);
		back = new NonRepeatingShortcutKey(backKeycode);
		pauseAndSelect = new NonRepeatingShortcutKey(pauseSelectKeycode);
	}
	
	@Override
	public boolean turnClockwise() {
		return clockwise.isPressed();
	}

	@Override
	public boolean turnCounterClockwise() {
		return counterClockwise.isPressed();
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		this.clockwise.keyPressed(e);
		this.counterClockwise.keyPressed(e);
		this.back.keyPressed(e);
		this.pauseAndSelect.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		this.clockwise.keyReleased(e);
		this.counterClockwise.keyReleased(e);
		this.back.keyReleased(e);
		this.pauseAndSelect.keyReleased(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public boolean pauseAndSelectButton() {
		return pauseAndSelect.isPressed();
	}

	@Override
	public boolean backButton() {
		return this.back.isPressed();
	}
	
}
