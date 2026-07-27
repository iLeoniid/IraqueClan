package gg.leo.IraqueClan.listener;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.AchievementManager;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.clan.LevelManager;
import gg.leo.IraqueClan.clan.QuestManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;

import java.util.HashSet;
import java.util.Set;

public class XpGainListener implements Listener {

    private final IraqueClan plugin;
    private final LevelManager levelManager;
    private final QuestManager questManager;
    private final AchievementManager achievementManager;

    private static final Set<Material> LOG_MATERIALS = new HashSet<>();
    private static final Set<Material> CROP_MATERIALS = new HashSet<>();
    private static final Set<Material> SEED_MATERIALS = new HashSet<>();
    private static final Set<Material> FOOD_MATERIALS = new HashSet<>();

    static {
        for (Material m : Material.values()) {
            String name = m.name();
            if (name.endsWith("_LOG") || name.endsWith("_WOOD") || name.equals("CRIMSON_STEM") || name.equals("WARPED_STEM") || name.equals("MANGROVE_ROOTS") || name.equals("MANGROVE_LOG")) {
                LOG_MATERIALS.add(m);
            }
        }
        CROP_MATERIALS.add(Material.WHEAT);
        CROP_MATERIALS.add(Material.CARROTS);
        CROP_MATERIALS.add(Material.POTATOES);
        CROP_MATERIALS.add(Material.BEETROOTS);
        CROP_MATERIALS.add(Material.NETHER_WART);
        CROP_MATERIALS.add(Material.MELON);
        CROP_MATERIALS.add(Material.PUMPKIN);
        CROP_MATERIALS.add(Material.COCOA);

        SEED_MATERIALS.add(Material.WHEAT_SEEDS);
        SEED_MATERIALS.add(Material.BEETROOT_SEEDS);
        SEED_MATERIALS.add(Material.PUMPKIN_SEEDS);
        SEED_MATERIALS.add(Material.MELON_SEEDS);
        SEED_MATERIALS.add(Material.NETHER_WART);
        SEED_MATERIALS.add(Material.CARROT);
        SEED_MATERIALS.add(Material.POTATO);

        FOOD_MATERIALS.add(Material.BREAD);
        FOOD_MATERIALS.add(Material.COOKED_BEEF);
        FOOD_MATERIALS.add(Material.COOKED_PORKCHOP);
        FOOD_MATERIALS.add(Material.COOKED_MUTTON);
        FOOD_MATERIALS.add(Material.COOKED_CHICKEN);
        FOOD_MATERIALS.add(Material.COOKED_RABBIT);
        FOOD_MATERIALS.add(Material.COOKED_COD);
        FOOD_MATERIALS.add(Material.COOKED_SALMON);
        FOOD_MATERIALS.add(Material.BAKED_POTATO);
        FOOD_MATERIALS.add(Material.PUMPKIN_PIE);
        FOOD_MATERIALS.add(Material.GOLDEN_CARROT);
        FOOD_MATERIALS.add(Material.GOLDEN_APPLE);
        FOOD_MATERIALS.add(Material.ENCHANTED_GOLDEN_APPLE);
        FOOD_MATERIALS.add(Material.APPLE);
        FOOD_MATERIALS.add(Material.COOKIE);
        FOOD_MATERIALS.add(Material.MELON_SLICE);
        FOOD_MATERIALS.add(Material.SWEET_BERRIES);
        FOOD_MATERIALS.add(Material.GLOW_BERRIES);
        FOOD_MATERIALS.add(Material.CARROT);
        FOOD_MATERIALS.add(Material.BEETROOT);
        FOOD_MATERIALS.add(Material.POTATO);
        FOOD_MATERIALS.add(Material.MUSHROOM_STEW);
        FOOD_MATERIALS.add(Material.RABBIT_STEW);
        FOOD_MATERIALS.add(Material.BEETROOT_SOUP);
        FOOD_MATERIALS.add(Material.DRIED_KELP);
        FOOD_MATERIALS.add(Material.CHORUS_FRUIT);
    }

    public XpGainListener(IraqueClan plugin) {
        this.plugin = plugin;
        this.levelManager = new LevelManager(plugin);
        this.questManager = new QuestManager(plugin);
        this.achievementManager = new AchievementManager(plugin);
    }

