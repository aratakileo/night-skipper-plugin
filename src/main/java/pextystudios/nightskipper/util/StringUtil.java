package pextystudios.nightskipper.util;

import org.apache.commons.lang.ArrayUtils;

public final class StringUtil {
    public static String[] saveSplit(String string, String separator, int minLength) {
        String[] splitted = string.split(separator);

        while (splitted.length < minLength)
            splitted = (String[]) ArrayUtils.add(splitted, "");

        return splitted;
    }
}
