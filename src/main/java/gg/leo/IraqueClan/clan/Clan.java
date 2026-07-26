package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.clan.role.ClanRole;
import java.util.*;

public class Clan {

    public enum DiplomacyType {
        ALLY,
        RIVAL,
        NEUTRAL
    }

    public static class SimpleLocation {
        private final String world;
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;

        public SimpleLocation(String world, double x, double y, double z, float yaw, float pitch) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public org.bukkit.Location toBukkitLocation() {
            org.bukkit.World w = org.bukkit.Bukkit.getWorld(this.world);
            return new org.bukkit.Location(w, this.x, this.y, this.z, this.yaw, this.pitch);
        }

        public static SimpleLocation fromBukkitLocation(org.bukkit.Location loc) {
            return new SimpleLocation(
                    loc.getWorld() != null ? loc.getWorld().getName() : "world",
                    loc.getX(), loc.getY(), loc.getZ(),
                    loc.getYaw(), loc.getPitch()
            );
        }

        public String getWorld() { return this.world; }
        public double getX() { return this.x; }
        public double getY() { return this.y; }
        public double getZ() { return this.z; }
        public float getYaw() { return this.yaw; }
        public float getPitch() { return this.pitch; }
    }

    public record ClanMail(UUID sender, String message, long timestamp) {}

    public record ClanQuest(String id, String type, int required, int current, double rewardXP, double rewardMoney) {
        public boolean isComplete() {
            return this.current >= this.required;
        }

        public ClanQuest withProgress(int newCurrent) {
            return new ClanQuest(this.id, this.type, this.required, newCurrent, this.rewardXP, this.rewardMoney);
        }
    }

    public record ClanLog(String action, UUID player, String details, long timestamp) {}

    public record DiplomacyRelation(UUID otherClanUUID, DiplomacyType type, long timestamp) {}

    private static final int BASE_MAX_MEMBERS = 20;
    private static final int MAX_HOMES_BASE = 1;
    private static final int MAX_UPGRADE_HOMES_BONUS = 5;

    private final String name;
    private String tag;
    private String tagColor;
    private UUID leader;
    private final Map<UUID, ClanRole> members;
    private long createdAt;
    private int totalKills;
    private long lastActiveTime;

    private double bank;
    private String description;
    private String icon;
    private String motd;
    private int level;
    private long xp;
    private int deaths;
    private int warWins;
    private int warLosses;
    private int warDraws;

    private final Map<String, SimpleLocation> homes;
    private final List<ClanMail> mails;
    private final Map<String, Integer> upgrades;
    private final List<DiplomacyRelation> diplomacy;
    private final List<String> achievements;
    private final Map<String, ClanQuest> activeQuests;
    private final List<ClanLog> logs;
    private final Map<ClanRole, String> roleNames;
    private final Map<UUID, Long> killCooldowns;

    public Clan(String name, String tag, String tagColor, UUID leader) {
        this.name = name;
        this.tag = tag;
        this.tagColor = tagColor;
        this.leader = leader;
        this.members = new HashMap<>();
        this.createdAt = System.currentTimeMillis();
        this.totalKills = 0;
        this.lastActiveTime = System.currentTimeMillis();
        this.bank = 0.0;
        this.description = "";
        this.icon = "PAPER";
        this.motd = "";
        this.level = 1;
        this.xp = 0;
        this.deaths = 0;
        this.warWins = 0;
        this.warLosses = 0;
        this.warDraws = 0;
        this.homes = new HashMap<>();
        this.mails = new ArrayList<>();
        this.upgrades = new HashMap<>();
        this.diplomacy = new ArrayList<>();
        this.achievements = new ArrayList<>();
        this.activeQuests = new HashMap<>();
        this.logs = new ArrayList<>();
        this.roleNames = new EnumMap<>(ClanRole.class);
        this.killCooldowns = new HashMap<>();
        this.members.put(leader, ClanRole.LIDER);
    }

    public Clan(String name, String tag, String tagColor, UUID leader,
                Map<UUID, ClanRole> members, long createdAt, int totalKills, long lastActiveTime) {
        this.name = name;
        this.tag = tag;
        this.tagColor = tagColor;
        this.leader = leader;
        this.members = members != null ? members : new HashMap<>();
        this.createdAt = createdAt;
        this.totalKills = totalKills;
        this.lastActiveTime = lastActiveTime;
        this.bank = 0.0;
        this.description = "";
        this.icon = "PAPER";
        this.motd = "";
        this.level = 1;
        this.xp = 0;
        this.deaths = 0;
        this.warWins = 0;
        this.warLosses = 0;
        this.warDraws = 0;
        this.homes = new HashMap<>();
        this.mails = new ArrayList<>();
        this.upgrades = new HashMap<>();
        this.diplomacy = new ArrayList<>();
        this.achievements = new ArrayList<>();
        this.activeQuests = new HashMap<>();
        this.logs = new ArrayList<>();
        this.roleNames = new EnumMap<>(ClanRole.class);
        this.killCooldowns = new HashMap<>();
    }

