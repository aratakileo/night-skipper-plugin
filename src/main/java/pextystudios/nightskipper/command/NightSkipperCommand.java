package pextystudios.nightskipper.command;

import com.google.common.collect.Lists;
import org.bukkit.command.CommandSender;
import pextystudios.nightskipper.NightSkipper;
import pextystudios.nightskipper.util.PlayerUtil;
import pextystudios.nightskipper.util.SkipUtil;
import pextystudios.nightskipper.util.SleepUtil;

import java.util.HashMap;
import java.util.List;

public class NightSkipperCommand extends AbstractCommand {
    public NightSkipperCommand() {
        super("nightskipper");
    }

    @Override
    public void execute(CommandSender commandSender, String label, String[] args) {
        HashMap<String, String> formatVars = new HashMap<>();
        formatVars.put("sender", commandSender.getName());
        formatVars.put("label", label);
        formatVars.put("prefix", commandSender.getName().equals("CONSOLE") ? "" : "/");

        if (commandSender.hasPermission("nightskipper.admin"))
            if (args.length > 0) {
                if (args.length == 1 && args[0].equals("skip")) {
                    String target = NightSkipper.getCurrentWorld().isThundering() ? NightSkipper.getText("thunderstorm") : NightSkipper.getText("night");
                    target = NightSkipper.getCurrentWorld().getTime() >= SleepUtil.timeToSleep && NightSkipper.getCurrentWorld().isThundering() ? target + " " + NightSkipper.getText("and") + " " + NightSkipper.getText("night") : target;

                    formatVars.put("target", target);

                    if (SleepUtil.isNightSkipActive()) {
                        commandSender.sendMessage(NightSkipper.getText("already-in-progress", formatVars));
                        return;
                    }

                    if (SleepUtil.isSleepTime()) {
                        SleepUtil.skipNight(() -> {
                            commandSender.sendMessage(NightSkipper.getText("finished", formatVars));
                        });

                        commandSender.sendMessage(NightSkipper.getText("in-progress", formatVars));
                        return;
                    }

                    commandSender.sendMessage(NightSkipper.getText("cannot-skip", formatVars));
                    return;
                }

                if (args[0].equals("config") && args.length == 2)
                    switch (args[1]) {
                        case "reset":
                            NightSkipper.resetConfigValues();
                            commandSender.sendMessage(NightSkipper.getText("config-reseted", formatVars));
                            return;
                        case "reload":
                            NightSkipper.reloadConfigValues();
                            commandSender.sendMessage(NightSkipper.getText("config-reloaded", formatVars));
                            return;
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
                        completeList.addAll(Lists.newArrayList("reset", "reload"));
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

        return completeList;
    }
}
