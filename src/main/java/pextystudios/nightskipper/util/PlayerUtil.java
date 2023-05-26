package pextystudios.nightskipper.util;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pextystudios.nightskipper.NightSkipper;

import java.util.Arrays;
import java.util.HashSet;

public final class PlayerUtil {
    private final static String whoAlwaysVoteFile = "who-always-vote.list";

    private static String[] alwaysVotingPlayers = null;
    private static String[] lyingPlayers = new String[]{};
    private static String[] cmdVotingPlayers = new String[]{};

    public static void saveAlwaysVotingPlayerList() {
        if (alwaysVotingPlayers == null) return;

        FileUtil.writeFile(whoAlwaysVoteFile, String.join("\n", alwaysVotingPlayers));
    }

    private static void checkDataInit() {
        if (alwaysVotingPlayers != null) return;

        alwaysVotingPlayers = FileUtil.readFile(whoAlwaysVoteFile).split("\n");

        int i = 0;
        for (String player: alwaysVotingPlayers) {
            if (player.length() == 0)
                alwaysVotingPlayers = (String[]) ArrayUtils.remove(alwaysVotingPlayers, i);

            i++;
        }
    }

    private static HashSet<String> getVotedPlayers(boolean includeOffline) {
        checkDataInit();

        HashSet<String> votedPlayers = new HashSet<>(Arrays.asList(lyingPlayers));

        if (NightSkipper.getFeatureEnabled("command.always-vote"))
            votedPlayers.addAll(Arrays.asList(alwaysVotingPlayers));

        if (NightSkipper.getFeatureEnabled("command.now-vote"))
            votedPlayers.addAll(Arrays.asList(cmdVotingPlayers));

        if (!includeOffline) {
            HashSet<String> onlinePlayers = getPlayerNames(true);

            votedPlayers.removeIf(player -> !onlinePlayers.contains(player));
        }

        return votedPlayers;
    }

    public static @NotNull HashSet<Player> getPlayers(boolean includeConfigSettings) {
        HashSet<Player> players = new HashSet<>();

        ConfigurationSection words_list = NightSkipper.getInstance().getConfig().getConfigurationSection("feature.worlds-list");

        for (World world: NightSkipper.getInstance().getServer().getWorlds()) {
            if (!includeConfigSettings) {
                players.addAll(world.getPlayers());
                continue;
            }

            if (words_list.getList("worlds").contains(world.getKey().asString()))
                if (words_list.getString("mode").equals("blacklist")) continue;
            else if (words_list.getString("mode").equals("whitelist")) continue;

            players.addAll(world.getPlayers());
        }

        if (!includeConfigSettings) return players;

        for (Player player: NightSkipper.getCurrentWorld().getPlayers()) {
            if (player.getGameMode() == GameMode.ADVENTURE && NightSkipper.getFeatureEnabled("exclude.adventure"))
                players.remove(player);

            if (player.getGameMode() == GameMode.CREATIVE && NightSkipper.getFeatureEnabled("exclude.creative"))
                players.remove(player);

            if (player.getGameMode() == GameMode.SPECTATOR && NightSkipper.getFeatureEnabled("exclude.spectator"))
                players.remove(player);

            if (player.isInvisible() && NightSkipper.getFeatureEnabled("exclude.vanished"))
                players.remove(player);
        }

        return players;
    }

    public static HashSet<String> getPlayerNames(boolean includeConfigSettings) {
        HashSet<String> players = new HashSet<>();

        for (Player player: getPlayers(true))
            players.add(player.getName());

        return players;
    }

    public static boolean hasAlwaysVotingPlayer(String playerNickname) {
        checkDataInit();
        return ArrayUtils.contains(alwaysVotingPlayers, playerNickname);
    }

    public static boolean hasLyingPlayer(String playerNickname) {
        return ArrayUtils.contains(lyingPlayers, playerNickname);
    }

    public static boolean hasCmdVotingPlayer(String playerNickname) {
        return ArrayUtils.contains(cmdVotingPlayers, playerNickname);
    }

    public static boolean hasVotedPlayer(String playerNickname) {
        return getVotedPlayers(false).contains(playerNickname);
    }

    public static void addAlwaysVotingPlayer(String playerNickname) {
        if (hasAlwaysVotingPlayer(playerNickname)) return;

        alwaysVotingPlayers = (String[]) ArrayUtils.add(alwaysVotingPlayers, playerNickname);

        saveAlwaysVotingPlayerList();
    }

    public static void addLyingPlayer(String playerNickname) {
        if (hasLyingPlayer(playerNickname)) return;

        lyingPlayers = (String[]) ArrayUtils.add(lyingPlayers, playerNickname);
    }

    public static void addCmdVotingPlayer(String playerNickname) {
        if (hasCmdVotingPlayer(playerNickname)) return;

        cmdVotingPlayers = (String[]) ArrayUtils.add(cmdVotingPlayers, playerNickname);
    }

    public static void removeAlwaysVotingPlayer(String playerNickname) {
        if (!hasAlwaysVotingPlayer(playerNickname)) return;

        alwaysVotingPlayers = (String[]) ArrayUtils.remove(alwaysVotingPlayers, ArrayUtils.indexOf(alwaysVotingPlayers, playerNickname));
        saveAlwaysVotingPlayerList();
    }

    public static void removeLyingPlayer(String playerNickname) {
        if (!hasLyingPlayer(playerNickname)) return;

        lyingPlayers = (String[]) ArrayUtils.remove(lyingPlayers, ArrayUtils.indexOf(lyingPlayers, playerNickname));
    }

    public static void removeCmdVotingPlayer(String playerNickname) {
        if (!hasCmdVotingPlayer(playerNickname)) return;

        cmdVotingPlayers = (String[]) ArrayUtils.remove(cmdVotingPlayers, ArrayUtils.indexOf(cmdVotingPlayers, playerNickname));
    }

    public static void removeAllCmdPlayer() {
        cmdVotingPlayers = new String[]{};
    }

    public static int lyingPlayerCount() {
        return lyingPlayers.length;
    }

    public static int votePlayerCount() {
        return votePlayerCount(false);
    }

    public static int votePlayerCount(boolean includeOffline) {
        return getVotedPlayers(includeOffline).size();
    }

    public static int getPlayerCount(boolean includeConfigSettings) {
        return getPlayers(includeConfigSettings).size();
    }

    public static void resetPhantomStatistic() {
        for (Player player: NightSkipper.getCurrentWorld().getPlayers())
            player.setStatistic(Statistic.TIME_SINCE_REST, 0);
    }
}
