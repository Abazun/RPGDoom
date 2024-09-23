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

public class Riddle1 extends FixedSetPrompt {
	@SuppressWarnings("unused")
	private final Main plugin;
	private final String playerName;
		
	public Riddle1(Main plugin, String playerName) {
		this.playerName = playerName;
		this.plugin = plugin;
		DoomUtil.getInstance();
		TagUtil.getInstance();
	}

	@Override
	public String getPromptText(ConversationContext c) {
		return ": " + ChatColor.DARK_GRAY + "To my riddles, you shall comply. To this message, you shall reply.";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext c, String s) {
		Conversable cv = c.getForWhom();
		if (s.length() > 0) {
			if (s.toLowerCase().contains("bitch") || s.toLowerCase().contains("fuck")) {
				cv.sendRawMessage(ChatColor.WHITE + "[Zamfau]: " + ChatColor.DARK_GRAY + ".....at least your ears work...");
			} else {
				cv.sendRawMessage(ChatColor.WHITE + "[Zamfau]: " + ChatColor.DARK_GRAY + "Very good.");
			}
			DoomUtil.resetDoomTimer(playerName);
			TagUtil.removeRiddleTag(Bukkit.getPlayer(playerName));
		}	
		return END_OF_CONVERSATION;
	}
	
	@Override
	protected boolean isInputValid(ConversationContext c, String s) {
		return true;
	}
}
