package org.cptgum.superhopperswebui;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.cptgum.superhopperswebui.utils.DataManager.Updater;
import org.cptgum.superhopperswebui.utils.LoggerUtils;
import org.cptgum.superhopperswebui.utils.SchedulerManager;
import org.cptgum.superhopperswebui.utils.webserver.FetchIP;
import org.cptgum.superhopperswebui.utils.webserver.WebServerManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    private static Main instance;
    WebServerManager webServerManager = new WebServerManager(this);
    SchedulerManager schedulerManager = new SchedulerManager(this);
    Updater updater = new Updater(this);
    private boolean pluginEnabled = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        pluginEnabled = true;
        createLogsFolder();
        LoggerUtils.setPlugin(this);
        loadConfig();
        // Extracts necessary resources from the JAR file
        updater.updateConfig();
        updater.updateWebFolder();
        // Start webserver
        webServerManager.jettyStart();
        // Start Scheduler
        schedulerManager.startScheduler();
        registerCommands();
        LoggerUtils.logInfo("SuperHoppersWebUI has been enabled!");
        LoggerUtils.logInfo("Hello SuperHoppers, I'm here! HUG!");
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
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    public static void createLogsFolder() {
        File pluginFolder = instance.getDataFolder();
        File logsFolder = new File(pluginFolder, "logs");

        if (!logsFolder.exists()) {
            if (logsFolder.mkdirs()) {
                LoggerUtils.logDebug("Logs folder created successfully!");
            } else {
                LoggerUtils.logError("Failed to create logs folder!");
            }
        }
    }

    public class Commands implements CommandExecutor, TabCompleter {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.YELLOW +"Usage: /sui <link|restart>");
                return true;
            }

            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "link":
                    if (sender.hasPermission("sui.link")) {
                        String serverIP = FetchIP.getIP();
                        int webServerPort = getConfig().getInt("web-server-port");

                        String url = "http://" + serverIP + ":" + webServerPort + "/?search=" + sender.getName();

                        String clickMessage = "Click here to open the SuperHopperWebUI.";

                        TextComponent clickableUrl = new TextComponent(clickMessage);
                        clickableUrl.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));

                        sender.sendMessage(String.valueOf(clickableUrl));
                    }else{
                        sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
                    }
                    break;
                case "restart":
                    if (sender.hasPermission("sui.restart")) {
                    webServerManager.jettyRestart();
                    }else{
                        sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
                    }
                    break;

                default:
                    sender.sendMessage("Unknown subcommand: " + subCommand);
                    break;
            }

            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            List<String> tabCompletions = new ArrayList<>();

            if (args.length == 1) {
                tabCompletions.add("link");
                tabCompletions.add("restart");
            }

            return tabCompletions;
        }
    }

    private void registerCommands() {
        getCommand("sui").setExecutor(new Commands());
    }
}

