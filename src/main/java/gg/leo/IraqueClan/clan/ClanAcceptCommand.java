package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.List;
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
        List<ClanInvite> invites = this.plugin.getInviteManager().getInvites(player.getUniqueId());
        if (invites.isEmpty()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.no-pending"));
            return;
        }

        ClanInvite target;
        if (args.length >= 2) {
            target = this.plugin.getInviteManager().getInvite(player.getUniqueId(), args[1]);
            if (target == null) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.not-invited"));
                return;
            }
        } else if (invites.size() == 1) {
            target = invites.get(0);
        } else {
            InviteMessenger.sendInviteList(player, this.plugin, invites);
            return;
        }

        Clan leaderClan = this.plugin.getClanManager().getClan(target.clanName()).orElse(null);
        if (leaderClan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-found")
                    .replace("{clan}", target.clanName()));
            this.plugin.getInviteManager().removeInvite(player.getUniqueId(), target.clanName());
            return;
        }
        if (leaderClan.getMemberCount() >= leaderClan.getMaxMembers()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.clan-full")
                    .replace("{max}", String.valueOf(leaderClan.getMaxMembers())));
            this.plugin.getInviteManager().removeInvite(player.getUniqueId(), target.clanName());
            return;
        }

        boolean joined = this.plugin.getClanManager().joinClan(player.getUniqueId(), leaderClan.getName());
        this.plugin.getInviteManager().removeInvite(player.getUniqueId(), target.clanName());
        if (joined) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.accepted")
                    .replace("{clan}", leaderClan.getName()));
            InviteMessenger.sendWelcome(player, leaderClan);
            this.plugin.getClanManager().addLogToClan(leaderClan.getName(), "JOIN",
                    player.getUniqueId(), player.getName() + " entrou no clã");
            Player leader = this.plugin.getServer().getPlayer(target.inviterUuid());
            if (leader != null && leader.isOnline()) {
                leader.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.player-joined")
                        .replace("{player}", player.getName()));
            }
        } else {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.join-error"));
        }
    }
}
