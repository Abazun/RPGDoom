package org.eizyunga.doom.util;

import org.bukkit.Bukkit;

public class DoomUtil {

    private DoomUtil(){}

    public static void resetDoomTimer(String playerName) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "doom " + playerName + " 1.0");
    }
}