package rpgDoom;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import util.TagUtil;

public class bMoonTag implements CommandExecutor{
	private final Main plugin;
	
	public bMoonTag(Main plugin) {
		this.plugin = plugin;
		TagUtil.getInstance();
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if (!(arg0 instanceof Player) && arg3[0] != null) {
    	if (arg3[0].matches("on")) {
    		this.plugin.setBloodMoon(true);
    	} else {
    		this.plugin.setBloodMoon(false);
    	}
		}		
		return false;
	}	
}
