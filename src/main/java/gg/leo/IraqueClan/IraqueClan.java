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
import gg.leo.IraqueClan.placeholder.ClanPlaceholderExpansion;
import gg.leo.IraqueClan.utils.ClanUtils;
import gg.leo.IraqueClan.utils.StartupReport;
import gg.leo.IraqueClan.utils.StartupReport.Step;
import gg.leo.IraqueClan.utils.menu.MenuListener;
import gg.leo.IraqueClan.war.WarListener;
import gg.leo.IraqueClan.war.WarManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class IraqueClan extends JavaPlugin {
    private ClanManager clanManager;
    private WarManager warManager;
    private ClanConfigManager configManager;
    private AchievementManager achievementManager;
    private QuestManager questManager;

    @Override
    public void onEnable() {
        StartupReport report = new StartupReport(this.getLogger());
        String version = this.getDescription().getVersion();

        report.printBanner(version);

        // 1. Config
        Step step = report.startStep("Carregando config.yml");
        try {
            this.saveDefaultConfig();
            this.configManager = new ClanConfigManager(this);
            this.configManager.load();
            report.finishStep(step, "Configuracao carregada");
        } catch (Exception e) {
            report.finishStepError(step, "Erro ao carregar config.yml: " + e.getMessage());
            this.getLogger().severe("FATAL: Nao foi possivel carregar config.yml. Desabilitando plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 2. Messages
        step = report.startStep("Carregando messages.yml");
        try {
            this.configManager.loadMessages();
            report.finishStep(step, "Mensagens carregadas");
        } catch (Exception e) {
            report.finishStepWarning(step, "Erro ao carregar messages.yml: " + e.getMessage() + " (usando fallbacks)");
        }

        // 3. Clan data
        step = report.startStep("Carregando dados dos clãs");
        try {
            this.clanManager = new ClanManager(this);
            this.clanManager.load();
            int count = this.clanManager.getAllClans().size();
            report.finishStep(step, count + " clã(s) carregado(s)");
        } catch (Exception e) {
            report.finishStepError(step, "Erro ao carregar dados: " + e.getMessage());
            this.getLogger().severe("FATAL: Nao foi possivel carregar dados dos clãs. Desabilitando plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 4. Achievements
        step = report.startStep("Carregando sistema de conquistas");
        try {
            this.achievementManager = new AchievementManager(this);
            this.achievementManager.loadAchievements();
            int count = this.achievementManager.getAllAchievements().size();
            report.finishStep(step, count + " conquista(s) definida(s)");
        } catch (Exception e) {
            report.finishStepWarning(step, "Erro ao carregar conquistas: " + e.getMessage());
        }

        // 5. Quests
        step = report.startStep("Carregando sistema de missões");
        try {
            this.questManager = new QuestManager(this);
            int questCount = 0;
            for (var clan : this.clanManager.getAllClans()) {
                this.questManager.generateQuests(clan);
                questCount += clan.getActiveQuests().size();
            }
            report.finishStep(step, questCount + " quest(s) ativa(s)");
        } catch (Exception e) {
            report.finishStepWarning(step, "Erro ao carregar missões: " + e.getMessage());
        }

        // 6. War system
        step = report.startStep("Carregando sistema de guerras");
        try {
            this.warManager = new WarManager(this);
            this.warManager.startTask();
            report.finishStep(step, "Tarefa de guerra iniciada");
        } catch (Exception e) {
            report.finishStepWarning(step, "Erro ao carregar sistema de guerras: " + e.getMessage());
        }

        // 7. ClanUtils
        step = report.startStep("Inicializando utilitários");
        try {
            ClanUtils.init(this);
            report.finishStep(step);
        } catch (Exception e) {
            report.finishStepWarning(step, "Erro ao inicializar utilitários: " + e.getMessage());
        }

        // 8. Commands
        step = report.startStep("Registrando comandos");
        try {
            this.registerCommands();
            report.finishStep(step, "/clan e /clana");
        } catch (Exception e) {
            report.finishStepError(step, "Erro ao registrar comandos: " + e.getMessage());
        }

        // 9. Listeners
        step = report.startStep("Registrando listeners");
        try {
            this.registerListeners();
            report.finishStep(step, "7 listeners");
        } catch (Exception e) {
            report.finishStepError(step, "Erro ao registrar listeners: " + e.getMessage());
        }

        // 10. Vault check
        step = report.startStep("Verificando integração com Vault");
        try {
            if (this.getServer().getPluginManager().getPlugin("Vault") != null) {
                report.finishStep(step, "Vault encontrado");
            } else {
                report.finishStepSkipped(step, "Vault não encontrado (economia desativada)");
            }
        } catch (Exception e) {
            report.finishStepSkipped(step, "Não foi possível verificar Vault");
        }

        // 11. IraqueCore check
        step = report.startStep("Verificando dependência IraqueCore");
        try {
            if (this.getServer().getPluginManager().getPlugin("IraqueCore") != null) {
                report.finishStep(step, "IraqueCore encontrado");
            } else {
                report.finishStepWarning(step, "IraqueCore NÃO encontrado! Plugin pode não funcionar corretamente.");
            }
        } catch (Exception e) {
            report.finishStepWarning(step, "Não foi possível verificar IraqueCore");
        }

        // 12. PlaceholderAPI
        step = report.startStep("Registrando placeholders (PlaceholderAPI)");
        try {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new ClanPlaceholderExpansion(this).register();
                report.finishStep(step, "Placeholders registrados");
            } else {
                report.finishStepSkipped(step, "PlaceholderAPI não encontrado");
            }
        } catch (Exception e) {
            report.finishStepWarning(step, "Erro ao registrar placeholders: " + e.getMessage());
        }

        // Summary
        report.printSummary(version);
    }

    @Override
    public void onDisable() {
        if (this.clanManager != null) {
            try {
                this.clanManager.saveAll();
                this.getLogger().info("[IraqueClan] Dados dos clãs salvos com sucesso.");
            } catch (Exception e) {
                this.getLogger().severe("[IraqueClan] Erro ao salvar dados: " + e.getMessage());
            }
        }
        if (this.warManager != null) {
            try {
                this.warManager.shutdown();
            } catch (Exception e) {
                this.getLogger().severe("[IraqueClan] Erro ao desligar sistema de guerras: " + e.getMessage());
            }
        }
        this.getLogger().info("[IraqueClan] Plugin desabilitado.");
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
        this.getLogger().info("[Discord] " + message);
    }
}
