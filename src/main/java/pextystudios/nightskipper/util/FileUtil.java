package pextystudios.nightskipper.util;

import pextystudios.nightskipper.NightSkipper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public final class FileUtil {
    public static String getLocalAbsolutePath() {
        return NightSkipper.getInstance().getDataFolder().getAbsolutePath();
    }

    private static String getPath(String path, boolean isInGlobalSpace) {
        return (isInGlobalSpace ? "" : getLocalAbsolutePath()) + "/" + path.replace("\\", "/");
    }

    public static boolean writeFile(String path, String text) {
        return writeFile(path, text, false);
    }

    public static boolean writeFile(String path, String text, boolean isInGlobalSpace) {
        try {
            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(getPath(path, isInGlobalSpace)), StandardCharsets.UTF_8);
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

    public static boolean isFile(String path, boolean isInGlobalSpace) {
        try {
            (new FileReader(getPath(path, isInGlobalSpace))).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String readFile(String path) {
        return readFile(path, true, false);
    }

    public static String readFile(String path, boolean safeRead) {
        return readFile(path, safeRead, false);
    }

    public static String readFile(String path, boolean safeRead, boolean isInGlobalSpace) {
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(getPath(path, isInGlobalSpace)), StandardCharsets.UTF_8);
            String text = bufferedReader.lines().collect(Collectors.joining("\n"));
            bufferedReader.close();

            return text;
        } catch (Exception e) {
            if (safeRead) {
                writeFile(getPath(path, isInGlobalSpace), "", isInGlobalSpace);
                return "";
            }

            return null;
        }
    }
}
