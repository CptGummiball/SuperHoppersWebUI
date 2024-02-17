package org.cptgum.superhopperswebui.utils.webserver;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.cptgum.superhopperswebui.utils.LoggerUtils;
import org.cptgum.superhopperswebui.utils.DataManager.ConfigUtils;

import java.io.ObjectInputFilter;

public class WebServerManager {

    private final JavaPlugin plugin;

    private static final int DELAY_SECONDS = 30; // 30 seconds delay
    public WebServerManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void jettyStart() {
        FileConfiguration config = plugin.getConfig();
        int port = config.getInt("Port", 8080);
        try {
            ConfigUtils.jsonsync();
            Jetty.start(port);
            LoggerUtils.logInfo("Web Server started on port " + port);
        } catch (Exception e) {
            LoggerUtils.logError("Failed to start Web Server: " + e);
        }
    }

    public void jettyStop() {
        try {
            Jetty.stop();
            LoggerUtils.logInfo("Web Server stopped.");
        } catch (Exception e) {
            LoggerUtils.logError("Failed to stop Web Server: " + e);
        }
    }

    public void jettyRestart() {
        // Stop Jetty Server after a delay
        new BukkitRunnable() {
            @Override
            public void run() {
                jettyStop();
                // Start Jetty Server after another delay
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        jettyStart();
                    }
                }.runTaskLater(plugin, DELAY_SECONDS * 20); // 20 ticks per second
            }
        }.runTaskLater(plugin, DELAY_SECONDS * 20); // 20 ticks per second
    }


}
