package gg.leo.IraqueClan.war;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import java.util.UUID;
import org.bukkit.entity.Player;

public class WarAcceptCommand implements gg.leo.IraqueClan.clan.ClanSubCommand {
    private final IraqueClan plugin;

    public WarAcceptCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("iraqueclan.war.accept")) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-clan"));
            return;
        }
        if (!clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("not-leader"));
            return;
        }
        UUID defenderId = UUID.nameUUIDFromBytes(clan.getName().getBytes());
        War pendingWar = this.plugin.getWarManager().getPendingWar(defenderId);
        if (pendingWar == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("war.no-pending"));
            return;
        }
        boolean accepted = this.plugin.getWarManager().acceptWar(defenderId);
        if (accepted) {
            Clan challengerClan = this.plugin.getClanManager().getClanByPlayerDirect(pendingWar.getChallengerLeader());
            String challengerName = challengerClan != null ? challengerClan.getName() : "Desconhecido";
            String msg = this.plugin.getConfigManager().getPrefixedMessage("war.accepted")
                    .replace("{clan1}", challengerName)
                    .replace("{clan2}", clan.getName());
            if (challengerClan != null) {
                for (UUID uuid : challengerClan.getMembers().keySet()) {
                    Player member = this.plugin.getServer().getPlayer(uuid);
                    if (member != null && member.isOnline()) {
                        member.sendMessage(this.plugin.getConfigManager().deserialize(this.plugin.getConfigManager().translate(msg)));
                    }
                }
            }
            for (UUID uuid : clan.getMembers().keySet()) {
                Player member = this.plugin.getServer().getPlayer(uuid);
                if (member != null && member.isOnline()) {
                    member.sendMessage(this.plugin.getConfigManager().deserialize(this.plugin.getConfigManager().translate(msg)));
                }
            }
            this.plugin.sendDiscordMessage(this.plugin.getConfigManager().getMessage("war.accepted-discord")
                    .replace("{clan1}", challengerName)
                    .replace("{clan2}", clan.getName()));
        }
    }
}
