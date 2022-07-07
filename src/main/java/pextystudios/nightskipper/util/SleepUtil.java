package pextystudios.nightskipper.util;

import pextystudios.nightskipper.NightSkipper;

public final class SleepUtil {
    public interface SkipFinishingInterface {
        void onFinish();
    }
    private static SkipFinishingInterface skipFinishingInterface = null;

    private static int taskID = -1;

    public static final long maxTime = 24000;
    public static final long wakeupTime = 23475;
    public static final long timeToSleep = 12545;
    private static boolean useThunderstormStep = false;

    private static long getTimeStepSkip() {
        return useThunderstormStep ? NightSkipper.getInstance().getConfig().getLong("value.thunderstorm-skip-step") : NightSkipper.getInstance().getConfig().getLong("value.skip-step");
    }

    public static boolean isSleepTime() {
        long currentTime = NightSkipper.getCurrentWorld().getTime();
        return currentTime >= timeToSleep && currentTime < wakeupTime || WeatherUtil.isThunderstorm();
    }
    public static boolean isNightSkipActive() {return taskID != -1;}

    public static void skipNight(SkipFinishingInterface _skipFinishingInterface) {
        if (NightSkipper.getCurrentWorld().getTime() >= (wakeupTime - getTimeStepSkip() * 2) && WeatherUtil.hasRain() && isNightSkipActive())
            WeatherUtil.clear();

        if (_skipFinishingInterface != null) skipFinishingInterface = _skipFinishingInterface;

        if (!isSleepTime()) {
            if (skipFinishingInterface != null) skipFinishingInterface.onFinish();
            skipFinishingInterface = null;

            taskID = -1;

            return;
        }
        if (!isNightSkipActive())
            useThunderstormStep = WeatherUtil.isThunderstorm() && NightSkipper.getCurrentWorld().getTime() < maxTime / 3;

        taskID = NightSkipper.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(NightSkipper.getInstance(), (() -> {
            NightSkipper.getCurrentWorld().setTime(NightSkipper.getCurrentWorld().getTime() + getTimeStepSkip());

            skipNight(null);
        }), 1);
    }

    public static void cancelSkipNight() {
        if (isNightSkipActive()) {
            NightSkipper.getInstance().getServer().getScheduler().cancelTask(taskID);
            taskID = -1;

            LoggerUtil.err(NightSkipper.getCurrentWorld().getTime());
            if (!isSleepTime() && skipFinishingInterface != null)
                skipFinishingInterface.onFinish();

            skipFinishingInterface = null;
        }
    }
}
