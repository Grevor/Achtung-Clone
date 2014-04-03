import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import org.ejml.data.FixedMatrix2_64F;


public class World {
	private static double wallSpawnOffset = Snake.getTurnRadius() + Snake.DEFAULT_SNAKE_RADIUS * 2;
	private static final int collisionTickLag = 2;
	private int collisionRGBColor;
	private final BufferedImage map, collisionMap;
	private int nPlayers;
	private PlayerData[] players;
	private Snake[] snakes;
	private int[] score;
	private int nAliveSnakes;
	private boolean	roundAlive;
	private long currentTick;

	public World (PlayerData[] players, int width, int height) {
		int numberOfPlayers = players.length;
		if (numberOfPlayers < 1) {
			throw new IllegalArgumentException("World need at least 1 player");
		}
		this.players = players;
		map = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		collisionMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		nPlayers = numberOfPlayers;
		snakes = new Snake[numberOfPlayers];
		score = new int[numberOfPlayers];
		initSnakes(players);
		resetWorld();
	}

	public void resetWorld() {
		clearBufferedImage(map);
		clearBufferedImage(collisionMap);
		collisionRGBColor = collisionMap.getRGB(0, 0);
		drawEdgeCollision(collisionMap);
		drawEdgeCollision(map);
		currentTick = 0;
		for (int i = 0; i < nPlayers; i++) {
			score[i] = 0;
		}
		nextRound();
	}

	private void drawEdgeCollision(BufferedImage collMap) {
		Graphics2D g = collMap.createGraphics();
		g.setColor(Color.white);
		g.setStroke(new BasicStroke(6));
		g.drawLine(0, 0, collMap.getWidth(), 0);
		g.drawLine(0, 0, 0, collMap.getHeight());
		g.drawLine(collMap.getWidth(), 0, collMap.getWidth(), collMap.getHeight());
		g.drawLine(0, collMap.getHeight(), collMap.getWidth(), collMap.getHeight());
		g.dispose();
	}

	private void clearBufferedImage(BufferedImage image) {
		Graphics g = image.createGraphics();
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
		g.dispose();
	}

	public void nextRound() {
		nAliveSnakes = nPlayers;
		for (int i = 0; i < nPlayers; i++) {
			snakes[i].respawn(
					wallSpawnOffset + Math.random()*(this.getWidth()-2*wallSpawnOffset),
					wallSpawnOffset + Math.random()*(this.getHeight()-2*wallSpawnOffset)
					);
		}
		roundAlive = true;
	}

	public void drawHeads() {
		for (int i = 0; i < snakes.length; i++) {
			drawNewSnakeHead(snakes[i],i, map);
		}
	}

	public void endRound() {
		roundAlive = false;
	}

	public boolean isAlive() {
		return roundAlive;
	}

	public int getWidth() {
		return map.getWidth();
	}

	public int getHeight() {
		return map.getHeight();
	}

