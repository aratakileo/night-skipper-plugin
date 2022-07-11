package pextystudios.nightskipper.util;

import org.bukkit.entity.Player;
import pextystudios.nightskipper.NightSkipper;

import java.util.HashMap;
import java.util.HashSet;

public final class MailingUtil {
    private static final HashSet<String> mailedPlayers = new HashSet<>();

    public static void tryMailToPlayers() {
        if (!NightSkipper.getFeatureEnabled("command.now-vote") || !NightSkipper.getFeatureEnabled("send-skip-suggestion")) return;

        HashMap<String, String> formatVars = new HashMap<>(NightSkipper.getGlobalVars());

        formatVars.put("target", SleepUtil.getTarget());
        formatVars.put("players", String.valueOf(PlayerUtil.getPlayerCount(true)));
        formatVars.put("voted", String.valueOf(PlayerUtil.votePlayerCount()));
        formatVars.put("sleeping", String.valueOf(PlayerUtil.lyingPlayerCount()));

        for (Player player: PlayerUtil.getPlayers(true)) {
            String playerNickname = player.getName();

            formatVars.put("sender", playerNickname);

            if (!PlayerUtil.hasVotedPlayer(playerNickname) && !mailedPlayers.contains(playerNickname)) {
                String[] text_pieces = StringUtil.saveSplit(NightSkipper.getText("skip-suggestion", formatVars), "%vote-button%", 2);

                player.sendMessage(RawTextUtil.compile(RawTextUtil.getTextsJson(
                        RawTextUtil.getTextJson(text_pieces[0]),
                        RawTextUtil.getTextJson(
                                NightSkipper.getText("vote-button", formatVars),
                                RawTextUtil.getCommandActionJson("ns vote now"),
                                RawTextUtil.getShowTextActionJson(RawTextUtil.getExtraTextJson(NightSkipper.getText("vote-button-extra", formatVars)))
                        ),
                        RawTextUtil.getTextJson(text_pieces[1])
                )));

                mailedPlayers.add(playerNickname);
            }
        }
    }

    public static void clear() {
        mailedPlayers.clear();
    }
}
