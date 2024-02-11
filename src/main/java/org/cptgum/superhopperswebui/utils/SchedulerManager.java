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
        // Lese das Intervall aus der Konfiguration oder setze einen Standardwert
        long interval = plugin.getConfig().getLong("Interval", 3600L); // Default: 1 Stunde

        // Plane den Task mit dem angegebenen Intervall
        new FileGeneratorTask().runTaskTimer(plugin, 0L, interval * 20L); // Konvertiere Sekunden in Ticks
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
