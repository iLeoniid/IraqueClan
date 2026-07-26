package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanInviteCommand implements CommandExecutor {
    private final IraqueClan plugin;

    public ClanInviteCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }
        if (!player.hasPermission("iraqueclan.invite")) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return true;
        }
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-clan"));
            return true;
        }
        if (!clan.canKick(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-invite"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("player-not-found"));
            return true;
        }
        if (this.plugin.getClanManager().isPlayerInClan(target.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.target-already-in-clan"));
            return true;
        }
        if (clan.getMemberCount() >= this.plugin.getConfigManager().getMaxMembers()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.clan-full")
                    .replace("{max}", String.valueOf(this.plugin.getConfigManager().getMaxMembers())));
            return true;
        }
        this.plugin.getClanManager().setPendingInvite(target.getUniqueId(), player.getUniqueId());
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.sent")
                .replace("{player}", target.getName()));
        target.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.received")
                .replace("{player}", player.getName())
                .replace("{clan}", clan.getName()));
        return true;
    }
}
