package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ClanHomeCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanHomeCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-in-clan"));
            return;
        }
        Map<String, Clan.SimpleLocation> homes = this.plugin.getClanManager().getHomes(player.getUniqueId());
        if (homes.isEmpty()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.no-homes"));
            return;
        }
        String homeName;
        if (args.length >= 2) {
            homeName = args[1].toLowerCase();
        } else {
            homeName = homes.keySet().iterator().next();
        }
        Clan.SimpleLocation simpleLoc = homes.get(homeName);
        if (simpleLoc == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.not-found")
                    .replace("{name}", homeName));
            return;
        }
        Location loc = simpleLoc.toBukkitLocation();
        if (loc == null || loc.getWorld() == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.world-changed")
                    .replace("{name}", homeName));
            return;
        }
        int cooldownSeconds = this.plugin.getConfig().getInt("casas.cooldown-segundos", 5);
        long cooldownKey = player.getUniqueId().getMostSignificantBits();
        String cooldownCacheKey = "home_cooldown_" + player.getUniqueId();
        Long lastTeleport = (Long) player.getPersistentDataContainer().get(
                new org.bukkit.NamespacedKey(this.plugin, "home_cooldown"),
                org.bukkit.persistence.PersistentDataType.LONG);
        if (lastTeleport != null) {
            long elapsed = (System.currentTimeMillis() - lastTeleport) / 1000;
            if (elapsed < cooldownSeconds) {
                long remaining = cooldownSeconds - elapsed;
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.cooldown")
                        .replace("{time}", remaining + "s"));
                return;
            }
        }
        player.getPersistentDataContainer().set(
                new org.bukkit.NamespacedKey(this.plugin, "home_cooldown"),
                org.bukkit.persistence.PersistentDataType.LONG,
                System.currentTimeMillis());
        int delay = this.plugin.getConfig().getInt("casas.atraso-teletransporte", 0);
        if (delay > 0) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.teleport")
                    .replace("{name}", homeName));
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                if (player.isOnline()) {
                    player.teleport(loc);
                    player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.teleported")
                            .replace("{name}", homeName));
                }
            }, delay * 20L);
        } else {
            player.teleport(loc);
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.teleported")
                    .replace("{name}", homeName));
        }
    }
}
