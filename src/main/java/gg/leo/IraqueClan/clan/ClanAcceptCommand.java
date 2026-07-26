package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.entity.Player;

public class ClanAcceptCommand implements gg.leo.IraqueClan.clan.ClanSubCommand {
    private final IraqueClan plugin;

    public ClanAcceptCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("iraqueclan.use")) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        if (this.plugin.getClanManager().isPlayerInClan(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.already-in-clan"));
            return;
        }
        java.util.UUID leaderUuid = this.plugin.getClanManager().getPendingInvite(player.getUniqueId());
        if (leaderUuid == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.not-invited"));
            return;
        }
        Clan leaderClan = this.plugin.getClanManager().getClanByPlayerDirect(leaderUuid);
        if (leaderClan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-found"));
            this.plugin.getClanManager().removePendingInvite(player.getUniqueId());
            return;
        }
        if (leaderClan.getMemberCount() >= this.plugin.getConfigManager().getMaxMembers()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.clan-full")
                    .replace("{max}", String.valueOf(this.plugin.getConfigManager().getMaxMembers())));
            this.plugin.getClanManager().removePendingInvite(player.getUniqueId());
            return;
        }
        boolean joined = this.plugin.getClanManager().joinClan(player.getUniqueId(), leaderClan.getName());
        this.plugin.getClanManager().removePendingInvite(player.getUniqueId());
        if (joined) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.accepted")
                    .replace("{clan}", leaderClan.getName()));
            Player leader = this.plugin.getServer().getPlayer(leaderUuid);
            if (leader != null && leader.isOnline()) {
                leader.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.player-joined")
                        .replace("{player}", player.getName()));
            }
        } else {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.join-error"));
        }
    }
}
