import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class GameStartMenu extends JPanel {
	private final static int maxPlayers = 6;
	private static final int padding = 15;
	private final static Font titleFont = new Font("Helvectica", Font.PLAIN, 30);
	private final static Color textColor = Color.white;
	private final JTextField[] playerNames = new JTextField[maxPlayers];
	private final JPanel playerPanel = new JPanel(new GridLayout(maxPlayers+1,3, padding,padding));
	private final JButton start = new JButton("Start Game!");
	
	public GameStartMenu() {
		super();
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
	}
	
	private void initTextFields() {
		for (int i=0; i<maxPlayers; i++) {
			playerNames[i].setText("Player"+(i+1));
		}
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
		final int offset = 1;
		for (int player = 0; player < maxPlayers; player++) {
			playerNames[player] = new JTextField("Player"+player);
			playerNames[player].setForeground(PlayerColors.getColor(player));
			playerPanel.add(playerNames[player]);
			playerPanel.add(new HotkeyButton(player));
			playerPanel.add(new HotkeyButton(player));
		}
	}
	
	private class HotkeyButton extends JButton {
		private int keyCode = 0;
		int playerIndex;
		public HotkeyButton(int id) {
			super();
			playerIndex = id;
			setForeground(PlayerColors.getColor(id));
		}
	}
}
