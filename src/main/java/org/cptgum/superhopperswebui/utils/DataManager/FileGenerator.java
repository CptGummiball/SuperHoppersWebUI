package org.cptgum.superhopperswebui.utils.DataManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

import org.cptgum.superhopperswebui.utils.LoggerUtils;

public class FileGenerator {

    public static void main(String[] args) {
        generateOutputJson();
    }

    private static void generateOutputJson() {
        try {
        File itemFolder = new File("plugins/SuperHoppers/hoppers/item");
        File mobFolder = new File("plugins/SuperHoppers/hoppers/mob");

        List<Map<String, Object>> result = new ArrayList<>();

        processFolder(itemFolder, result, "item");
        processFolder(mobFolder, result, "mob");

        writeToJsonFile(result, "plugins/SuperHoppersWebUI/web/output.json");
    } catch (Exception e) {
        LoggerUtils.logError("Error while generating output.json" + e.getMessage());
    }
    }

    private static void processFolder(File folder, List<Map<String, Object>> result, String hopperType) {
        try {

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    processYaml(file, result, hopperType);
                }
            }
        }
        } catch (Exception e) {
            LoggerUtils.logError("Error while processing hopper folders" + e.getMessage());
        }
    }

    private static void processYaml(File file, List<Map<String, Object>> result, String hopperType) {
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(FileReader(file));

            // Bereinige Strings und erstelle UUID
            cleanAndProcessData(yamlData, hopperType);

            // Füge die bereinigten Daten zur Ergebnisliste hinzu
            result.add(yamlData);
        } catch (Exception e) {
            LoggerUtils.logError("Error while processing yaml file" + e.getMessage());
        }
    }

    private static Reader FileReader(File file) throws IOException {
        return new FileReader(file);
    }

    private static void cleanAndProcessData(Map<String, Object> data, String hopperType) {
        try {
            // Bereinige Strings
            cleanString(data, "hopperName");
            cleanStringList(data, "trusted");
            cleanStringList(data, "void_filter");

            // Entferne "hologram_entities"
            LoggerUtils.logDebug("Removing hologram_entities for " + data.get("hopperName"));
            data.remove("hologram_entities");

            // Setze hopperType
            LoggerUtils.logDebug("Setting hopperType for " + data.get("hopperName"));
            data.put("hopperType", hopperType);

            // Erstelle UUIDs
            createUUID(data, "hopperUUID");

            // Bereinige filter_materials und storage_items
            cleanFilterMaterials(data);

            if ("mob".equals(hopperType)) {
                // Bereinige mob_storage_items
                cleanMobStorageItems(data);
                addWorthToMobStorageItems(data);
                addTotalWorthToData(data);
                addHopperWorth(data);
            } else {
                // Bereinige storage_items
                cleanStorageItems(data);

                // Füge "worth" zu "storage_items" hinzu
                addWorthToStorageItems(data);

                // Füge "total_worth" für jedes einzelne Item hinzu
                addTotalWorthToData(data);

                // Füge "hopperWorth" hinzu
                addHopperWorth(data);
            }
        } catch (Exception e) {
            LoggerUtils.logError("Error while processing data" + e.getMessage());
        }
    }

    private static void cleanString(Map<String, Object> data, String key) {
        try {
            LoggerUtils.logDebug("Cleaning data for " + data.get("hopperName"));
            if (data.containsKey(key)) {
                Object value = data.get(key);
                if (value instanceof String) {
                    // Entferne Farbcodes und Sonderzeichen, inklusive "§"
                    String cleanedValue = ((String) value).replaceAll("§.", "");
                    data.put(key, cleanedValue);
                }
            }
        } catch (Exception e) {
            LoggerUtils.logError("Error while cleaning string" + e.getMessage());
        }
    }

    private static void cleanStringList(Map<String, Object> data, String key) {
        try {
            LoggerUtils.logDebug("Cleaning " + key + " list for " + data.get("hopperName"));
            if (data.containsKey(key)) {
                Object value = data.get(key);
                if (value instanceof List<?>) {
                    List<String> stringList = (List<String>) value;
                    List<String> cleanedList = new ArrayList<>();
                    for (String str : stringList) {
                    // Bereinige jeden String in der Liste
                    String cleanedValue = str.replaceAll("[^a-zA-Z0-9_]", "");
                    cleanedList.add(cleanedValue);
                }
                data.put(key, cleanedList);
                }
            }
        } catch (Exception e) {
            LoggerUtils.logError("Error while cleaning string list" + e.getMessage());
        }
    }

    private static void createUUID(Map<String, Object> data, String key) {
        try {
            LoggerUtils.logDebug("Creating UUID for " + data.get("hopperName"));
            if (!data.containsKey(key)) {
                // Erstelle eine neue UUID, da sie nicht in der YAML-Datei vorhanden ist
                UUID uuid = UUID.randomUUID();
                data.put(key, uuid.toString());
            }
        } catch (Exception e) {
            LoggerUtils.logError("Error while creating UUID" + e.getMessage());
        }
    }

    private static void cleanFilterMaterials(Map<String, Object> data) {
        try {
            LoggerUtils.logDebug("Cleaning filter_materials for " + data.get("hopperName"));
            if (data.containsKey("filter_materials")) {
                Object value = data.get("filter_materials");
                if (value instanceof List<?>) {
                    List<Map<String, Object>> filterMaterials = (List<Map<String, Object>>) value;
                    List<String> cleanedFilterMaterials = new ArrayList<>();
                    for (Map<String, Object> material : filterMaterials) {
                        if (material.containsKey("type")) {
                            cleanedFilterMaterials.add(material.get("type").toString());
                        }
                    }
                    data.put("filter_materials", cleanedFilterMaterials);
                }
            }
        } catch (Exception e) {
            LoggerUtils.logError("Error while cleaning filter materials" + e.getMessage());
        }
    }

    private static void cleanMobStorageItems(Map<String, Object> data) {
        try {
            LoggerUtils.logDebug("Cleaning mob_storage_items for " + data.get("hopperName"));
            if ("mob".equals(data.get("hopperType")) && data.containsKey("storage_items")) {
                Object value = data.get("storage_items");
                if (value instanceof Map<?, ?>) {
                    Map<String, Map<String, Object>> mobStorageItems = (Map<String, Map<String, Object>>) value;
                    List<Map<String, Object>> cleanedMobStorageItems = new ArrayList<>();
                    for (Map.Entry<String, Map<String, Object>> entry : mobStorageItems.entrySet()) {
                        Map<String, Object> mobStorageItem = entry.getValue();
                        if (mobStorageItem.containsKey("item")) {
                            String mobType = (String) mobStorageItem.get("item");
                            int amount = Integer.parseInt(mobStorageItem.get("amount").toString());
                            Map<String, Object> cleanedItem = new HashMap<>();
                            cleanedItem.put("amount", amount);
                            cleanedItem.put("mob", mobType);
                            cleanedMobStorageItems.add(cleanedItem);
                        }
                    }
                    data.put("storage_items", cleanedMobStorageItems);
                }
            }
        } catch (Exception e) {
            LoggerUtils.logError("Error while cleaning mob storage items" + e.getMessage());
        }
    }

    private static void cleanStorageItems(Map<String, Object> data) {
        try {
            LoggerUtils.logDebug("Cleaning storage_items for " + data.get("hopperName"));
            if (data.containsKey("storage_items")) {
                Object value = data.get("storage_items");
                if (value instanceof Map<?, ?>) {
                    Map<String, Map<String, Object>> storageItems = (Map<String, Map<String, Object>>) value;
                    List<Map<String, Object>> cleanedStorageItems = new ArrayList<>();
                    for (Map.Entry<String, Map<String, Object>> entry : storageItems.entrySet()) {
                        Map<String, Object> storageItemData = entry.getValue();
                        if (storageItemData.containsKey("item") && storageItemData.get("item") instanceof Map<?, ?>) {
                            Map<String, Object> itemData = (Map<String, Object>) storageItemData.get("item");
                            if (itemData.containsKey("type")) {
                                Map<String, Object> cleanedItemData = new HashMap<>();
                                cleanedItemData.put("amount", Integer.parseInt(storageItemData.get("amount").toString()));
                                cleanedItemData.put("item", itemData.get("type").toString());
                                cleanedStorageItems.add(cleanedItemData);
                            }
                        }
                    }
                    data.put("storage_items", cleanedStorageItems);
                }
            }
        } catch (Exception e) {
            LoggerUtils.logError("Error while cleaning storage items" + e.getMessage());
        }
    }

    private static void addWorthToStorageItems(Map<String, Object> data) {
        try {
            LoggerUtils.logDebug("Adding worth to storage_items for " + data.get("hopperName"));
            if (data.containsKey("storage_items")) {
                Object value = data.get("storage_items");
                if (value instanceof List<?>) {
                    List<Map<String, Object>> storageItems = (List<Map<String, Object>>) value;
                    for (Map<String, Object> storageItem : storageItems) {
                        if (storageItem.containsKey("item")) {
                            Object itemValue = storageItem.get("item");
                            if (itemValue instanceof String) {
                                String itemType = (String) itemValue;
                                double worth = getItemWorth(itemType);
                                storageItem.put("worth", worth);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LoggerUtils.logError("Error while adding worth to storage items" + e.getMessage());
        }
    }

    private static void addWorthToMobStorageItems(Map<String, Object> data) {
        try {
            LoggerUtils.logDebug("Adding worth to storage_items for " + data.get("hopperName"));
            if (data.containsKey("storage_items")) {
                Object value = data.get("storage_items");
                if (value instanceof List<?>) {
                    List<Map<String, Object>> storageItems = (List<Map<String, Object>>) value;
                    for (Map<String, Object> storageItem : storageItems) {
                        if (storageItem.containsKey("item")) {
                            Object itemValue = storageItem.get("item");
                            if (itemValue instanceof String) {
                                String itemType = (String) itemValue;
                                double worth = getMobPrice(itemType); // Änderung hier
                                storageItem.put("worth", worth);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LoggerUtils.logError("Error while adding worth to storage items" + e.getMessage());
        }
    }

    private static double getItemWorth(String itemType) {
        File lootFile = new File("plugins/SuperHoppers/loot.yml");
        try {
            LoggerUtils.logDebug("Reading loot.yml for " + itemType);
            Yaml yaml = new Yaml();
            Map<String, Map<String, Double>> lootData = yaml.load(new FileInputStream(lootFile));

            if (lootData.containsKey("item_worth")) {
                Map<String, Double> itemWorthMap = lootData.get("item_worth");
                return itemWorthMap.getOrDefault(itemType, 0.0);
            }
        } catch (IOException e) {
            LoggerUtils.logError("Error while reading loot.yml: " + e.getMessage());
        }
        return 0.0;
    }

    private static double getMobPrice(String mobType) {
        File mobsFile = new File("plugins/SuperHoppers/mobs.yml");
        try {
            LoggerUtils.logDebug("Reading mobs.yml for " + mobType);
            Yaml yaml = new Yaml();
            Map<String, Map<String, Map<String, Double>>> mobsData = yaml.load(new FileInputStream(mobsFile));

            if (mobsData.containsKey("mobs")) {
                Map<String, Map<String, Double>> mobsMap = mobsData.get("mobs");
                if (mobsMap.containsKey(mobType)) {
                    Map<String, Double> mobValues = mobsMap.get(mobType);
                    return mobValues.getOrDefault("price", 0.0);
                } else {
                    LoggerUtils.logDebug("Mob type not found in mobs.yml: " + mobType);
                }
            }
        } catch (IOException e) {
            LoggerUtils.logError("Error while reading mobs.yml: " + e.getMessage());
        }
        return 0.0;
    }

    private static void addTotalWorthToData(Map<String, Object> data) {
        try {
            LoggerUtils.logDebug("Adding total worth to data for " + data.get("hopperName"));
            if (data.containsKey("storage_items")) {
                Object value = data.get("storage_items");
                if (value instanceof List<?>) {
                    List<Map<String, Object>> storageItems = (List<Map<String, Object>>) value;
                    for (Map<String, Object> storageItem : storageItems) {
                        if (storageItem.containsKey("worth") && storageItem.containsKey("amount")) {
                            double itemWorth = (double) storageItem.get("worth");
                            int amount = (int) storageItem.get("amount");
                            double totalWorth = itemWorth * amount;
                            storageItem.put("total_worth", totalWorth);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LoggerUtils.logError("Error while adding total worth to data" + e.getMessage());
        }
    }

    private static void addHopperWorth(Map<String, Object> data) {
        double hopperWorth = 0.0;
        try {
            LoggerUtils.logDebug("Adding hopper worth for " + data.get("hopperName"));
            if (data.containsKey("storage_items")) {
                Object value = data.get("storage_items");
                if (value instanceof List<?>) {
                    List<Map<String, Object>> storageItems = (List<Map<String, Object>>) value;
                    for (Map<String, Object> storageItem : storageItems) {
                        if (storageItem.containsKey("total_worth")) {
                            double totalWorth = (double) storageItem.get("total_worth");
                            hopperWorth += totalWorth;
                        }
                    }
                }
            }

            data.put("hopperWorth", hopperWorth);
        } catch (Exception e) {
            LoggerUtils.logError("Error while adding hopper worth" + e.getMessage());
        }
    }

    private static void writeToJsonFile(List<Map<String, Object>> dataList, String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            LoggerUtils.logDebug("Writing to JSON file: " + outputPath);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(dataList);
            writer.write(jsonString);
        } catch (IOException e) {
            LoggerUtils.logError("Error while writing to JSON file: " + e.getMessage());
        }
    }
}