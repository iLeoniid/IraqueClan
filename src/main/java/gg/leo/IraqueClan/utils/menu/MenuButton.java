package gg.leo.IraqueClan.utils.menu;

import gg.leo.IraqueClan.utils.ItemBuilder;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuButton {
    private final Material material;
    private final String name;
    private final List<String> lore;
    private final Consumer<Player> action;
    private final boolean closeOnClick;
    private final boolean glowing;
    private final int customModelData;

    public MenuButton(Material material, String name, List<String> lore, Consumer<Player> action) {
        this(material, name, lore, action, false, false, 0);
    }

    public MenuButton(Material material, String name, List<String> lore, Consumer<Player> action, boolean closeOnClick, boolean glowing, int customModelData) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.action = action;
        this.closeOnClick = closeOnClick;
        this.glowing = glowing;
        this.customModelData = customModelData;
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(this.material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ItemBuilder.color(this.name));
            if (this.lore != null) {
                meta.setLore(this.lore.stream().map(ItemBuilder::color).toList());
            }
            if (this.glowing) {
                meta.setEnchantmentGlintOverride(true);
            }
            if (this.customModelData > 0) {
                meta.setCustomModelData(this.customModelData);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public Consumer<Player> getAction() {
        return this.action;
    }

    public boolean shouldCloseOnClick() {
        return this.closeOnClick;
    }
}
