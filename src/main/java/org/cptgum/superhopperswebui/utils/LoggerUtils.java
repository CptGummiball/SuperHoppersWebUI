package org.cptgum.superhopperswebui.utils;

import org.bukkit.ChatColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerUtils {

    private static final Logger logger = LoggerFactory.getLogger("SuperHoppersWebUI");

    public static void logInfo(String message) {
        logger.info(ChatColor.BLUE + "[SuperHoppersWebUI] " + ChatColor.RESET + message);
    }

    public static void logWarning(String message) {
        logger.warn(ChatColor.BLUE + "[SuperHoppersWebUI] " + ChatColor.YELLOW + message);
    }

    public static void logError(String message) {
        logger.error(ChatColor.BLUE + "[SuperHoppersWebUI] " + ChatColor.RED + message);
        writeErrorToFile(message);
    }

    private static void writeErrorToFile(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("error.log", true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.println(timestamp + " - " + message);
        } catch (IOException e) {
            e.printStackTrace(); // Log the error, not be replaceable by logger output because it is an internal error of the logger
        }
    }
}