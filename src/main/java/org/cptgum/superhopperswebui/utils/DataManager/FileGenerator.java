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

    private static void processYamlFiles() {
        // Durchsuche den Ordner "/plugins/SuperHoppers" nach .yml-Dateien
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
        // Lese OwnerUUID aus der YAML-Datei
        String ownerUUID = readOwnerUUIDFromYaml(yamlFile);

        // Erzeuge den Zielordner im "/plugins/SuperHoppersWebUI/web"-Verzeichnis
        String webFolder = WEB_FOLDER + "/" + ownerUUID;
        createWebFolder(webFolder);

        // Kopiere den Inhalt von "/web/template" in den Zielordner, wenn dieser nicht existiert
        copyTemplate(webFolder);

        // Konvertiere YAML in JSON und schreibe es in die output.json-Datei im Zielordner
        convertYamlToJson(yamlFile, webFolder + "/" + OUTPUT_FILE);
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

    private static void convertYamlToJson(File yamlFile, String outputFile) {
        try (FileInputStream fis = new FileInputStream(yamlFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
             FileWriter writer = new FileWriter(outputFile)) {

            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(reader);

            // Annahme: yamlData ist ein Map-Objekt
            String jsonArray = convertMapToJsonArray(yamlData);

            // Schreibe das JSON-Array in die output.json-Datei
            writer.write(jsonArray);
        } catch (IOException e) {
            LoggerUtils.logError("Error converting YAML to JSON: " + e);
        }
    }

    private static String convertMapToJsonArray(Map<String, Object> yamlData) {
        // Annahme: YAML-Map enthält eine Liste von Objekten mit einem eindeutigen Schlüssel "id"
        // Die JSON-Ausgabe könnte wie folgt aussehen:
        // [{"id": "value1", "key1": "value1", "key2": "value2"}, {"id": "value2", "key1": "value3", "key2": "value4"}, ...]

        StringBuilder jsonArrayBuilder = new StringBuilder("[");
        boolean firstEntry = true;

        for (Map.Entry<String, Object> entry : yamlData.entrySet()) {
            if (entry.getKey().equals("OwnerUUID")) {
                continue; // Ignoriere den Schlüssel "OwnerUUID" im JSON-Array
            }

            if (!firstEntry) {
                jsonArrayBuilder.append(", ");
            } else {
                firstEntry = false;
            }

            jsonArrayBuilder.append("{");
            jsonArrayBuilder.append("\"id\": \"").append(entry.getKey()).append("\", ");

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

        jsonArrayBuilder.append("]");

        return jsonArrayBuilder.toString();
    }

    // Füge hier die Methode convertMapToJsonArray hinzu, um das Map-Objekt in ein JSON-Array umzuwandeln

}
