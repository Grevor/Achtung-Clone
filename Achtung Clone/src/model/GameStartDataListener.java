package model;
import java.util.LinkedList;


public interface GameStartDataListener {
	public abstract void start();
	public abstract LinkedList<PlayerData> getPlayerData();
}
