package gg.leo.IraqueClan.utils;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import java.util.UUID;
import org.bukkit.Bukkit;

public class ClanUtils {
    private static IraqueClan plugin;

    public static void init(IraqueClan pluginInstance) {
        plugin = pluginInstance;
    }

    public static String getClanPrefix(UUID playerUuid) {
        if (plugin == null) return "";
        Clan clan = plugin.getClanManager().getClanByPlayerDirect(playerUuid);
        if (clan == null) return "";
        return clan.getFormattedTag();
    }

    public static String getClanName(UUID playerUuid) {
        if (plugin == null) return "";
        Clan clan = plugin.getClanManager().getClanByPlayerDirect(playerUuid);
        if (clan == null) return "";
        return clan.getName();
    }

    public static String getClanTag(UUID playerUuid) {
        if (plugin == null) return "";
        Clan clan = plugin.getClanManager().getClanByPlayerDirect(playerUuid);
        if (clan == null) return "";
        return clan.getTag();
    }

    @SuppressWarnings("deprecation")
    public static String getPlayerName(UUID uuid) {
        org.bukkit.entity.Player online = Bukkit.getPlayer(uuid);
        if (online != null && online.isOnline()) return online.getName();
        org.bukkit.OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return player.getName() != null ? player.getName() : uuid.toString().substring(0, 8);
    }
}
