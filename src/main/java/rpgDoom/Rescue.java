package rpgDoom;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import util.TagUtil;

public class Rescue implements CommandExecutor {
	@SuppressWarnings("unused")
	private final Main plugin;
	
	public Rescue(Main plugin) {
		this.plugin = plugin;
		TagUtil.getInstance();
	}	
	
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		Player player = Bukkit.getPlayer(arg0.getName());
		if (arg0 instanceof Player && TagUtil.hasMonumentTag(player) && TagUtil.hasDoomTag(player)) {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tp " + arg0.getName() + " world");		
			TagUtil.removeMonumentTag(player);			
		}
		return false;		
	}
}