    // ===== Basic getters/setters =====

    public String getName() { return this.name; }

    public String getTag() { return this.tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getTagColor() { return this.tagColor; }
    public void setTagColor(String tagColor) { this.tagColor = tagColor; }

    public UUID getLeader() { return this.leader; }
    public void setLeader(UUID leader) { this.leader = leader; }

    public Map<UUID, ClanRole> getMembers() { return this.members; }

    public long getCreatedAt() { return this.createdAt; }

    public int getTotalKills() { return this.totalKills; }
    public void addKills(int kills) { this.totalKills += kills; }

    public long getLastActiveTime() { return this.lastActiveTime; }
    public void setLastActiveTime(long lastActiveTime) { this.lastActiveTime = lastActiveTime; }

    // ===== Member helpers =====

    public void addMember(UUID uuid, ClanRole role) { this.members.put(uuid, role); }
    public void removeMember(UUID uuid) { this.members.remove(uuid); }
    public boolean isMember(UUID uuid) { return this.members.containsKey(uuid); }

    public ClanRole getMemberRole(UUID uuid) {
        return this.members.getOrDefault(uuid, null);
    }

    public void setMemberRole(UUID uuid, ClanRole role) {
        if (this.members.containsKey(uuid)) {
            this.members.put(uuid, role);
        }
    }

    public int getMemberCount() { return this.members.size(); }

    public String getFormattedTag() {
        if (this.tagColor == null || this.tagColor.isEmpty()) {
            return "[" + this.tag + "]";
        }
        return this.tagColor + "[" + this.tag + "]";
    }

    public boolean canKick(UUID requester) {
        ClanRole role = this.members.get(requester);
        return role != null && role.isSameOrHigher(ClanRole.SUB_LIDER);
    }

    public boolean canPromote(UUID requester) {
        ClanRole role = this.members.get(requester);
        return role != null && role.isHigherThan(ClanRole.MEMBRO);
    }

    public boolean canDisband(UUID requester) {
        return this.leader.equals(requester);
    }

    // ===== Bank / Economy =====

    public double getBank() { return this.bank; }
    public void setBank(double bank) { this.bank = bank; }
    public void addBank(double amount) { this.bank += amount; }

    public boolean removeBank(double amount) {
        if (this.bank < amount) return false;
        this.bank -= amount;
        return true;
    }

    public boolean hasBank(double amount) { return this.bank >= amount; }

    // ===== Description / Profile =====

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return this.icon; }
    public void setIcon(String icon) { this.icon = icon; }

    // ===== MOTD =====

    public String getMotd() { return this.motd; }
    public void setMotd(String motd) { this.motd = motd; }
    public void clearMotd() { this.motd = ""; }

    // ===== Level / XP =====

    public int getLevel() { return this.level; }
    public void setLevel(int level) { this.level = level; }

    public long getXp() { return this.xp; }
    public void setXp(long xp) { this.xp = xp; }

    public void addXp(long amount) { this.xp += amount; }

    // ===== Deaths =====

