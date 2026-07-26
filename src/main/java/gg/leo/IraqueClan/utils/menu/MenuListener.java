package gg.leo.IraqueClan.utils.menu;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuListener implements Listener {
    private static final Map<UUID, BaseMenu> openMenus = new ConcurrentHashMap<>();

    public static void register(UUID playerUuid, BaseMenu menu) {
        openMenus.put(playerUuid, menu);
    }

    public static void unregister(UUID playerUuid) {
        openMenus.remove(playerUuid);
    }

    public static BaseMenu getOpenMenu(UUID playerUuid) {
        return openMenus.get(playerUuid);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        BaseMenu menu = openMenus.get(player.getUniqueId());
        if (menu == null || menu.getInventory() == null || event.getInventory() != menu.getInventory()) {
            return;
        }
        event.setCancelled(true);
        if (event.getRawSlot() >= 0 && event.getRawSlot() < menu.getInventory().getSize()) {
            menu.handleClick(event.getRawSlot());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            BaseMenu menu = openMenus.get(player.getUniqueId());
            if (menu != null && menu.getInventory() != null && event.getInventory() == menu.getInventory()) {
                openMenus.remove(player.getUniqueId());
            }
        }
    }
}
