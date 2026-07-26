package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClanKickCommand implements gg.leo.IraqueClan.clan.ClanSubCommand {
    private final IraqueClan plugin;

    public ClanKickCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("iraqueclan.kick")) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-clan"));
            return;
        }
        if (!clan.canKick(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-kick"));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("player-not-found"));
            return;
        }
        if (clan.getLeader().equals(target.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.cannot-kick-leader"));
            return;
        }
        if (!clan.isMember(target.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.target-not-in-clan"));
            return;
        }
        boolean kicked = this.plugin.getClanManager().kickMember(player.getUniqueId(), target.getUniqueId());
        if (kicked) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.kicked-player")
                    .replace("{player}", target.getName()));
            target.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.kicked")
                    .replace("{clan}", clan.getName()));
        }
    }
}
