package model;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import math.VectorUtilities;

import org.ejml.data.FixedMatrix2_64F;


public class World {
	private static double wallSpawnOffset = Snake.getTurnRadius() + Snake.DEFAULT_SNAKE_RADIUS * 2;
	private static final int collisionTickLag = 2;
	private static final int collisionClearRGBColor = 0xFF000000; //ARGB
	private static final int borderSize = 3;
	private final BufferedImage displayMap, collisionMap;
	private int nPlayers;
	private PlayerData[] players;
	private Snake[] snakes;
	private int nAliveSnakes;
	private boolean	roundAlive;
	private long currentTick;
	private boolean wrap = false;
	private boolean scoreChanged;

	public World (PlayerData[] players, int width, int height) {
		int numberOfPlayers = players.length;
		if (numberOfPlayers < 1) {
			throw new IllegalArgumentException("World need at least 1 player");
		}
		this.players = players;
		displayMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		collisionMap = new BufferedImage(width-2*borderSize, height-2*borderSize, BufferedImage.TYPE_INT_RGB);
		nPlayers = numberOfPlayers;
		snakes = new Snake[numberOfPlayers];
		initSnakes(players);
		resetWorld();
	}
	
	public boolean hasScoreChanged() {
		return scoreChanged;
	}

	public void resetWorld() {
		clearBufferedImage(displayMap);
		clearBufferedImage(collisionMap);
		drawEdgeCollision(displayMap);
		currentTick = 0;
		nextRound();
	}

