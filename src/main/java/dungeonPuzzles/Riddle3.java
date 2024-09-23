package dungeonPuzzles;

import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;

import net.md_5.bungee.api.ChatColor;
import rpgDoom.Main;
import util.DoomUtil;
import util.TagUtil;

public class Riddle3 extends FixedSetPrompt {
	private final Main plugin;
	private final String playerName;
	private int tries = 0;

	public Riddle3(Main plugin, String playerName) {
		this.playerName = playerName;
		this.plugin = plugin;
		DoomUtil.getInstance();
		TagUtil.getInstance();
	}

	@Override
	public String getPromptText(ConversationContext c) {
		return ": " + ChatColor.DARK_GRAY + "Curiosity lets the mind wander. How many black pillars are there out yonder?";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext c, String s) {
		Conversable cv = c.getForWhom();
		if (s.toLowerCase().contains("4")) {
			cv.sendRawMessage(ChatColor.WHITE + "[Zamfau]: " + ChatColor.DARK_GRAY + "The next one won't be so easy.");
			DoomUtil.resetDoomTimer(playerName);
			TagUtil.removeRiddleTag(Bukkit.getPlayer(playerName));
		} else {
			this.plugin.death(playerName);
		}
		return END_OF_CONVERSATION;
	}	
	
	@Override
	protected boolean isInputValid(ConversationContext c, String s) {
		tries++;
		return s.toLowerCase().contains("4") || tries == 3;
	}
}
