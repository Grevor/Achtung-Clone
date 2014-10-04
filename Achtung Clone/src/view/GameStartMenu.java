package view;

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
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import model.GameStartDataListener;
import model.KeyCode;
import model.PlayerData;
import core.Game;

public class GameStartMenu extends JPanel implements MouseListener {
	private final static int			padding		= 15;
	private final static Font			titleFont	= new Font("Helvectica", Font.PLAIN, 30);
	private final static Font			playerFont	= new Font("Helvectica", Font.BOLD, 15);
	private final static Color			textColor	= Color.white;
	
	private final PlayerRow[]			playerRows = new PlayerRow[Game.maxPlayers];
	private final JPanel				playerPanel	= new JPanel(new GridLayout(Game.maxPlayers + 1, 3, padding, padding));
	private final JCheckBox				wrap		= new JCheckBox("Wrap map");
	private final JButton				start		= new JButton("Start Game!");
	private final GameStartDataListener	startListener;

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
		gc.insets = new Insets(20, 0, 0, 0);
		gc.gridx = 0;
		gc.gridy = 1;
		wrap.setFocusPainted(false);
		this.add(wrap, gc);
		gc.gridy++;
		this.add(start, gc);
		start.addMouseListener(this);
	}

	private void addTitles() {
		playerPanel.add(newTitleLabel("Player"));
		playerPanel.add(newTitleLabel("Left"));
		playerPanel.add(newTitleLabel("Right"));
	}

	private JLabel newTitleLabel(String text) {
		final JLabel lbl = new JLabel(text, SwingConstants.CENTER);
		lbl.setForeground(textColor);
		lbl.setFont(titleFont);
		return lbl;
	}

	private void addPlayers() {
		Iterator<PlayerData> playerDataIter = startListener.getPlayerData();
		for (int player = 0; player < Game.maxPlayers; player++) {
			playerRows[player] = new PlayerRow(playerDataIter.next());
		}
	}

	private class PlayerRow implements DocumentListener {
		private PlayerData			playerData;
		private final JTextField	nameTextField	= new JTextField();
		private final HotkeyButton	leftKeyButton	= new HotkeyButton(),
													rightKeyButton = new HotkeyButton();

		public PlayerRow(PlayerData pd) {
			nameTextField.setFont(playerFont);
			nameTextField.setHorizontalAlignment(JTextField.CENTER);
			nameTextField.setOpaque(false);
			this.setPlayerData(pd);
			playerPanel.add(nameTextField);
			playerPanel.add(leftKeyButton);
			playerPanel.add(rightKeyButton);
			nameTextField.getDocument().addDocumentListener(this);
		}

		public void setPlayerData(PlayerData pd) {
			this.playerData = pd;
			nameTextField.setText(pd.getName());
			nameTextField.setForeground(pd.getColor());
			leftKeyButton.setKeyCodeObject(pd.getLeftKeyCode());
			leftKeyButton.setForeground(pd.getColor());
			rightKeyButton.setKeyCodeObject(pd.getRightKeyCode());
			rightKeyButton.setForeground(pd.getColor());
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			playerData.setName(nameTextField.getText());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			playerData.setName(nameTextField.getText());
		}
	}

	private class HotkeyButton extends JButton implements KeyListener, MouseListener {
		private KeyCode	hotkey;

		public HotkeyButton() {
			super();
			setFocusPainted(false);
			setContentAreaFilled(false);
			setOpaque(false);
			this.addMouseListener(this);
		}

		public void setKeyCodeObject(KeyCode kcObject) {
			this.hotkey = kcObject;
			if (hotkey.getKeyCode() == 0)
				this.setText("");
			else
				this.setText(KeyEvent.getKeyText(hotkey.getKeyCode()));
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			switch (arg0.getKeyCode()) {
				case 0:
				case KeyEvent.VK_SPACE:
					break;
				case KeyEvent.VK_ESCAPE:
					hotkey.setKeyCode(0);
					this.setText("");
					break;
				default:
					hotkey.setKeyCode(arg0.getKeyCode());
					this.setText(KeyEvent.getKeyText(hotkey.getKeyCode()));
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
		startListener.start(wrap.isSelected());
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
