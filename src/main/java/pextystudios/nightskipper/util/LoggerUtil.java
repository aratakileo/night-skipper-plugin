package pextystudios.nightskipper.util;

import pextystudios.nightskipper.NightSkipper;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class LoggerUtil {
    public static Logger getLogger() {
        return getLogger(false);
    }

    public static Logger getLogger(boolean global) {
        return Logger.getLogger(global ? "Minecraft" : NightSkipper.getInstance().getName());
    }

    public static void log(Level level, String text) {
        log(level, text, false);
    }

    public static void log(Level level, String text, boolean global) {
        getLogger(global).log(level, text);
    }

    public static void log(Level level, int i) {
        log(level, i, false);
    }

    public static void log(Level level, int i, boolean global) {
        getLogger(global).log(level, String.valueOf(i));
    }

    public static void log(Level level, long l) {
        log(level, l, false);
    }

    public static void log(Level level, long l, boolean global) {
        getLogger(global).log(level, String.valueOf(l));
    }

    public static void log(Level level, boolean b) {
        log(level, b, false);
    }

    public static void log(Level level, boolean b, boolean global) {
        getLogger(global).log(level, String.valueOf(b));
    }

    public static void log(Level level, char c) {
        log(level, c, false);
    }

    public static void log(Level level, char c, boolean global) {
        getLogger(global).log(level, String.valueOf(c));
    }

    public static void log(Level level, float f) {
        log(level, f, false);
    }

    public static void log(Level level, float f, boolean global) {
        getLogger(global).log(level, String.valueOf(f));
    }

    public static void log(Level level, double d) {
        log(level, d, false);
    }

    public static void log(Level level, double d, boolean global) {
        getLogger(global).log(level, String.valueOf(d));
    }

    public static void log(Level level, Object o) {
        log(level, o, false);
    }

    public static void log(Level level, Object o, boolean global) {
        getLogger(global).log(level, o == null ? "null" : o.toString());
    }

    public static <T> void warn(T t) {
        log(Level.WARNING, t);
    }

    public static <T> void warn(T t, boolean global) {
        log(Level.WARNING, t, global);
    }

    public static <T> void err(T t) {
        log(Level.SEVERE, t);
    }

    public static <T> void err(T t, boolean global) {
        log(Level.SEVERE, t, global);
    }

    public static <T> void info(T t) {
        log(Level.INFO, t);
    }

    public static <T> void info(T t, boolean global) {
        log(Level.INFO, t, global);
    }
}
