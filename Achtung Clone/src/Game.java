import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;


public class Game implements GameStartDataListener {
	public final static int maxPlayers = 6;
	public static final int maxFPS = 60;
	private static final long nsPerFrame = 1000000000 / maxFPS;
	
	private final Window gameWindow;
	private LinkedList<PlayerData> playerData;
	private Controler[] controllers;
	private World world;
	
	public Game () {
		playerData = new LinkedList<PlayerData>();
		for (int player = 0; player < maxPlayers; player++) {
			if (player == 0)
				playerData.add(new PlayerData("Bängen spanar", KeyEvent.VK_Q, KeyEvent.VK_W));
			else
				playerData.add(new PlayerData("Player "+player, 0,0));
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
	
	private void createControllers() {
		int nPlayers = numberOfPlayers();
		ListIterator<PlayerData> playerDataIter = playerData.listIterator();
		controllers = new Controler[nPlayers];
		int player = 0;
		for (Iterator iterator = playerData.iterator(); iterator.hasNext();) {
			PlayerData pd = (PlayerData) iterator.next();
			if (pd.bothKeysSet()) {
				controllers[player] = new LocalKeyboardControler(pd.getRightKeyCode().keyCode, pd.getLeftKeyCode().keyCode);
				gameWindow.addKeyListener((LocalKeyboardControler)controllers[player]);
				player++;
			}
		}
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
		createControllers();
		world = new World(controllers, 800, 800); 
				//gameWindow.getContentPane().getWidth(), gameWindow.getContentPane().getHeight());
		gameWindow.displayGame(world.getBufferedImage());
		new Thread( new Runnable() {
			@Override
			public void run() {
				gameLoop();
			}
		}).start();
		
	}

	@Override
	public LinkedList<PlayerData> getPlayerData() {
		return playerData;
	}
}
