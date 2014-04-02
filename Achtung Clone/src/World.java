import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.ejml.data.FixedMatrix2_64F;


public class World {
	private static double wallSpawnOffset = 10;
	private final BufferedImage map;
	private int nPlayers;
	private Snake[] snakes;
	private int[] score;
	private int nAliveSnakes;
	private boolean	roundAlive;
	
	public World (Controler[] controlers, int width, int height) {
		int numberOfPlayers = controlers.length;
		map = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		nPlayers = numberOfPlayers;
		snakes = new Snake[numberOfPlayers];
		score = new int[numberOfPlayers];
		initSnakes(controlers);
		nextRound();
	}
	
	public void resetWorld() {
		Graphics g = map.createGraphics();
		g.clearRect(0, 0, map.getWidth(), map.getHeight());
		g.dispose();
		for (int i = 0; i < nPlayers; i++) {
			score[i] = 0;
		}
		nextRound();
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
				snakes[i] = new Snake(SnakeColor.getColor(i), controlers[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void update() {
		if (!isAlive()) {
			return;
		}
		int[] pixelDest = new int[1];
		for (int i = 0; i < nPlayers; i++) {
			if (snakes[i].isAlive()) {
				snakes[i].update();
				FixedMatrix2_64F newPos = snakes[i].getPosition();
				int x = (int)Math.round(newPos.get(0, 0));
				int y = (int)Math.round(newPos.get(0, 1));
				if (map.getData().getPixel(x, y, pixelDest)[0] != 0) {
					killSnake(i);
				}
				else {
					map.setRGB(x, y, snakes[i].getColorAsRGB());
				}
			}
		}
	}
	
	public void killSnake(int id) {
		if (snakes[id].isAlive()) {
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
	
}
