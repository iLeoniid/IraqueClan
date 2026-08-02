package gg.leo.IraqueClan.config;

import gg.leo.IraqueClan.IraqueClan;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ClanConfigManager {
    private final IraqueClan plugin;
    private FileConfiguration config;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    
    // General settings
    private int maxMembers;
    private int maxNameLength;
    private int maxTagLength;
    private int minTagLength;
    private int maxDescLength;
    private double createCost;
    
    // War settings
    private int warMaxDuration; // minutes
    private int warAcceptTimeout; // seconds

    // Invite settings
    private long inviteTimeoutMillis; // millis
    private int maxPendingInvites;
    
    // Clan permissions by role
    private List<String> leaderPermissions;
    private List<String> subLeaderPermissions;
    private List<String> memberPermissions;
    
    private static final MiniMessage MINI = MiniMessage.miniMessage();

    public ClanConfigManager(IraqueClan plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
        this.loadGeneral();
        this.loadWar();
        this.loadInvites();
        this.loadPermissions();
    }

    public List<String> reloadAll() {
        ArrayList<String> reloaded = new ArrayList<>();
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
        reloaded.add("config.yml");
        this.loadGeneral();
        this.loadWar();
        this.loadInvites();
        this.loadPermissions();
        reloaded.add("config settings");
        this.loadMessages();
        reloaded.add("messages.yml");
        if (this.plugin.getQuestManager() != null) {
            this.plugin.getQuestManager().reloadConfig();
            reloaded.add("quests.yml");
        }
        if (this.plugin.getClanManager() != null) {
            this.plugin.getClanManager().load();
            reloaded.add("clan data");
        }
        if (this.plugin.getAchievementManager() != null) {
            this.plugin.getAchievementManager().loadAchievements();
            reloaded.add("achievements");
        }
        return reloaded;
    }

    public void loadMessages() {
        this.messagesFile = new File(this.plugin.getDataFolder(), "messages.yml");
        if (!this.messagesFile.exists()) {
            this.plugin.saveResource("messages.yml", false);
        }
        this.messagesConfig = YamlConfiguration.loadConfiguration(this.messagesFile);
    }

    private void loadGeneral() {
        ConfigurationSection section = this.config.getConfigurationSection("criacao");
        if (section == null) return;
        this.maxMembers = section.getInt("max-membros", 20);
        this.maxNameLength = section.getInt("max-nome-tamanho", 20);
        this.maxTagLength = section.getInt("max-tag-tamanho", 5);
        this.minTagLength = section.getInt("min-tag-tamanho", 2);
        this.createCost = section.getDouble("custo-criacao", 0);
        ConfigurationSection perfis = this.config.getConfigurationSection("perfis");
        this.maxDescLength = perfis != null ? perfis.getInt("max-desc-tamanho", 200) : 200;
    }

    private void loadWar() {
        ConfigurationSection section = this.config.getConfigurationSection("guerra");
        if (section == null) return;
        this.warMaxDuration = section.getInt("duracao-maxima-minutos", 1440);
        this.warAcceptTimeout = section.getInt("tempo-aceitar-segundos", 60);
    }

    private void loadInvites() {
        ConfigurationSection section = this.config.getConfigurationSection("convites");
        this.inviteTimeoutMillis = section != null
                ? section.getLong("tempo-expiracao-segundos", 120) * 1000L : 120_000L;
        this.maxPendingInvites = section != null ? section.getInt("max-pendentes", 5) : 5;
    }

    private void loadPermissions() {
        ConfigurationSection section = this.config.getConfigurationSection("permissions");
        if (section == null) {
            this.leaderPermissions = List.of("iraqueclan.*");
            this.subLeaderPermissions = List.of("iraqueclan.invite", "iraqueclan.kick", "iraqueclan.promote", "iraqueclan.demote");
            this.memberPermissions = List.of("iraqueclan.use", "iraqueclan.chat");
            return;
        }
        this.leaderPermissions = section.getStringList("LIDER");
        this.subLeaderPermissions = section.getStringList("SUB_LIDER");
        this.memberPermissions = section.getStringList("MEMBRO");
    }

    public String getMessage(String path) {
        return this.getMessage(path, "&#FF5555Mensagem não encontrada: " + path);
    }

    public String getMessage(String path, String fallback) {
        if (this.messagesConfig == null) return fallback;
        return this.messagesConfig.getString(path, fallback);
    }

    public String getPrefixedMessage(String path) {
        String prefix = this.getMessage("prefix", "&#e63946[&#f1faeeIraqueClan&#e63946] &#FFFFFF");
        return this.translate(prefix + this.getMessage(path));
    }

    public String translate(String s) {
        if (s == null) return "";
        s = Pattern.compile("[&\u00a7]x([&\u00a7]([0-9a-fA-F])){6}", 2).matcher(s).replaceAll(mr -> {
            String m = mr.group();
            StringBuilder hex = new StringBuilder("\u00a7x");
            for (int i = 4; i < m.length(); i += 2) {
                hex.append('\u00a7').append(Character.toLowerCase(m.charAt(i)));
            }
            return hex.toString();
        });
        s = Pattern.compile("&#([0-9a-fA-F]{6})").matcher(s).replaceAll(mr -> {
            String hex = mr.group(1).toLowerCase();
            return "\u00a7x\u00a7" + String.join("\u00a7", hex.split(""));
        });
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if ((c == '&' || c == '\u00a7') && i + 1 < s.length()) {
                char next = Character.toLowerCase(s.charAt(i + 1));
                if ((next >= '0' && next <= '9') || (next >= 'a' && next <= 'f') || next == 'k' || next == 'l' || next == 'm' || next == 'n' || next == 'o' || next == 'r') {
                    sb.append('\u00a7').append(next);
                    ++i;
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private String getLegacyColorMap(char c) {
        return switch (Character.toLowerCase(c)) {
            case '0' -> "<black>";
            case '1' -> "<dark_blue>";
            case '2' -> "<dark_green>";
            case '3' -> "<dark_aqua>";
            case '4' -> "<dark_red>";
            case '5' -> "<dark_purple>";
            case '6' -> "<gold>";
            case '7' -> "<gray>";
            case '8' -> "<dark_gray>";
            case '9' -> "<blue>";
            case 'a' -> "<green>";
            case 'b' -> "<aqua>";
            case 'c' -> "<red>";
            case 'd' -> "<light_purple>";
            case 'e' -> "<yellow>";
            case 'f' -> "<white>";
            case 'k' -> "<obfuscated>";
            case 'l' -> "<bold>";
            case 'm' -> "<strikethrough>";
            case 'n' -> "<underlined>";
            case 'o' -> "<italic>";
            case 'r' -> "<reset>";
            default -> null;
        };
    }

    public Component deserialize(String miniString) {
        return MINI.deserialize(miniString);
    }

    public String toLegacy(String s) {
        if (s == null) return "";
        return LegacyComponentSerializer.legacySection().serialize(MINI.deserialize(s));
    }

    // Getters
    public int getMaxMembers() { return this.maxMembers; }
    public int getMaxNameLength() { return this.maxNameLength; }
    public int getMaxTagLength() { return this.maxTagLength; }
    public int getMinTagLength() { return this.minTagLength; }
    public int getMaxDescLength() { return this.maxDescLength; }
    public double getCreateCost() { return this.createCost; }
    public int getWarMaxDurationMinutes() { return this.warMaxDuration; }
    public int getWarAcceptTimeoutSeconds() { return this.warAcceptTimeout; }
    public long getInviteTimeoutMillis() { return this.inviteTimeoutMillis; }
    public int getMaxPendingInvites() { return this.maxPendingInvites; }
    public List<String> getLeaderPermissions() { return this.leaderPermissions; }
    public List<String> getSubLeaderPermissions() { return this.subLeaderPermissions; }
    public List<String> getMemberPermissions() { return this.memberPermissions; }
}
