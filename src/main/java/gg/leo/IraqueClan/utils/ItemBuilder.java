package gg.leo.IraqueClan.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;

    private ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.meta = this.item.getItemMeta();
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material, 1);
    }

    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public static String color(String text) {
        if (text == null) return null;
        return ChatColor.translateAlternateColorCodes('&',
                text.replaceAll("&#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])",
                        "\u00a7x\u00a7$1\u00a7$2\u00a7$3\u00a7$4\u00a7$5\u00a7$6"));
    }

    public ItemBuilder name(String name) {
        if (name == null) return this;
        this.meta.setDisplayName(ItemBuilder.color(name));
        return this;
    }

    public ItemBuilder lore(String... lines) {
        this.meta.setLore(Arrays.stream(lines).map(ItemBuilder::color).collect(Collectors.toList()));
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        if (lines == null) return this;
        this.meta.setLore(lines.stream().map(ItemBuilder::color).collect(Collectors.toList()));
        return this;
    }

    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return this.item;
    }
}
