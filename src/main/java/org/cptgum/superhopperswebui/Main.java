package org.cptgum.superhopperswebui;

import org.bukkit.plugin.java.JavaPlugin;
import org.cptgum.superhopperswebui.utils.DataManager.Updater;
import org.cptgum.superhopperswebui.utils.LoggerUtils;
import org.cptgum.superhopperswebui.utils.SchedulerManager;
import org.cptgum.superhopperswebui.utils.Translator;
import org.cptgum.superhopperswebui.utils.webserver.WebServerManager;

public final class Main extends JavaPlugin {

    private static Main instance;
    WebServerManager webServerManager = new WebServerManager(this);
    SchedulerManager schedulerManager = new SchedulerManager(this);
    Translator translator = Translator.getInstance();
    Updater updater = new Updater(this);
    private boolean pluginEnabled = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        pluginEnabled = true;
        loadConfig();
        // Extracts necessary resources from the JAR file
        updater.updateWebFolder();
        updater.updateConfig();
        // Load languages
        translator.loadLanguages(this);
        // Start webserver
        webServerManager.jettyStart();
        // Start Scheduler
        schedulerManager.startScheduler();

        LoggerUtils.logInfo("SuperHoppersWebUI has been enabled!");
        LoggerUtils.logInfo("Hello SuperHoppers, I'm here! HUG!");
    }

    @Override
    public void onDisable() {
        // Stop Web Server
        pluginEnabled = false;
        if (webServerManager != null) {
            webServerManager.jettyStop();
        }

        LoggerUtils.logInfo("SuperHoppersWebUI has been disabled!");
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }
}
