package pextystudios.nightskipper.util;

import pextystudios.nightskipper.NightSkipper;

public final class WeatherUtil {
    public static boolean isRain() {
        return NightSkipper.getCurrentWorld().hasStorm() && !NightSkipper.getCurrentWorld().isThundering();
    }

    public static boolean hasRain() {
        return NightSkipper.getCurrentWorld().hasStorm();
    }

    public static boolean isThunderstorm() {
        return NightSkipper.getCurrentWorld().hasStorm() && NightSkipper.getCurrentWorld().isThundering();
    }

    public static void clear() {
        NightSkipper.getCurrentWorld().setStorm(false);
    }
}
