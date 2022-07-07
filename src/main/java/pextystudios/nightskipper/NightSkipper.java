package pextystudios.nightskipper;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pextystudios.nightskipper.command.NightSkipperCommand;
import pextystudios.nightskipper.util.FormatUtil;
import pextystudios.nightskipper.util.NotificationUtil;
import pextystudios.nightskipper.util.PlayerUtil;

import java.util.HashMap;

public final class NightSkipper extends JavaPlugin {
    private static NightSkipper instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        saveDefaultConfig();
        reloadConfig();

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        new NightSkipperCommand();
    }

    @Override
    public void onDisable() {
        NotificationUtil.clear();
        PlayerUtil.saveAlwaysVotingPlayerList();
        // Plugin shutdown logic
    }

    public static void resetConfigValues() {
        instance.saveResource("config.yml", true);
    }

    public static NightSkipper getInstance() {
        return instance;
    }

    public static World getCurrentWorld() {
        return instance.getServer().getWorlds().get(0);
    }

    public static String getText(@NotNull String path) {
        return getText(path, null);
    }

    public static String getText(@NotNull String path, HashMap<String, String> formatVars) {
        return FormatUtil.format(instance.getConfig().getString("text." + path), formatVars);
    }

    public static String getMode() {
        return instance.getConfig().getString("mode");
    }
}
