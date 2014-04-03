package controller;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class LocalKeyboardController implements Controller, KeyListener {

	ShortcutKey clockwise;
	ShortcutKey counterClockwise;
	
	public LocalKeyboardController(int clockwiseKeycode, int counterClockwiseKeycode) {
		this.clockwise = new ShortcutKey(clockwiseKeycode);
		this.counterClockwise = new ShortcutKey(counterClockwiseKeycode);
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
	}

	@Override
	public void keyReleased(KeyEvent e) {
		this.clockwise.keyReleased(e);
		this.counterClockwise.keyReleased(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public boolean pauseAndSelectButton() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean backButton() {
		// TODO Auto-generated method stub
		return false;
	}
}