	private void initSnakes(PlayerData[] players) {
		for (int i = 0; i < nPlayers; i++) {
			try {
				snakes[i] = new Snake(players[i].getColor(), players[i].getControler());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void update() { update(true); }

	public void update(boolean shouldRenderTails) {
		if (!isAlive()) {
			return;
		}
		currentTick++;
		updateAllSnakes();

		Graphics2D g = map.createGraphics();
		for (int i = 0; i < nPlayers; i++) {
			if (snakes[i].isAlive()) {
				FixedMatrix2_64F newPos = snakes[i].getPosition();
				int x = (int)Math.round(newPos.get(0, 0));
				int y = (int)Math.round(newPos.get(0, 1));
				if (snakeCollides(snakes[i])) {
					killSnake(i);
				}
				else {
					if(!snakes[i].hasHoleThisTick() && shouldRenderTails) {
						Point oldPosition = VectorUtilities.vectorToPoint(snakes[i].getLastPosition());
						clearOldSnakeHead(snakes[i], snakes[i].getColorAsRGB());
						g.setStroke(new BasicStroke((float)snakes[i].getRadius() * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
						g.setColor(snakes[i].getColor());
						g.drawLine(x, y, oldPosition.x, oldPosition.y);
					} else {
						clearOldSnakeHead(snakes[i], snakes[i].getColorAsRGB());
						drawNewSnakeHead(snakes[i], i, map);
					}
				}
				updateCollision(snakes[i], i);
			}
		}
		g.dispose();
	}

	private void drawNewSnakeHead(Snake snake, int id, BufferedImage img){ drawNewSnakeHead(snake, id,img, true);}
	
	private void drawNewSnakeHead(Snake snake, int id, BufferedImage img, boolean respectOldColor) {
		ArrayList<Point> getAllPoints = getCollisionPoints(snake.getPosition(), snake.getRadius());

		for (Point p : getAllPoints) {
			if(!respectOldColor || (respectOldColor && img.getRGB(p.x, p.y) != snake.getColorAsRGB())) img.setRGB(p.x, p.y, PlayerColors.getSnakeHeadColor(id).getRGB());
		}
	}

	private void clearOldSnakeHead(Snake snake, int colorAsRGB) {
		ArrayList<Point> getAllPoints = getCollisionPoints(snake.getLastPosition(), snake.getRadius());

		for (Point p : getAllPoints) {
			if(map.getRGB(p.x, p.y) != colorAsRGB) map.setRGB(p.x, p.y, Color.BLACK.getRGB());
		}
	}

	private void updateAllSnakes() {
		for (int i = 0; i < snakes.length; i++) {
			if (snakes[i].isAlive()) {
				snakes[i].update();
			}
		}
	}

	private void updateCollision(Snake snake, int i) {
		if(collisionTickLag < currentTick) {
			snake.popCollisionPosition();
			if(!snake.hasHoleThisTick()) drawNewSnakeHead(snake, i, collisionMap, false);
		}
	}

	private boolean snakeCollides(Snake snake) {
		double radius = snake.getRadius();
		FixedMatrix2_64F center = snake.getPosition();
		ArrayList<Point> allPoints = getCollisionPoints(center, radius);
		for (int i = 0; i < allPoints.size(); i++) {
			Point p = allPoints.get(i);
			System.err.println(collisionMap.getRGB(p.x,  p.y));
			//Magic value! :D
			//-16777216
			if(collisionMap.getRGB(p.x, p.y) != collisionRGBColor && !pointIsInPreviousSnakePositions(p, snake)) 
				return true;
		}
		return false;
	}

	private boolean pointIsInPreviousSnakePositions(Point p, Snake snake) {
		Iterator<CollisionData> iter = snake.getLastPositionsIterator();
		for(;iter.hasNext();) {
			ArrayList<Point> points = getCollisionPoints(iter.next().getPosition(), snake.getRadius());
			for(Point pp : points) {
				if (p.x == pp.x && p.y == pp.y)
					return true;
			}
		}
		return false;
	}

	/**
	 * Gets all grid-points which are of interest when checking for collision.
	 * @param center - The center of the object.
	 * @param radius - The radius.
	 * @return
	 * All points which the snake will collide with. These points will be inside the grid.
	 */
	private ArrayList<Point> getCollisionPoints(FixedMatrix2_64F center, double radius) {
		ArrayList<Point> ret = new ArrayList<Point>(20);
		int startX = (int)(center.a1 - radius);
		int startY = (int)(center.a2 - radius);
		int endX = startX + (int)(2 * radius);
		int endY = startY + (int)(2 * radius);
		for(int y = startY; y <= endY; y++) {
			for(int x = startX; x <= endX; x++) {
				if(validMapCoordinate(x,y) && getDistanceToPoint(x,y, center) <= radius) ret.add(new Point(x,y));
			}
		}
		return ret;
	}

	private double getDistanceToPoint(int x, int y, FixedMatrix2_64F center) {
		return VectorUtilities.getLength(
				new FixedMatrix2_64F(center.a1 - x - .5,center.a2 - y - .5));
	}

	private boolean validMapCoordinate(int x, int y) {
		return 0 <= x && x < getWidth() && 0 <= y && y < getHeight();
	}

	public void killSnake(int id) {
		if (!snakes[id].isAlive()) {
			throw new IllegalArgumentException("Snake " + id + " is already dead.");
		}
		snakes[id].kill();
		nAliveSnakes--;
		for (int i = 0; i < nPlayers; i++) {
			if (snakes[i].isAlive()) {
				players[i].incrementScore(1);
			}
		}
		if (nAliveSnakes < 2) {
			endRound();
		}
	}

	public BufferedImage getBufferedImage() {
		return map;
	}

	public void kill() {
		this.roundAlive = false;
	}

}
