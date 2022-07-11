package pextystudios.nightskipper.util;

import pextystudios.nightskipper.NightSkipper;

import java.util.ArrayList;

public final class LifecycleUtil {
    public interface TickerInterface {
        void tick();
    }

    private static int scheduleSyncDelayedTaskID = -1;
    private static final ArrayList<TickerInterface> tickersQueue = new ArrayList<>();

    private static void tick() {
        for (TickerInterface tickerInterface : tickersQueue)
            tickerInterface.tick();

        scheduleSyncDelayedTaskID = NightSkipper.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(NightSkipper.getInstance(), LifecycleUtil::tick, 1);
    }

    public static void init() {
        if (scheduleSyncDelayedTaskID == -1) tick();
    }

    public static void addTicker(TickerInterface tickerInterface) {
        tickersQueue.add(tickerInterface);
    }

    public static void destroy() {
        NightSkipper.getInstance().getServer().getScheduler().cancelTask(scheduleSyncDelayedTaskID);
        tickersQueue.clear();
    }
}
