package rpgDoom;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import dungeonPuzzles.RiddleHandler;
import net.md_5.bungee.api.ChatColor;
import util.TagUtil;

public class Main extends JavaPlugin {
	
	private static JavaPlugin rpgDoom = null;
	private HashMap<UUID, Integer> taskMap;
	private HashMap<UUID, Double> doomProgress = new HashMap<UUID, Double>();

	private boolean bloodMoonOn = false;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		rpgDoom = this;
		this.taskMap = new HashMap<UUID, Integer>();
		this.getCommand("doom").setExecutor(new Doom(this));
		this.getCommand("esuna").setExecutor(new Esuna(this));
		this.getCommand("monument").setExecutor(new RiddleHandler(this));
		this.getCommand("rpgbmoon").setExecutor(new bMoonTag(this));
		this.getCommand("rescue").setExecutor(new Rescue(this));
				
		if (this.getConfig().contains("task")) {
			loadSchedules();
		}
		if (this.getConfig().contains("progress")) {
			loadDoomProgress();
		}
		
		Bukkit.getPluginManager().registerEvents(new EventListener(this), rpgDoom);
	}
	
	@Override
	public void onDisable() {
		rpgDoom = null;
		saveSchedules();
		saveDoomProgress();
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			removeBossBar(p.getName());
		}
	}
	
	public boolean isBloodMoonOn() {
		return bloodMoonOn;
	}
	
	public void setBloodMoon(boolean val) {
		bloodMoonOn = val;
	}
	
	protected HashMap<UUID, Integer> getTaskData() {
		return this.taskMap;
	}
	
	protected HashMap<UUID, Double> getProgressData() {
		return this.doomProgress;
	}
	
	protected void saveSchedules() {		
		for (Map.Entry<UUID, Integer> entry : taskMap.entrySet()) {
			this.getConfig().set("task." + entry.getKey(), entry.getValue());
		}
		this.saveConfig();
	}
	
	private void saveDoomProgress() {
		for (Map.Entry<UUID, Double> entry : doomProgress.entrySet()) {
			this.getConfig().set("progress." + entry.getKey(), entry.getValue());
		}
		this.saveConfig();
	}
	
	private void saveEverything() {
		saveDoomProgress();
		saveSchedules();
	}
	
	private void loadSchedules() {
		this.getConfig().getConfigurationSection("task").getKeys(false).forEach(key -> {
			Integer taskValue = (Integer) this.getConfig().get("task." + key);
			taskMap.put(UUID.fromString(key), taskValue);
		});
	}
	
	private void loadDoomProgress() {
  	this.getConfig().getConfigurationSection("progress").getKeys(false).forEach(key -> {
			Double progressValue = (Double) this.getConfig().get("progress." + key);
			doomProgress.put(UUID.fromString(key), progressValue);
		});
	}
	
	protected int getTaskId(UUID playerUID) {
		return this.taskMap.get(playerUID);
	}	
	
	private NamespacedKey getBarKey(String name) {
		return new NamespacedKey(this, "doom" + name.toLowerCase());
	}
	
	protected void removeBossBar(String name) {
		KeyedBossBar bar = Bukkit.getBossBar(this.getBarKey(name));
		if (bar != null) {
			bar.setVisible(false);
			bar.removeAll();
			Bukkit.removeBossBar(this.getBarKey(name));
		}
	}
	
	protected void stopScheduler(String name) {
		Bukkit.getScheduler().cancelTask(this.getTaskId(Bukkit.getPlayerExact(name).getUniqueId()));
   	this.taskMap.remove(Bukkit.getPlayerExact(name).getUniqueId());
   	saveEverything();
	}
	
	protected void stopScheduler(PlayerDeathEvent e) {
   	Bukkit.getScheduler().cancelTask(this.getTaskId(e.getEntity().getUniqueId()));
   	this.taskMap.remove(e.getEntity().getUniqueId());
  	saveEverything();
	}
	
	protected HashMap<UUID, Integer> getTaskMap() {
		return taskMap;
	}
	
	protected void addToTaskMap(UUID uuid, Integer task) {
		taskMap.put(uuid, task);
	}
		 
	protected void storeDoomProgress(String name, double progress) {
		this.doomProgress.put(Bukkit.getPlayerExact(name).getUniqueId(), progress);
	}
	
	protected Double getDoomProgress(String name) {
		return this.doomProgress.get(Bukkit.getPlayerExact(name).getUniqueId());
	}
	
	public int createTask(final KeyedBossBar bar, final String name) {
		return createTask(bar, Bukkit.getPlayerExact(name).getUniqueId());
	}
	
	protected int createTask(final KeyedBossBar bar, final String name, double progressValue) {
		return createTask(bar, Bukkit.getPlayerExact(name).getUniqueId(), progressValue);
	}
	
	protected int createTask(final KeyedBossBar bar, final UUID player, double progressValue) {
		final OfflinePlayer targetedPlayer = Bukkit.getOfflinePlayer(player);
		final String name = Bukkit.getOfflinePlayer(player).getName();
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(rpgDoom, new Runnable() {
			
			int count = -1;
			double progress = progressValue > 0.0 ? progressValue : getDoomProgress(name) != null ? getDoomProgress(name) : 1.0;
			double time = 1.0 / (1800); // seconds
			
			public void run() {
				if (targetedPlayer.isOnline() && targetedPlayer.getPlayer().getScoreboardTags().contains("Doom")) {
					
					progress = getDoomIncrementor(progress, name);

					if (progress > 0.0) {
						bar.setProgress(progress);
					}
					
					switch(count) {
					case -1:
						break;
					case 0:
						death(name);
						break;
					}
					
					if (progress >= 0.0) {
						progress -= time;
						storeDoomProgress(name, progress);
					}
					if (progress <= 0.0) {
						count ++;
					}
				}
			}
		}, 0, 20);	
	}
	
	protected int createTask(final KeyedBossBar bar, final UUID player) {
		return this.createTask(bar, player, -1.0);
	}
	
	private double getDoomIncrementor(double value, String name) {
		if (TagUtil.hasDoomIncrementTag(Bukkit.getPlayer(name)) && value < 0.998) {
			value += 0.001;
			TagUtil.removeDoomIncrementTag(Bukkit.getPlayer(name));
		}
		return value;
	}
	
	public void death(String name) {
		Bukkit.getPlayerExact(name).setHealth(0);
	}
	
	public void death(Player player) {
		player.setHealth(0);
	}
	
	protected KeyedBossBar createBossBar(String name) {
		KeyedBossBar bar = Bukkit.createBossBar(this.getBarKey(name), ChatColor.DARK_PURPLE + "Lifeforce", BarColor.PURPLE, BarStyle.SOLID);
		bar.addPlayer(Bukkit.getPlayerExact(name));
		bar.setVisible(true);
		
		return bar;
	}	
}
