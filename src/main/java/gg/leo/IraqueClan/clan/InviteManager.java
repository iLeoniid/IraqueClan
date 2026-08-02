package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class InviteManager {
    private final IraqueClan plugin;
    private final Map<UUID, List<ClanInvite>> invites = new ConcurrentHashMap<>();

    public InviteManager(IraqueClan plugin) {
        this.plugin = plugin;
    }

    public long getTimeoutMillis() {
        return this.plugin.getConfigManager().getInviteTimeoutMillis();
    }

    public int getMaxPending() {
        return this.plugin.getConfigManager().getMaxPendingInvites();
    }

    public void addInvite(UUID targetUuid, ClanInvite invite) {
        List<ClanInvite> list = this.invites.computeIfAbsent(targetUuid, k -> new CopyOnWriteArrayList<>());
        list.removeIf(i -> i.clanName().equalsIgnoreCase(invite.clanName()));
        int max = this.getMaxPending();
        while (list.size() >= max) {
            list.remove(0);
        }
        list.add(invite);
    }

    public List<ClanInvite> getInvites(UUID targetUuid) {
        List<ClanInvite> list = this.invites.getOrDefault(targetUuid, List.of());
        long timeout = this.getTimeoutMillis();
        return list.stream().filter(i -> !i.isExpired(timeout)).collect(Collectors.toList());
    }

    public int getInviteCount(UUID targetUuid) {
        return this.getInvites(targetUuid).size();
    }

    public ClanInvite getInvite(UUID targetUuid, String clanName) {
        return this.getInvites(targetUuid).stream()
                .filter(i -> i.clanName().equalsIgnoreCase(clanName))
                .findFirst()
                .orElse(null);
    }

    public boolean hasInvite(UUID targetUuid, String clanName) {
        return this.getInvite(targetUuid, clanName) != null;
    }

    public boolean removeInvite(UUID targetUuid, String clanName) {
        List<ClanInvite> list = this.invites.get(targetUuid);
        if (list == null) return false;
        boolean removed = list.removeIf(i -> i.clanName().equalsIgnoreCase(clanName));
        if (list.isEmpty()) {
            this.invites.remove(targetUuid);
        }
        return removed;
    }

    public void clearInvites(UUID targetUuid) {
        this.invites.remove(targetUuid);
    }

    public void removeExpired() {
        long timeout = this.getTimeoutMillis();
        this.invites.entrySet().removeIf(entry -> {
            entry.getValue().removeIf(i -> i.isExpired(timeout));
            return entry.getValue().isEmpty();
        });
    }
}
