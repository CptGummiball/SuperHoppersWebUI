package org.cptgum.superhopperswebui.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerUtils {

    private static JavaPlugin plugin;
    public static void setPlugin(JavaPlugin plugin) {
        LoggerUtils.plugin = plugin;
    }
    private static final String DEBUG_LOG_PATH = "plugins/SuperHoppersWebUI/logs/debug.log";
    private static final String ERROR_LOG_PATH = "plugins/SuperHoppersWebUI/logs/error.log";
    private static final Logger logger = LoggerFactory.getLogger("SuperHoppersWebUI");

    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logWarning(String message) {
        logger.warn(message);
    }

    public static void logError(String message) {
        logger.error(message);
        writeErrorToFile(message);
    }

    public static void logDebug(String message) {
        writeDebugToFile(message);
        if (plugin != null && plugin.getConfig().getBoolean("DebugMode", false)) {
            logger.info(message);
        }
    }

    private static void writeErrorToFile(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ERROR_LOG_PATH, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.println(timestamp + " - " + message);
        } catch (IOException e) {
            e.printStackTrace(); // Log the error, not be replaceable by logger output because it is an internal error of the logger
        }
    }

    private static void writeDebugToFile(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DEBUG_LOG_PATH, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.println(timestamp + " - " + message);
        } catch (IOException e) {
            e.printStackTrace(); // Log the error, not be replaceable by logger output because it is an internal error of the logger
        }
    }

}