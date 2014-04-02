
public class Game {
	public static final int maxFPS = 60;
	private static final long nsPerFrame = 1000000000 / maxFPS;
	
	private final Window gameWindow;
	private World world;
	
	public Game () {
		gameWindow = new Window();
		
		//world = new World(controler, width, height);
		/*new Thread( new Runnable() {
	    @Override
	    public void run() {
	    	gameLoop();
	    }
		}).start();*/
	}
	
	private void gameLoop() {
		long previousTime = System.nanoTime();
		long newTime = 0;
		long timeDiff = 0;
		long workTime = 0;
		while(world.isAlive()) {
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
}
