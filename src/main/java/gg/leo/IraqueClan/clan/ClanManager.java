package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.role.ClanRole;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ClanManager {
    private final IraqueClan plugin;
    private final Map<String, Clan> clansByName;
    private final Map<UUID, String> playerClans;
    private final Map<UUID, UUID> pendingInvites;
    private File dataFile;

    public ClanManager(IraqueClan plugin) {
        this.plugin = plugin;
        this.clansByName = new HashMap<>();
        this.playerClans = new HashMap<>();
        this.pendingInvites = new HashMap<>();
    }

    // ===== Data persistence =====

    public void load() {
        this.dataFile = new File(this.plugin.getDataFolder(), "clans.yml");
        this.clansByName.clear();
        this.playerClans.clear();
        if (!this.dataFile.exists()) {
            return;
        }
        YamlConfiguration data = YamlConfiguration.loadConfiguration(this.dataFile);
        ConfigurationSection clansSection = data.getConfigurationSection("clans");
        if (clansSection == null) return;
        for (String clanName : clansSection.getKeys(false)) {
            ConfigurationSection cs = clansSection.getConfigurationSection(clanName);
            if (cs == null) continue;

            String tag = cs.getString("tag", "");
            String tagColor = cs.getString("tag-color", "");
            UUID leader = UUID.fromString(cs.getString("leader", "00000000-0000-0000-0000-000000000000"));
            long createdAt = cs.getLong("created-at", System.currentTimeMillis());
            int totalKills = cs.getInt("total-kills", 0);
            long lastActiveTime = cs.getLong("last-active", System.currentTimeMillis());

            Map<UUID, ClanRole> members = new HashMap<>();
            ConfigurationSection membersSection = cs.getConfigurationSection("members");
            if (membersSection != null) {
                for (String uuidStr : membersSection.getKeys(false)) {
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        ClanRole role = ClanRole.fromConfigKey(membersSection.getString(uuidStr, "MEMBRO"));
                        members.put(uuid, role);
                    } catch (IllegalArgumentException ignored) {}
                }
            }

            Clan clan = new Clan(clanName, tag, tagColor, leader, members, createdAt, totalKills, lastActiveTime);

            // Bank
            clan.setBank(cs.getDouble("bank", 0.0));

            // Profile
            clan.setDescription(cs.getString("description", ""));
            clan.setIcon(cs.getString("icon", "PAPER"));

            // MOTD
            clan.setMotd(cs.getString("motd", ""));

            // Level / XP
            clan.setLevel(cs.getInt("level", 1));
            clan.setXp(cs.getLong("xp", 0));

            // Deaths
            clan.setDeaths(cs.getInt("deaths", 0));

            // War stats
            clan.setWarWins(cs.getInt("war-wins", 0));
            clan.setWarLosses(cs.getInt("war-losses", 0));
            clan.setWarDraws(cs.getInt("war-draws", 0));

            // Homes
            ConfigurationSection homesSection = cs.getConfigurationSection("homes");
            if (homesSection != null) {
                for (String homeName : homesSection.getKeys(false)) {
                    ConfigurationSection hs = homesSection.getConfigurationSection(homeName);
                    if (hs == null) continue;
                    Clan.SimpleLocation sl = new Clan.SimpleLocation(
                            hs.getString("world", "world"),
                            hs.getDouble("x", 0),
                            hs.getDouble("y", 64),
                            hs.getDouble("z", 0),
                            (float) hs.getDouble("yaw", 0),
                            (float) hs.getDouble("pitch", 0)
                    );
                    addHomeToClan(clan, homeName, sl);
                }
            }

            // Mails
            List<Map<?, ?>> mailList = cs.getMapList("mails");
            if (mailList != null) {
                for (Map<?, ?> mailMap : mailList) {
                    try {
                        UUID sender = UUID.fromString(String.valueOf(mailMap.get("sender")));
                        String message = String.valueOf(mailMap.get("message"));
                        long timestamp = mailMap.containsKey("timestamp") ?
                                Long.parseLong(String.valueOf(mailMap.get("timestamp"))) : System.currentTimeMillis();
                        clan.addMail(new Clan.ClanMail(sender, message, timestamp));
                    } catch (Exception ignored) {}
                }
            }

            // Upgrades
            ConfigurationSection upgradesSection = cs.getConfigurationSection("upgrades");
            if (upgradesSection != null) {
                for (String upgradeName : upgradesSection.getKeys(false)) {
                    clan.setUpgrade(upgradeName, upgradesSection.getInt(upgradeName, 0));
                }
            }

            // Diplomacy
            ConfigurationSection diplomacySection = cs.getConfigurationSection("diplomacy");
            if (diplomacySection != null) {
                for (String otherUuidStr : diplomacySection.getKeys(false)) {
                    ConfigurationSection ds = diplomacySection.getConfigurationSection(otherUuidStr);
                    if (ds == null) continue;
                    try {
                        UUID otherUUID = UUID.fromString(otherUuidStr);
                        Clan.DiplomacyType type = Clan.DiplomacyType.valueOf(
                                ds.getString("type", "NEUTRAL"));
                        long timestamp = ds.getLong("timestamp", System.currentTimeMillis());
                        clan.addDiplomacy(new Clan.DiplomacyRelation(otherUUID, type, timestamp));
                    } catch (Exception ignored) {}
                }
            }

            // Achievements
            List<String> achievementList = cs.getStringList("achievements");
            for (String ach : achievementList) {
                clan.addAchievement(ach);
            }

            // Quests
            ConfigurationSection questsSection = cs.getConfigurationSection("quests");
            if (questsSection != null) {
                for (String questId : questsSection.getKeys(false)) {
                    ConfigurationSection qs = questsSection.getConfigurationSection(questId);
                    if (qs == null) continue;
                    clan.addQuest(new Clan.ClanQuest(
                            questId,
                            qs.getString("type", ""),
                            qs.getInt("required", 0),
                            qs.getInt("current", 0),
                            qs.getDouble("reward-xp", 0),
                            qs.getDouble("reward-money", 0)
                    ));
                }
            }

            // Logs
            List<Map<?, ?>> logList = cs.getMapList("logs");
            if (logList != null) {
                for (Map<?, ?> logMap : logList) {
                    try {
                        String action = String.valueOf(logMap.get("action"));
                        UUID player = logMap.containsKey("player") ?
                                UUID.fromString(String.valueOf(logMap.get("player"))) : null;
                        String details = String.valueOf(logMap.get("details"));
                        long timestamp = logMap.containsKey("timestamp") ?
                                Long.parseLong(String.valueOf(logMap.get("timestamp"))) : System.currentTimeMillis();
                        clan.addLog(new Clan.ClanLog(action, player, details, timestamp));
                    } catch (Exception ignored) {}
                }
            }

            // Role names
            ConfigurationSection roleNamesSection = cs.getConfigurationSection("role-names");
            if (roleNamesSection != null) {
                for (String roleKey : roleNamesSection.getKeys(false)) {
                    ClanRole role = ClanRole.fromConfigKey(roleKey);
                    String customName = roleNamesSection.getString(roleKey, role.getDisplayName());
                    clan.setRoleName(role, customName);
                }
            }

            // Kill cooldowns
            ConfigurationSection cooldownsSection = cs.getConfigurationSection("kill-cooldowns");
            if (cooldownsSection != null) {
                for (String victimStr : cooldownsSection.getKeys(false)) {
                    try {
                        UUID victimUUID = UUID.fromString(victimStr);
                        long timestamp = cooldownsSection.getLong(victimStr, 0);
                        clan.setKillCooldown(victimUUID, timestamp);
                    } catch (Exception ignored) {}
                }
            }

            this.clansByName.put(clanName.toLowerCase(), clan);
            for (UUID memberUuid : members.keySet()) {
                this.playerClans.put(memberUuid, clanName.toLowerCase());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addHomeToClan(Clan clan, String homeName, Clan.SimpleLocation loc) {
        try {
            var field = Clan.class.getDeclaredField("homes");
            field.setAccessible(true);
            Map<String, Clan.SimpleLocation> homes = (Map<String, Clan.SimpleLocation>) field.get(clan);
            homes.put(homeName.toLowerCase(), loc);
        } catch (Exception ignored) {}
    }

    public void saveAll() {
        YamlConfiguration data = new YamlConfiguration();
        for (Map.Entry<String, Clan> entry : this.clansByName.entrySet()) {
            Clan clan = entry.getValue();
            String path = "clans." + clan.getName();

            // Basic fields
            data.set(path + ".tag", clan.getTag());
            data.set(path + ".tag-color", clan.getTagColor());
            data.set(path + ".leader", clan.getLeader().toString());
            data.set(path + ".created-at", clan.getCreatedAt());
            data.set(path + ".total-kills", clan.getTotalKills());
            data.set(path + ".last-active", clan.getLastActiveTime());

            // Extended fields
            data.set(path + ".bank", clan.getBank());
            data.set(path + ".description", clan.getDescription());
            data.set(path + ".icon", clan.getIcon());
            data.set(path + ".motd", clan.getMotd());
            data.set(path + ".level", clan.getLevel());
            data.set(path + ".xp", clan.getXp());
            data.set(path + ".deaths", clan.getDeaths());
            data.set(path + ".war-wins", clan.getWarWins());
            data.set(path + ".war-losses", clan.getWarLosses());
            data.set(path + ".war-draws", clan.getWarDraws());

            // Members
            for (Map.Entry<UUID, ClanRole> memberEntry : clan.getMembers().entrySet()) {
                data.set(path + ".members." + memberEntry.getKey().toString(), memberEntry.getValue().getConfigKey());
            }

            // Homes
            for (Map.Entry<String, Clan.SimpleLocation> homeEntry : clan.getHomes().entrySet()) {
                String homePath = path + ".homes." + homeEntry.getKey();
                Clan.SimpleLocation loc = homeEntry.getValue();
                data.set(homePath + ".world", loc.getWorld());
                data.set(homePath + ".x", loc.getX());
                data.set(homePath + ".y", loc.getY());
                data.set(homePath + ".z", loc.getZ());
                data.set(homePath + ".yaw", loc.getYaw());
                data.set(homePath + ".pitch", loc.getPitch());
            }

            // Mails
            List<Map<String, Object>> mailMaps = new ArrayList<>();
            for (Clan.ClanMail mail : clan.getMails()) {
                Map<String, Object> mailMap = new HashMap<>();
                mailMap.put("sender", mail.sender().toString());
                mailMap.put("message", mail.message());
                mailMap.put("timestamp", mail.timestamp());
                mailMaps.add(mailMap);
            }
            data.set(path + ".mails", mailMaps);

            // Upgrades
            for (Map.Entry<String, Integer> upgradeEntry : clan.getUpgrades().entrySet()) {
                data.set(path + ".upgrades." + upgradeEntry.getKey(), upgradeEntry.getValue());
            }

            // Diplomacy
            for (Clan.DiplomacyRelation rel : clan.getDiplomacy()) {
                String relPath = path + ".diplomacy." + rel.otherClanUUID().toString();
                data.set(relPath + ".type", rel.type().name());
                data.set(relPath + ".timestamp", rel.timestamp());
            }

            // Achievements
            data.set(path + ".achievements", new ArrayList<>(clan.getAchievements()));

            // Quests
            for (Map.Entry<String, Clan.ClanQuest> questEntry : clan.getActiveQuests().entrySet()) {
                Clan.ClanQuest q = questEntry.getValue();
                String questPath = path + ".quests." + q.id();
                data.set(questPath + ".type", q.type());
                data.set(questPath + ".required", q.required());
                data.set(questPath + ".current", q.current());
                data.set(questPath + ".reward-xp", q.rewardXP());
                data.set(questPath + ".reward-money", q.rewardMoney());
            }

            // Logs
            List<Map<String, Object>> logMaps = new ArrayList<>();
            for (Clan.ClanLog log : clan.getLogs()) {
                Map<String, Object> logMap = new HashMap<>();
                logMap.put("action", log.action());
                logMap.put("player", log.player() != null ? log.player().toString() : "");
                logMap.put("details", log.details());
                logMap.put("timestamp", log.timestamp());
                logMaps.add(logMap);
            }
            data.set(path + ".logs", logMaps);

            // Role names
            for (Map.Entry<ClanRole, String> rnEntry : clan.getRoleNames().entrySet()) {
                data.set(path + ".role-names." + rnEntry.getKey().getConfigKey(), rnEntry.getValue());
            }

            // Kill cooldowns
            for (Map.Entry<UUID, Long> cdEntry : clan.getKillCooldowns().entrySet()) {
                data.set(path + ".kill-cooldowns." + cdEntry.getKey().toString(), cdEntry.getValue());
            }
        }

        try {
            this.dataFile.getParentFile().mkdirs();
            data.save(this.dataFile);
        } catch (IOException e) {
            this.plugin.getLogger().warning("Error saving clans.yml: " + e.getMessage());
        }
    }

    // ===== Core clan operations =====

    public boolean createClan(String name, String tag, String tagColor, UUID leaderUuid) {
        if (this.clansByName.containsKey(name.toLowerCase())) return false;
        if (this.playerClans.containsKey(leaderUuid)) return false;
        Clan clan = new Clan(name, tag, tagColor, leaderUuid);
        this.clansByName.put(name.toLowerCase(), clan);
        this.playerClans.put(leaderUuid, name.toLowerCase());
        this.saveAll();
        return true;
    }

    public boolean disbandClan(String clanName) {
        Clan clan = this.clansByName.remove(clanName.toLowerCase());
        if (clan == null) return false;
        for (UUID memberUuid : clan.getMembers().keySet()) {
            this.playerClans.remove(memberUuid);
        }
        this.saveAll();
        return true;
    }

    public boolean joinClan(UUID playerUuid, String clanName) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan == null) return false;
        if (this.playerClans.containsKey(playerUuid)) return false;
        if (!clan.canAddMember()) return false;
        clan.addMember(playerUuid, ClanRole.MEMBRO);
        this.playerClans.put(playerUuid, clanName.toLowerCase());
        this.saveAll();
        return true;
    }

    public boolean leaveClan(UUID playerUuid) {
        String clanName = this.playerClans.get(playerUuid);
        if (clanName == null) return false;
        Clan clan = this.clansByName.get(clanName);
        if (clan == null) return false;
        if (clan.getLeader().equals(playerUuid)) return false;
        clan.removeMember(playerUuid);
        this.playerClans.remove(playerUuid);
        this.saveAll();
        return true;
    }

    public boolean kickMember(UUID requesterUuid, UUID targetUuid) {
        String clanName = this.playerClans.get(requesterUuid);
        if (clanName == null) return false;
        Clan clan = this.clansByName.get(clanName);
        if (clan == null) return false;
        if (!clan.canKick(requesterUuid)) return false;
        if (clan.getLeader().equals(targetUuid)) return false;
        if (!clan.isMember(targetUuid)) return false;
        clan.removeMember(targetUuid);
        this.playerClans.remove(targetUuid);
        this.saveAll();
        return true;
    }

    public boolean promoteMember(UUID requesterUuid, UUID targetUuid) {
        String clanName = this.playerClans.get(requesterUuid);
        if (clanName == null) return false;
        Clan clan = this.clansByName.get(clanName);
        if (clan == null) return false;
        if (!clan.canPromote(requesterUuid)) return false;
        ClanRole requesterRole = clan.getMemberRole(requesterUuid);
        ClanRole targetRole = clan.getMemberRole(targetUuid);
        if (targetRole == null) return false;
        if (requesterRole != null && !requesterRole.isHigherThan(targetRole)) return false;
        if (targetRole == ClanRole.LIDER) return false;
        ClanRole newRole = targetRole == ClanRole.MEMBRO ? ClanRole.SUB_LIDER : ClanRole.LIDER;
        if (newRole == ClanRole.LIDER && !requesterRole.isHigherThan(ClanRole.SUB_LIDER)) return false;
        clan.setMemberRole(targetUuid, newRole);
        this.saveAll();
        return true;
    }

    public boolean demoteMember(UUID requesterUuid, UUID targetUuid) {
        String clanName = this.playerClans.get(requesterUuid);
        if (clanName == null) return false;
        Clan clan = this.clansByName.get(clanName);
        if (clan == null) return false;
        if (!clan.canPromote(requesterUuid)) return false;
        ClanRole requesterRole = clan.getMemberRole(requesterUuid);
        ClanRole targetRole = clan.getMemberRole(targetUuid);
        if (targetRole == null) return false;
        if (requesterRole != null && !requesterRole.isHigherThan(targetRole)) return false;
        if (targetRole == ClanRole.LIDER) return false;
        ClanRole newRole = targetRole == ClanRole.SUB_LIDER ? ClanRole.MEMBRO : null;
        if (newRole == null) return false;
        clan.setMemberRole(targetUuid, newRole);
        this.saveAll();
        return true;
    }

    // ===== Invites =====

    public void setPendingInvite(UUID targetUuid, UUID clanLeaderUuid) {
        this.pendingInvites.put(targetUuid, clanLeaderUuid);
    }

    public UUID getPendingInvite(UUID targetUuid) {
        return this.pendingInvites.get(targetUuid);
    }

    public void removePendingInvite(UUID targetUuid) {
        this.pendingInvites.remove(targetUuid);
    }

    // ===== Lookups =====

    public Optional<Clan> getClan(String name) {
        return Optional.ofNullable(this.clansByName.get(name.toLowerCase()));
    }

    public Optional<Clan> getClanByPlayer(UUID playerUuid) {
        String clanName = this.playerClans.get(playerUuid);
        if (clanName == null) return Optional.empty();
        return Optional.ofNullable(this.clansByName.get(clanName));
    }

    public Clan getClanByPlayerDirect(UUID playerUuid) {
        String clanName = this.playerClans.get(playerUuid);
        if (clanName == null) return null;
        return this.clansByName.get(clanName);
    }

    public boolean isPlayerInClan(UUID playerUuid) {
        return this.playerClans.containsKey(playerUuid);
    }

    public boolean clanNameExists(String name) {
        return this.clansByName.containsKey(name.toLowerCase());
    }

    public boolean tagExists(String tag) {
        return this.clansByName.values().stream().anyMatch(c -> c.getTag().equalsIgnoreCase(tag));
    }

    public Clan getClanByUUID(UUID clanUuid) {
        for (Clan clan : this.clansByName.values()) {
            UUID computed = UUID.nameUUIDFromBytes(clan.getName().getBytes());
            if (computed.equals(clanUuid)) {
                return clan;
            }
        }
        return null;
    }

    public Collection<Clan> getAllClans() {
        return Collections.unmodifiableCollection(this.clansByName.values());
    }

    // ===== Sorted queries =====

    public List<Clan> getClansSortedByKills() {
        return this.clansByName.values().stream()
                .sorted(Comparator.comparingInt(Clan::getTotalKills).reversed())
                .collect(Collectors.toList());
    }

    public List<Clan> getClansSortedByMembers() {
        return this.clansByName.values().stream()
                .sorted(Comparator.comparingInt(Clan::getMemberCount).reversed())
                .collect(Collectors.toList());
    }

    public List<Clan> getClansSortedByTime() {
        return this.clansByName.values().stream()
                .sorted(Comparator.comparingLong(Clan::getCreatedAt))
                .collect(Collectors.toList());
    }

    public List<Clan> getClansSortedByLevel() {
        return this.clansByName.values().stream()
                .sorted(Comparator.comparingInt(Clan::getLevel).reversed())
                .collect(Collectors.toList());
    }

    public List<Clan> getClansSortedByBank() {
        return this.clansByName.values().stream()
                .sorted(Comparator.comparingDouble(Clan::getBank).reversed())
                .collect(Collectors.toList());
    }

    // ===== Stats shortcuts =====

    public void addClanKills(String clanName, int kills) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan != null) {
            clan.addKills(kills);
        }
    }

    public void addKill(UUID killerUuid, UUID victimUuid) {
        Clan killerClan = getClanByPlayerDirect(killerUuid);
        Clan victimClan = getClanByPlayerDirect(victimUuid);
        if (killerClan != null) {
            killerClan.addKill();
        }
        if (victimClan != null) {
            victimClan.addDeaths(1);
        }
    }

    public void addDeath(UUID playerUuid) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan != null) {
            clan.addDeaths(1);
        }
    }

    public double getClanKDR(String clanName) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan == null) return 0.0;
        return clan.getKDR();
    }

    // ===== Economy =====

    public boolean depositClan(UUID playerUuid, double amount) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        if (amount <= 0) return false;
        clan.addBank(amount);
        this.saveAll();
        return true;
    }

    public boolean withdrawClan(UUID playerUuid, double amount) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        if (amount <= 0) return false;
        if (!clan.removeBank(amount)) return false;
        this.saveAll();
        return true;
    }

    public double getClanBalance(UUID playerUuid) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return 0.0;
        return clan.getBank();
    }

    public boolean depositToClan(String clanName, double amount) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan == null || amount <= 0) return false;
        clan.addBank(amount);
        this.saveAll();
        return true;
    }

    public boolean withdrawFromClan(String clanName, double amount) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan == null || amount <= 0) return false;
        if (!clan.removeBank(amount)) return false;
        this.saveAll();
        return true;
    }

    public double getClanBalanceByName(String clanName) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan == null) return 0.0;
        return clan.getBank();
    }

    // ===== Homes =====

    public boolean addHome(UUID playerUuid, String homeName, org.bukkit.Location location) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        if (!clan.canAddHome()) return false;
        clan.addHome(homeName, location);
        this.saveAll();
        return true;
    }

    public boolean removeHome(UUID playerUuid, String homeName) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        boolean removed = clan.removeHome(homeName);
        if (removed) this.saveAll();
        return removed;
    }

    public Clan.SimpleLocation getHome(UUID playerUuid, String homeName) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return null;
        return clan.getHome(homeName);
    }

    public Map<String, Clan.SimpleLocation> getHomes(UUID playerUuid) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return Map.of();
        return clan.getHomes();
    }

    // ===== Mail =====

    public boolean addMail(UUID playerUuid, String message) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        clan.addMail(new Clan.ClanMail(playerUuid, message, System.currentTimeMillis()));
        this.saveAll();
        return true;
    }

    public boolean clearMails(UUID playerUuid) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        clan.clearMails();
        this.saveAll();
        return true;
    }

    public List<Clan.ClanMail> getMails(UUID playerUuid) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return List.of();
        return clan.getMails();
    }

    // ===== MOTD =====

    public boolean setMOTD(UUID playerUuid, String motd) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        if (!clan.getLeader().equals(playerUuid)) return false;
        clan.setMotd(motd);
        this.saveAll();
        return true;
    }

    public boolean clearMOTD(UUID playerUuid) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        if (!clan.getLeader().equals(playerUuid)) return false;
        clan.clearMotd();
        this.saveAll();
        return true;
    }

    public String getMOTD(UUID playerUuid) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return "";
        return clan.getMotd();
    }

    // ===== Level / XP =====

    public boolean addXP(UUID playerUuid, long amount) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null || amount <= 0) return false;
        clan.addXp(amount);
        checkLevelUp(clan);
        this.saveAll();
        return true;
    }

    public boolean addXPToClan(String clanName, long amount) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan == null || amount <= 0) return false;
        clan.addXp(amount);
        checkLevelUp(clan);
        this.saveAll();
        return true;
    }

    public int getLevel(UUID playerUuid) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return 0;
        return clan.getLevel();
    }

    public boolean setLevel(UUID playerUuid, int level) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        clan.setLevel(level);
        this.saveAll();
        return true;
    }

    public boolean setLevelForClan(String clanName, int level) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan == null) return false;
        clan.setLevel(level);
        this.saveAll();
        return true;
    }

    private void checkLevelUp(Clan clan) {
        int currentLevel = clan.getLevel();
        long currentXP = clan.getXp();
        long requiredXP = calculateRequiredXP(currentLevel + 1);
        while (currentXP >= requiredXP && currentLevel < 100) {
            currentLevel++;
            clan.setLevel(currentLevel);
            requiredXP = calculateRequiredXP(currentLevel + 1);
        }
    }

    public long calculateRequiredXP(int level) {
        return (long) (100 * Math.pow(level, 1.5));
    }

    // ===== Upgrades =====

    public boolean purchaseUpgrade(UUID playerUuid, String upgradeName) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;

        ConfigurationSection upgradeSection = this.plugin.getConfig()
                .getConfigurationSection("loja.upgrades." + upgradeName);
        if (upgradeSection == null) return false;

        int maxLevel = upgradeSection.getInt("nivel-maximo", 5);
        int currentLevel = clan.getUpgradeLevel(upgradeName);
        if (currentLevel >= maxLevel) return false;

        double basePrice = upgradeSection.getDouble("preco-base", 1000);
        double multiplier = upgradeSection.getDouble("multiplicador-preco", 1.5);
        double price = basePrice * Math.pow(multiplier, currentLevel);

        if (!clan.removeBank(price)) return false;
        clan.setUpgrade(upgradeName, currentLevel + 1);
        this.saveAll();
        return true;
    }

    public int getUpgradeLevel(UUID playerUuid, String upgradeName) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return 0;
        return clan.getUpgradeLevel(upgradeName);
    }

    public int getUpgradeLevelByClan(String clanName, String upgradeName) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan == null) return 0;
        return clan.getUpgradeLevel(upgradeName);
    }

    // ===== Diplomacy =====

    public boolean addRelation(UUID requesterUuid, UUID otherClanUUID, Clan.DiplomacyType type) {
        Clan clan = getClanByPlayerDirect(requesterUuid);
        if (clan == null) return false;
        if (!clan.getLeader().equals(requesterUuid)) return false;
        Clan otherClan = getClanByUUID(otherClanUUID);
        if (otherClan == null) return false;
        clan.addDiplomacy(new Clan.DiplomacyRelation(otherClanUUID, type, System.currentTimeMillis()));
        otherClan.addDiplomacy(new Clan.DiplomacyRelation(
                UUID.nameUUIDFromBytes(clan.getName().getBytes()), type, System.currentTimeMillis()));
        this.saveAll();
        return true;
    }

    public boolean removeRelation(UUID requesterUuid, UUID otherClanUUID) {
        Clan clan = getClanByPlayerDirect(requesterUuid);
        if (clan == null) return false;
        if (!clan.getLeader().equals(requesterUuid)) return false;
        clan.removeDiplomacy(otherClanUUID);
        Clan otherClan = getClanByUUID(otherClanUUID);
        if (otherClan != null) {
            otherClan.removeDiplomacy(UUID.nameUUIDFromBytes(clan.getName().getBytes()));
        }
        this.saveAll();
        return true;
    }

    public Clan.DiplomacyRelation getRelation(UUID playerUuid, UUID otherClanUUID) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return null;
        return clan.getDiplomacyRelation(otherClanUUID);
    }

    // ===== Achievements =====

    public boolean unlockAchievement(UUID playerUuid, String achievementId) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        if (clan.hasAchievement(achievementId)) return false;
        clan.addAchievement(achievementId);
        this.saveAll();
        return true;
    }

    public boolean unlockAchievementForClan(String clanName, String achievementId) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan == null) return false;
        if (clan.hasAchievement(achievementId)) return false;
        clan.addAchievement(achievementId);
        this.saveAll();
        return true;
    }

    public List<String> getAchievements(UUID playerUuid) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return List.of();
        return clan.getAchievements();
    }

    // ===== Quests =====

    public boolean addQuest(UUID playerUuid, Clan.ClanQuest quest) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        clan.addQuest(quest);
        this.saveAll();
        return true;
    }

    public boolean updateQuestProgress(UUID playerUuid, String questId, int increment) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        Clan.ClanQuest existing = clan.getQuest(questId);
        if (existing == null) return false;
        int newCurrent = Math.min(existing.current() + increment, existing.required());
        clan.removeQuest(questId);
        clan.addQuest(existing.withProgress(newCurrent));
        this.saveAll();
        return true;
    }

    public boolean completeQuest(UUID playerUuid, String questId) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        Clan.ClanQuest quest = clan.getQuest(questId);
        if (quest == null || !quest.isComplete()) return false;
        clan.addXp((long) quest.rewardXP());
        clan.addBank(quest.rewardMoney());
        clan.removeQuest(questId);
        checkLevelUp(clan);
        this.saveAll();
        return true;
    }

    public Clan.ClanQuest getQuest(UUID playerUuid, String questId) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return null;
        return clan.getQuest(questId);
    }

    // ===== Logs =====

    public boolean addLog(UUID playerUuid, String action, String details) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        clan.addLog(new Clan.ClanLog(action, playerUuid, details, System.currentTimeMillis()));
        return true;
    }

    public boolean addLogToClan(String clanName, String action, UUID player, String details) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan == null) return false;
        clan.addLog(new Clan.ClanLog(action, player, details, System.currentTimeMillis()));
        return true;
    }

    public List<Clan.ClanLog> getLogs(UUID playerUuid) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return List.of();
        return clan.getLogs();
    }

    public boolean clearLogs(UUID playerUuid) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        clan.clearLogs();
        this.saveAll();
        return true;
    }

    // ===== Profile =====

    public boolean setDescription(UUID playerUuid, String description) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        clan.setDescription(description);
        this.saveAll();
        return true;
    }

    public boolean setIcon(UUID playerUuid, String icon) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        clan.setIcon(icon);
        this.saveAll();
        return true;
    }

    // ===== Role names =====

    public boolean setRoleName(UUID playerUuid, ClanRole role, String customName) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        if (!clan.getLeader().equals(playerUuid)) return false;
        clan.setRoleName(role, customName);
        this.saveAll();
        return true;
    }

    public String getRoleName(UUID playerUuid, ClanRole role) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return role.getDisplayName();
        return clan.getRoleName(role);
    }

    // ===== Tags =====

    public boolean changeTag(UUID playerUuid, String newTag) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        if (!clan.getLeader().equals(playerUuid)) return false;
        if (tagExists(newTag)) return false;
        clan.setTag(newTag);
        this.saveAll();
        return true;
    }

    public boolean changeColor(UUID playerUuid, String newColor) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        if (!clan.getLeader().equals(playerUuid)) return false;
        clan.setTagColor(newColor);
        this.saveAll();
        return true;
    }

    // ===== War stats shortcuts =====

    public void addWarWin(String clanName) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan != null) clan.addWarWin();
    }

    public void addWarLoss(String clanName) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan != null) clan.addWarLoss();
    }

    public void addWarDraw(String clanName) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan != null) clan.addWarDraw();
    }

    // ===== Admin =====

    public boolean forceJoin(UUID playerUuid, String clanName) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan == null) return false;
        if (this.playerClans.containsKey(playerUuid)) return false;
        clan.addMember(playerUuid, ClanRole.MEMBRO);
        this.playerClans.put(playerUuid, clanName.toLowerCase());
        this.saveAll();
        return true;
    }

    public boolean setLeader(UUID playerUuid, UUID newLeaderUuid) {
        Clan clan = getClanByPlayerDirect(playerUuid);
        if (clan == null) return false;
        if (!clan.getLeader().equals(playerUuid)) return false;
        if (!clan.isMember(newLeaderUuid)) return false;
        clan.setMemberRole(playerUuid, ClanRole.SUB_LIDER);
        clan.setMemberRole(newLeaderUuid, ClanRole.LIDER);
        clan.setLeader(newLeaderUuid);
        this.saveAll();
        return true;
    }

    public boolean renameClan(String oldName, String newName) {
        Clan clan = this.clansByName.remove(oldName.toLowerCase());
        if (clan == null) return false;
        if (this.clansByName.containsKey(newName.toLowerCase())) {
            this.clansByName.put(oldName.toLowerCase(), clan);
            return false;
        }
        this.clansByName.put(newName.toLowerCase(), clan);
        for (UUID memberUuid : clan.getMembers().keySet()) {
            this.playerClans.put(memberUuid, newName.toLowerCase());
        }
        this.saveAll();
        return true;
    }

    public void forceAddKill(UUID killerUuid, UUID victimUuid) {
        Clan killerClan = getClanByPlayerDirect(killerUuid);
        Clan victimClan = getClanByPlayerDirect(victimUuid);
        if (killerClan != null) killerClan.addKill();
        if (victimClan != null) victimClan.addDeaths(1);
    }

    public void forceSetLevel(String clanName, int level) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan != null) clan.setLevel(level);
    }

    public void forceAddXP(String clanName, long amount) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan != null) {
            clan.addXp(amount);
            checkLevelUp(clan);
        }
    }

    public void forceSetBank(String clanName, double amount) {
        Clan clan = this.clansByName.get(clanName.toLowerCase());
        if (clan != null) clan.setBank(amount);
    }
}
