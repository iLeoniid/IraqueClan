package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.entity.Player;

public class ClanLeaveCommand implements gg.leo.IraqueClan.clan.ClanSubCommand {
    private final IraqueClan plugin;

    public ClanLeaveCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-clan"));
            return;
        }
        if (clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.cannot-leave-as-leader"));
            return;
        }
        boolean left = this.plugin.getClanManager().leaveClan(player.getUniqueId());
        if (left) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.left")
                    .replace("{clan}", clan.getName()));
        }
    }
}
