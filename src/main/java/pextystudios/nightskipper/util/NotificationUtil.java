package pextystudios.nightskipper.util;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pextystudios.nightskipper.NightSkipper;

import java.util.Date;

public final class NotificationUtil {
    private static int taskID = -1;
    private static long finalTime = -1;

    public static void clear() {
        if (taskID != -1) NightSkipper.getInstance().getServer().getScheduler().cancelTask(taskID);

        finalTime = -1;
        taskID = -1;
    }

    public static void send(String string) {
        send(Component.text(string));
    }

    public static void send(Component component) {
        send(component, -1);
    }

    public static void send(String string, long duration) {
        send(Component.text(string), duration);
    }

    public static void send(Component component, long duration) {
        if (duration > 0) finalTime = new Date().getTime() + duration - 2000;

        iterationSend(component, false);
    }

    private static void iterationSend(Component component, boolean iteration) {
        final long currentTime = new Date().getTime();

        if (finalTime != -1 && currentTime >= finalTime && iteration) {
            taskID = -1;
            finalTime = -1;
            return;
        }

        if (taskID != -1) NightSkipper.getInstance().getServer().getScheduler().cancelTask(taskID);

        for (Player player : NightSkipper.getInstance().getServer().getOnlinePlayers())
            player.sendActionBar(component);

        taskID = NightSkipper.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
                NightSkipper.getInstance(),
                (() -> iterationSend(component, true)),
                1
        );
    }
}
