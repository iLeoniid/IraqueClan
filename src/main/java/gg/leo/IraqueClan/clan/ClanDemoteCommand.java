package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.role.ClanRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClanDemoteCommand implements gg.leo.IraqueClan.clan.ClanSubCommand {
    private final IraqueClan plugin;

    public ClanDemoteCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("iraqueclan.demote")) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-clan"));
            return;
        }
        if (!clan.canPromote(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-demote"));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("player-not-found"));
            return;
        }
        if (!clan.isMember(target.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.target-not-in-clan"));
            return;
        }
        boolean demoted = this.plugin.getClanManager().demoteMember(player.getUniqueId(), target.getUniqueId());
        if (demoted) {
            ClanRole newRole = clan.getMemberRole(target.getUniqueId());
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.demoted")
                    .replace("{player}", target.getName())
                    .replace("{role}", newRole != null ? newRole.getDisplayName() : "Membro"));
            target.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.you-were-demoted")
                    .replace("{role}", newRole != null ? newRole.getDisplayName() : "Membro"));
        } else {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.cannot-demote-higher"));
        }
    }
}
