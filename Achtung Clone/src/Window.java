import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Window extends JFrame {
	private static final String title = "Achtung!";
	private GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	private boolean isFullScreen = false;
	private GUIState state;
	private JPanel mainMenu, gameStartMenu;
	
	private class UnknownGUIStateException extends Exception {}
	
	private enum GUIState {
		MAIN_MENU, GAME_START_MENU, IN_GAME_PAUSED, IN_GAME_PLAYING;
	}
	
	public Window() {
		super(title);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				System.exit(0);
			}
		});
		setMinimumSize(new Dimension(800, 600));
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().setBackground(Color.black);
		setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
		createMenus();
		state = GUIState.MAIN_MENU;
		//setState(GUIState.MAIN_MENU);
		setState(GUIState.GAME_START_MENU);
		setVisible(true);
	}
	
	private void createMenus() {
		mainMenu = new MainMenu();
		gameStartMenu = new GameStartMenu();
	}
	
	private void setState(GUIState newState) {
		if ((state.ordinal() ^ newState.ordinal()) != 0) {
			getContentPane().removeAll();
			state = newState;
			getContentPane().add(getMenu(newState));
			validate();
		}
	}
	
	private JPanel getMenu(GUIState state) {
		switch(state) {
			case MAIN_MENU: return mainMenu;
			case GAME_START_MENU: return gameStartMenu;
			default:
				throw new IllegalArgumentException("Unknow/unimplemented GUIState.");
		}
	}
	
	public void setFullScreen(boolean fullscreen) {
		if (fullscreen ^ isFullScreen) {
			if (fullscreen) {
				goFullScreen();
			}
			else {
				restoreScreen();
			}
		}
	}
	
	private void goFullScreen() {
		setUndecorated(true);
		setResizable(false);
		screen.setFullScreenWindow(this);
		if(screen.isDisplayChangeSupported()) {
			screen.setDisplayMode(screen.getDisplayMode());
			isFullScreen = true;
		}
	}
	
	private void restoreScreen() {
		setUndecorated(false);
		setResizable(true);
		screen.setFullScreenWindow(null);
		isFullScreen = false;
	}
}
