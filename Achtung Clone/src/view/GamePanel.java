package view;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import model.PlayerData;

public class GamePanel extends JPanel {
	private ImageIcon gameImage;
	
	public GamePanel(Iterator<PlayerData> iterator) {
		this.setOpaque(false);
		this.setLayout(new GridBagLayout());
		this.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.setFocusable(false);
		setUpGameImageField();
		setUpScorePanel(iterator);
	}

	public void setGameField(BufferedImage map) {
		gameImage.setImage(map);
	}

	private void setUpGameImageField() {
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.insets = new Insets(10, 10, 10, 10);
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1;
		this.add(new JLabel(gameImage = new ImageIcon()), gc);
	}
	
	private void setUpScorePanel(Iterator<PlayerData> playerData) {
		ScorePanel scorePanel = new ScorePanel();
		scorePanel.setPlayerData(playerData);
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.insets = new Insets(10, 10, 10, 10);
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1;
		this.add(scorePanel, gc);
	}
}