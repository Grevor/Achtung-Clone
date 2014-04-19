package model;
import java.util.Iterator;


public interface GameStartDataListener {
	public abstract void start();
	public abstract Iterator<PlayerData> getPlayerData();
}
