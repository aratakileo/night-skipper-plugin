package pextystudios.nightskipper.command;

import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pextystudios.nightskipper.*;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {
    public static String command_name;

    public AbstractCommand(String _command_name) {
        command_name = _command_name;
        PluginCommand pluginCommand = NightSkipper.getInstance().getCommand(command_name);

        if (pluginCommand != null) pluginCommand.setExecutor(this);
    }

    public abstract void execute(CommandSender commandSender, String label, String[] args);

    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        execute(sender, label, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return filter(complete(sender, args), args);
    }

    private List<String> filter(List<String> list, String[] args) {
        if (list == null) return null;

        String last = args[args.length - 1];
        List<String> result = new ArrayList<>();

        for (String arg : list) {
            if (arg.toLowerCase().startsWith(last.toLowerCase())) result.add(arg);
        }

        return result;
    }
}
