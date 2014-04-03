import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ListIterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class GameStartMenu extends JPanel implements MouseListener {
	private static final int padding = 15;
	private final static Font titleFont = new Font("Helvectica", Font.PLAIN, 30);
	private final static Color textColor = Color.white;
	private final JTextField[] playerNames = new JTextField[Game.maxPlayers];
	private final JPanel playerPanel = new JPanel(new GridLayout(Game.maxPlayers+1,3, padding,padding));
	private final JButton start = new JButton("Start Game!");
	private final GameStartDataListener startListener;
	
	public GameStartMenu(GameStartDataListener startListener) {
		super();
		this.startListener = startListener;
		this.setOpaque(false);
		this.setBackground(Color.red);
		this.setLayout(new GridBagLayout());
		playerPanel.setOpaque(false);
		addTitles();
		addPlayers();
		this.add(playerPanel);
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(20,0,0,0);
		gc.gridx = 0;
		gc.gridy = 1;
		this.add(start, gc);
		start.addMouseListener(this);
	}
	
	private void addTitles() {
		playerPanel.add(newTitleLabel("Player"));
		playerPanel.add(newTitleLabel("Left"));
		playerPanel.add(newTitleLabel("Right"));
	}
	
	private JLabel newTitleLabel(String text) {
		final JLabel lbl = new JLabel(text);
		lbl.setForeground(textColor);
		lbl.setFont(titleFont);
		return lbl;
	}
	
	private void addPlayers() {
		ListIterator<PlayerData> playerDataIter = startListener.getPlayerData().listIterator();
		for (int player = 0; player < Game.maxPlayers; player++) {
			PlayerData playerData = playerDataIter.next();
			playerNames[player] = new JTextField(playerData.getName());
			playerNames[player].setForeground(PlayerColors.getColor(player));
			playerPanel.add(playerNames[player]);
			playerPanel.add(new HotkeyButton(player, playerData.getLeftKeyCode()));
			playerPanel.add(new HotkeyButton(player, playerData.getRightKeyCode()));
		}
	}
	
	private class HotkeyButton extends JButton implements KeyListener, MouseListener {
		private KeyCode hotkey;
		int playerIndex;
		
		public HotkeyButton(int id, KeyCode hotkey) {
			super();
			playerIndex = id;
			this.hotkey = hotkey;
			if(hotkey.keyCode == 0) this.setText("");
			else this.setText(KeyEvent.getKeyText(hotkey.keyCode));
			setForeground(PlayerColors.getColor(id));
			this.addMouseListener(this);
		}
		
		@Override
		public void keyPressed(KeyEvent arg0) {
			switch(arg0.getKeyCode()) {
				case 0:
				case KeyEvent.VK_SPACE:
					break;
				case KeyEvent.VK_ESCAPE:
					hotkey.keyCode = 0;
					this.setText("");
					break;
				default:
					hotkey.keyCode = arg0.getKeyCode();
					this.setText(KeyEvent.getKeyText(hotkey.keyCode));
			}
			this.removeKeyListener(this);
		}
		
		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
			this.addKeyListener(this);
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		startListener.start();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
