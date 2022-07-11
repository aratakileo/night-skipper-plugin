package pextystudios.nightskipper.util;

import pextystudios.nightskipper.NightSkipper;

public final class SleepUtil {
    public interface SkipFinishingInterface {
        void onFinish();
    }
    private static SkipFinishingInterface skipFinishingInterface = null;

    private static int taskID = -1;

    private static String target = "nothing";

    public static final long maxTime = 24000;
    public static final long wakeupTime = 23475;
    public static final long timeToSleep = 12545;
    private static boolean useThunderstormStep = false;

    private static long getTimeStepSkip() {
        return useThunderstormStep ? NightSkipper.getInstance().getConfig().getLong("feature.animation-frame.thunderstorm-amplitude") : NightSkipper.getInstance().getConfig().getLong("feature.animation-frame.night-amplitude");
    }

    public static boolean isSleepTime() {
        long currentTime = NightSkipper.getCurrentWorld().getTime();
        return currentTime >= timeToSleep && currentTime < wakeupTime && NightSkipper.getFeatureEnabled("skip.night") || WeatherUtil.isThunderstorm() && NightSkipper.getFeatureEnabled("skip.thunderstorm");
    }
    public static boolean isNightSkipActive() {return taskID != -1;}

    public static void reloadTarget() {
        target = NightSkipper.getCurrentWorld().isThundering() ? NightSkipper.getText("thunderstorm") : NightSkipper.getText("night");
        target = NightSkipper.getCurrentWorld().getTime() >= timeToSleep && NightSkipper.getCurrentWorld().isThundering() ? target + " " + NightSkipper.getText("and") + " " + NightSkipper.getText("night") : target;
    }

    public static String getTarget() {
        return target;
    }

    public static void skipNight(SkipFinishingInterface _skipFinishingInterface) {
        if (_skipFinishingInterface != null) skipFinishingInterface = _skipFinishingInterface;

        if (!NightSkipper.getFeatureEnabled("animation-frame.enabled") && isSleepTime()) NightSkipper.getCurrentWorld().setTime(wakeupTime);

        if (NightSkipper.getCurrentWorld().getTime() >= (wakeupTime - getTimeStepSkip() * 2) && isNightSkipActive() && (
                WeatherUtil.isThunderstorm() && NightSkipper.getFeatureEnabled("skip.thunderstorm")
                        ||
                        WeatherUtil.isRain() && NightSkipper.getFeatureEnabled("clear-rain")
        )
        )
            WeatherUtil.clear();

        if (!NightSkipper.getFeatureEnabled("animation-frame.enabled")) {
            skipFinishingInterface.onFinish();
            return;
        }

        if (!isSleepTime()) {
            if (skipFinishingInterface != null) skipFinishingInterface.onFinish();
            skipFinishingInterface = null;
            taskID = -1;

            if (NightSkipper.getFeatureEnabled("reset-phantom-statistic"))
                PlayerUtil.resetPhantomStatistic();

            return;
        }

        if (!isNightSkipActive()) {
            useThunderstormStep = WeatherUtil.isThunderstorm() && NightSkipper.getCurrentWorld().getTime() < maxTime / 3;
            reloadTarget();
        }

        taskID = NightSkipper.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(NightSkipper.getInstance(), (() -> {
            NightSkipper.getCurrentWorld().setTime(NightSkipper.getCurrentWorld().getTime() + getTimeStepSkip());

            skipNight(null);
        }), NightSkipper.getInstance().getConfig().getLong("feature.animation-frame.frequency"));
    }

    public static void cancelSkipNight() {
        if (isNightSkipActive()) {
            NightSkipper.getInstance().getServer().getScheduler().cancelTask(taskID);
            taskID = -1;

            if (!isSleepTime() && skipFinishingInterface != null)
                skipFinishingInterface.onFinish();

            skipFinishingInterface = null;
        }
    }
}