    public int getDeaths() { return this.deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void addDeaths(int count) { this.deaths += count; }

    // ===== War stats =====

    public int getWarWins() { return this.warWins; }
    public void setWarWins(int warWins) { this.warWins = warWins; }
    public void addWarWin() { this.warWins++; }

    public int getWarLosses() { return this.warLosses; }
    public void setWarLosses(int warLosses) { this.warLosses = warLosses; }
    public void addWarLoss() { this.warLosses++; }

    public int getWarDraws() { return this.warDraws; }
    public void setWarDraws(int warDraws) { this.warDraws = warDraws; }
    public void addWarDraw() { this.warDraws++; }

    // ===== Homes =====

    public boolean addHome(String homeName, org.bukkit.Location location) {
        this.homes.put(homeName.toLowerCase(), SimpleLocation.fromBukkitLocation(location));
        return true;
    }

    public boolean removeHome(String homeName) {
        return this.homes.remove(homeName.toLowerCase()) != null;
    }

    public SimpleLocation getHome(String homeName) {
        return this.homes.get(homeName.toLowerCase());
    }

    public Map<String, SimpleLocation> getHomes() {
        return Collections.unmodifiableMap(this.homes);
    }

    public int getHomeCount() { return this.homes.size(); }

    public int getMaxHomes() {
        int bonus = this.upgrades.getOrDefault("casas-extras", 0);
        return MAX_HOMES_BASE + Math.min(bonus, MAX_UPGRADE_HOMES_BONUS);
    }

    public boolean canAddHome() {
        return this.homes.size() < this.getMaxHomes();
    }

    // ===== Mail =====

    public void addMail(ClanMail mail) { this.mails.add(mail); }

    public void removeMail(int index) {
        if (index >= 0 && index < this.mails.size()) {
            this.mails.remove(index);
        }
    }

    public void clearMails() { this.mails.clear(); }

    public List<ClanMail> getMails() { return Collections.unmodifiableList(this.mails); }

    public int getMailCount() { return this.mails.size(); }

    // ===== Upgrades =====

    public void setUpgrade(String upgradeName, int level) {
        this.upgrades.put(upgradeName, level);
    }

    public int getUpgradeLevel(String upgradeName) {
        return this.upgrades.getOrDefault(upgradeName, 0);
    }

    public Map<String, Integer> getUpgrades() {
        return Collections.unmodifiableMap(this.upgrades);
    }

    // ===== Diplomacy =====

    public void addDiplomacy(DiplomacyRelation relation) {
        this.diplomacy.removeIf(d -> d.otherClanUUID().equals(relation.otherClanUUID()));
        this.diplomacy.add(relation);
    }

    public void removeDiplomacy(UUID otherClanUUID) {
        this.diplomacy.removeIf(d -> d.otherClanUUID().equals(otherClanUUID));
    }

    public DiplomacyRelation getDiplomacyRelation(UUID otherClanUUID) {
        return this.diplomacy.stream()
                .filter(d -> d.otherClanUUID().equals(otherClanUUID))
                .findFirst()
                .orElse(null);
    }

    public List<DiplomacyRelation> getDiplomacy() {
        return Collections.unmodifiableList(this.diplomacy);
    }

    public boolean isAlly(UUID otherClanUUID) {
        DiplomacyRelation rel = getDiplomacyRelation(otherClanUUID);
        return rel != null && rel.type() == DiplomacyType.ALLY;
    }

    public boolean isRival(UUID otherClanUUID) {
        DiplomacyRelation rel = getDiplomacyRelation(otherClanUUID);
        return rel != null && rel.type() == DiplomacyType.RIVAL;
    }

    // ===== Achievements =====

    public void addAchievement(String achievementId) {
        if (!this.achievements.contains(achievementId)) {
            this.achievements.add(achievementId);
        }
    }

    public boolean hasAchievement(String achievementId) {
        return this.achievements.contains(achievementId);
    }

    public List<String> getAchievements() {
        return Collections.unmodifiableList(this.achievements);
    }

    // ===== Quests =====

    public void addQuest(ClanQuest quest) {
        this.activeQuests.put(quest.id(), quest);
    }

    public void removeQuest(String questId) {
        this.activeQuests.remove(questId);
    }

    public ClanQuest getQuest(String questId) {
        return this.activeQuests.get(questId);
    }

    public Map<String, ClanQuest> getActiveQuests() {
        return Collections.unmodifiableMap(this.activeQuests);
    }

    // ===== Logs =====

    public void addLog(ClanLog log) { this.logs.add(log); }

    public List<ClanLog> getLogs() { return Collections.unmodifiableList(this.logs); }

    public void clearLogs() { this.logs.clear(); }

    // ===== Role names =====

    public void setRoleName(ClanRole role, String customName) {
        this.roleNames.put(role, customName);
    }

    public String getRoleName(ClanRole role) {
        return this.roleNames.getOrDefault(role, role.getDisplayName());
    }

    public Map<ClanRole, String> getRoleNames() {
        return Collections.unmodifiableMap(this.roleNames);
    }

    // ===== Kill cooldowns (anti-abuse) =====

    public Long getKillCooldown(UUID victimUUID) {
        return this.killCooldowns.get(victimUUID);
    }

    public void setKillCooldown(UUID victimUUID, long timestamp) {
        this.killCooldowns.put(victimUUID, timestamp);
    }

    public Map<UUID, Long> getKillCooldowns() {
        return Collections.unmodifiableMap(this.killCooldowns);
    }

    // ===== Stats =====

    public void addKill() { this.totalKills++; }

    public double getKDR() {
        if (this.deaths == 0) return this.totalKills;
        return Math.round((this.totalKills * 100.0) / this.deaths) / 100.0;
    }

    public int getMaxMembers() {
        int bonus = this.upgrades.getOrDefault("limite-membros", 0);
        return BASE_MAX_MEMBERS + (bonus * 5);
    }

    public boolean canAddMember() {
        return this.members.size() < this.getMaxMembers();
    }
}
