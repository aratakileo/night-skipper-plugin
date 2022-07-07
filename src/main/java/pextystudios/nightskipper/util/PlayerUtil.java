package pextystudios.nightskipper.util;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;
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

        alwaysVotingPlayers = FileUtil.readFile(whoAlwaysVoteFile, true).split("\n");

        int i = 0;
        for (String player: alwaysVotingPlayers) {
            if (player.length() == 0)
                alwaysVotingPlayers = (String[]) ArrayUtils.remove(alwaysVotingPlayers, i);

            i++;
        }
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
        checkDataInit();

        HashSet<String> hashSet = new HashSet<>(Arrays.asList(lyingPlayers));

        if (NightSkipper.getMode().equals("easy"))
            hashSet.addAll(Arrays.asList(alwaysVotingPlayers));

        if (!NightSkipper.getMode().equals("hard"))
            hashSet.addAll(Arrays.asList(cmdVotingPlayers));

        if (!includeOffline) {
            HashSet<String> onlinePlayers = new HashSet<>();
            for (Player player: NightSkipper.getCurrentWorld().getPlayers())
                onlinePlayers.add(player.getName());

            hashSet.removeIf(player -> !onlinePlayers.contains(player));
        }

        return hashSet.size();
    }

    public static int getPlayerCount() {
        return NightSkipper.getCurrentWorld().getPlayerCount();
    }
}
