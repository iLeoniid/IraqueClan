package gg.leo.IraqueClan.war;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class WarListener implements Listener {
    private final IraqueClan plugin;

    public WarListener(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Player dead = event.getEntity();
        if (killer == null || dead == null) return;
        if (killer.equals(dead)) return;
        this.plugin.getWarManager().recordKill(killer.getUniqueId(), dead.getUniqueId());
    }
}
