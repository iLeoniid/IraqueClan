package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class LevelManager {

    private final IraqueClan plugin;

    public LevelManager(IraqueClan plugin) {
        this.plugin = plugin;
    }

    public void addXP(Clan clan, long amount) {
        if (clan == null || amount <= 0) return;
        clan.addXp(amount);
        checkLevelUp(clan);
        this.plugin.getClanManager().saveAll();
    }

    public long calculateXPForLevel(int level) {
        return (long) (level * 1000.0 * (level * 0.5));
    }

    public void checkLevelUp(Clan clan) {
        if (clan == null) return;
        ConfigurationSection levelConfig = this.plugin.getConfig().getConfigurationSection("niveis");
        if (levelConfig == null || !levelConfig.getBoolean("habilitado", true)) return;

        int maxLevel = levelConfig.getInt("nivel-maximo", 100);
        boolean broadcast = levelConfig.getBoolean("anuncio-subida-nivel", true);

        int currentLevel = clan.getLevel();
        long currentXP = clan.getXp();
        boolean leveledUp = false;

        while (currentLevel < maxLevel) {
            long requiredXP = calculateXPForLevel(currentLevel + 1);
            if (currentXP < requiredXP) break;
            currentLevel++;
            clan.setLevel(currentLevel);
            leveledUp = true;
            deliverRewards(clan, currentLevel, broadcast);
        }

        if (leveledUp) {
            this.plugin.getClanManager().saveAll();
        }
    }

    private void deliverRewards(Clan clan, int level, boolean broadcast) {
        List<?> rewards = this.plugin.getConfig().getList("niveis.recompensas");
        if (rewards == null) return;

        for (Object obj : rewards) {
            if (!(obj instanceof java.util.Map<?, ?> rewardMap)) continue;
            int rewardLevel = 0;
            try {
                rewardLevel = Integer.parseInt(String.valueOf(rewardMap.get("nivel")));
            } catch (NumberFormatException ignored) {
                continue;
            }
            if (rewardLevel != level) continue;

            double rewardMoney = 0;
            try {
                Object moneyObj = rewardMap.get("recompensa-banco");
                rewardMoney = moneyObj != null ? Double.parseDouble(String.valueOf(moneyObj)) : 0;
            } catch (NumberFormatException ignored) {}

            long rewardXP = 0;
            try {
                Object xpObj = rewardMap.get("recompensa-xp");
                rewardXP = xpObj != null ? Long.parseLong(String.valueOf(xpObj)) : 0;
            } catch (NumberFormatException ignored) {}

            if (rewardMoney > 0) {
                clan.addBank(rewardMoney);
            }
            if (rewardXP > 0) {
                clan.addXp(rewardXP);
            }

            @SuppressWarnings("unchecked")
            List<String> commands = (List<String>) rewardMap.get("comandos");
            if (commands != null) {
                for (String cmd : commands) {
                    String resolved = cmd
                            .replace("%clan%", clan.getName())
                            .replace("%level%", String.valueOf(level));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), resolved);
                }
            }

            String message = (String) rewardMap.get("mensagem");
            if (message != null && broadcast) {
                String formatted = message
                        .replace("%level%", String.valueOf(level))
                        .replace("%clan%", clan.getName());
                Bukkit.broadcastMessage(gg.leo.IraqueClan.utils.ItemBuilder.color(formatted));
            }
        }
    }

    public List<?> getRewardsForLevel(int level) {
        List<?> rewards = this.plugin.getConfig().getList("niveis.recompensas");
        if (rewards == null) return List.of();
        return rewards.stream().filter(obj -> {
            if (!(obj instanceof java.util.Map<?, ?> map)) return false;
            try {
                return Integer.parseInt(String.valueOf(map.get("nivel"))) == level;
            } catch (NumberFormatException e) {
                return false;
            }
        }).toList();
    }
}
