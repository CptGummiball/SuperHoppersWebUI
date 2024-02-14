package org.cptgum.superhopperswebui.utils.DataManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.cptgum.superhopperswebui.Main;
import org.cptgum.superhopperswebui.utils.LoggerUtils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtils {
    private final JavaPlugin plugin;

    public ConfigUtils(Main plugin) {
        this.plugin = plugin;
    }

    private JSONObject loadJsonConfig() {
        File configFile = new File(plugin.getDataFolder(), "/web/assets/config.json");

        try (FileReader reader = new FileReader(configFile)) {
            return new JSONObject(new JSONTokener(reader));
        } catch (IOException e) {
            LoggerUtils.logWarning("Json could not be loaded" + e);
            return new JSONObject();
        }
    }

    private void syncValues(FileConfiguration yamlConfig, JSONObject jsonObj, FileConfiguration superHoppersConfig) {
        // Synchronize the values from SuperHoppers plugin config
        String currencySymbol = superHoppersConfig.getString("currency");
        String currencySymbolPosition = superHoppersConfig.getString("currency_before");

        // Map the values from SuperHoppers config to the expected format
        if (currencySymbolPosition != null) {
            if (currencySymbolPosition.equalsIgnoreCase("true")) {
                currencySymbolPosition = "before";
            } else if (currencySymbolPosition.equalsIgnoreCase("false")) {
                currencySymbolPosition = "after";
            }
        }

        // Set the values in the JSON config
        jsonObj.put("currencySymbol", currencySymbol);
        jsonObj.put("currencySymbolPosition", currencySymbolPosition);
    }

    private void saveJsonConfig(JSONObject jsonObj) {
        File configFile = new File(plugin.getDataFolder(), "/web/assets/config.json");

        try (FileWriter writer = new FileWriter(configFile)) {
            jsonObj.write(writer);
        } catch (IOException e) {
            LoggerUtils.logWarning("Json could not be saved" + e);
        }
    }

    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    private FileConfiguration getSuperHoppersConfig() {
        Plugin superHoppersPlugin = Bukkit.getPluginManager().getPlugin("SuperHoppers");

        if (superHoppersPlugin != null) {
            return superHoppersPlugin.getConfig();
        } else {
            LoggerUtils.logWarning("SuperHoppers plugin not found");
            return null;
        }
    }

    public void pluginconfigupdater(FileConfiguration oldConfig, FileConfiguration newConfig) {
        Map<String, Object> updates = new HashMap<>();
        // Iterate through the new configuration data
        for (String key : newConfig.getKeys(true)) {
            // Check if the key exists in the old configuration
            if (!oldConfig.contains(key)) {
                // Add new key to old configuration
                oldConfig.set(key, newConfig.get(key));
            }
        }

        // Iterate through the old configuration data
        for (String key : oldConfig.getKeys(true)) {
            // Check whether the key no longer exists in the new configuration
            if (!newConfig.contains(key)) {
                // Remove keys that no longer exist from the old configuration
                oldConfig.set(key, null);
            }
        }

        // Update the version number in the old configuration
        int newConfigVersion = newConfig.getInt("versions.config", 0);
        oldConfig.set("versions.config", newConfigVersion);

        // Create a temporary file for the updated configuration
        File tempConfigFile = new File("tempConfig.yml");

        try {
            // Save the updated configuration to the temporary file
            oldConfig.save(tempConfigFile);

            // Copy the temporary file to the plugin folder
            Files.copy(tempConfigFile.toPath(), new File(plugin.getDataFolder(), "config.yml").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LoggerUtils.logError("Could not update config file" + e);
        } finally {
            // Once finished, delete the temporary file
            tempConfigFile.delete();
        }
    }
}
