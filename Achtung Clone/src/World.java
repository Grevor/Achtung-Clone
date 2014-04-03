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
	private static double wallSpawnOffset = 10;
	private static final int collisionTickLag = (int)(4.8 * Snake.DEFAULT_SNAKE_RADIUS);
	private final BufferedImage map, collisionMap;
	private int nPlayers;
	private Snake[] snakes;
	private int[] score;
	private int nAliveSnakes;
	private boolean	roundAlive;
	private long currentTick;

	public World (Controler[] controlers, int width, int height) {
		int numberOfPlayers = controlers.length;
		if (numberOfPlayers < 1) {
			throw new IllegalArgumentException("World need at least 1 player");
		}
		map = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		collisionMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		nPlayers = numberOfPlayers;
		snakes = new Snake[numberOfPlayers];
		score = new int[numberOfPlayers];
		initSnakes(controlers);
		resetWorld();
	}

	public void resetWorld() {
		clearBufferedImage(map);
		clearBufferedImage(collisionMap);
		drawEdgeCollision(collisionMap);
		currentTick = 0;
		for (int i = 0; i < nPlayers; i++) {
			score[i] = 0;
		}
		nextRound();
	}

	private void drawEdgeCollision(BufferedImage collMap) {
		Graphics2D g = collMap.createGraphics();
		g.setColor(new Color(2));
		g.setStroke(new BasicStroke(2));
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

	private void initSnakes(Controler[] controlers) {
		for (int i = 0; i < nPlayers; i++) {
			try {
				snakes[i] = new Snake(PlayerColors.getColor(i), controlers[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void update() {
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
					if(!snakes[i].hasHoleThisTick()) {
						Point oldPosition = VectorUtilities.vectorToPoint(snakes[i].getLastPosition());
						//map.setRGB(x, y, snakes[i].getColorAsRGB());
						clearOldSnakeHead(snakes[i], snakes[i].getColorAsRGB());
						g.setStroke(new BasicStroke((float)snakes[i].getRadius() * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
						g.setColor(snakes[i].getColor());
						g.drawLine(x, y, oldPosition.x, oldPosition.y);
						//drawNewSnakeHead(snakes[i]);
					} else {
						Point oldPos = VectorUtilities.vectorToPoint(snakes[i].getLastPosition());
						
						//g.setStroke(new BasicStroke((float)snakes[i].getRadius() * 2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
						//g.setColor(Color.BLACK);
						//g.drawLine(oldPos.x, oldPos.y, oldPos.x, oldPos.y);
						clearOldSnakeHead(snakes[i], snakes[i].getColorAsRGB());
						drawNewSnakeHead(snakes[i], i);
						
						//g.setStroke(new BasicStroke((float)snakes[i].getRadius() * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
						//g.setColor(Color.YELLOW);
						//g.drawLine(x, y, x, y);
					}
				}
			}
		}
		g.dispose();
	}

	private void drawNewSnakeHead(Snake snake, int id) {
		// TODO Auto-generated method stub
		ArrayList<Point> getAllPoints = getCollisionPoints(snake.getPosition(), snake.getRadius());
		
		for (Point p : getAllPoints) {
			if(map.getRGB(p.x, p.y) != snake.getColorAsRGB()) map.setRGB(p.x, p.y, PlayerColors.getSnakeHeadColor(id).getRGB());
		}
	}

	private void clearOldSnakeHead(Snake snake, int colorAsRGB) {
		// TODO Auto-generated method stub
		ArrayList<Point> getAllPoints = getCollisionPoints(snake.getLastPosition(), snake.getRadius());
		
		for (Point p : getAllPoints) {
			if(map.getRGB(p.x, p.y) != colorAsRGB) map.setRGB(p.x, p.y, Color.BLACK.getRGB());
		}
	}

	private void updateAllSnakes() {
		Graphics2D g = collisionMap.createGraphics();
		for (int i = 0; i < snakes.length; i++) {
			if (snakes[i].isAlive()) {
				snakes[i].update();
				updateCollision(snakes[i], g);
			}
		}
		g.dispose();
	}

	private void updateCollision(Snake snake, Graphics2D g) {
		if(collisionTickLag < currentTick) {
			Point lastCollisionPos = VectorUtilities.vectorToPoint(snake.popCollisionPosition().getPosition());
			CollisionData currentCollisionData = snake.peekCollisionPosition();
			if(currentCollisionData.isHole()) return;
			Point thisCollisionPosition = VectorUtilities.vectorToPoint(currentCollisionData.getPosition());
			g.setColor(new Color(1));
			g.setStroke(new BasicStroke((float)snake.getRadius(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.drawLine(lastCollisionPos.x, lastCollisionPos.y, thisCollisionPosition.x, thisCollisionPosition.y);
		}
	}

	private boolean snakeCollides(Snake snake) {
		double radius = snake.getRadius();
		FixedMatrix2_64F center = snake.getPosition();
		ArrayList<Point> allPoints = getCollisionPoints(center, radius);
		for (int i = 0; i < allPoints.size(); i++) {
			Point p = allPoints.get(i);
			//System.err.println(collisionMap.getRGB(p.x,  p.y));
			//Magic value! :D
			if(collisionMap.getRGB(p.x, p.y) != -16777216) return true;
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
				score[i]++;
			}
		}
		if (nAliveSnakes < 2) {
			endRound();
		}
	}

	public BufferedImage getBufferedImage() {
		return map;
	}

}
