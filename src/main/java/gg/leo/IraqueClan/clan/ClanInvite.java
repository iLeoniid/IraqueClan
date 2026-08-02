package gg.leo.IraqueClan.clan;

import java.util.UUID;

public record ClanInvite(String clanName, String inviterName, UUID inviterUuid, long timestamp) {

    public boolean isExpired(long timeoutMillis) {
        return System.currentTimeMillis() - this.timestamp > timeoutMillis;
    }

    public long getRemainingMillis(long timeoutMillis) {
        return Math.max(0, timeoutMillis - (System.currentTimeMillis() - this.timestamp));
    }
}
