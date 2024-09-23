package util;

import org.bukkit.entity.Player;

public class TagUtil {
	
	private static final TagUtil instance = new TagUtil();
	
	public static TagUtil getInstance() {
		return instance;
	}
	
	private TagUtil(){}
	
	public static void removeTags(Player player) {
		if (hasDoomTag(player)) {
			removeDoomTag(player);
		}
		if (hasRiddleTag(player)) {
			removeRiddleTag(player);
		}
		if (hasFoodTag(player)) {
			removeFoodTag(player);
		}
		if (hasMonumentTag(player)) {
			removeMonumentTag(player);
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
	
	public static void addMonumentTag(Player player) {
		if (!hasMonumentTag(player)) {
			player.addScoreboardTag("inMonument");
		}
	}

	public static void addRiddleTag(Player player) {
		if (!hasRiddleTag(player)) {
			player.addScoreboardTag("riddleTime");
		}
	}
	
	public static void addFoodTag(Player player) {
		if (!hasFoodTag(player)) {
			player.addScoreboardTag("foodPoison");
		}
	}
	
	public static void removeRiddleTag(Player player) {
		if (hasRiddleTag(player)) {
			removeTag(player, "riddleTime");
		}
	}
	
	public static void removeDoomTag(Player player) {
		if (hasDoomTag(player)) {
			removeTag(player, "Doom");
		}
	}
	
	public static void removeFoodTag(Player player) {
		if (hasFoodTag(player)) {
			removeTag(player, "foodPoison");
		}
	}
	
	public static void removeDoomIncrementTag(Player player) {
		if (hasDoomIncrementTag(player)) {
			removeTag(player, "DoomInc");
		}
	}
	
	public static void removeMonumentTag(Player player) {
		if (hasMonumentTag(player)) {
			removeTag(player, "inMonument");
		}
	}
	
	private static void removeTag(Player player, String tag) {
		player.removeScoreboardTag(tag);
	}
	
	public static boolean hasDoomTag(Player player) {
		return player.getScoreboardTags().contains("Doom");
	}

	public static boolean hasRiddleTag(Player player) {
		return player.getScoreboardTags().contains("riddleTime");
	}
	
	public static boolean hasFoodTag(Player player) {
		return player.getScoreboardTags().contains("foodPoison");
	}
	
	public static boolean hasMonumentTag(Player player) {
		return player.getScoreboardTags().contains("inMonument");
	}
	
	public static boolean hasDoomIncrementTag(Player player) {
		return player.getScoreboardTags().contains("DoomInc");
	}
}
