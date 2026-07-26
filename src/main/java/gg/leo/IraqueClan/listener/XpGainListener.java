package gg.leo.IraqueClan.listener;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.AchievementManager;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.clan.LevelManager;
import gg.leo.IraqueClan.clan.QuestManager;
import gg.leo.IraqueClan.clan.AchievementManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class XpGainListener implements Listener {

    private final IraqueClan plugin;
    private final LevelManager levelManager;
    private final QuestManager questManager;
    private final AchievementManager achievementManager;

    public XpGainListener(IraqueClan plugin) {
        this.plugin = plugin;
        this.levelManager = new LevelManager(plugin);
        this.questManager = new QuestManager(plugin);
        this.achievementManager = new AchievementManager(plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player killer)) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (victim instanceof Player) return;

        ConfigurationSection levels = this.plugin.getConfig().getConfigurationSection("niveis");
        if (levels == null || !levels.getBoolean("habilitado", true)) return;
        if (event.getFinalDamage() < victim.getHealth()) return;

        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(killer.getUniqueId());
        if (clan == null) return;

        int xpAmount = levels.getInt("xp-matar-monstro", 5);
        int boostPercent = clan.getUpgradeLevel("boost-xp") * levels.getInt("loja.upgrades.boost-xp.percentual-por-nivel", 25);
        xpAmount = xpAmount + (xpAmount * boostPercent / 100);

        this.levelManager.addXP(clan, xpAmount);
        this.questManager.updateProgress(clan, "MATAR_MONSTROS", 1);
        this.achievementManager.checkAndUnlock(clan);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null || killer.equals(victim)) return;

        ConfigurationSection levels = this.plugin.getConfig().getConfigurationSection("niveis");
        if (levels == null || !levels.getBoolean("habilitado", true)) return;

        Clan killerClan = this.plugin.getClanManager().getClanByPlayerDirect(killer.getUniqueId());
        Clan victimClan = this.plugin.getClanManager().getClanByPlayerDirect(victim.getUniqueId());

        this.plugin.getClanManager().addKill(killer.getUniqueId(), victim.getUniqueId());

        if (killerClan != null) {
            int xpAmount = levels.getInt("xp-matar-jogador", 25);
            int boostPercent = killerClan.getUpgradeLevel("boost-xp") * 25;
            xpAmount = xpAmount + (xpAmount * boostPercent / 100);

            this.levelManager.addXP(killerClan, xpAmount);
            this.questManager.updateProgress(killerClan, "MATAR_JOGADORES", 1);
            this.achievementManager.checkAndUnlock(killerClan);
        }

        if (victimClan != null) {
            this.achievementManager.checkAndUnlock(victimClan);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        ConfigurationSection levels = this.plugin.getConfig().getConfigurationSection("niveis");
        if (levels == null || !levels.getBoolean("habilitado", true)) return;

        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) return;

        int xpAmount = levels.getInt("xp-minerar", 1);
        int boostPercent = clan.getUpgradeLevel("boost-xp") * 25;
        xpAmount = xpAmount + (xpAmount * boostPercent / 100);

        this.levelManager.addXP(clan, xpAmount);
        this.questManager.updateProgress(clan, "MINERAR_BLOCOS", 1);
        this.achievementManager.checkAndUnlock(clan);
    }

    public LevelManager getLevelManager() {
        return this.levelManager;
    }

    public QuestManager getQuestManager() {
        return this.questManager;
    }

    public AchievementManager getAchievementManager() {
        return this.achievementManager;
    }
}
