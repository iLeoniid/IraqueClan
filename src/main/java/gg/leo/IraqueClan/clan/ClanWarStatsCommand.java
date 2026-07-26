package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.entity.Player;

public class ClanWarStatsCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanWarStatsCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-in-clan"));
            return;
        }
        int wins = clan.getWarWins();
        int losses = clan.getWarLosses();
        int draws = clan.getWarDraws();
        double kdr = clan.getKDR();
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("war.stats")
                .replace("{wins}", String.valueOf(wins))
                .replace("{losses}", String.valueOf(losses))
                .replace("{draws}", String.valueOf(draws)));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("war.kdr")
                .replace("{kdr}", String.format("%.2f", kdr)));
    }
}