	private void drawEdgeCollision(BufferedImage collMap) {
		Graphics2D g = collMap.createGraphics();
		g.setColor(Color.white);
		g.setStroke(new BasicStroke(borderSize*2));
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
					wallSpawnOffset + Math.random()*(collisionMap.getWidth()-2*wallSpawnOffset),
					wallSpawnOffset + Math.random()*(collisionMap.getHeight()-2*wallSpawnOffset)
					);
		}
		roundAlive = true;
	}

	public void drawHeads() {
		for (int i = 0; i < snakes.length; i++) {
			drawNewSnakeHead(snakes[i],i, displayMap);
		}
	}

	public void endRound() {
		roundAlive = false;
	}

	public boolean isAlive() {
		return roundAlive;
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

	public void update() { 
		update(true); 
	}

	public void update(boolean shouldRenderTails) {
		if (!isAlive()) {
			return;
		}
		scoreChanged = false;
		currentTick++;
		updateAllSnakes();
		
		for (int i = 0; i < nPlayers; i++) {
			if (snakes[i].isAlive()) {
				if (snakeCollides(snakes[i])) {
					killSnake(i);
				}
				else {
					clearOldSnakeHead(snakes[i]);
					if(!snakes[i].hasHoleThisTick() && shouldRenderTails) {
						drawTail(snakes[i]);
					} else {
						drawNewSnakeHead(snakes[i], i, displayMap);
					}
				}
				updateCollision(snakes[i], i);
			}
		}
	}

	private void drawNewSnakeHead(Snake snake, int id, BufferedImage img) {
		drawNewSnakeHead(snake, id,img, true, borderSize, borderSize);
	}
	
	private void drawNewSnakeHead(Snake snake, int id, BufferedImage img, boolean respectOldColor, int offsetX, int offsetY) {
		for (Point p : getCollisionPointsWrapped(snake.getPosition(), snake.getRadius())) {
			int x = p.x + offsetX;
			int y = p.y + offsetY;
			if(!respectOldColor || (img.getRGB(x, y) != snake.getColorAsRGB())) {
				img.setRGB(x, y, PlayerColors.getSnakeHeadColor(id).getRGB());
			}
		}
	}

	private void clearOldSnakeHead(Snake snake) {
		for (Point p : getCollisionPointsWrapped(snake.getLastPosition(), snake.getRadius())) {
			int x = p.x + borderSize;
			int y = p.y + borderSize;
			if (displayMap.getRGB(x, y) != snake.getColorAsRGB()) 
				displayMap.setRGB(x, y, Color.BLACK.getRGB());
		}
	}
	
	private void drawTail(Snake snake) {
		//Graphics2D g = displayMap.createGraphics();
		/*FixedMatrix2_64F newPos = snakes[i].getPosition();
		int x = (int)Math.round(newPos.get(0, 0));
		int y = (int)Math.round(newPos.get(0, 1));
		Point oldPosition = VectorUtilities.vectorToPoint(snakes[i].getLastPosition());
		g.setStroke(new BasicStroke((float)snakes[i].getRadius() * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		g.setColor(snakes[i].getColor());
		g.drawLine(x, y, oldPosition.x, oldPosition.y);*/
		//g.dispose();
		//TODO: Visuals might get messy for high speed snakes but works fine for now
		for (Point p : getCollisionPointsWrapped(snake.getPosition(), snake.getRadius())) {
			int x = p.x + borderSize;
			int y = p.y + borderSize;
			if (displayMap.getRGB(x, y) == collisionClearRGBColor)
				displayMap.setRGB(x, y, snake.getColorAsRGB());
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
			if(!snake.hasHoleThisTick()) drawNewSnakeHead(snake, i, collisionMap, false, 0,0);
		}
	}

	private boolean snakeCollides(Snake snake) {
		if (!wrap && snakeWraps(snake)) {
			return true;
		}
		double radius = snake.getRadius();
		FixedMatrix2_64F center = snake.getPosition();
		ArrayList<Point> collisionPoints = getCollisionPointsWrapped(center, radius);
		for (int i = 0; i < collisionPoints.size(); i++) {
			Point p = collisionPoints.get(i);
			if(collisionMap.getRGB(p.x, p.y) != collisionClearRGBColor && !isPointInPreviousSnakePositions(p, snake)) 
				return true;
		}
		return false;
	}
	
	private boolean snakeWraps(Snake snake) {
		FixedMatrix2_64F lastPos = snake.getLastPosition();
		FixedMatrix2_64F curPos = snake.getPosition();
		double minX, maxX;
		if (lastPos.a1 < curPos.a1) {
			minX = lastPos.a1;
			maxX = curPos.a1;
		}
		else {
			maxX = lastPos.a1;
			minX = curPos.a1;
		}
		minX -= snake.getRadius();
		maxX += snake.getRadius();
		if (minX < 0 || maxX > collisionMap.getWidth()) {
			return true;
		}
		double minY, maxY;
		if (lastPos.a2 < curPos.a2) {
			minY = lastPos.a2;
			maxY = curPos.a2;
		}
		else {
			maxY = lastPos.a2;
			minY = curPos.a2;
		}
		minY -= snake.getRadius();
		maxY += snake.getRadius();
		if (minY < 0 || maxY > collisionMap.getHeight()) {
			return true;
		}
		return false;
	}

	private boolean isPointInPreviousSnakePositions(Point p, Snake snake) {
		Iterator<CollisionData> iter = snake.getLastPositionsIterator();
		while(iter.hasNext()) {
			ArrayList<Point> points = getCollisionPointsWrapped(iter.next().getPosition(), snake.getRadius());
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
	
	/**
	 * Gets all grid-points which are of interest when checking for collision.
	 * @param center - The center of the object.
	 * @param radius - The radius.
	 * @return
	 * All points which the snake will collide with. These points will be inside the grid.
	 */
	private ArrayList<Point> getCollisionPointsWrapped(FixedMatrix2_64F center, double radius) {
		ArrayList<Point> ret = new ArrayList<Point>(20);
		int startX = (int)(center.a1 - radius);
		int startY = (int)(center.a2 - radius);
		int endX = startX + (int)(2 * radius);
		int endY = startY + (int)(2 * radius);
		for(int y = startY; y <= endY; y++) {
			for(int x = startX; x <= endX; x++) {
				if(getDistanceToPoint(x,y, center) <= radius) {
					int xWrapped = x % (collisionMap.getWidth());
					if (xWrapped < 0)
						xWrapped += collisionMap.getWidth();
					int yWrapped = y % (collisionMap.getHeight());
					if (yWrapped < 0)
						yWrapped += collisionMap.getHeight();
					ret.add(new Point(xWrapped,yWrapped)); 
				}
			}
		}
		return ret;
	}

	private double getDistanceToPoint(int x, int y, FixedMatrix2_64F center) {
		return VectorUtilities.getLength(
				new FixedMatrix2_64F(center.a1 - x - .5,center.a2 - y - .5));
	}

	private boolean validMapCoordinate(int x, int y) {
		return 0 <= x && x < collisionMap.getWidth() && 0 <= y && y < collisionMap.getHeight();
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
		scoreChanged = true;
	}

	public BufferedImage getBufferedImage() {
		return displayMap;
	}

	public void kill() {
		this.roundAlive = false;
	}

}