    private void handleProgress(Player player, String questType, int amount) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) return;

        this.questManager.updateProgress(clan, questType, amount);
        this.achievementManager.checkAndUnlock(clan);
    }

    private Clan getClanAndProgress(Player player, String questType, int xpBase) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) return null;

        ConfigurationSection levels = this.plugin.getConfig().getConfigurationSection("niveis");
        if (levels != null && levels.getBoolean("habilitado", true)) {
            int boostPercent = clan.getUpgradeLevel("boost-xp") * 25;
            int xpAmount = xpBase + (xpBase * boostPercent / 100);
            this.levelManager.addXP(clan, xpAmount);
        }

        this.questManager.updateProgress(clan, questType, 1);
        this.achievementManager.checkAndUnlock(clan);
        return clan;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player killer)) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (victim instanceof Player) return;

        if (event.getFinalDamage() < victim.getHealth()) return;

        ConfigurationSection levels = this.plugin.getConfig().getConfigurationSection("niveis");
        if (levels == null || !levels.getBoolean("habilitado", true)) return;

        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(killer.getUniqueId());
        if (clan == null) return;

        int xpAmount = levels.getInt("xp-matar-monstro", 5);
        int boostPercent = clan.getUpgradeLevel("boost-xp") * 25;
        xpAmount = xpAmount + (xpAmount * boostPercent / 100);

        this.levelManager.addXP(clan, xpAmount);
        this.questManager.updateProgress(clan, "MATAR_MONSTROS", 1);
        this.achievementManager.checkAndUnlock(clan);

        if (event.getDamager() instanceof org.bukkit.entity.Arrow arrow) {
            if (arrow.getShooter() instanceof Player) {
                this.handleProgress(killer, "USAR_ARCO", 1);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null || killer.equals(victim)) return;

        Clan killerClan = this.plugin.getClanManager().getClanByPlayerDirect(killer.getUniqueId());
        Clan victimClan = this.plugin.getClanManager().getClanByPlayerDirect(victim.getUniqueId());

        this.plugin.getClanManager().addKill(killer.getUniqueId(), victim.getUniqueId());

        if (killerClan != null) {
            ConfigurationSection levels = this.plugin.getConfig().getConfigurationSection("niveis");
            int xpAmount = 25;
            if (levels != null) {
                xpAmount = levels.getInt("xp-matar-jogador", 25);
                int boostPercent = killerClan.getUpgradeLevel("boost-xp") * 25;
                xpAmount = xpAmount + (xpAmount * boostPercent / 100);
            }

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

        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) return;

        ConfigurationSection levels = this.plugin.getConfig().getConfigurationSection("niveis");
        if (levels != null && levels.getBoolean("habilitado", true)) {
            int xpAmount = levels.getInt("xp-minerar", 1);
            int boostPercent = clan.getUpgradeLevel("boost-xp") * 25;
            xpAmount = xpAmount + (xpAmount * boostPercent / 100);
            this.levelManager.addXP(clan, xpAmount);
        }

        Material type = event.getBlock().getType();

        if (LOG_MATERIALS.contains(type)) {
            this.questManager.updateProgress(clan, "CORTAR_MADEIRA", 1);
        }

        if (CROP_MATERIALS.contains(type)) {
            this.questManager.updateProgress(clan, "COLHER_PLANTAS", 1);
        }

        this.questManager.updateProgress(clan, "MINERAR_BLOCOS", 1);
        this.achievementManager.checkAndUnlock(clan);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) return;

        this.questManager.updateProgress(clan, "CONSTRUIR_BLOCOS", 1);
        this.achievementManager.checkAndUnlock(clan);

        Material type = event.getItemInHand().getType();
        if (SEED_MATERIALS.contains(type) || type.name().endsWith("_SAPLING")) {
            this.questManager.updateProgress(clan, "PLANTAR", 1);
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof org.bukkit.entity.Item)) return;

        this.handleProgress(event.getPlayer(), "PESCAR", 1);
    }

    @EventHandler
    public void onEntityTame(org.bukkit.event.entity.EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player player)) return;

        this.handleProgress(player, "DOMAR_ANIMAIS", 1);
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        this.handleProgress(player, "USAR_ARCO", 1);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        this.handleProgress(player, "CRAFTAR", 1);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Material type = event.getItem().getType();

        if (FOOD_MATERIALS.contains(type)) {
            this.handleProgress(player, "COMER", 1);
        }

        if (type.name().endsWith("_POTION") || type.name().endsWith("_WATER_BOTTLE") || type.equals(Material.MILK_BUCKET)) {
            this.handleProgress(player, "BEBER", 1);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        this.handleProgress(event.getPlayer(), "USAR_BALDE", 1);
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        this.handleProgress(event.getEnchanter(), "ENCANTAR", 1);
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
