import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class ScorePanel extends JPanel {
	private static final int padding = 10;
	private static final Color headerColor = Color.white;
	private static final Font headerFont = new Font("Helvetica", Font.PLAIN, 30);
	private static final Font playerFont = new Font("Helvetica", Font.PLAIN, 25);
	
	private final PlayerRow[] playerRows;
	//private final NameLabel[] nameLabels;
	//private final ScoreLabel[] scoreLabels;
	
	public ScorePanel() {
		super();
		playerRows = new PlayerRow[Game.maxPlayers];
		//nameLabels = new NameLabel[Game.maxPlayers];
		//scoreLabels = new ScoreLabel[Game.maxPlayers];
		this.setOpaque(false);
		this.setLayout(new GridLayout(1+Game.maxPlayers, 2, padding, padding));
		this.setFocusable(false);
		addHeaderLabel("Player");
		addHeaderLabel("Score");
		addPlayerRows(Game.maxPlayers);
	}
	
	public void setPlayerData(LinkedList<PlayerData> playerData) {
		int i = 0;
		for (Iterator<PlayerData> iter = playerData.iterator(); iter.hasNext();) {
			PlayerData pd = (PlayerData) iter.next();
			playerRows[i].subscribe(pd);
			//nameLabels[i].subscribe(pd);
			//scoreLabels[i].subscribe(pd);
			i++;
		}
		for (; i < playerRows.length; i++) {
			playerRows[i].desubscribe();
			//nameLabels[i].desubscribe();
			//scoreLabels[i].desubscribe();
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
			//add(nameLabels[i] = new NameLabel());
			//add(scoreLabels[i] = new ScoreLabel());
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
		
		public void subscribe(PlayerData pd) {
			desubscribe();
			subscription = pd;
			pd.addUpdateListener(this);
			nameLabel.setForeground(pd.getColor());
			scoreLabel.setForeground(pd.getColor());
			if (pd.isPlayerActivated()) {
				nameLabel.setText(pd.getName());
				scoreLabel.setText(Integer.toString(pd.getScore()));
			}
		}
		
		public void desubscribe() {
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
		
		/*
		private class ScoreLabel extends JLabel implements ScoreUpdateListener {
			PlayerData subscription;

			public ScoreLabel() {
				super("",SwingConstants.CENTER);
				this.setFont(playerFont);
			}

			public void subscribe(PlayerData pd) {
				if (subscription != null)
					throw new Error("This ScoreLabel is already subscribed to a PlayerData object.");
				this.setForeground(pd.getColor());
				subscription = pd;
				pd.addScoreListener(this);
				if (pd.isPlayerActivated()) {
					this.setText(Integer.toString(pd.getScore()));
				}
			}

			public void desubscribe() {
				if (subscription != null) {
					subscription.clearScoreUpdateListener(this);
					subscription = null;
					this.setText("");
				}
			}

			@Override
			public void scoreChanged(int newScore) {
				this.setText(Integer.toString(newScore));
			}

		}

		private class NameLabel extends JLabel implements NameUpdateListener {
			PlayerData subscription;

			public NameLabel() {
				this.setFont(playerFont);
			}

			public void subscribe(PlayerData pd) {
				if (subscription != null)
					throw new Error("This NameLabel is already subscribed to a PlayerData object.");
				this.setForeground(pd.getColor());
				subscription = pd;
				pd.setNameListener(this);
				if (pd.isPlayerActivated()) {
					this.setText(pd.getName());
				}
			}

			public void desubscribe() {
				if (subscription != null) {
					subscription.clearNameListener();
					subscription = null;
					this.setText("");
				}
			}

			@Override
			public void nameChanged(String newName) {
				this.setText(newName);
			}

		}*/
	}
}
