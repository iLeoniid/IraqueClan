package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.entity.Player;

public class ClanDeclineCommand implements gg.leo.IraqueClan.clan.ClanSubCommand {
    private final IraqueClan plugin;

    public ClanDeclineCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("iraqueclan.use")) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        if (args.length >= 2) {
            String clanName = args[1];
            boolean removed = this.plugin.getInviteManager().removeInvite(player.getUniqueId(), clanName);
            if (removed) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.declined")
                        .replace("{clan}", clanName));
            } else {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.not-invited"));
            }
            return;
        }
        int count = this.plugin.getInviteManager().getInviteCount(player.getUniqueId());
        if (count == 0) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.no-pending"));
            return;
        }
        this.plugin.getInviteManager().clearInvites(player.getUniqueId());
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.all-declined")
                .replace("{count}", String.valueOf(count)));
    }
}
