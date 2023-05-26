package pextystudios.nightskipper.util;

import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FormatUtil {
    private final static Pattern pattern = Pattern.compile("&([\\dA-Fa-fk-oK-OrR+]|\\+\\+|--|#[\\da-fA-F]{6})");
    private final static char PARAGRAPH_CHAR = '\u00A7';

    public static @NotNull String format(@NotNull String text) {
        return format(text, null);
    }

    public static @NotNull String format(@NotNull String text, @Nullable HashMap<String, String> formatVars) {
        final Matcher matcher = pattern.matcher(text);

        if (formatVars != null && !formatVars.isEmpty())
            for (String key: formatVars.keySet())
                if (text.contains('%' + key + '%'))
                    text = format(text.replace('%' + key + '%', formatVars.get(key)), formatVars);

        while (matcher.find()) {
            String value = matcher.group(1);

            if ("+".equals(value)) {
                String[] textPieces = text.substring(matcher.start() + 2).split(" ");
                textPieces[0] = WordUtils.capitalize(textPieces[0]);
                text = text.substring(0, matcher.start()) + String.join(" ", textPieces);
                continue;
            }

            text = text.replace("&" + value, PARAGRAPH_CHAR + value);
        }

        return text;
    }
}
