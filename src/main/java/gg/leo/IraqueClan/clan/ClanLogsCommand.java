package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClanLogsCommand implements ClanSubCommand {
    private final IraqueClan plugin;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ClanLogsCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-in-clan"));
            return;
        }
        if (args.length >= 2 && args[1].equalsIgnoreCase("clear")) {
            handleClear(player, clan);
            return;
        }
        List<Clan.ClanLog> logs = this.plugin.getClanManager().getLogs(player.getUniqueId());
        if (logs.isEmpty()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("logs.empty"));
            return;
        }
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("logs.header"));
        int maxDisplay = Math.min(logs.size(), 15);
        int startIndex = Math.max(0, logs.size() - maxDisplay);
        for (int i = startIndex; i < logs.size(); i++) {
            Clan.ClanLog log = logs.get(i);
            String playerName = "Sistema";
            if (log.player() != null) {
                Player logPlayer = Bukkit.getPlayer(log.player());
                if (logPlayer != null) {
                    playerName = logPlayer.getName();
                } else {
                    String offlineName = Bukkit.getOfflinePlayer(log.player()).getName();
                    if (offlineName != null) playerName = offlineName;
                }
            }
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("logs.entry")
                    .replace("{time}", this.dateFormat.format(new Date(log.timestamp())))
                    .replace("{action}", log.action())
                    .replace("{player}", playerName)
                    .replace("{details}", log.details()));
        }
    }

    private void handleClear(Player player, Clan clan) {
        if (!clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        this.plugin.getClanManager().clearLogs(player.getUniqueId());
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("logs.cleared"));
    }
}
