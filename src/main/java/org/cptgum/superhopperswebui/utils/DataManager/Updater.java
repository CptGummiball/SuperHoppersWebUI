package org.cptgum.superhopperswebui.utils.DataManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.cptgum.superhopperswebui.Main;
import org.cptgum.superhopperswebui.utils.LoggerUtils;

public class Updater {

    private final JavaPlugin plugin;

    public Updater(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void updateConfig() {
        int currentConfigVersion = plugin.getConfig().getInt("configversion", 0);
        File newConfigFile = getConfigFromJar();

        if (newConfigFile == null) {
            LoggerUtils.logError("Unable to find or create the new config file.");
            return;
        }

        FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        FileConfiguration newConfig = YamlConfiguration.loadConfiguration(newConfigFile);

        int newConfigVersion = newConfig.getInt("versions.config", 0);

        LoggerUtils.logInfo("Current config version: " + currentConfigVersion);

        if (newConfigVersion > currentConfigVersion) {
            LoggerUtils.logInfo("New config version: " + newConfigVersion);
            LoggerUtils.logInfo("Updating config.yml... ");
            ConfigUtils configUpdater = new ConfigUtils((Main) plugin);
            configUpdater.pluginconfigupdater(oldConfig, newConfig);
        }
    }

    public void updateWebFolder() {
        WebFileUtils webFileUtils = new WebFileUtils(plugin);
        int currentWebFilesVersion = plugin.getConfig().getInt("versions.web", 0);
        int newWebFilesVersion = YamlConfiguration.loadConfiguration(getConfigFromJar()).getInt("versions.web", 0);
        LoggerUtils.logInfo("Current web files version: {}" + currentWebFilesVersion);
        File webFolder = new File(plugin.getDataFolder(), "web");
        if (!webFolder.exists() || !webFolder.isDirectory()) {
            LoggerUtils.logInfo("Transferring all web files...");
            webFileUtils.copy();
        } else if (newWebFilesVersion > currentWebFilesVersion) {
            LoggerUtils.logInfo("New web files version: {}" + newWebFilesVersion);
            LoggerUtils.logInfo("Updating web files... ");
            webFileUtils.copy();
            plugin.getConfig().set("versions.web", newWebFilesVersion);
            plugin.saveConfig();
        }
    }

    private File getConfigFromJar() {
        // Loading the "config.yml" from the JAR file
        InputStream resource = plugin.getResource("config.yml");
        // Check if the resource is null
        if (resource == null) {
            LoggerUtils.logError("Unable to find config.yml in the JAR file.");
            return null;
        }
        // Save the "config.yml" from the JAR file in a temporary directory
        File tempConfigFile;
        try {
            tempConfigFile = File.createTempFile("tempConfig", ".yml");
            Files.copy(resource, tempConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            resource.close();
        } catch (IOException e) {
            LoggerUtils.logError("An error occurred while copying config.yml from JAR to a temporary directory" + e);
            return null;
        }
        // Load the "config.yml" from the temporary directory
        return tempConfigFile;
    }

    private void updateConfigFile() {
        InputStream jarConfigStream = plugin.getResource("config.yml");

        if (jarConfigStream != null) {
            File configFile = new File(plugin.getDataFolder(), "config.yml");

            // Manually copy the file from JAR to plugin folder
            try {
                Files.copy(jarConfigStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LoggerUtils.logError("Failed to copy config.yml from JAR to plugin folder" + e);
            }
        }
    }
}
