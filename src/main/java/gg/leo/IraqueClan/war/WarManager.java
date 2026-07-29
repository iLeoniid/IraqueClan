package gg.leo.IraqueClan.war;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.scheduler.BukkitRunnable;

public class WarManager {
    private final IraqueClan plugin;
    private final Map<String, War> activeWars;
    private final Map<UUID, War> pendingWars;

    public WarManager(IraqueClan plugin) {
        this.plugin = plugin;
        this.activeWars = new ConcurrentHashMap<>();
        this.pendingWars = new ConcurrentHashMap<>();
    }

    public void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkExpiredWars();
            }
        }.runTaskTimer(this.plugin, 1200L, 1200L);
    }

    private void checkExpiredWars() {
        long maxDuration = this.plugin.getConfigManager().getWarMaxDurationMinutes() * 60000L;
        if (maxDuration <= 0) return;
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, War> entry : this.activeWars.entrySet()) {
            War war = entry.getValue();
            if (war.hasTimedOut(maxDuration)) {
                toRemove.add(entry.getKey());
                this.endWarByTimeout(war);
            }
        }
        for (String key : toRemove) {
            this.activeWars.remove(key);
        }
    }

    private void endWarByTimeout(War war) {
        war.setStatus(War.WarStatus.FINALIZADA);
        UUID winnerId;
        UUID loserId;
        if (war.getChallengerKills() >= war.getDefenderKills()) {
            winnerId = war.getChallengerClanId();
            loserId = war.getDefenderClanId();
        } else {
            winnerId = war.getDefenderClanId();
            loserId = war.getChallengerClanId();
        }
        Clan winner = this.plugin.getClanManager().getClanByUUID(winnerId);
        Clan loser = this.plugin.getClanManager().getClanByUUID(loserId);
        if (winner != null) {
            this.plugin.getClanManager().addWarWin(winner.getName());
        }
        if (loser != null) {
            this.plugin.getClanManager().addWarLoss(loser.getName());
        }
        if (winner != null && loser != null) {
            this.notifyWarEnd(winner, loser, war);
        }
    }

    public boolean declareWar(UUID challengerLeaderUuid) {
        Clan challenger = this.plugin.getClanManager().getClanByPlayerDirect(challengerLeaderUuid);
        if (challenger == null) return false;
        if (!challenger.getLeader().equals(challengerLeaderUuid)) return false;
        UUID challengerId = UUID.nameUUIDFromBytes(challenger.getName().getBytes());
        for (War war : this.activeWars.values()) {
            if (war.involvesClan(challengerId)) return false;
        }
        return true;
    }

    public boolean createWar(UUID challengerClanId, UUID defenderClanId, UUID challengerLeader) {
        String key = this.getWarKey(challengerClanId, defenderClanId);
        if (this.activeWars.containsKey(key)) return false;
        if (this.pendingWars.containsKey(defenderClanId)) return false;
        War war = new War(challengerClanId, defenderClanId, challengerLeader);
        this.pendingWars.put(defenderClanId, war);
        return true;
    }

    public War getPendingWar(UUID defenderClanId) {
        return this.pendingWars.get(defenderClanId);
    }

    public boolean acceptWar(UUID defenderClanId) {
        War war = this.pendingWars.remove(defenderClanId);
        if (war == null) return false;
        war.setStatus(War.WarStatus.ATIVA);
        String key = this.getWarKey(war.getChallengerClanId(), war.getDefenderClanId());
        this.activeWars.put(key, war);
        return true;
    }

    public boolean declineWar(UUID defenderClanId) {
        War war = this.pendingWars.remove(defenderClanId);
        return war != null;
    }

    public War getActiveWar(UUID clanId) {
        for (War war : this.activeWars.values()) {
            if (war.involvesClan(clanId)) return war;
        }
        return null;
    }

    public boolean isClanInWar(UUID clanId) {
        return this.getActiveWar(clanId) != null;
    }

    public void recordKill(UUID killerUuid, UUID victimUuid) {
        Clan killerClan = this.plugin.getClanManager().getClanByPlayerDirect(killerUuid);
        Clan victimClan = this.plugin.getClanManager().getClanByPlayerDirect(victimUuid);
        if (killerClan == null || victimClan == null) return;
        if (killerClan.getName().equals(victimClan.getName())) return;
        UUID killerClanId = UUID.nameUUIDFromBytes(killerClan.getName().getBytes());
        UUID victimClanId = UUID.nameUUIDFromBytes(victimClan.getName().getBytes());
        String key = this.getWarKey(killerClanId, victimClanId);
        War war = this.activeWars.get(key);
        if (war == null) return;
        if (war.isClanAttacker(killerClanId)) {
            war.addChallengerKill();
        } else {
            war.addDefenderKill();
        }
    }

    public void endWar(UUID clanId) {
        War war = this.getActiveWar(clanId);
        if (war == null) return;
        war.setStatus(War.WarStatus.FINALIZADA);
        UUID winnerId;
        UUID loserId;
        if (war.getChallengerKills() > war.getDefenderKills()) {
            winnerId = war.getChallengerClanId();
            loserId = war.getDefenderClanId();
        } else if (war.getDefenderKills() > war.getChallengerKills()) {
            winnerId = war.getDefenderClanId();
            loserId = war.getChallengerClanId();
        } else {
            Clan c1 = this.plugin.getClanManager().getClanByUUID(war.getChallengerClanId());
            Clan c2 = this.plugin.getClanManager().getClanByUUID(war.getDefenderClanId());
            if (c1 != null) this.plugin.getClanManager().addWarDraw(c1.getName());
            if (c2 != null) this.plugin.getClanManager().addWarDraw(c2.getName());
            String key = this.getWarKey(war.getChallengerClanId(), war.getDefenderClanId());
            this.activeWars.remove(key);
            return;
        }
        Clan winner = this.plugin.getClanManager().getClanByUUID(winnerId);
        Clan loser = this.plugin.getClanManager().getClanByUUID(loserId);
        if (winner != null) this.plugin.getClanManager().addWarWin(winner.getName());
        if (loser != null) this.plugin.getClanManager().addWarLoss(loser.getName());
        String key = this.getWarKey(war.getChallengerClanId(), war.getDefenderClanId());
        this.activeWars.remove(key);
    }

    public void cancelWarsForClan(UUID clanId) {
        this.activeWars.values().removeIf(war -> {
            if (war.involvesClan(clanId)) {
                war.setStatus(War.WarStatus.FINALIZADA);
                return true;
            }
            return false;
        });
        this.pendingWars.values().removeIf(war -> {
            if (war.involvesClan(clanId)) {
                war.setStatus(War.WarStatus.FINALIZADA);
                return true;
            }
            return false;
        });
    }

    private void notifyWarEnd(Clan winner, Clan loser, War war) {
        String winnerMsg = this.plugin.getConfigManager().getPrefixedMessage("war.ended")
                .replace("{winner}", winner.getName())
                .replace("{kills1}", String.valueOf(war.getChallengerKills()))
                .replace("{kills2}", String.valueOf(war.getDefenderKills()));
        for (UUID uuid : winner.getMembers().keySet()) {
            org.bukkit.entity.Player player = this.plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.sendMessage(this.plugin.getConfigManager().deserialize(this.plugin.getConfigManager().translate(winnerMsg)));
            }
        }
        for (UUID uuid : loser.getMembers().keySet()) {
            org.bukkit.entity.Player player = this.plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.sendMessage(this.plugin.getConfigManager().deserialize(this.plugin.getConfigManager().translate(winnerMsg)));
            }
        }
        this.plugin.sendDiscordMessage(this.plugin.getConfigManager().getMessage("war.ended-discord")
                .replace("{winner}", winner.getName())
                .replace("{kills1}", String.valueOf(war.getChallengerKills()))
                .replace("{kills2}", String.valueOf(war.getDefenderKills())));
    }

    private String getWarKey(UUID id1, UUID id2) {
        return id1.toString().compareTo(id2.toString()) < 0
            ? id1.toString() + ":" + id2.toString()
            : id2.toString() + ":" + id1.toString();
    }

    public void shutdown() {
        for (War war : this.activeWars.values()) {
            war.setStatus(War.WarStatus.FINALIZADA);
        }
        this.activeWars.clear();
        this.pendingWars.clear();
    }
}
