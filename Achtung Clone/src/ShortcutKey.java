import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ShortcutKey implements KeyListener {
	private int shortcut;
	protected boolean isPressed = false;
	public ShortcutKey(int shortcutKeycode) {
		this.shortcut = shortcutKeycode;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(this.shortcut == e.getKeyCode()) 
			this.isPressed = true;
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(this.shortcut == e.getKeyCode()) this.isPressed = false;		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	public boolean isPressed() {
		return this.isPressed;
	}
}
