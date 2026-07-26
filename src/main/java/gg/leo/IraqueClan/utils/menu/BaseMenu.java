package gg.leo.IraqueClan.utils.menu;

import gg.leo.IraqueClan.utils.ItemBuilder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class BaseMenu {
    protected final Player player;
    protected final String title;
    protected final int size;
    protected final MenuType type;
    protected final Map<Integer, MenuButton> buttons = new LinkedHashMap<>();
    protected Inventory inventory;
    protected int page = 1;

    protected BaseMenu(Player player, String title, int size, MenuType type) {
        this.player = player;
        this.title = title;
        this.size = size;
        this.type = type;
    }

    public final void openMenu() {
        this.buttons.clear();
        this.buildMenu();
        this.inventory = Bukkit.createInventory(null, this.size, ItemBuilder.color(this.title));
        for (Map.Entry<Integer, MenuButton> entry : this.buttons.entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < this.size) {
                this.inventory.setItem(entry.getKey(), entry.getValue().build());
            }
        }
        this.player.openInventory(this.inventory);
        MenuListener.register(this.player.getUniqueId(), this);
    }

    public abstract void buildMenu();

    protected void registerButton(int slot, Material material, String name, List<String> lore, Consumer<Player> action) {
        this.registerButton(slot, new MenuButton(material, name, lore, action));
    }

    protected void registerButton(int slot, MenuButton button) {
        if (slot >= 0 && slot < this.size) {
            this.buttons.put(slot, button);
        }
    }

    protected void addBorder(Material material, String name) {
        for (int slot = 0; slot < this.size; slot++) {
            boolean border = slot < 9 || slot % 9 == 0 || slot % 9 == 8 || slot >= this.size - 9;
            if (border && !this.buttons.containsKey(slot)) {
                this.buttons.put(slot, new MenuButton(material, name, List.of(), p -> {}));
            }
        }
    }

    protected void addBackButton(int slot, Consumer<Player> action) {
        this.registerButton(slot, new MenuButton(Material.ARROW, "&c&lVoltar", List.of("&7Retorna ao menu anterior"), action));
    }

    public void updateMenu() {
        if (this.inventory == null) {
            this.openMenu();
            return;
        }
        this.buttons.clear();
        this.buildMenu();
        this.inventory.clear();
        for (Map.Entry<Integer, MenuButton> entry : this.buttons.entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < this.size) {
                this.inventory.setItem(entry.getKey(), entry.getValue().build());
            }
        }
        this.player.updateInventory();
    }

    public void handleClick(int slot) {
        MenuButton button = this.buttons.get(slot);
        if (button == null || button.getAction() == null) {
            return;
        }
        button.getAction().accept(this.player);
        if (button.shouldCloseOnClick()) {
            this.player.closeInventory();
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = Math.max(1, page);
    }

    public MenuType getType() {
        return this.type;
    }
}
