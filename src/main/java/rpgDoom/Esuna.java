package rpgDoom;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import util.TagUtil;

public class Esuna implements CommandExecutor {
	private final Main plugin;
	
	public Esuna(Main plugin) {
		this.plugin = plugin;
		TagUtil.getInstance();
	}	
	
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if (!(arg0 instanceof Player) || arg0.isOp()) {
			if (arg3 != null && arg3.length > 0) {
				String name = arg3[0];
				Player playerObject = Bukkit.getPlayer(name);
				if (playerObject != null) {
					OfflinePlayer targetedPlayer = Bukkit.getOfflinePlayer(playerObject.getUniqueId());
					
					if (targetedPlayer.isOnline()) {
						if (targetedPlayer != null && TagUtil.hasDoomTag(targetedPlayer.getPlayer())) {
							esuna(name);
						} else {
							arg0.sendMessage(name + " is not afflicted with Doom.");
						}
					} else {
						arg0.sendMessage(name + " is not online.");
					}
				} else {
					arg0.sendMessage("Invalid argument(s) for /esuna.");
				}
			} else {
				arg0.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /esuna {player name}");
			}
		}
		return false;
	}	
	
	private void esuna(String name) {
		this.plugin.removeBossBar(name);
		this.plugin.stopScheduler(name);
		TagUtil.removeDoomTag(Bukkit.getPlayerExact(name));
		Bukkit.getPlayerExact(name).sendMessage(ChatColor.GREEN + "Your life no longer fades.");
	}
}
