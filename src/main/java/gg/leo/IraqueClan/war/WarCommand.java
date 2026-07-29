package gg.leo.IraqueClan.war;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import java.util.UUID;
import org.bukkit.entity.Player;

public class WarCommand implements gg.leo.IraqueClan.clan.ClanSubCommand {
    private final IraqueClan plugin;

    public WarCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("iraqueclan.war.declare")) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        Clan challengerClan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (challengerClan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-clan"));
            return;
        }
        if (!challengerClan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("not-leader"));
            return;
        }
        if (args.length < 3) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-war"));
            return;
        }
        String targetClanName = args[2];
        if (targetClanName.equalsIgnoreCase(challengerClan.getName())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("war.yourself"));
            return;
        }
        java.util.Optional<Clan> targetOpt = this.plugin.getClanManager().getClan(targetClanName);
        if (targetOpt.isEmpty()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-found"));
            return;
        }
        Clan targetClan = targetOpt.get();
        UUID challengerId = UUID.nameUUIDFromBytes(challengerClan.getName().getBytes());
        UUID defenderId = UUID.nameUUIDFromBytes(targetClan.getName().getBytes());
        if (this.plugin.getWarManager().isClanInWar(challengerId) || this.plugin.getWarManager().isClanInWar(defenderId)) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("war.already-at-war"));
            return;
        }
        boolean created = this.plugin.getWarManager().createWar(challengerId, defenderId, player.getUniqueId());
        if (created) {
            String msg = this.plugin.getConfigManager().getPrefixedMessage("war.declared")
                    .replace("{clan1}", challengerClan.getName())
                    .replace("{clan2}", targetClan.getName());
            for (UUID uuid : challengerClan.getMembers().keySet()) {
                Player member = this.plugin.getServer().getPlayer(uuid);
                if (member != null && member.isOnline()) {
                    member.sendMessage(this.plugin.getConfigManager().deserialize(this.plugin.getConfigManager().translate(msg)));
                }
            }
            for (UUID uuid : targetClan.getMembers().keySet()) {
                Player member = this.plugin.getServer().getPlayer(uuid);
                if (member != null && member.isOnline()) {
                    member.sendMessage(this.plugin.getConfigManager().deserialize(this.plugin.getConfigManager().translate(
                            this.plugin.getConfigManager().getPrefixedMessage("war.pending"))));
                }
            }
            this.plugin.sendDiscordMessage(this.plugin.getConfigManager().getMessage("war.declared-discord")
                    .replace("{clan1}", challengerClan.getName())
                    .replace("{clan2}", targetClan.getName()));
        } else {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("war.already-at-war"));
        }
    }
}
