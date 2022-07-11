package pextystudios.nightskipper.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RawTextUtil {
    public static @NotNull String getTextsJson(@NotNull String @NotNull ... textJsons) {
        String textsJson = "[";

        int i = 0;
        for (String textJson: textJsons) {
            textsJson = textsJson.concat(textJson);
            if (i != textJsons.length - 1) textsJson += ",";
            i++;
        }

        return textsJson + "]";
    }

    public static @NotNull String getTextJson(@NotNull String string) {
        return getTextJson(string, null, null);
    }

    public static @NotNull String getTextJson(@NotNull String string, @NotNull String clickEventJson) {
        return getTextJson(string, clickEventJson, null);
    }

    public static @NotNull String getTextJson(@NotNull String string, @Nullable String clickEventJson, @Nullable String hoverEventJson) {
        String textJson = "{\"text\":\"" + string + "\"";

        if (clickEventJson != null)
            textJson += ",\"clickEvent\":" + clickEventJson;

        if (hoverEventJson != null)
            textJson += ",\"hoverEvent\":" + hoverEventJson;

        return textJson + "}";
    }

    public static @NotNull String getExtraTextJson(@NotNull String extra) {
        return getExtraTextJson(extra, null);
    }

    public static @NotNull String getExtraTextJson(@NotNull String extra, @Nullable String text) {
        return "{\"text\":\"" + (text == null ? "" : text) + "\",\"extra\":[" + getTextJson(extra) + "]}";
    }

    public static @NotNull String getOpenUrlActionJson(@NotNull String url) {
        return getActionJson("open_url", url, false);
    }

    public static @NotNull String getCommandActionJson(@NotNull String command) {
        return getActionJson("run_command", "/" + command, false);
    }

    public static @NotNull String getShowTextActionJson(@NotNull String textJson) {
        return getActionJson("show_text", textJson, true);
    }

    public static @NotNull String getActionJson(@NotNull String action, @NotNull String value) {
        return getActionJson(action, value, false);
    }

    public static @NotNull String getActionJson(@NotNull String action, @NotNull String value, boolean isJsonValue) {
        String textJson = "{\"action\":\"" + action + "\",\"value\":";

        textJson += isJsonValue ? value : "\"" + value + "\"";

        return textJson + "}";
    }

    public static @NotNull Component compile(@NotNull String textJson) {
        return GsonComponentSerializer.gson().deserialize(textJson);
    }
}
