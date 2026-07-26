package gg.leo.IraqueClan.listener;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.ItemBuilder;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class ClanProtectionListener implements Listener {

    private final IraqueClan plugin;
    private final boolean worldGuardEnabled;

    public ClanProtectionListener(IraqueClan plugin) {
        this.plugin = plugin;
        Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");
        this.worldGuardEnabled = wg != null && wg.isEnabled();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        if (isProtected(player, loc)) {
            event.setCancelled(true);
            sendProtectionMessage(player);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        if (isProtected(player, loc)) {
            event.setCancelled(true);
            sendProtectionMessage(player);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Player player = event.getPlayer();
        Location loc = event.getClickedBlock().getLocation();
        if (isProtected(player, loc)) {
            event.setCancelled(true);
            sendProtectionMessage(player);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player victim)) return;
        Location loc = victim.getLocation();

        ConfigurationSection config = this.plugin.getConfig().getConfigurationSection("protecao");
        if (config == null || !config.getBoolean("habilitado", true)) return;

        for (Clan clan : this.plugin.getClanManager().getAllClans()) {
            if (isWithinProtectionRadius(clan, loc)) {
                if (!isMemberOrAlly(clan, attacker.getUniqueId())) {
                    event.setCancelled(true);
                    sendProtectionMessage(attacker);
                    return;
                }
            }
        }
    }

    private boolean isProtected(Player player, Location loc) {
        ConfigurationSection config = this.plugin.getConfig().getConfigurationSection("protecao");
        if (config == null || !config.getBoolean("habilitado", true)) return false;

        for (Clan clan : this.plugin.getClanManager().getAllClans()) {
            if (isWithinProtectionRadius(clan, loc)) {
                if (!isMemberOrAlly(clan, player.getUniqueId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWithinProtectionRadius(Clan clan, Location loc) {
        int upgradeLevel = clan.getUpgradeLevel("raio-base");
        int baseRadius = this.plugin.getConfig().getInt("protecao.raio-padrao", 15);
        int radiusPerLevel = this.plugin.getConfig().getInt("loja.upgrades.raio-base.raio-por-nivel", 5);
        int radius = baseRadius + (upgradeLevel * radiusPerLevel);

        for (Clan.SimpleLocation home : clan.getHomes().values()) {
            Location homeLoc = home.toBukkitLocation();
            if (homeLoc.getWorld() == null || loc.getWorld() == null) continue;
            if (!homeLoc.getWorld().getName().equals(loc.getWorld().getName())) continue;
            double distance = loc.distance(homeLoc);
            if (distance <= radius) return true;
        }
        return false;
    }

    private boolean isMemberOrAlly(Clan clan, UUID playerUUID) {
        if (clan.isMember(playerUUID)) return true;
        Clan playerClan = this.plugin.getClanManager().getClanByPlayerDirect(playerUUID);
        if (playerClan == null) return false;
        UUID playerClanUUID = UUID.nameUUIDFromBytes(playerClan.getName().getBytes());
        return clan.isAlly(playerClanUUID);
    }

    private void sendProtectionMessage(Player player) {
        String msg = this.plugin.getConfigManager().getPrefixedMessage("protection.blocked");
        for (Clan clan : this.plugin.getClanManager().getAllClans()) {
            if (isWithinProtectionRadius(clan, player.getLocation())) {
                msg = msg.replace("{clan}", clan.getName());
                break;
            }
        }
        player.sendMessage(ItemBuilder.color(msg));
    }
}
