package org.cptgum.superhopperswebui.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.cptgum.superhopperswebui.utils.DataManager.FileGenerator;


public class SchedulerManager {
    private final JavaPlugin plugin;

    public SchedulerManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void startScheduler() {
        // Read the interval from the configuration or set a default value
        long interval = plugin.getConfig().getLong("Interval", 3600L); // Default: 1 hour

        // Schedule the task at the specified interval
        new FileGeneratorTask().runTaskTimer(plugin, 0L, interval * 20L); // Convert seconds to ticks
    }

    private class FileGeneratorTask extends BukkitRunnable {
        @Override
        public void run() {
            // Rufe den FileGenerator auf
            FileGenerator.processYamlFiles();
            plugin.getLogger().info("FileGenerator executed!");
        }
    }
}
