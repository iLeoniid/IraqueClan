package gg.leo.IraqueClan.war;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.scheduler.BukkitRunnable;

public class WarManager {
    private final IraqueClan plugin;
    private final Map<String, War> activeWars; // key: "clanId1:clanId2"
    private final Map<UUID, War> pendingWars; // challengerClanId -> War

    public WarManager(IraqueClan plugin) {
        this.plugin = plugin;
        this.activeWars = new HashMap<>();
        this.pendingWars = new HashMap<>();
    }

    public void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkExpiredWars();
            }
        }.runTaskTimerAsynchronously(this.plugin, 1200L, 1200L); // Check every 60 seconds
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
        if (winner != null && loser != null) {
            this.notifyWarEnd(winner, loser, war);
        }
    }

    public boolean declareWar(UUID challengerLeaderUuid) {
        Clan challenger = this.plugin.getClanManager().getClanByPlayerDirect(challengerLeaderUuid);
        if (challenger == null) return false;
        if (!challenger.getLeader().equals(challengerLeaderUuid)) return false;
        // Check if already in war
        for (War war : this.activeWars.values()) {
            if (war.involvesClan(UUID.fromString(challenger.getName()))) return false;
        }
        return true;
    }

    public boolean createWar(UUID challengerClanId, UUID defenderClanId, UUID challengerLeader) {
        String key = this.getWarKey(challengerClanId, defenderClanId);
        if (this.activeWars.containsKey(key)) return false;
        if (this.pendingWars.containsKey(challengerClanId)) return false;
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
        UUID killerClanId = this.getPlayerClanId(killerUuid);
        UUID victimClanId = this.getPlayerClanId(victimUuid);
        if (killerClanId == null || victimClanId == null) return;
        String key = this.getWarKey(killerClanId, victimClanId);
        War war = this.activeWars.get(key);
        if (war == null) return;
        if (war.isClanAttacker(killerClanId)) {
            war.addChallengerKill();
        } else {
            war.addDefenderKill();
        }
    }

    private UUID getPlayerClanId(UUID playerUuid) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(playerUuid);
        if (clan == null) return null;
        // Use clan name as a pseudo-ID since we don't have UUID-based clan IDs
        return UUID.nameUUIDFromBytes(clan.getName().getBytes());
    }

    public void endWar(UUID clanId) {
        War war = this.getActiveWar(clanId);
        if (war == null) return;
        war.setStatus(War.WarStatus.FINALIZADA);
        String key = this.getWarKey(war.getChallengerClanId(), war.getDefenderClanId());
        this.activeWars.remove(key);
    }

    private void notifyWarEnd(Clan winner, Clan loser, War war) {
        String winnerMsg = this.plugin.getConfigManager().getPrefixedMessage("war.ended")
                .replace("{winner}", winner.getName())
                .replace("{kills1}", String.valueOf(war.getChallengerKills()))
                .replace("{kills2}", String.valueOf(war.getDefenderKills()));
        String loserMsg = winnerMsg;
        // Send to online members of both clans
        for (UUID uuid : winner.getMembers().keySet()) {
            org.bukkit.entity.Player player = this.plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.sendMessage(this.plugin.getConfigManager().deserialize(this.plugin.getConfigManager().translate(winnerMsg)));
            }
        }
        for (UUID uuid : loser.getMembers().keySet()) {
            org.bukkit.entity.Player player = this.plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.sendMessage(this.plugin.getConfigManager().deserialize(this.plugin.getConfigManager().translate(loserMsg)));
            }
        }
        // Send to Discord
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
        // Wars are not persisted - they reset on server restart
    }
}
