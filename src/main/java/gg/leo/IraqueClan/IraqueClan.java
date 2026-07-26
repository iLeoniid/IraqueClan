package gg.leo.IraqueClan;

import gg.leo.IraqueClan.admin.AdminCommand;
import gg.leo.IraqueClan.clan.AchievementManager;
import gg.leo.IraqueClan.clan.ClanCommand;
import gg.leo.IraqueClan.clan.ClanManager;
import gg.leo.IraqueClan.clan.QuestManager;
import gg.leo.IraqueClan.config.ClanConfigManager;
import gg.leo.IraqueClan.listener.ClanChatListener;
import gg.leo.IraqueClan.listener.ClanJoinQuitListener;
import gg.leo.IraqueClan.listener.ClanProtectionListener;
import gg.leo.IraqueClan.listener.ClanScoreboardListener;
import gg.leo.IraqueClan.listener.XpGainListener;
import gg.leo.IraqueClan.utils.ClanUtils;
import gg.leo.IraqueClan.utils.menu.MenuListener;
import gg.leo.IraqueClan.war.WarListener;
import gg.leo.IraqueClan.war.WarManager;
import org.bukkit.plugin.java.JavaPlugin;

public class IraqueClan extends JavaPlugin {
    private ClanManager clanManager;
    private WarManager warManager;
    private ClanConfigManager configManager;
    private AchievementManager achievementManager;
    private QuestManager questManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.configManager = new ClanConfigManager(this);
        this.configManager.load();
        this.clanManager = new ClanManager(this);
        this.clanManager.load();
        this.achievementManager = new AchievementManager(this);
        this.achievementManager.loadAchievements();
        this.questManager = new QuestManager(this);
        for (var clan : this.clanManager.getAllClans()) {
            this.questManager.generateQuests(clan);
        }
        this.warManager = new WarManager(this);
        this.warManager.startTask();
        ClanUtils.init(this);
        this.registerCommands();
        this.registerListeners();
        this.getLogger().info("IraqueClan habilitado com sucesso!");
    }

    @Override
    public void onDisable() {
        this.clanManager.saveAll();
        this.warManager.shutdown();
        this.getLogger().info("IraqueClan desabilitado.");
    }

    private void registerCommands() {
        ClanCommand clanCmd = new ClanCommand(this);
        this.getCommand("clan").setExecutor(clanCmd);
        this.getCommand("clan").setTabCompleter(clanCmd);
        AdminCommand adminCmd = new AdminCommand(this);
        this.getCommand("clana").setExecutor(adminCmd);
        this.getCommand("clana").setTabCompleter(adminCmd);
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new MenuListener(), this);
        this.getServer().getPluginManager().registerEvents(new ClanJoinQuitListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ClanChatListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ClanScoreboardListener(this), this);
        this.getServer().getPluginManager().registerEvents(new WarListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ClanProtectionListener(this), this);
        this.getServer().getPluginManager().registerEvents(new XpGainListener(this), this);
    }

    public ClanManager getClanManager() {
        return this.clanManager;
    }

    public WarManager getWarManager() {
        return this.warManager;
    }

    public ClanConfigManager getConfigManager() {
        return this.configManager;
    }

    public AchievementManager getAchievementManager() {
        return this.achievementManager;
    }

    public QuestManager getQuestManager() {
        return this.questManager;
    }

    public void sendDiscordMessage(String message) {
        if (message == null || message.isEmpty()) return;
        // Placeholder for Discord webhook integration
        this.getLogger().info("[Discord] " + message);
    }
}
