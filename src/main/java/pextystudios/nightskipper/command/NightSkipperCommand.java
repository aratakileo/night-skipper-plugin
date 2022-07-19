package pextystudios.nightskipper.command;

import com.google.common.collect.Lists;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import pextystudios.nightskipper.NightSkipper;
import pextystudios.nightskipper.util.PlayerUtil;
import pextystudios.nightskipper.util.SkipUtil;
import pextystudios.nightskipper.util.SleepUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class NightSkipperCommand extends AbstractCommand {
    public NightSkipperCommand() {
        super("nightskipper");
    }

    private static ArrayList<String> getConfigKeys() {
        String[] ignore = {
                "feature",
                "feature.skip",
                "feature.command",
                "feature.exclude",
                "feature.worlds-list",
                "feature.animation-frame",
                "text",
                "condition",
                "condition.sleep",
                "condition.vote"
        };

        ArrayList<String> keys = new ArrayList<>();

        for (String key: NightSkipper.getInstance().getConfig().getKeys(true))
            if (!ArrayUtils.contains(ignore, key))
                keys.add(key);

        int size = NightSkipper.getInstance().getConfig().getList("feature.worlds-list.worlds").size();
        for (int i = 0; i <= size; i++)
            keys.add("feature.worlds-list.worlds[" + i + ']');

        return keys;
    }

    private static String getValueType(@Nullable String value) {
        if (Pattern.compile("^true|false|yes|no$").matcher(value).find()) return "bool";
        if (Pattern.compile("^\\d+$").matcher(value).find()) return "int";
        if (Pattern.compile("^blacklist|whitelist$").matcher(value).find()) return "pass";
        if (Pattern.compile("^<=|>=|==|!=|<|>$").matcher(value).find()) return "op";

        return "str";
    }

    private static List<String> getWorldsList() {
        List<String> stringList = NightSkipper.getInstance().getConfig().getStringList("feature.worlds-list.worlds");
        stringList.add(stringList.get(0));
        return stringList;
    }

    private static int getWorldsListIndex(String key) {
        return (int)Double.parseDouble(key.substring("feature.worlds-list.worlds".length() + 1, key.length() - 1));
    }

    private static String getWorldsListValue(String key) {
        return getWorldsList().get(getWorldsListIndex(key));
    }

    @Override
    public void execute(CommandSender commandSender, String label, String[] args) {
        HashMap<String, String> formatVars = new HashMap<>(NightSkipper.getGlobalVars());

        formatVars.put("sender", commandSender.getName());
        formatVars.put("label", label);
        formatVars.put("prefix", commandSender.getName().equals("CONSOLE") ? "" : "/");
        formatVars.put("target", SleepUtil.getTarget());

        if (commandSender.hasPermission("nightskipper.admin"))
            if (args.length > 0) {
                if (args.length == 1 && args[0].equals("skip")) {
                    if (SleepUtil.isNightSkipActive()) {
                        commandSender.sendMessage(NightSkipper.getText("already-in-progress", formatVars));
                        return;
                    }

                    if (SleepUtil.isSleepTime()) {
                        SkipUtil.skipCmdInserted();
                        SleepUtil.skipNight(() -> commandSender.sendMessage(NightSkipper.getText("finished", formatVars)));

                        commandSender.sendMessage(NightSkipper.getText("in-progress", formatVars));
                        return;
                    }

                    commandSender.sendMessage(NightSkipper.getText("cannot-skip", formatVars));
                    return;
                }

                if (args[0].equals("config")) {
                    if (args.length == 2)
                        switch (args[1]) {
                            case "reset":
                                NightSkipper.resetConfigValues();
                                commandSender.sendMessage(NightSkipper.getText("config-reseted", formatVars));
                                return;
                            case "reload":
                                NightSkipper.reloadConfigValues();
                                commandSender.sendMessage(NightSkipper.getText("config-reloaded", formatVars));
                                return;
                            case "value":
                                commandSender.sendMessage("Config keys:\n" + StringUtils.join(getConfigKeys(), "\n"));
                                return;
                        }

                    if (args.length >= 3 && args[1].equals("value")) {
                        if (!getConfigKeys().contains(args[2])) {
                            formatVars.put("key", args[2]);
                            commandSender.sendMessage(NightSkipper.getText("invalid-config-key", formatVars));
                            return;
                        }

                        if (args.length == 3) {
                            String value;
                            if (StringUtils.startsWith(args[2], "feature.worlds-list.worlds[")) {
                                value = getWorldsListValue(args[2]);

                                if (args[2].equals("feature.worlds-list.worlds[" + (getWorldsList().size() - 1) + "]")) {
                                    commandSender.sendMessage(ChatColor.RED + "This value does not exist!");
                                    return;
                                }
                            }
                            else if (StringUtils.startsWith(args[2], "feature.worlds-list.worlds"))
                                value = NightSkipper.getInstance().getConfig().getStringList(args[2]).toString();
                            else value = NightSkipper.getInstance().getConfig().getString(args[2]);

                            commandSender.sendMessage(args[2] + ": " + value.replace("\n", "\\n"));
                            return;
                        } else {
                            String newValue = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                            String oldValue;

                            if (args[2].startsWith("feature.worlds-list.worlds[")) {
                                List<String> stringList = NightSkipper.getInstance().getConfig().getStringList("feature.worlds-list.worlds");
                                int index = getWorldsListIndex(args[2]);
                                stringList.set(index, newValue);
                                oldValue = getWorldsListValue(args[2]);
                            } else {
                                oldValue = NightSkipper.getInstance().getConfig().getString(args[2]);

                                if (Pattern.compile("^true|false$").matcher(newValue).find())
                                    NightSkipper.getInstance().getConfig().set(args[2], newValue.equals("true"));
                                else if (Pattern.compile("^\\d+$").matcher(newValue).find())
                                    NightSkipper.getInstance().getConfig().set(args[2], (int)Double.parseDouble(newValue));
                                else
                                    NightSkipper.getInstance().getConfig().set(args[2], newValue.replace("\\\n", "\n"));
                            }

                            NightSkipper.getInstance().saveConfig();

                            commandSender.sendMessage(ChatColor.GREEN + args[2] + ": " + oldValue.replace("\n", "\\\n") + " -> " + newValue);
                            return;
                        }
                    }
                }
            }

        if (args.length == 2 && args[0].equals("vote")) {
            if (!PlayerUtil.getPlayerNames(true).contains(commandSender.getName())) {
                commandSender.sendMessage(NightSkipper.getText("cannot-vote", formatVars));
                return;
            }

            switch (args[1]) {
                case "always":
                    if (!NightSkipper.getFeatureEnabled("command.always-vote")) {
                        commandSender.sendMessage(NightSkipper.getText("feature-disabled", formatVars));
                        return;
                    }

                    if (!PlayerUtil.hasAlwaysVotingPlayer(commandSender.getName())) {
                        commandSender.sendMessage(NightSkipper.getText("always-vote-enabled", formatVars));
                        PlayerUtil.addAlwaysVotingPlayer(commandSender.getName());
                    } else {
                        commandSender.sendMessage(NightSkipper.getText("always-vote-disabled", formatVars));
                        PlayerUtil.removeAlwaysVotingPlayer(commandSender.getName());
                    }

                    if (SleepUtil.isSleepTime())
                        SkipUtil.tryToSkip();

                    return;
                case "now":
                    if (!NightSkipper.getFeatureEnabled("command.now-vote")) {
                        commandSender.sendMessage(NightSkipper.getText("feature-disabled", formatVars));
                        return;
                    }

                    if (!SleepUtil.isSleepTime()) {
                        commandSender.sendMessage(NightSkipper.getText("cannot-skip", formatVars));
                        return;
                    }

                    if (NightSkipper.getFeatureEnabled("command.always-vote") && PlayerUtil.hasAlwaysVotingPlayer(commandSender.getName()) || PlayerUtil.hasCmdVotingPlayer(commandSender.getName())) {
                        commandSender.sendMessage(NightSkipper.getText("already-voted", formatVars));
                        return;
                    }

                    SkipUtil.tryToSkip();

                    PlayerUtil.addCmdVotingPlayer(commandSender.getName());
                    commandSender.sendMessage(NightSkipper.getText("vote-taken", formatVars));
                    return;
            }
        }

        if (args.length > 0) {
            commandSender.sendMessage(NightSkipper.getText("invalid-format", formatVars));
            return;
        }

        commandSender.sendMessage(NightSkipper.getText("usage", formatVars));
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> completeList = Lists.newArrayList();

        if (args.length == 1) {
            if (sender.hasPermission("nightskipper.admin"))
                completeList.addAll(Lists.newArrayList("skip", "config"));

            if (NightSkipper.getFeatureEnabled("command.always-vote") || NightSkipper.getFeatureEnabled("command.now-vote") && PlayerUtil.getPlayerNames(true).contains(sender.getName()))
                completeList.add("vote");

            return completeList;
        }

        if (args.length == 2) {
            switch (args[0]) {
                case "config":
                    if (sender.hasPermission("nightskipper.admin"))
                        completeList.addAll(Lists.newArrayList("reset", "reload", "value"));
                    break;
                case "vote":
                    if (NightSkipper.getFeatureEnabled("command.now-vote") && (!NightSkipper.getFeatureEnabled("command.always-vote") || !PlayerUtil.hasAlwaysVotingPlayer(sender.getName())))
                        completeList.add("now");

                    if (NightSkipper.getFeatureEnabled("command.always-vote"))
                        completeList.add("always");

                    break;
            }

            return completeList;
        }

        if (args.length >= 3 && args[0].equals("config") && args[1].equals("value")) {
            if (args.length == 3)
                completeList.addAll(getConfigKeys());

            if (!getConfigKeys().contains(args[2])) return completeList;
            String value;
            if (StringUtils.startsWith(args[2], "feature.worlds-list.worlds[")) {
                value = getWorldsListValue(args[2]);

                completeList.addAll(getWorldsList());
            } else if (StringUtils.startsWith(args[2], "feature.worlds-list.worlds"))
                value = "";
            else value = NightSkipper.getInstance().getConfig().getString(args[2]);

            completeList.add(value);

            switch (getValueType(value)) {
                case "str":
                    if (Pattern.compile("condition\\.(?:vote|sleep)\\.[rl]value").matcher(args[2]).find()) {
                        if (args.length == 4) {
                            if (Pattern.compile("^\\d+%?$").matcher(args[3]).find()) {
                                completeList.add(args[3] + " ");

                                if (args[3].charAt(args[3].length() - 1) != '%')
                                    completeList.add(args[3] + '%');
                            } else for (int i = 0; i <= 9; i++) completeList.add(String.valueOf(i));

                            completeList.add("voted");
                            completeList.add("sleeping");
                            break;
                        }

                        completeList.clear();
                        break;
                    }

                    if (StringUtils.startsWith(args[2], "feature.worlds-list.worlds[")) {
                        if (args.length > 4)
                            completeList.clear();

                        break;
                    } else if (StringUtils.startsWith(args[2], "feature.worlds-list.worlds")) {
                        completeList.clear();
                        break;
                    }

                    if (args.length >= 4 && args[args.length - 1].length() > 0)
                        completeList.add(args[args.length - 1] + " ");

                    break;
                case "bool":
                    if (args.length == 4) {
                        completeList.add("true");
                        completeList.add("false");
                    }
                    break;
                case "int":
                    if (args.length == 4) {
                        if (Pattern.compile("^\\d+$").matcher(args[3]).find())
                            completeList.add(args[3] + " ");

                    }
                    break;
                case "pass":
                    if (args.length == 4) {
                        completeList.add("whitelist");
                        completeList.add("blacklist");
                    }
                    break;
                case "op":
                    if (args.length == 4) {
                        completeList.add(">");
                        completeList.add("<");
                        completeList.add(">=");
                        completeList.add("<=");
                        completeList.add("!=");
                        completeList.add("==");
                    }
                    break;
            }
        }

        return completeList;
    }
}
