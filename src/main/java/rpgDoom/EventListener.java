package rpgDoom;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import util.TagUtil;

public class EventListener implements Listener {
	private final Main plugin;
	
	public EventListener(Main plugin) {
		this.plugin = plugin;
		TagUtil.getInstance();
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e){			
		if (TagUtil.hasFoodTag(e.getEntity())) {
   	 	e.setDeathMessage(ChatColor.DARK_PURPLE + e.getEntity().getName() + " died of food poisoning");
		} else if (TagUtil.hasRiddleTag(e.getEntity())) {
   	 	e.setDeathMessage(ChatColor.DARK_PURPLE + e.getEntity().getName() + " failed a trial");
		}
		
		if(TagUtil.hasDoomTag(e.getEntity())){
	     if (!this.plugin.isBloodMoonOn() && !TagUtil.hasFoodTag(e.getEntity()) && !TagUtil.hasRiddleTag(e.getEntity())) {
	    	 e.setDeathMessage(ChatColor.DARK_PURPLE + e.getEntity().getName() + " ran out of time");
	     }
	        
	   	 String name = e.getEntity().getName();	    	 
	   	 plugin.removeBossBar(name); 	
	   	 plugin.stopScheduler(e);
	   } else if (e.getEntity().getLastDamage() > 88.0 && e.getEntity().getLastDamageCause().getCause() == DamageCause.THORNS) {
	  	 e.setDeathMessage(e.getEntity().getName() + " was crushed by the might of the " + ChatColor.GOLD + "Helmet of Darkness");
	   }

		TagUtil.removeTags(e.getEntity());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (TagUtil.hasDoomTag(e.getPlayer())) {
	    String name = e.getPlayer().getName();
	    e.setQuitMessage(ChatColor.DARK_PURPLE + name + " left the game");

	   	plugin.stopScheduler(name);
	   	plugin.removeBossBar(name);
		}
	}
	
	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent e) {
		String displayName = e.getItem().getItemMeta().getDisplayName();
		
		if (displayName.contains("Funky Mess")) {
			new PotionEffect(PotionEffectType.CONFUSION, 200, 2).apply(e.getPlayer());

		} else if (displayName.contains("Death-touched") || displayName.contains("Ghastly")) {
			TagUtil.addFoodTag(e.getPlayer());
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + e.getPlayer().getName() + " only maztm:endingk");		
			this.plugin.death(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		UUID id = e.getPlayer().getUniqueId();
		String name = e.getPlayer().getName();
		if (TagUtil.hasDoomTag(e.getPlayer())) {
			e.setJoinMessage(ChatColor.DARK_PURPLE + name + " joined the game");
			plugin.getTaskData().put(id, plugin.createTask(plugin.createBossBar(name), name));
		}
	}
	
	@EventHandler
	public void onFishCaught(PlayerFishEvent e) {
		if (e.getPlayer().getWorld().getName().equals("Mirage") && e.getCaught() != null && !(e.getCaught() instanceof Player)) {
		  ItemMeta itemMeta = ((Item) e.getCaught()).getItemStack().getItemMeta();

			if (this.plugin.isBloodMoonOn()) {
				if (e.getCaught().getName().contains("Tropical Fish")) {
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + e.getPlayer().getName() + " only maztm:nemo2");		
				}
				
			  itemMeta.setDisplayName(ChatColor.DARK_RED + "Ghastly " + e.getCaught().getName());
			  itemMeta.addEnchant(Enchantment.LURE, 9, true);
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + e.getPlayer().getName() + " only maztm:careless2");		
			} else if (TagUtil.hasDoomTag(e.getPlayer())) {
				if (e.getCaught().getName().contains("Tropical Fish")) {
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + e.getPlayer().getName() + " only maztm:nemo");		
				}
				
			  itemMeta.setDisplayName(ChatColor.GOLD + "Death-touched " + e.getCaught().getName());
			  itemMeta.addEnchant(Enchantment.CHANNELING, 9, true);
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + e.getPlayer().getName() + " only maztm:careless");		
			}
			
		  itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  ((Item) e.getCaught()).getItemStack().setItemMeta(itemMeta);
		}
	}
	
	@EventHandler
	public void onPlayerHarvest(PlayerHarvestBlockEvent e) {
		if (TagUtil.hasDoomTag(e.getPlayer())) {
			e.getItemsHarvested().forEach(item -> {
			  item.setItemMeta(modifyHarvestItemData(item));
			});
		}
	}
	
	@EventHandler public void onPlayerBreak(BlockDropItemEvent e) {
		if (TagUtil.hasDoomTag(e.getPlayer())) {
			e.getItems().forEach(item -> {
			  item.getItemStack().setItemMeta(modifyHarvestItemData(item.getItemStack()));
			});
		}
	}
	
	private ItemMeta modifyHarvestItemData(ItemStack item) {
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Tainted "  + toTitleCase(item.getType().name()));
	  itemMeta.addEnchant(Enchantment.LOYALTY, 9, true);  
	  itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
	  return(itemMeta);
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
	
	@EventHandler
	public void onMobDeath(EntityDeathEvent e) {
		if (e.getEntity().getKiller() instanceof Player && !(e.getEntity() instanceof Player)) {
			Player player = e.getEntity().getKiller();			
			if (TagUtil.hasDoomTag(player)) {
				if (!TagUtil.hasDoomIncrementTag(player)) {
					TagUtil.addDoomIncrementTag(player);
				}
				
				e.getDrops().forEach(item -> {					
					ItemMeta itemMeta = item.getItemMeta();
					
					itemMeta.setDisplayName(ChatColor.DARK_GRAY + "Cursed " + toTitleCase(item.getType().name()));
				  itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				  itemMeta.addEnchant(Enchantment.RIPTIDE, 9, true);  
				  item.setItemMeta(itemMeta);				
				});
			}
		}
	}
	
	private boolean isGodSlayer(ItemMeta weapon) {
		return weapon.getDisplayName().contains(ChatColor.GOLD + "Deity's Bane") && weapon.hasEnchant(Enchantment.CHANNELING) 
				? weapon.getEnchantLevel(Enchantment.CHANNELING) == 74 : false;
	}
	
	private String toTitleCase(String name) {
		name = name.toLowerCase();
		return name.replace(name.charAt(0), name.toUpperCase().charAt(0));
	}
	
	private boolean isGodLevel(Entity entity) {
		return entity.getScoreboardTags().contains("TheGod");
	}
}
