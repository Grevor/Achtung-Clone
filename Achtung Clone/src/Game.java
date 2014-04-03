import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;


public class Game implements GameStartDataListener {
	public final static int maxPlayers = 6;
	public static final int maxFPS = 60;
	private static final long nsPerFrame = 1000000000 / maxFPS;
	
	private final Window gameWindow;
	private LinkedList<PlayerData> playerData;
	private World world;
	
	public Game () {
		playerData = new LinkedList<PlayerData>();
		for (int player = 0; player < maxPlayers; player++) {
			if (player == 0)
				playerData.add(new PlayerData("Bängen spanar", PlayerColors.getColor(player), KeyEvent.VK_Q, KeyEvent.VK_W));
			else
				playerData.add(new PlayerData("Player "+(player+1), PlayerColors.getColor(player), 0,0));
		}
		gameWindow = new Window(this);
	}
	
	private void gameLoop() {
		long previousTime = System.nanoTime();
		long newTime = 0;
		long timeDiff = 0;
		long workTime = 0;
		while(world.isAlive()) {
			gameWindow.repaint();
			newTime = System.nanoTime();
			timeDiff = newTime - previousTime;
			workTime += timeDiff;
			previousTime = newTime;
			while(workTime >= 0) {
				world.update();
				workTime -= nsPerFrame;
			}
			try {
				Thread.sleep(-workTime / 1000000);
			} catch(final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean createControllers() {
		int nPlayers = numberOfPlayers();
		if (nPlayers < 1)
			return false;
		for (Iterator<PlayerData> iterator = playerData.iterator(); iterator.hasNext();) {
			PlayerData pd = iterator.next();
			if (pd.bothKeysSet()) {
				pd.setControler(
						new LocalKeyboardControler(pd.getRightKeyCode().keyCode, pd.getLeftKeyCode().keyCode));
				gameWindow.addKeyListener((LocalKeyboardControler)pd.getControler());
			}
		}
		return true;
	}
	
	private int numberOfPlayers() {
		ListIterator<PlayerData> playerDataIter = playerData.listIterator();
		int nPlayers = 0;
		while(playerDataIter.hasNext()) {
			if (playerDataIter.next().bothKeysSet())
				nPlayers++;
		}
		return nPlayers;
	}
	
	@Override
	public void start() {
		if (createControllers()) {
			world = new World(playersToArray(), 800, 800); 
			//gameWindow.getContentPane().getWidth(), gameWindow.getContentPane().getHeight());
			gameWindow.displayGame(world.getBufferedImage());
			new Thread( new Runnable() {
				@Override
				public void run() {
					gameLoop();
				}
			}).start();
		}
		else {
			//TODO: not enough players
		}
	}

	private PlayerData[] playersToArray() {
		ArrayList<PlayerData> ret= new ArrayList<PlayerData>();
		for (Iterator<PlayerData> iter = playerData.iterator(); iter.hasNext();) {
			PlayerData pd = iter.next();
			if(pd.bothKeysSet()) ret.add(pd);
		}
		return (PlayerData[]) ret.toArray(new PlayerData[numberOfPlayers()]);
	}

	@Override
	public LinkedList<PlayerData> getPlayerData() {
		return playerData;
	}
}
