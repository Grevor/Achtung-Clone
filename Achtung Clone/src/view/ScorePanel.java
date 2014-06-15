package view;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.PlayerData;
import model.PlayerDataUpdateListener;
import core.Game;


public class ScorePanel extends JPanel {
	private static final int padding = 10;
	private static final Color headerColor = Color.white;
	private static final Font headerFont = new Font("Helvetica", Font.PLAIN, 30);
	private static final Font playerFont = new Font("Helvetica", Font.PLAIN, 25);
	
	private final PlayerRow[] playerRows;
	private PlayerData[] sortedPlayers;
	
	public ScorePanel() {
		super();
		playerRows = new PlayerRow[Game.maxPlayers];
		this.setOpaque(false);
		this.setLayout(new GridLayout(1+Game.maxPlayers, 2, padding, padding));
		this.setFocusable(false);
		addHeaderLabel("Player");
		addHeaderLabel("Score");
		addPlayerRows(Game.maxPlayers);
	}
	
	public void setPlayerData(PlayerData[] activePlayers) {
		sortedPlayers = activePlayers;
		update();
	}
	
	public void update() {
		Arrays.sort(sortedPlayers, PlayerData.DescendingScoreComparator.instance);
		updatePlayerRows();
	}
	
	public void setPlayerData(Iterator<PlayerData> iter) {
		int i = 0;
		while(iter.hasNext()) {
			PlayerData pd = (PlayerData) iter.next();
			playerRows[i].subscribe(pd);
			i++;
		}
		for (; i < playerRows.length; i++) {
			playerRows[i].unsubscribe();
		}
	}
	
	private void updatePlayerRows() {
		for (int i = 0; i < sortedPlayers.length; i++) {
			playerRows[i].display(sortedPlayers[i]);
		}
		for (int i = sortedPlayers.length; i < playerRows.length; i++) {
			playerRows[i].clear();
		}
	}
	
	private void addHeaderLabel(String text) {
		JLabel lbl = new JLabel(text);
		lbl.setFont(headerFont);
		lbl.setForeground(headerColor);
		add(lbl);
	}
	
	private void addPlayerRows(int maxplayers) {
		for (int i = 0; i < playerRows.length; i++) {
			playerRows[i] = new PlayerRow();
			add(playerRows[i].nameLabel);
			add(playerRows[i].scoreLabel);
		}
	}
	
	private class PlayerRow implements PlayerDataUpdateListener {
		private final JLabel nameLabel, scoreLabel;
		private PlayerData subscription;
		
		public PlayerRow() {
			scoreLabel = new JLabel("",SwingConstants.CENTER);
			initPlayerLabel(scoreLabel);
			nameLabel = new JLabel();
			initPlayerLabel(nameLabel);
		}
		
		private void initPlayerLabel(JLabel lbl) {
			lbl.setFont(playerFont);
		}
		
		public void display(PlayerData pd) {
			this.nameLabel.setForeground(pd.getColor());
			this.nameLabel.setText(pd.getName());
			this.scoreLabel.setForeground(pd.getColor());
			this.scoreLabel.setText(Integer.toString(pd.getScore()));
		}
		
		public void clear() {
			this.nameLabel.setText("");
			this.scoreLabel.setText("");
		}
		
		public void subscribe(PlayerData pd) {
			unsubscribe();
			subscription = pd;
			pd.addUpdateListener(this);
			nameLabel.setForeground(pd.getColor());
			scoreLabel.setForeground(pd.getColor());
			if (pd.isPlayerActivated()) {
				nameLabel.setText(pd.getName());
				scoreLabel.setText(Integer.toString(pd.getScore()));
			}
		}
		
		public void unsubscribe() {
			if (subscription != null) {
				subscription.clearUpdateListener();
				subscription = null;
				nameLabel.setText("");
				scoreLabel.setText("");
			}
		}
		
		public void scoreChanged(int newScore) {
			if (subscription.isPlayerActivated())
				scoreLabel.setText(Integer.toString(newScore));
		}
		
		public void nameChanged(String newName) {
			if (subscription.isPlayerActivated())
				nameLabel.setText(newName);
		}

		@Override
		public void activated() {
			nameLabel.setText(subscription.getName());
			scoreLabel.setText(Integer.toString(subscription.getScore()));
		}

		@Override
		public void deactivated() {
			nameLabel.setText("");
			scoreLabel.setText("");
		}
	}
}
