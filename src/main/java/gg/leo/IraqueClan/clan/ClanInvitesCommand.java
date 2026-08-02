package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.List;
import org.bukkit.entity.Player;

public class ClanInvitesCommand implements gg.leo.IraqueClan.clan.ClanSubCommand {
    private final IraqueClan plugin;

    public ClanInvitesCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("iraqueclan.use")) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        List<ClanInvite> invites = this.plugin.getInviteManager().getInvites(player.getUniqueId());
        if (invites.isEmpty()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.no-pending"));
            return;
        }
        InviteMessenger.sendInviteList(player, this.plugin, invites);
    }
}
