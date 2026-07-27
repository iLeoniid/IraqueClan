package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ClanShopCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanShopCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("shop.no-clan"));
            return;
        }
        ConfigurationSection upgradesSection = this.plugin.getConfig().getConfigurationSection("loja.upgrades");
        if (upgradesSection == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("shop.no-clan"));
            return;
        }
        Set<String> upgradeKeys = upgradesSection.getKeys(false);
        if (upgradeKeys.isEmpty()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("shop.no-clan"));
            return;
        }
        if (args.length >= 2) {
            handlePurchase(player, clan, args[1], upgradesSection);
            return;
        }
        openShopGUI(player, clan, upgradeKeys, upgradesSection);
    }

    private void openShopGUI(Player player, Clan clan, Set<String> upgradeKeys, ConfigurationSection upgradesSection) {
        int size = ((upgradeKeys.size() / 9) + 1) * 9;
        size = Math.max(27, Math.min(size, 54));
        Inventory gui = org.bukkit.Bukkit.createInventory(null, size,
                gg.leo.IraqueClan.utils.ItemBuilder.color("&#555555Loja do Clã"));
        int slot = 0;
        for (String key : upgradeKeys) {
            ConfigurationSection upSection = upgradesSection.getConfigurationSection(key);
            if (upSection == null || slot >= gui.getSize()) continue;
            String name = upSection.getString("nome", key);
            String desc = upSection.getString("descricao", "");
            int maxLevel = upSection.getInt("nivel-maximo", 5);
            double basePrice = upSection.getDouble("preco-base", 1000);
            double multiplier = upSection.getDouble("multiplicador-preco", 1.5);
            int currentLevel = clan.getUpgradeLevel(key);
            double nextPrice = basePrice * Math.pow(multiplier, currentLevel);
            Material mat = Material.PAPER;
            try {
                mat = Material.valueOf(upSection.getString("material", "PAPER").toUpperCase());
            } catch (IllegalArgumentException ignored) {}
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(gg.leo.IraqueClan.utils.ItemBuilder.color(name));
                meta.setLore(java.util.Arrays.asList(
                        gg.leo.IraqueClan.utils.ItemBuilder.color(desc),
                        gg.leo.IraqueClan.utils.ItemBuilder.color("&#AAAAAANível atual: &#FFFFFF" + currentLevel + "/" + maxLevel),
                        gg.leo.IraqueClan.utils.ItemBuilder.color("&#AAAAAAPróximo preço: &#FFFFFF$" + String.format("%.2f", nextPrice))
                ));
                item.setItemMeta(meta);
            }
            gui.setItem(slot, item);
            slot++;
        }
        player.openInventory(gui);
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("shop.opened"));
    }

    private void handlePurchase(Player player, Clan clan, String upgradeName, ConfigurationSection upgradesSection) {
        if (!clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("shop.not-leader"));
            return;
        }
        ConfigurationSection upSection = upgradesSection.getConfigurationSection(upgradeName);
        if (upSection == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("shop.no-clan"));
            return;
        }
        int maxLevel = upSection.getInt("nivel-maximo", 5);
        int currentLevel = clan.getUpgradeLevel(upgradeName);
        if (currentLevel >= maxLevel) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("shop.already-max"));
            return;
        }
        double basePrice = upSection.getDouble("preco-base", 1000);
        double multiplier = upSection.getDouble("multiplicador-preco", 1.5);
        double price = basePrice * Math.pow(multiplier, currentLevel);
        if (!clan.hasBank(price)) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("shop.insufficient-funds")
                    .replace("{cost}", String.format("%.2f", price)));
            return;
        }
        boolean purchased = this.plugin.getClanManager().purchaseUpgrade(player.getUniqueId(), upgradeName);
        if (purchased) {
            String displayName = upSection.getString("nome", upgradeName);
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("shop.purchased")
                    .replace("{upgrade}", displayName)
                    .replace("{level}", String.valueOf(currentLevel + 1)));
            this.plugin.getClanManager().addLog(player.getUniqueId(), "SHOP_PURCHASE",
                    "Comprou upgrade " + displayName + " nível " + (currentLevel + 1));
            this.plugin.sendDiscordMessage(player.getName() + " comprou upgrade " + displayName
                    + " nível " + (currentLevel + 1) + " no clã " + clan.getName());
        } else {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("shop.cannot-afford"));
        }
    }
}
