package util;

import org.bukkit.Bukkit;


public class DoomUtil {
	private static final DoomUtil instance = new DoomUtil();
	
	public static DoomUtil getInstance() {
		return instance;
	}
		
	private DoomUtil(){}
	
	public static void setRiddleTime(String playerName) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "doom " + playerName + " 0.018");
	}
	
	public static void resetDoomTimer(String playerName) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "doom " + playerName + " 1.0");
	}
}
