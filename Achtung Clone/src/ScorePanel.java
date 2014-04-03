import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class ScorePanel extends JPanel {
	private static final int padding = 10;
	private static final Color headerColor = Color.white;
	private static final Font headerFont = new Font("Helvetica", Font.PLAIN, 30);
	private static final Font playerFont = new Font("Helvetica", Font.PLAIN, 25);
	
	private final NameLabel[] nameLabels;
	private final ScoreLabel[] scoreLabels;
	
	public ScorePanel() {
		super();
		this.setOpaque(false);
		nameLabels = new NameLabel[Game.maxPlayers];
		scoreLabels = new ScoreLabel[Game.maxPlayers];
		this.setLayout(new GridLayout(1+Game.maxPlayers, 2, padding, padding));
		addHeaderLabel("Player");
		addHeaderLabel("Score");
		addPlayerRows(Game.maxPlayers);
	}
	
	public void setPlayerData(LinkedList<PlayerData> playerData) {
		int i = 0;
		for (Iterator iter = playerData.iterator(); iter.hasNext();) {
			PlayerData pd = (PlayerData) iter.next();
			nameLabels[i].subscribe(pd);
			scoreLabels[i].subscribe(pd);
			i++;
		}
		for (; i < nameLabels.length; i++) {
			nameLabels[i].desubscribe();
			scoreLabels[i].desubscribe();
		}
	}
	
	private void addPlayerRows(int maxplayers) {
		for (int i = 0; i < nameLabels.length; i++) {
			add(nameLabels[i] = new NameLabel());
			add(scoreLabels[i] = new ScoreLabel());
		}
	}
	
	private JLabel playerLabel() {
		JLabel lbl = new JLabel();
		
		return lbl;
	}

	private void addHeaderLabel(String text) {
		JLabel lbl = new JLabel(text);
		lbl.setFont(headerFont);
		lbl.setForeground(headerColor);
		add(lbl);
	}
	
	private class ScoreLabel extends JLabel implements ScoreUpdateListener {
		PlayerData subscription;
		
		public ScoreLabel() {
			this.setFont(playerFont);
		}
		
		public void subscribe(PlayerData pd) {
			this.setForeground(pd.getColor());
			subscription = pd;
			pd.addScoreListener(this);
			this.setText(Integer.toString(pd.getScore()));
		}
		
		public void desubscribe() {
			if (subscription != null) {
				subscription.clearScoreUpdateListener(this);
				subscription = null;
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
			this.setForeground(pd.getColor());
			subscription = pd;
			pd.setNameListener(this);
			this.setText(pd.getName());
		}
		
		public void desubscribe() {
			if (subscription != null) {
				subscription.clearNameListener();
				subscription = null;
			}
		}
		
		@Override
		public void nameChanged(String newName) {
			this.setText(newName);
		}
		
	}
}
