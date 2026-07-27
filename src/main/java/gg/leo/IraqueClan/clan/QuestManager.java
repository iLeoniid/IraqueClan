package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class QuestManager {

    private final IraqueClan plugin;
    private FileConfiguration questsConfig;

    public QuestManager(IraqueClan plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        this.questsConfig = new YamlConfiguration();
        java.io.File file = new java.io.File(this.plugin.getDataFolder(), "quests.yml");
        if (!file.exists()) {
            this.plugin.saveResource("quests.yml", false);
        }
        try {
            this.questsConfig.load(file);
        } catch (Exception e) {
            this.plugin.getLogger().severe("Erro ao carregar quests.yml: " + e.getMessage());
        }
    }

    public void reloadConfig() {
        loadConfig();
    }

    public FileConfiguration getConfig() {
        return this.questsConfig;
    }

    public void generateQuests(Clan clan) {
        if (clan == null) return;
        ConfigurationSection section = this.questsConfig.getConfigurationSection("missoes");
        if (section == null || !section.getBoolean("habilitado", true)) return;

        int maxQuests = section.getInt("max-missoes-ativas", 3);
        ConfigurationSection typesSection = section.getConfigurationSection("tipos");
        if (typesSection == null) return;

        Map<String, Clan.ClanQuest> active = clan.getActiveQuests();
        if (active.size() >= maxQuests) return;

        List<String> availableTypes = new ArrayList<>(typesSection.getKeys(false));
        if (availableTypes.isEmpty()) return;

        int toGenerate = maxQuests - active.size();
        java.util.Collections.shuffle(availableTypes);

        for (int i = 0; i < Math.min(toGenerate, availableTypes.size()); i++) {
            String questType = availableTypes.get(i);
            ConfigurationSection typeSection = typesSection.getConfigurationSection(questType);
            if (typeSection == null) continue;

            String name = typeSection.getString("nome", questType);
            int minQty = typeSection.getInt("quantidade-minima", 10);
            int maxQty = typeSection.getInt("quantidade-maxima", 100);
            int required = minQty + (int) (Math.random() * (maxQty - minQty + 1));

            double xpMultiplier = typeSection.getDouble("multiplicador-recompensa-xp", 5);
            double moneyMultiplier = typeSection.getDouble("multiplicador-recompensa-dinheiro", 0.1);
            double rewardXP = required * xpMultiplier;
            double rewardMoney = required * moneyMultiplier;

            String questId = questType.toLowerCase() + "-" + System.currentTimeMillis() + "-" + i;
            Clan.ClanQuest quest = new Clan.ClanQuest(questId, questType, required, 0, rewardXP, rewardMoney);
            clan.addQuest(quest);
        }

        this.plugin.getClanManager().saveAll();
    }

    public void updateProgress(Clan clan, String questType, int amount) {
        if (clan == null || questType == null || amount <= 0) return;
        Map<String, Clan.ClanQuest> active = clan.getActiveQuests();

        for (Map.Entry<String, Clan.ClanQuest> entry : active.entrySet()) {
            Clan.ClanQuest quest = entry.getValue();
            if (!quest.type().equalsIgnoreCase(questType)) continue;
            if (quest.isComplete()) continue;

            int newCurrent = Math.min(quest.current() + amount, quest.required());
            clan.removeQuest(quest.id());
            clan.addQuest(quest.withProgress(newCurrent));

            if (quest.withProgress(newCurrent).isComplete()) {
                completeQuest(clan, quest.id());
            }
        }
        this.plugin.getClanManager().saveAll();
    }

    public void completeQuest(Clan clan, String questId) {
        if (clan == null || questId == null) return;
        Clan.ClanQuest quest = clan.getQuest(questId);
        if (quest == null || !quest.isComplete()) return;

        clan.addXp((long) quest.rewardXP());
        clan.addBank(quest.rewardMoney());
        clan.removeQuest(questId);

        this.plugin.getClanManager().saveAll();
    }

    public Map<String, Clan.ClanQuest> getActiveQuests(Clan clan) {
        if (clan == null) return Map.of();
        return clan.getActiveQuests();
    }
}
