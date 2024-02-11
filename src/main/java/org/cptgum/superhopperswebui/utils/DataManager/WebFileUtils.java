package org.cptgum.superhopperswebui.utils.DataManager;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.cptgum.superhopperswebui.utils.LoggerUtils;

public class WebFileUtils {
    private final Plugin plugin;

    public WebFileUtils(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @SuppressWarnings("SpellCheckingInspection")
    public void copy() {
        {
            //Webfiles
            plugin.saveResource("web/index.html", true);
            LoggerUtils.logInfo("web files updated");
        }
    }
}
