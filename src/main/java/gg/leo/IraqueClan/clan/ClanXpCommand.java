package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.entity.Player;

public class ClanXpCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanXpCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-in-clan"));
            return;
        }
        int level = clan.getLevel();
        long xp = clan.getXp();
        long required = this.plugin.getClanManager().calculateRequiredXP(level + 1);
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("level.current")
                .replace("{level}", String.valueOf(level))
                .replace("{xp}", String.valueOf(xp))
                .replace("{required}", String.valueOf(required)));
    }
}
