package dungeonPuzzles;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import rpgDoom.Main;
import util.DoomUtil;
import util.TagUtil;

public class RiddleHandler implements CommandExecutor {
	private final Main plugin;

	protected ConversationFactory factory = null;
	
	public RiddleHandler(Main plugin) {
		this.plugin = plugin;
		DoomUtil.getInstance();
		TagUtil.getInstance();
		factory = new ConversationFactory(this.plugin);
	}

	public boolean onCommand(CommandSender arg0, Command command, String arg2, String[] params) {
		if (params[1] != null && !(arg0 instanceof Player)) {
			TagUtil.addRiddleTag(toPlayer(params[1]));
			 DoomUtil.setRiddleTime(params[1]);
			
			switch (params[0]) {
				case "1":
					doRiddle1(toPlayer(params[1]));
					break;
				case "2":
					doRiddle2(toPlayer(params[1]));
					break;
				case "3":
					doRiddle3(toPlayer(params[1]));
					break;
			}
		}
	
		return false;
	}
	
	private Player toPlayer(String playerName) {
		return Bukkit.getPlayer(playerName);
	}
	
	private void doRiddle1(Player player) {
		factory.withFirstPrompt(new Riddle1(this.plugin, player.getName())).withTimeout(30).
		withPrefix(c -> "[Zamfau]").withModality(true).thatExcludesNonPlayersWithMessage("Cannot compute.").
		withLocalEcho(false).buildConversation((Conversable) player).begin();	
	}
	
	private void doRiddle2(Player player) {
		factory.withFirstPrompt(new Riddle2(this.plugin, player.getName())).withTimeout(30).
		withPrefix(c -> "[Zamfau]").withModality(true).thatExcludesNonPlayersWithMessage("Cannot compute.").
		withLocalEcho(false).buildConversation((Conversable) player).begin();	
	}
	
	private void doRiddle3(Player player) {
		factory.withFirstPrompt(new Riddle3(this.plugin, player.getName())).withTimeout(30).
		withPrefix(c -> "[Zamfau]").withModality(true).thatExcludesNonPlayersWithMessage("Cannot compute.").
		withLocalEcho(false).buildConversation((Conversable) player).begin();	
	}
}
