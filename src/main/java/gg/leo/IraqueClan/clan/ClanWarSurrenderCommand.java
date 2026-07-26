package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.war.War;
import java.util.UUID;
import org.bukkit.entity.Player;

public class ClanWarSurrenderCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanWarSurrenderCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("iraqueclan.war.surrender")) {
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
        UUID clanId = UUID.nameUUIDFromBytes(clan.getName().getBytes());
        War activeWar = this.plugin.getWarManager().getActiveWar(clanId);
        if (activeWar == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("war.no-active"));
            return;
        }
        UUID opponentId = activeWar.getOpponentClanId(clanId);
        Clan opponentClan = this.plugin.getClanManager().getClanByUUID(opponentId);
        String opponentName = opponentClan != null ? opponentClan.getName() : "Desconhecido";
        this.plugin.getWarManager().endWar(clanId);
        this.plugin.getClanManager().addWarLoss(clan.getName());
        if (opponentClan != null) {
            this.plugin.getClanManager().addWarWin(opponentClan.getName());
        }
        String surrenderMsg = this.plugin.getConfigManager().getPrefixedMessage("war.surrendered")
                .replace("{clan}", clan.getName())
                .replace("{enemy}", opponentName);
        for (UUID uuid : clan.getMembers().keySet()) {
            Player member = this.plugin.getServer().getPlayer(uuid);
            if (member != null && member.isOnline()) {
                member.sendMessage(this.plugin.getConfigManager().deserialize(this.plugin.getConfigManager().translate(surrenderMsg)));
            }
        }
        if (opponentClan != null) {
            for (UUID uuid : opponentClan.getMembers().keySet()) {
                Player member = this.plugin.getServer().getPlayer(uuid);
                if (member != null && member.isOnline()) {
                    member.sendMessage(this.plugin.getConfigManager().deserialize(this.plugin.getConfigManager().translate(surrenderMsg)));
                }
            }
        }
        this.plugin.getClanManager().addLog(player.getUniqueId(), "WAR_SURRENDER",
                "Guerra rendida contra " + opponentName);
        this.plugin.sendDiscordMessage(clan.getName() + " se rendeu na guerra contra " + opponentName);
    }
}
