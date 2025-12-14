package org.eizyunga.doom;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eizyunga.doom.util.TagUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Doom implements CommandExecutor {
    private final Main plugin;

    public Doom(Main plugin) {
        this.plugin = plugin;
    }

    private void makeBossbar(String name, double optionalProgress) {
        KeyedBossBar bar = plugin.createBossBar(name);
        this.plugin.addToTaskMap(Objects.requireNonNull(Bukkit.getPlayerExact(name)).getUniqueId(), plugin.createTask(bar, name, optionalProgress));
        this.plugin.saveSchedules();
    }

    private boolean isValidValue(double value) {
        return value >= 0.0 && value <= 1.0;
    }

    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2, @NotNull String @NotNull [] arg3) {
        if (!(arg0 instanceof Player) || arg0.isOp()) {
            if (arg3.length > 0) {
                String name = arg3[0];
                Player playerObject = Bukkit.getPlayer(name);
                if (playerObject != null) {
                    double optionalProgress = arg3.length > 1 ? Double.parseDouble(arg3[1]) : -1.0;
                    OfflinePlayer targetedPlayer = Bukkit.getOfflinePlayer(playerObject.getUniqueId());
                    if (targetedPlayer.isOnline()) {
                        if (!TagUtil.hasDoomTag(Objects.requireNonNull(targetedPlayer.getPlayer()))) {
                            TagUtil.addDoomTag(Bukkit.getPlayerExact(name));
                            Objects.requireNonNull(Bukkit.getPlayerExact(name)).sendMessage(NamedTextColor.DARK_PURPLE + "Your life begins to fade...");
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + name + " only maztm:doom");
                            makeBossbar(name, isValidValue(optionalProgress) ? optionalProgress : -1.0);
                        } else if (isValidValue(optionalProgress) && TagUtil.hasDoomTag(targetedPlayer.getPlayer())) {
                            this.plugin.removeBossBar(name);
                            this.plugin.stopScheduler(name);
                            makeBossbar(name, isValidValue(optionalProgress) ? optionalProgress : -1.0);
                            Objects.requireNonNull(Bukkit.getPlayerExact(name)).sendMessage(NamedTextColor.DARK_PURPLE + "You feel a shift in the aether....");
                        } else {
                            arg0.sendMessage(name + " is already afflicted with Doom.");
                        }
                    } else {
                        arg0.sendMessage(name + " is not online.");
                    }
                } else {
                    arg0.sendMessage("Invalid argument(s) for /doom.");
                }
            } else {
                arg0.sendMessage(NamedTextColor.LIGHT_PURPLE + "Usage: /doom {player name} {optional time}");
            }
        }
        return false;
    }
}
