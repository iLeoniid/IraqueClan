package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ClanAchievementCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanAchievementCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-in-clan"));
            return;
        }
        ConfigurationSection achievementsSection = this.plugin.getConfig().getConfigurationSection("conquistas.lista");
        if (achievementsSection == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("achievement.list-header"));
            return;
        }
        List<String> unlocked = this.plugin.getClanManager().getAchievements(player.getUniqueId());
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("achievement.list-header"));
        for (String id : achievementsSection.getKeys(false)) {
            ConfigurationSection achSection = achievementsSection.getConfigurationSection(id);
            if (achSection == null) continue;
            String name = achSection.getString("nome", id);
            String description = achSection.getString("descricao", "");
            boolean isUnlocked = unlocked.contains(id);
            String status = isUnlocked
                    ? this.plugin.getConfigManager().getPrefixedMessage("achievement.completed")
                    : this.plugin.getConfigManager().getPrefixedMessage("achievement.locked");
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("achievement.list-entry")
                    .replace("{status}", status)
                    .replace("{name}", name)
                    .replace("{description}", description));
        }
    }
}
