package org.cptgum.superhopperswebui.utils.DataManager;

import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.plugin.java.JavaPlugin;
import org.cptgum.superhopperswebui.Main;
import org.cptgum.superhopperswebui.utils.LoggerUtils;

import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtils {
    private final JavaPlugin plugin;

    public ConfigUtils(Main plugin) {
        this.plugin = plugin;
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

    public static void jsonsync() {
        try {
            String yamlFilePath = "plugins/SuperHoppers/config.yml";
            String jsonFilePath = "plugins/SuperHoppersWebUi/web/assets/config.json";

            String existingJsonString = readJsonFile(jsonFilePath);
            JSONObject existingJsonObject = new JSONObject(existingJsonString);

            Map<String, Object> yamlObject = readYamlFile(yamlFilePath);

            String currencyValue = getValue(yamlObject, "currency");
            String currencyBeforeValue = getValue(yamlObject, "currency_before");

            existingJsonObject.put("currency", currencyValue);
            existingJsonObject.put("currency_before", currencyBeforeValue);

            try (FileWriter fileWriter = new FileWriter(jsonFilePath)) {
                fileWriter.write(existingJsonObject.toString());
            }

            System.out.println("Synchronisation abgeschlossen.");

        } catch (Exception e) {
            LoggerUtils.logError("Error while synchronizing config.yml and config.json: " + e);
        }
    }

    private static String getValue(Map<String, Object> yamlObject, String key) {
        if (yamlObject.containsKey(key)) {
            return yamlObject.get(key).toString();
        }
        return null;
    }

    private static Map<String, Object> readYamlFile(String yamlFilePath) throws Exception {
        Yaml yaml = new Yaml();
        try (FileReader reader = new FileReader(yamlFilePath)) {
            return yaml.load(reader);
        }
    }

    private static String readJsonFile(String jsonFilePath) throws Exception {
        Path path = Paths.get(jsonFilePath);
        if (Files.exists(path)) {
            return Files.readString(path);
        }
        return "{}";
    }
}
