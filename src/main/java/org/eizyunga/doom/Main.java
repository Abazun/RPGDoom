package org.eizyunga.doom;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.eizyunga.doom.util.TagUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class Main extends JavaPlugin {

    private static JavaPlugin rpgDoom = null;
    private HashMap<UUID, Integer> taskMap;
    private final HashMap<UUID, Double> doomProgress = new HashMap<UUID, Double>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        rpgDoom = this;
        this.taskMap = new HashMap<UUID, Integer>();
        Objects.requireNonNull(this.getCommand("doom")).setExecutor(new Doom(this));
        Objects.requireNonNull(this.getCommand("esuna")).setExecutor(new Esuna(this));
        Objects.requireNonNull(this.getCommand("rescue")).setExecutor(new Rescue(this));

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
            Bukkit.removeBossBar(NamespacedKey.minecraft(p.getName()));
        }
    }

    void saveSchedules() {
        for (Map.Entry<UUID, Integer> entry : taskMap.entrySet()) {
            this.getConfig().set("task." + entry.getKey(), entry.getValue());
        }
        this.saveConfig();
    }

    HashMap<UUID, Integer> getTaskData() {
        return this.taskMap;
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

    private int getTaskId(UUID playerUID) {
        return this.taskMap.get(playerUID);
    }

    private void loadSchedules() {
        Objects.requireNonNull(this.getConfig().getConfigurationSection("task")).getKeys(false).forEach(key -> {
            Integer taskValue = (Integer) this.getConfig().get("task." + key);
            taskMap.put(UUID.fromString(key), taskValue);
        });
    }

    private void loadDoomProgress() {
        Objects.requireNonNull(this.getConfig().getConfigurationSection("progress")).getKeys(false).forEach(key -> {
            Double progressValue = (Double) this.getConfig().get("progress." + key);
            doomProgress.put(UUID.fromString(key), progressValue);
        });
    }

    private double getDoomIncrementer(double value, String name) {
        if (TagUtil.hasDoomIncrementTag(Objects.requireNonNull(Bukkit.getPlayer(name))) && value < 0.998) {
            value += 0.001;
            TagUtil.removeDoomIncrementTag(Bukkit.getPlayer(name));
        }
        return value;
    }

    void stopScheduler(String name) {
        Bukkit.getScheduler().cancelTask(this.getTaskId(Objects.requireNonNull(Bukkit.getPlayerExact(name)).getUniqueId()));
        this.taskMap.remove(Objects.requireNonNull(Bukkit.getPlayerExact(name)).getUniqueId());
        saveEverything();
    }

    void stopScheduler(PlayerDeathEvent e) {
        Bukkit.getScheduler().cancelTask(this.getTaskId(e.getEntity().getUniqueId()));
        this.taskMap.remove(e.getEntity().getUniqueId());
        saveEverything();
    }

    private HashMap<UUID, Integer> getTaskMap() {
        return taskMap;
    }

    void addToTaskMap(UUID uuid, Integer task) {
        taskMap.put(uuid, task);
    }

    private void storeDoomProgress(String name, double progress) {
        this.doomProgress.put(Objects.requireNonNull(Bukkit.getPlayerExact(name)).getUniqueId(), progress);
    }

    private Double getDoomProgress(String name) {
        return this.doomProgress.get(Objects.requireNonNull(Bukkit.getPlayerExact(name)).getUniqueId());
    }

    public int createTask(final KeyedBossBar bar, final String name) {
        return createTask(bar, Objects.requireNonNull(Bukkit.getPlayerExact(name)).getUniqueId());
    }

    int createTask(final KeyedBossBar bar, final String name, double progressValue) {
        return createTask(bar, Objects.requireNonNull(Bukkit.getPlayerExact(name)).getUniqueId(), progressValue);
    }

    private int createTask(final KeyedBossBar bar, final UUID player, double progressValue) {
        final OfflinePlayer targetedPlayer = Bukkit.getOfflinePlayer(player);
        final String name = Bukkit.getOfflinePlayer(player).getName();
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(rpgDoom, new Runnable() {

            int count = -1;
            double progress = progressValue > 0.0 ? progressValue : getDoomProgress(name) != null ? getDoomProgress(name) : 1.0;
            final double time = 1.0 / (1800); // seconds

            public void run() {
                if (targetedPlayer.isOnline() && Objects.requireNonNull(targetedPlayer.getPlayer()).getScoreboardTags().contains("Doom")) {

                    progress = getDoomIncrementer(progress, name);

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

    public void death(String name) {
        Objects.requireNonNull(Bukkit.getPlayerExact(name)).setHealth(0);
    }

    private int createTask(final KeyedBossBar bar, final UUID player) {
        return this.createTask(bar, player, -1.0);
    }

    void removeBossBar(String name) {
        KeyedBossBar bar = Bukkit.getBossBar(this.getBarKey(name));
        if (bar != null) {
            bar.setVisible(false);
            bar.removeAll();
            Bukkit.removeBossBar(this.getBarKey(name));
        }
    }

    private NamespacedKey getBarKey(String name) {
        return new NamespacedKey(this, "doom" + name.toLowerCase());
    }

    KeyedBossBar createBossBar(String name) {
        KeyedBossBar bar = Bukkit.createBossBar(this.getBarKey(name), NamedTextColor.DARK_PURPLE + "Lifeforce", BarColor.PURPLE, BarStyle.SOLID);
        bar.addPlayer(Objects.requireNonNull(Bukkit.getPlayerExact(name)));
        bar.setVisible(true);

        return bar;
    }
}
