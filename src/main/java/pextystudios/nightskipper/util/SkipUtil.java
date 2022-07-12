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
            conditionEngine.addGetter("sleeping", PlayerUtil::lyingPlayerCount);
        }
    }

    public static void tryToSkip() {
        init();

        if (!SleepUtil.isSleepTime()) {
            MailingUtil.clear();
            return;
        }

        FileConfiguration config = NightSkipper.getInstance().getConfig();
        HashMap<String, String> formatVars = new HashMap<>(NightSkipper.getGlobalVars());

        formatVars.put("target", SleepUtil.getTarget());
        formatVars.put("players", String.valueOf(PlayerUtil.getPlayerCount(true)));
        formatVars.put("voted", String.valueOf(PlayerUtil.votePlayerCount()));
        formatVars.put("sleeping", String.valueOf(PlayerUtil.lyingPlayerCount()));

        if (!conditionEngine.exec(config.getString("condition.sleep.lvalue"), config.getString("condition.sleep.rvalue"), config.getString("condition.sleep.op"))) {
            NotificationUtil.send(NightSkipper.getText("voted-layed-now", formatVars));
            SleepUtil.cancelSkipNight();
            SleepUtil.reloadTarget();
            MailingUtil.tryMailToPlayers();
            return;
        }

        if (!conditionEngine.exec(config.getString("condition.vote.lvalue"), config.getString("condition.vote.rvalue"), config.getString("condition.vote.op"))) {
            NotificationUtil.send(NightSkipper.getText("voted-now", formatVars));
            SleepUtil.cancelSkipNight();
            SleepUtil.reloadTarget();
            MailingUtil.tryMailToPlayers();
            return;
        }

        NotificationUtil.send(NightSkipper.getText("goodnight", formatVars));

        if (!SleepUtil.isNightSkipActive())
            SleepUtil.skipNight(() -> {
                NotificationUtil.send(NightSkipper.getText("wakeup", formatVars), 5000);
                PlayerUtil.removeAllCmdPlayer();
                MailingUtil.clear();
            });
    }
}
