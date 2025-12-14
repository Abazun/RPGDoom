package org.eizyunga.doom.util;

import org.bukkit.entity.Player;

public class TagUtil {

    private TagUtil(){}

    public static void removeTags(Player player) {
        if (hasDoomTag(player)) {
            removeDoomTag(player);
        }
        if (hasDoomIncrementTag(player)) {
            removeDoomIncrementTag(player);
        }
    }

    public static void addDoomTag(Player player) {
        if (!hasDoomTag(player)) {
            player.addScoreboardTag("Doom");
        }
    }

    public static void addDoomIncrementTag(Player player) {
        if (!hasDoomIncrementTag(player)) {
            player.addScoreboardTag("DoomInc");
        }
    }

    public static void removeDoomTag(Player player) {
        if (hasDoomTag(player)) {
            removeTag(player, "Doom");
        }
    }

    public static void removeDoomIncrementTag(Player player) {
        if (hasDoomIncrementTag(player)) {
            removeTag(player, "DoomInc");
        }
    }

    public static void removeJumpingPuzzleTag(Player player) {
        if (hasJumpingPuzzleTag(player)) {
            removeTag(player, "inJumpingPuzzle");
        }
    }

    private static void removeTag(Player player, String tag) {
        player.removeScoreboardTag(tag);
    }

    public static boolean hasDoomTag(Player player) {
        return player.getScoreboardTags().contains("Doom");
    }

    public static boolean hasDoomIncrementTag(Player player) {
        return player.getScoreboardTags().contains("DoomInc");
    }

    public static boolean hasJumpingPuzzleTag(Player player) {
        return player.getScoreboardTags().contains("inJumpingPuzzle");
    }
}

