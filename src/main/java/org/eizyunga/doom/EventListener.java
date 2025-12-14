package org.eizyunga.doom;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.eizyunga.doom.util.TagUtil;

import java.util.UUID;

public class EventListener implements Listener {
    private final Main plugin;

    public EventListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        if(TagUtil.hasDoomTag(e.getEntity())){
            String name = e.getEntity().getName();
            plugin.removeBossBar(name);
            plugin.stopScheduler(e);
        }

        TagUtil.removeTags(e.getEntity());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (TagUtil.hasDoomTag(e.getPlayer())) {
            String name = e.getPlayer().getName();
            e.quitMessage(Component.text(NamedTextColor.DARK_PURPLE + name + " left the game"));

            plugin.stopScheduler(name);
            plugin.removeBossBar(name);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        String name = e.getPlayer().getName();
        if (TagUtil.hasDoomTag(e.getPlayer())) {
            e.joinMessage(Component.text(NamedTextColor.DARK_PURPLE + name + " joined the game"));
            plugin.getTaskData().put(id, plugin.createTask(plugin.createBossBar(name), name));
        }
    }

    //Take more damage under Doom event handler
//	@EventHandler
//	public void onTakeDamage(EntityDamageByEntityEvent e) {
//		Entity victim = e.getEntity();
//
//		if (e.getDamager() instanceof Player) {
//			Player damager = (Player) e.getDamager();
//			if (damager.getInventory().getItemInMainHand() != null) {
//				ItemStack wp = damager.getInventory().getItemInMainHand();
//				if (wp != null) {
//					ItemMeta weapon = wp.getItemMeta();
//
//					if (isGodSlayer(weapon)) {
//						if (!isGodLevel(victim)) {
//							e.setDamage(0);
//						}
//					} else if (isGodLevel(victim)) {
//						e.setDamage(0);
//					}
//				}
//			}
//		} else if (isGodLevel(victim)) {
//			e.setDamage(0);
//		}
//	}

    private String toTitleCase(String name) {
        name = name.toLowerCase();
        return name.replace(name.charAt(0), name.toUpperCase().charAt(0));
    }
}

