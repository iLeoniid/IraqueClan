package gg.leo.IraqueClan.listener;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ClanScoreboardListener implements Listener {
    private final IraqueClan plugin;

    public ClanScoreboardListener(IraqueClan plugin) {
        this.plugin = plugin;
    }

    // This listener provides the {clan} placeholder
    // IraqueCore's ScoreboardManager will use ClanUtils.getClanName()
    // to replace {clan} in scoreboard lines

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Trigger scoreboard update for the joining player
        // The actual placeholder replacement happens in IraqueCore
    }

    public static String getClanNameForScoreboard(UUID playerUuid, gg.leo.IraqueClan.IraqueClan plugin) {
        Clan clan = plugin.getClanManager().getClanByPlayerDirect(playerUuid);
        return clan != null ? clan.getName() : "Nenhum";
    }
}
