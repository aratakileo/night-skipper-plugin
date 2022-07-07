package pextystudios.nightskipper;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pextystudios.nightskipper.util.PlayerUtil;
import pextystudios.nightskipper.util.SkipUtil;
import pextystudios.nightskipper.util.SleepUtil;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        if (!e.isCancelled()) {
            PlayerUtil.addLyingPlayer(e.getPlayer().getName());
            SkipUtil.tryToSkip();
        }

        PlayerUtil.removeLyingPlayer(e.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent e) {
        PlayerUtil.removeLyingPlayer(e.getPlayer().getName());

        if (SleepUtil.isSleepTime()) {
            SkipUtil.tryToSkip();
            return;
        }

        PlayerUtil.removeAllCmdPlayer();
    }

    @EventHandler
    public void onPlayerLeftGame(PlayerQuitEvent e) {
        PlayerUtil.removeCmdVotingPlayer(e.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        PlayerUtil.removeCmdVotingPlayer(e.getPlayer().getName());
    }
}
