package gg.leo.IraqueClan.listener;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.clan.role.ClanRole;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;

public class ClanJoinQuitListener implements Listener {
    private final IraqueClan plugin;
    private final Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    public ClanJoinQuitListener(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Apply clan role permissions
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(uuid);
        if (clan != null) {
            this.applyClanPermissions(player, clan);
            clan.setLastActiveTime(System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Remove clan permissions
        this.removeClanPermissions(player);

        // Save clan data
        this.plugin.getClanManager().saveAll();
    }

    public void applyClanPermissions(Player player, Clan clan) {
        this.removeClanPermissions(player);
        ClanRole role = clan.getMemberRole(player.getUniqueId());
        if (role == null) return;

        List<String> permissions = switch (role) {
            case LIDER -> this.plugin.getConfigManager().getLeaderPermissions();
            case SUB_LIDER -> this.plugin.getConfigManager().getSubLeaderPermissions();
            case MEMBRO -> this.plugin.getConfigManager().getMemberPermissions();
        };

        PermissionAttachment attachment = player.addAttachment(this.plugin);
        for (String perm : permissions) {
            if (perm.equals("*")) continue;
            attachment.setPermission(perm, true);
        }
        this.attachments.put(player.getUniqueId(), attachment);
        player.recalculatePermissions();
    }

    public void removeClanPermissions(Player player) {
        PermissionAttachment attachment = this.attachments.remove(player.getUniqueId());
        if (attachment != null) {
            player.removeAttachment(attachment);
            player.recalculatePermissions();
        }
    }
}
