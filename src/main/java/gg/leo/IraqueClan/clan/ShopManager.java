package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;

public class ShopManager {

    private final IraqueClan plugin;
    private final List<UpgradeData> upgrades = new ArrayList<>();

    public record UpgradeData(
            String id,
            String name,
            String description,
            int maxLevel,
            double basePrice,
            double priceMultiplier
    ) {}

    public ShopManager(IraqueClan plugin) {
        this.plugin = plugin;
    }

    public void loadUpgrades() {
        this.upgrades.clear();
        ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("loja.upgrades");
        if (section == null) return;

        for (String id : section.getKeys(false)) {
            ConfigurationSection upg = section.getConfigurationSection(id);
            if (upg == null) continue;

            String name = upg.getString("nome", id);
            String desc = upg.getString("descricao", "");
            int maxLevel = upg.getInt("nivel-maximo", 5);
            double basePrice = upg.getDouble("preco-base", 1000);
            double multiplier = upg.getDouble("multiplicador-preco", 1.5);

            this.upgrades.add(new UpgradeData(id, name, desc, maxLevel, basePrice, multiplier));
        }
    }

    public UpgradeData getUpgrade(String id) {
        return this.upgrades.stream()
                .filter(u -> u.id().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public double getUpgradePrice(String id, int currentLevel) {
        UpgradeData data = getUpgrade(id);
        if (data == null) return -1;
        return data.basePrice() * Math.pow(data.priceMultiplier(), currentLevel);
    }

    public boolean canPurchase(Clan clan, String upgradeId) {
        if (clan == null) return false;
        UpgradeData data = getUpgrade(upgradeId);
        if (data == null) return false;
        int currentLevel = clan.getUpgradeLevel(upgradeId);
        if (currentLevel >= data.maxLevel()) return false;
        double price = getUpgradePrice(upgradeId, currentLevel);
        return clan.hasBank(price);
    }

    public boolean purchaseUpgrade(Clan clan, String upgradeId) {
        if (!canPurchase(clan, upgradeId)) return false;
        UpgradeData data = getUpgrade(upgradeId);
        if (data == null) return false;
        int currentLevel = clan.getUpgradeLevel(upgradeId);
        double price = getUpgradePrice(upgradeId, currentLevel);
        clan.removeBank(price);
        clan.setUpgrade(upgradeId, currentLevel + 1);
        this.plugin.getClanManager().saveAll();
        return true;
    }

    public List<UpgradeData> getAllUpgrades() {
        return List.copyOf(this.upgrades);
    }
}
