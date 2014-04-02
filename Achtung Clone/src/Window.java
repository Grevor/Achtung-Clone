import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Window extends JFrame implements FocusListener {
	private static final String	title					= "Achtung!";
	private GraphicsDevice			screen				= GraphicsEnvironment.getLocalGraphicsEnvironment()
																								.getDefaultScreenDevice();
	private boolean							isFullScreen	= false;
	private GUIState						state;
	private JPanel							mainMenu, gameStartMenu, gamePanel;
	private Game								game;

	private class UnknownGUIStateException extends Exception {
	}

	private enum GUIState {
		MAIN_MENU, GAME_START_MENU, IN_GAME_PAUSED, IN_GAME_PLAYING;
	}

	public Window(Game game) {
		super(title);
		this.addFocusListener(this);
		this.game = game;
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
		// setState(GUIState.MAIN_MENU);
		setState(GUIState.GAME_START_MENU);
		setVisible(true);
	}

	private void createMenus() {
		mainMenu = new MainMenu();
		gameStartMenu = new GameStartMenu(game);
		gamePanel = new JPanel();
		gamePanel.setBorder(new EmptyBorder(0,0,0,0));
		gamePanel.setFocusable(false);
		gamePanel.addFocusListener(this);
	}

	private void setState(GUIState newState) {
		if ((state.ordinal() ^ newState.ordinal()) != 0) {
			getContentPane().removeAll();
			state = newState;
			getContentPane().add(getMenu(newState));
			validate();
		}
	}
	
	public JPanel getGamePanel() {
		return this.gamePanel;
	}

	private JPanel getMenu(GUIState state) {
		switch (state) {
			case MAIN_MENU:
				return mainMenu;
			case GAME_START_MENU:
				return gameStartMenu;
			case IN_GAME_PAUSED:
			case IN_GAME_PLAYING:
				return gamePanel;
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
		if (screen.isDisplayChangeSupported()) {
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

	public void displayGame(BufferedImage bufferedImage) {
		// TODO Auto-generated method stub
		gamePanel.add(new JLabel(new ImageIcon(bufferedImage)));
		setState(GUIState.IN_GAME_PAUSED);
		this.requestFocus();
	}
	
	@Override
	public void focusGained(final FocusEvent arg0) {
		this.requestFocus();
	}

	@Override
	public void focusLost(final FocusEvent arg0) {
		// do nothing
	}
}
