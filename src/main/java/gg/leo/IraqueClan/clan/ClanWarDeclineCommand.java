package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.war.War;
import java.util.UUID;
import org.bukkit.entity.Player;

public class ClanWarDeclineCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanWarDeclineCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("iraqueclan.war.decline")) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-in-clan"));
            return;
        }
        if (!clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("war.not-leader"));
            return;
        }
        UUID defenderId = UUID.nameUUIDFromBytes(clan.getName().getBytes());
        War pendingWar = this.plugin.getWarManager().getPendingWar(defenderId);
        if (pendingWar == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("war.no-pending"));
            return;
        }
        boolean declined = this.plugin.getWarManager().declineWar(defenderId);
        if (declined) {
            Clan challengerClan = this.plugin.getClanManager().getClanByPlayerDirect(pendingWar.getChallengerLeader());
            String challengerName = challengerClan != null ? challengerClan.getName() : "Desconhecido";
            String msg = this.plugin.getConfigManager().getPrefixedMessage("war.declined")
                    .replace("{clan}", clan.getName());
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
            this.plugin.getClanManager().addLog(player.getUniqueId(), "WAR_DECLINED",
                    "Guerra recusada contra " + challengerName);
        }
    }
}
