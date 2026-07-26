package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClanProfileCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanProfileCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan;
        if (args.length >= 2) {
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) nameBuilder.append(" ");
                nameBuilder.append(args[i]);
            }
            String clanName = nameBuilder.toString();
            java.util.Optional<Clan> clanOpt = this.plugin.getClanManager().getClan(clanName);
            if (clanOpt.isEmpty()) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.not-found")
                        .replace("{name}", clanName));
                return;
            }
            clan = clanOpt.get();
        } else {
            clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
            if (clan == null) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-in-clan"));
                return;
            }
        }
        UUID leaderUuid = clan.getLeader();
        String leaderName = "Desconhecido";
        Player leaderPlayer = Bukkit.getPlayer(leaderUuid);
        if (leaderPlayer != null) {
            leaderName = leaderPlayer.getName();
        } else {
            String offlineName = Bukkit.getOfflinePlayer(leaderUuid).getName();
            if (offlineName != null) leaderName = offlineName;
        }
        String allies = clan.getDiplomacy().stream()
                .filter(d -> d.type() == Clan.DiplomacyType.ALLY)
                .map(d -> {
                    Clan other = this.plugin.getClanManager().getClanByUUID(d.otherClanUUID());
                    return other != null ? other.getName() : "Desconhecido";
                })
                .collect(Collectors.joining(", "));
        if (allies.isEmpty()) allies = "Nenhum";
        String rivals = clan.getDiplomacy().stream()
                .filter(d -> d.type() == Clan.DiplomacyType.RIVAL)
                .map(d -> {
                    Clan other = this.plugin.getClanManager().getClanByUUID(d.otherClanUUID());
                    return other != null ? other.getName() : "Desconhecido";
                })
                .collect(Collectors.joining(", "));
        if (rivals.isEmpty()) rivals = "Nenhum";
        String desc = clan.getDescription();
        if (desc == null || desc.isEmpty()) desc = "Sem descrição";
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.header"));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.name")
                .replace("{name}", clan.getName()));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.tag")
                .replace("{tag}", clan.getFormattedTag()));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.leader")
                .replace("{leader}", leaderName));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.level")
                .replace("{level}", String.valueOf(clan.getLevel())));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.kills")
                .replace("{kills}", String.valueOf(clan.getTotalKills())));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.deaths")
                .replace("{deaths}", String.valueOf(clan.getDeaths())));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.kdr")
                .replace("{kdr}", String.format("%.2f", clan.getKDR())));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.bank")
                .replace("{bank}", String.format("%.2f", clan.getBank())));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.members")
                .replace("{current}", String.valueOf(clan.getMemberCount()))
                .replace("{max}", String.valueOf(clan.getMaxMembers())));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.description")
                .replace("{description}", desc));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.allies")
                .replace("{allies}", allies));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("profile.rivals")
                .replace("{rivals}", rivals));
    }
}
