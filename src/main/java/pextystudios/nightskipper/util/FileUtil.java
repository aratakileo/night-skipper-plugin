package pextystudios.nightskipper.util;

import pextystudios.nightskipper.NightSkipper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public final class FileUtil {
    public static String getLocalAbsolutePath() {
        return NightSkipper.getInstance().getDataFolder().getAbsolutePath();
    }

    private static String getPath(String path, boolean globalSpace) {
        return (globalSpace ? "" : getLocalAbsolutePath()) + "/" + path.replace("\\", "/");
    }

    public static boolean writeFile(String path, String text) {
        return writeFile(path, text, false);
    }

    public static boolean writeFile(String path, String text, boolean globalSpace) {
        try {
            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(getPath(path, globalSpace)), StandardCharsets.UTF_8);
            bufferedWriter.write(text);
            bufferedWriter.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFile(String path) {
        return isFile(path, false);
    }

    public static boolean isFile(String path, boolean globalSpace) {
        try {
            (new FileReader(getPath(path, globalSpace))).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String readFile(String path) {
        return readFile(path, false, false);
    }

    public static String readFile(String path, boolean saveReading) {
        return readFile(path, saveReading, false);
    }

    public static String readFile(String path, boolean saveReading, boolean globalSpace) {
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(getPath(path, globalSpace)), StandardCharsets.UTF_8);
            String text = bufferedReader.lines().collect(Collectors.joining("\n"));
            bufferedReader.close();

            return text;
        } catch (Exception e) {
            if (saveReading) {
                writeFile(getPath(path, globalSpace), "", globalSpace);
                return "";
            }

            return null;
        }
    }
}
