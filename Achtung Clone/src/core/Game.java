package core;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import model.GameStartDataListener;
import model.PlayerColors;
import model.PlayerData;
import model.World;
import view.Window;
import controller.LocalKeyboardController;


public class Game implements GameStartDataListener {
	public final static int maxPlayers = 6;
	public static final int maxFPS = 60;
	private static final long nsPerFrame = 1000000000 / maxFPS;
	
	private final Window gameWindow;
	private LinkedList<PlayerData> playerData;
	private World world;
	private boolean running = false;
	
	public Game () {
		playerData = new LinkedList<PlayerData>();
		playerData.add(new PlayerData("Player1", PlayerColors.getColor(0), KeyEvent.VK_Z, KeyEvent.VK_X));
		playerData.add(new PlayerData("Player2", PlayerColors.getColor(1), KeyEvent.VK_PERIOD, KeyEvent.VK_MINUS));
		for (int player = 2; player < maxPlayers; player++) {
			playerData.add(new PlayerData("Player"+(player+1), PlayerColors.getColor(player), 0,0));
		}
		gameWindow = new Window(this);
	}
	
	private void gameLoop() {
		long previousTime = System.nanoTime();
		long newTime = 0;
		long timeDiff = 0;
		long workTime = 0;
		while(world.isAlive()) {
			
			gameWindow.repaint(world.hasScoreChanged());
			newTime = System.nanoTime();
			timeDiff = newTime - previousTime;
			workTime += timeDiff;
			previousTime = newTime;
			while(workTime >= 0) {
				if(this.running) world.update();
				workTime -= nsPerFrame;
			}
			try {
				Thread.sleep(-workTime / 1000000);
			} catch(final InterruptedException e) {
				e.printStackTrace();
			}
		}
		gameWindow.repaint(world.hasScoreChanged());
	}
	
	private boolean createControllers() {
		int nPlayers = numberOfPlayers();
		if (nPlayers < 1)
			return false;
		for (Iterator<PlayerData> iterator = playerData.iterator(); iterator.hasNext();) {
			PlayerData pd = iterator.next();
			if (pd.isPlayerActivated()) {
				pd.setControler(
						new LocalKeyboardController(pd.getRightKeyCode().getKeyCode(), pd.getLeftKeyCode().getKeyCode()));
				gameWindow.addKeyListener((LocalKeyboardController)pd.getControler());
			}
		}
		return true;
	}
	
	private int numberOfPlayers() {
		ListIterator<PlayerData> playerDataIter = playerData.listIterator();
		int nPlayers = 0;
		while(playerDataIter.hasNext()) {
			if (playerDataIter.next().isPlayerActivated())
				nPlayers++;
		}
		return nPlayers;
	}
	
	@Override
	public void start(boolean wrapMap) {
		if (createControllers()) {
			Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
			world = new World(getActivePlayers(), screenDimensions.width * 4/5, screenDimensions.height * 9/10, wrapMap); 
			gameWindow.displayGame(world.getBufferedImage());
			restart();
		}
		else {
			//TODO: not enough players
		}
	}

	public void restart() {
		this.resume();
		world.resetWorld();
		pause();
		new Thread( new Runnable() {
			@Override
			public void run() {
				gameLoop();
			}
		}).start();
	}
	
	public boolean gameEnded() {
		if(world == null) return false;
		else return !world.isAlive();
	}
	
	public void pause() {
		this.running = false;
	}
	
	public void resume() {
		this.running = true;
	}

	public PlayerData[] getActivePlayers() {
		ArrayList<PlayerData> ret= new ArrayList<PlayerData>();
		for (Iterator<PlayerData> iter = playerData.iterator(); iter.hasNext();) {
			PlayerData pd = iter.next();
			if(pd.isPlayerActivated()) ret.add(pd);
		}
		return (PlayerData[]) ret.toArray(new PlayerData[numberOfPlayers()]);
	}

	@Override
	public Iterator<PlayerData> getPlayerData() {
		return playerData.iterator();
	}

	public void stop() {
		world.kill();
		for(PlayerData d : playerData) {
			d.resetScore();
		}
	}
}
