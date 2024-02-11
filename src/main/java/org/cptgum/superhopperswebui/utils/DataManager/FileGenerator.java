package org.cptgum.superhopperswebui.utils.DataManager;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.cptgum.superhopperswebui.utils.LoggerUtils;

public class FileGenerator {

    private static final String PLUGIN_FOLDER = "/plugins/SuperHoppers";
    private static final String WEB_FOLDER = "/plugins/SuperHoppersWebUI/web";
    private static final String TEMPLATE_FOLDER = "/web/template";
    private static final String OUTPUT_FILE = "output.json";

    public static void main(String[] args) {
        processYamlFiles();
    }

    public static void processYamlFiles() {
        File superHoppersFolder = new File(PLUGIN_FOLDER);
        if (superHoppersFolder.exists() && superHoppersFolder.isDirectory()) {
            File[] yamlFiles = superHoppersFolder.listFiles((dir, name) -> name.endsWith(".yml"));

            if (yamlFiles != null) {
                for (File yamlFile : yamlFiles) {
                    processYamlFile(yamlFile);
                }
            }
        }
    }

    private static void processYamlFile(File yamlFile) {
        String ownerUUID = readOwnerUUIDFromYaml(yamlFile);
        String ownerName = readOwnerNameFromYaml(yamlFile);
        String webFolder = WEB_FOLDER + "/" + ownerUUID;
        createWebFolder(webFolder);
        copyTemplate(webFolder);
        convertYamlToJson(yamlFile, webFolder + "/" + OUTPUT_FILE, ownerName);
    }

    private static String readOwnerUUIDFromYaml(File yamlFile) {
        try (FileInputStream fis = new FileInputStream(yamlFile)) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(fis);
            return (String) yamlData.get("OwnerUUID");
        } catch (IOException e) {
            LoggerUtils.logError("Error reading YAML file: " + yamlFile.getName() + e);
        }
        return null;
    }

    private static String readOwnerNameFromYaml(File yamlFile) {
        try (FileInputStream fis = new FileInputStream(yamlFile)) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(fis);
            return (String) yamlData.get("OwnerName");
        } catch (IOException e) {
            LoggerUtils.logError("Error reading YAML file: " + yamlFile.getName() + e);
        }
        return null;
    }

    private static void createWebFolder(String webFolder) {
        Path path = Paths.get(webFolder);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                LoggerUtils.logError("Error creating web folder: " + webFolder + e);
            }
        }
    }

    private static void copyTemplate(String destinationFolder) {
        Path sourcePath = Paths.get(PLUGIN_FOLDER + TEMPLATE_FOLDER);
        Path destinationPath = Paths.get(destinationFolder);

        if (Files.exists(sourcePath) && Files.isDirectory(sourcePath)) {
            if (!Files.exists(destinationPath)) {
                try {
                    Files.createDirectories(destinationPath);
                    Files.walk(sourcePath)
                            .forEach(source -> {
                                Path destination = destinationPath.resolve(sourcePath.relativize(source));
                                try {
                                    Files.copy(source, destination);
                                } catch (IOException e) {
                                    LoggerUtils.logError("Error copying template files: " + e);
                                }
                            });
                } catch (IOException e) {
                    LoggerUtils.logError("Error copying template files: " + e);
                }
            }
        }
    }

    private static void convertYamlToJson(File yamlFile, String outputFile, String ownerName) {
        try (FileInputStream fis = new FileInputStream(yamlFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
             FileWriter writer = new FileWriter(outputFile)) {

            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(reader);

            String jsonArray = convertMapToJsonArray(yamlData, ownerName);

            writer.write(jsonArray);
        } catch (IOException e) {
            LoggerUtils.logError("Error converting YAML to JSON: " + e);
        }
    }

    private static String convertMapToJsonArray(Map<String, Object> yamlData, String ownerName) {

        StringBuilder jsonArrayBuilder = new StringBuilder("[");
        boolean firstEntry = true;

        jsonArrayBuilder.append("{");
        jsonArrayBuilder.append("\"OwnerUUID\": \"").append(yamlData.get("OwnerUUID")).append("\", ");
        jsonArrayBuilder.append("\"OwnerName\": \"").append(ownerName).append("\", ");
        jsonArrayBuilder.append("\"HopperData\": [");

        for (Map.Entry<String, Object> entry : yamlData.entrySet()) {
            if (entry.getKey().equals("OwnerUUID") || entry.getKey().equals("OwnerName")) {
                continue;
            }

            if (!firstEntry) {
                jsonArrayBuilder.append(", ");
            } else {
                firstEntry = false;
            }

            jsonArrayBuilder.append("{");
            jsonArrayBuilder.append("\"HopperUUID\": \"").append(entry.getKey()).append("\", ");

            if (entry.getValue() instanceof Map) {
                Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();
                for (Map.Entry<String, Object> innerEntry : innerMap.entrySet()) {
                    jsonArrayBuilder.append("\"").append(innerEntry.getKey()).append("\": \"").append(innerEntry.getValue()).append("\", ");
                }
            } else {
                // Handle other data types if needed
            }

            jsonArrayBuilder.delete(jsonArrayBuilder.length() - 2, jsonArrayBuilder.length()); // Remove trailing comma
            jsonArrayBuilder.append("}");
        }

        jsonArrayBuilder.append("]}");

        return jsonArrayBuilder.toString();
    }
}