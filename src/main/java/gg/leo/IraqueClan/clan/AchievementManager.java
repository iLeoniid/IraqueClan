package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;

public class AchievementManager {

    private final IraqueClan plugin;
    private final List<AchievementData> achievements = new ArrayList<>();

    public record AchievementData(
            String id,
            String name,
            String description,
            String requirementType,
            int requirementValue,
            double rewardMoney,
            double rewardXP
    ) {}

    public AchievementManager(IraqueClan plugin) {
        this.plugin = plugin;
    }

    public void loadAchievements() {
        this.achievements.clear();
        ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("conquistas.lista");
        if (section == null) return;

        for (String id : section.getKeys(false)) {
            ConfigurationSection ach = section.getConfigurationSection(id);
            if (ach == null) continue;

            String name = ach.getString("nome", id);
            String desc = ach.getString("descricao", "");
            String type = ach.getString("tipo-requisito", "KILLS");
            int value = ach.getInt("valor-requisito", 1);
            double rewardMoney = ach.getDouble("recompensa-dinheiro", 0);
            double rewardXP = ach.getDouble("recompensa-xp", 0);

            this.achievements.add(new AchievementData(id, name, desc, type, value, rewardMoney, rewardXP));
        }
    }

    public void checkAndUnlock(Clan clan) {
        if (clan == null) return;
        for (AchievementData ach : this.achievements) {
            if (clan.hasAchievement(ach.id())) continue;
            if (!isRequirementMet(clan, ach)) continue;
            clan.addAchievement(ach.id());
            clan.addBank(ach.rewardMoney());
            clan.addXp((long) ach.rewardXP());
        }
        this.plugin.getClanManager().saveAll();
    }

    private boolean isRequirementMet(Clan clan, AchievementData ach) {
        return switch (ach.requirementType().toUpperCase()) {
            case "KILLS" -> clan.getTotalKills() >= ach.requirementValue();
            case "WINS" -> clan.getWarWins() >= ach.requirementValue();
            case "BANK" -> clan.getBank() >= ach.requirementValue();
            case "MEMBERS" -> clan.getMemberCount() >= ach.requirementValue();
            case "LEVEL" -> clan.getLevel() >= ach.requirementValue();
            default -> false;
        };
    }

    public int getUnlockedCount(Clan clan) {
        if (clan == null) return 0;
        return (int) clan.getAchievements().stream()
                .filter(id -> this.achievements.stream().anyMatch(a -> a.id().equals(id)))
                .count();
    }

    public List<AchievementData> getAllAchievements() {
        return List.copyOf(this.achievements);
    }
}
