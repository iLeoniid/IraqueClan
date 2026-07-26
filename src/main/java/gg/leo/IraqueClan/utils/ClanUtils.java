package gg.leo.IraqueClan.utils;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

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

    public static String getPlayerName(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return player.getName() != null ? player.getName() : uuid.toString().substring(0, 8);
    }
}
