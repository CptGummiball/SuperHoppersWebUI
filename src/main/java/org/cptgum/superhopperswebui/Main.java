package org.cptgum.superhopperswebui;

import org.bukkit.plugin.java.JavaPlugin;
import org.cptgum.superhopperswebui.utils.DataManager.ConfigUtils;
import org.cptgum.superhopperswebui.utils.DataManager.Updater;
import org.cptgum.superhopperswebui.utils.LoggerUtils;
import org.cptgum.superhopperswebui.utils.Translator;
import org.cptgum.superhopperswebui.utils.webserver.WebServerManager;

public final class Main extends JavaPlugin {

    private static Main instance;
    WebServerManager webServerManager = new WebServerManager(this);
    private boolean pluginEnabled = false;
    int UpdateInterval;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        pluginEnabled = true;
        loadConfig();
        // Extracts necessary resources from the JAR file
        new Updater(this).updateWebFolder();
        new Updater(this).updateConfig();
        new ConfigUtils(this).webConfigUpdater();
        // Load languages
        Translator translator = Translator.getInstance();
        translator.loadLanguages(this);
        //start webserver
        webServerManager.jettyStart();

        LoggerUtils.logInfo("SuperHoppersWebUI has been enabled!");
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
        // Load config for UpdateInterval
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        // Read the value of UpdateInterval from the configuration
        UpdateInterval = getConfig().getInt("UpdateInterval", 6000);
    }
}
