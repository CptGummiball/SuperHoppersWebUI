package org.cptgum.superhopperswebui.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Translator {

    private static final Translator INSTANCE = new Translator();
    private final Map<String, YamlConfiguration> languageConfigs = new HashMap<>();
    private String defaultLanguage = "en"; // Fallback language

    private Translator() {
        // Private constructor to prevent instantiation
    }

    public static Translator getInstance() {
        return INSTANCE;
    }

    public void loadLanguages(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        defaultLanguage = config.getString("Language", "en"); // Default to "en" if not set

        for (String language : new String[]{"cn", "de", "en", "es", "fr", "it", "pl", "pt", "ua"}) {
            String fileName = "translations/" + language + ".yml";
            YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(getResourceReader(plugin, fileName));

            languageConfigs.put(language, langConfig);
        }
    }

    public String translate(String key) {
        YamlConfiguration config = languageConfigs.getOrDefault(defaultLanguage, languageConfigs.get("en"));

        if (config == null) {
            return "Translation error";
        }

        String translation = config.getString(key, "Translation missing for: " + key);

        // Replace color codes
        translation = translation.replaceAll("&", "\u00A7");

        return translation;
    }

    private Reader getResourceReader(JavaPlugin plugin, String resourcePath) {
        return new InputStreamReader(plugin.getResource(resourcePath), StandardCharsets.UTF_8);
    }
}
