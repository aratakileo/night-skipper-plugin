package pextystudios.nightskipper.util;

import org.bukkit.configuration.file.FileConfiguration;
import pextystudios.nightskipper.ConditionEngine;
import pextystudios.nightskipper.NightSkipper;

import java.util.HashMap;

public final class SkipUtil {
    private static final ConditionEngine conditionEngine = new ConditionEngine();
    private static boolean inited = false;

    private static void init() {
        if (!inited) {
            inited = true;

            conditionEngine.addGetter("voted", PlayerUtil::votePlayerCount);
        }
    }

    public static void tryToSkip() {
        init();

        FileConfiguration config = NightSkipper.getInstance().getConfig();

        if (!SleepUtil.isSleepTime()) return;
        HashMap<String, String> formatVars = new HashMap<>();

        String target = NightSkipper.getCurrentWorld().isThundering() ? NightSkipper.getText("thunderstorm") : NightSkipper.getText("night");
        target = NightSkipper.getCurrentWorld().getTime() >= SleepUtil.timeToSleep && NightSkipper.getCurrentWorld().isThundering() ? target + " " + NightSkipper.getText("and") + " " + NightSkipper.getText("night") : target;

        formatVars.put("target", target);
        formatVars.put("players", String.valueOf(PlayerUtil.getPlayerCount(true)));
        formatVars.put("voted", String.valueOf(PlayerUtil.votePlayerCount()));
        formatVars.put("sleeping", String.valueOf(PlayerUtil.lyingPlayerCount()));

        if (PlayerUtil.lyingPlayerCount() == 0) {
            NotificationUtil.send(NightSkipper.getText("voted-layed-now", formatVars));
            SleepUtil.cancelSkipNight();
            return;
        }

        if (!conditionEngine.exec(config.getString("condition.lvalue"), config.getString("condition.rvalue"), config.getString("condition.op"))) {
            NotificationUtil.send(NightSkipper.getText("voted-now", formatVars));
            SleepUtil.cancelSkipNight();
            return;
        }

        NotificationUtil.send(NightSkipper.getText("goodnight", formatVars));

        SleepUtil.skipNight(() -> {
            NotificationUtil.send(NightSkipper.getText("finished", formatVars), 5000);
            PlayerUtil.removeAllCmdPlayer();
        });
    }
}
