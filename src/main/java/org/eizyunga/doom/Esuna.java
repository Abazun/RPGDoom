package org.eizyunga.doom;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eizyunga.doom.util.TagUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Esuna implements CommandExecutor {
    private final Main plugin;

    public Esuna(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2, @NotNull String @NotNull [] arg3) {
        if (!(arg0 instanceof Player) || arg0.isOp()) {
            if (arg3.length > 0) {
                String name = arg3[0];
                Player playerObject = Bukkit.getPlayer(name);
                if (playerObject != null) {
                    OfflinePlayer targetedPlayer = Bukkit.getOfflinePlayer(playerObject.getUniqueId());

                    if (targetedPlayer.isOnline()) {
                        if (TagUtil.hasDoomTag(Objects.requireNonNull(targetedPlayer.getPlayer()))) {
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
                arg0.sendMessage(NamedTextColor.LIGHT_PURPLE + "Usage: /esuna {player name}");
            }
        }
        return false;
    }

    private void esuna(String name) {
        this.plugin.removeBossBar(name);
        this.plugin.stopScheduler(name);
        TagUtil.removeDoomTag(Bukkit.getPlayerExact(name));
        Objects.requireNonNull(Bukkit.getPlayerExact(name)).sendMessage(NamedTextColor.GREEN + "Your life no longer fades.");
    }
}