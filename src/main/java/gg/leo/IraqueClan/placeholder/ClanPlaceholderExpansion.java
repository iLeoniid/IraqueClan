package gg.leo.IraqueClan.placeholder;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class ClanPlaceholderExpansion extends PlaceholderExpansion {

    private final IraqueClan plugin;

    public ClanPlaceholderExpansion(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "iraqueclan";
    }

    @Override
    public String getAuthor() {
        return "Leo";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return "";

        Clan clan = plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());

        return switch (identifier) {
            case "clan" -> clan != null ? clan.getName() : "";
            case "tag" -> clan != null ? clan.getFormattedTag() : "";
            case "tag_formatted" -> clan != null ? clan.getFormattedTag() : "";
            case "leader" -> clan != null ? getLeaderName(clan) : "";
            case "members" -> clan != null ? String.valueOf(clan.getMemberCount()) : "0";
            case "max_members" -> clan != null ? String.valueOf(clan.getMaxMembers()) : "0";
            case "level" -> clan != null ? String.valueOf(clan.getLevel()) : "0";
            case "xp" -> clan != null ? String.valueOf(clan.getXp()) : "0";
            case "kills" -> clan != null ? String.valueOf(clan.getTotalKills()) : "0";
            case "deaths" -> clan != null ? String.valueOf(clan.getDeaths()) : "0";
            case "kdr" -> clan != null ? String.valueOf(clan.getKDR()) : "0";
            case "bank" -> clan != null ? String.format("%.2f", clan.getBank()) : "0.00";
            case "war_wins" -> clan != null ? String.valueOf(clan.getWarWins()) : "0";
            case "war_losses" -> clan != null ? String.valueOf(clan.getWarLosses()) : "0";
            case "war_draws" -> clan != null ? String.valueOf(clan.getWarDraws()) : "0";
            case "role" -> clan != null ? getRoleName(clan, player) : "";
            case "description" -> clan != null ? clan.getDescription() : "";
            case "homes" -> clan != null ? String.valueOf(clan.getHomeCount()) : "0";
            case "max_homes" -> clan != null ? String.valueOf(clan.getMaxHomes()) : "0";
            case "in_clan" -> clan != null ? "true" : "false";
            default -> "";
        };
    }

    @SuppressWarnings("deprecation")
    private String getLeaderName(Clan clan) {
        var leader = org.bukkit.Bukkit.getOfflinePlayer(clan.getLeader());
        return leader.getName() != null ? leader.getName() : "?";
    }

    private String getRoleName(Clan clan, Player player) {
        var role = clan.getMemberRole(player.getUniqueId());
        return role != null ? role.getDisplayName() : "";
    }
}
