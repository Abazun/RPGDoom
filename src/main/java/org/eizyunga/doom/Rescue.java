package org.eizyunga.doom;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eizyunga.doom.util.TagUtil;
import org.jetbrains.annotations.NotNull;

public class Rescue implements CommandExecutor {

    public Rescue(Main plugin) {
    }

    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2, @NotNull String @NotNull [] arg3) {
        Player player = Bukkit.getPlayer(arg0.getName());
        if (arg0 instanceof Player) {
            assert player != null;
            if (TagUtil.hasJumpingPuzzleTag(player) && TagUtil.hasDoomTag(player)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tp " + arg0.getName() + " world");
                TagUtil.removeJumpingPuzzleTag(player);
            }
        }
        return false;
    }
}
