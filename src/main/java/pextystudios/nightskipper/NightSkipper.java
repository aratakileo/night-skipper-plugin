package pextystudios.nightskipper;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pextystudios.nightskipper.command.NightSkipperCommand;
import pextystudios.nightskipper.util.*;

import java.util.HashMap;

public final class NightSkipper extends JavaPlugin {
    private static NightSkipper instance;
    private static final HashMap<String, String> globalVars = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic

        instance = this;

        saveDefaultConfig();
        reloadConfigValues();

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        new NightSkipperCommand();

        if (SleepUtil.isSleepTime()) SleepUtil.reloadTarget();

        LifecycleUtil.init();
        LifecycleUtil.addTicker(() -> {
            if (SleepUtil.isSleepTime()) SkipUtil.tryToSkip();
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        PlayerUtil.saveAlwaysVotingPlayerList();
        LifecycleUtil.destroy();
    }

    public static void reloadConfigValues() {
        instance.reloadConfig();

        if (!NightSkipper.getFeatureEnabled("skip.night") && !NightSkipper.getFeatureEnabled("skip.thunderstorm"))
            LoggerUtil.err("Since you have disabled `feature.skip.night` and `feature.skip.thunderstorm` the plugin cannot function properly, we recommend that you consider removing this plugin, or enabling one of these features in `config.yml`!");

        reloadGlobalVariables();
    }

    public static void resetConfigValues() {
        instance.saveResource("config.yml", true);
        reloadConfigValues();
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

    public static boolean getFeatureEnabled(String feature) {
        return instance.getConfig().getBoolean("feature." + feature);
    }

    public static HashMap<String, String> getGlobalVars() {
        return globalVars;
    }

    private static void reloadGlobalVariables() {
        globalVars.clear();

        for (String key: instance.getConfig().getConfigurationSection("text").getKeys(false))
            globalVars.put("text." + key, instance.getConfig().getString("text." + key));
    }
}
