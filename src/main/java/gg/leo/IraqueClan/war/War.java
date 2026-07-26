package gg.leo.IraqueClan.war;

import java.util.UUID;

public class War {
    public enum WarStatus {
        PENDENTE,
        ATIVA,
        FINALIZADA
    }

    private final UUID challengerClanId;
    private final UUID defenderClanId;
    private final UUID challengerLeader;
    private long startTime;
    private int challengerKills;
    private int defenderKills;
    private WarStatus status;

    public War(UUID challengerClanId, UUID defenderClanId, UUID challengerLeader) {
        this.challengerClanId = challengerClanId;
        this.defenderClanId = defenderClanId;
        this.challengerLeader = challengerLeader;
        this.startTime = System.currentTimeMillis();
        this.challengerKills = 0;
        this.defenderKills = 0;
        this.status = WarStatus.PENDENTE;
    }

    public War(UUID challengerClanId, UUID defenderClanId, UUID challengerLeader, long startTime, int challengerKills, int defenderKills, WarStatus status) {
        this.challengerClanId = challengerClanId;
        this.defenderClanId = defenderClanId;
        this.challengerLeader = challengerLeader;
        this.startTime = startTime;
        this.challengerKills = challengerKills;
        this.defenderKills = defenderKills;
        this.status = status;
    }

    public UUID getChallengerClanId() {
        return this.challengerClanId;
    }

    public UUID getDefenderClanId() {
        return this.defenderClanId;
    }

    public UUID getChallengerLeader() {
        return this.challengerLeader;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public int getChallengerKills() {
        return this.challengerKills;
    }

    public void addChallengerKill() {
        this.challengerKills++;
    }

    public int getDefenderKills() {
        return this.defenderKills;
    }

    public void addDefenderKill() {
        this.defenderKills++;
    }

    public WarStatus getStatus() {
        return this.status;
    }

    public void setStatus(WarStatus status) {
        this.status = status;
    }

    public boolean involvesClan(UUID clanId) {
        return this.challengerClanId.equals(clanId) || this.defenderClanId.equals(clanId);
    }

    public boolean isClanAttacker(UUID clanId) {
        return this.challengerClanId.equals(clanId);
    }

    public UUID getOpponentClanId(UUID clanId) {
        if (this.challengerClanId.equals(clanId)) {
            return this.defenderClanId;
        }
        return this.challengerClanId;
    }

    public int getTotalKills() {
        return this.challengerKills + this.defenderKills;
    }

    public boolean hasTimedOut(long maxDurationMillis) {
        if (maxDurationMillis <= 0) {
            return false;
        }
        return System.currentTimeMillis() - this.startTime > maxDurationMillis;
    }
}
