package model;
import java.util.Iterator;


public interface GameStartDataListener {
	public abstract void start(boolean wrapMap);
	public abstract Iterator<PlayerData> getPlayerData();
}
