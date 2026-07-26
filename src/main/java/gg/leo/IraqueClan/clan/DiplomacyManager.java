package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;

public class DiplomacyManager {

    private final IraqueClan plugin;
    private final Map<UUID, Clan.DiplomacyRelation> pendingRequests = new HashMap<>();

    public DiplomacyManager(IraqueClan plugin) {
        this.plugin = plugin;
    }

    public void sendRequest(Clan from, Clan to, Clan.DiplomacyType type) {
        if (from == null || to == null || type == null) return;
        if (from.getName().equalsIgnoreCase(to.getName())) return;

        UUID fromUUID = UUID.nameUUIDFromBytes(from.getName().getBytes());
        UUID toUUID = UUID.nameUUIDFromBytes(to.getName().getBytes());

        Clan.DiplomacyRelation existing = to.getDiplomacyRelation(fromUUID);
        if (existing != null && existing.type() == type) return;

        long expirationMinutes = this.plugin.getConfig().getLong("diplomacia.tempo-expiracao-minutos", 60);
        long timestamp = System.currentTimeMillis() + (expirationMinutes * 60_000);

        Clan.DiplomacyRelation request = new Clan.DiplomacyRelation(fromUUID, type, timestamp);
        pendingRequests.put(toUUID, request);
    }

    public boolean acceptRequest(Clan accepting, UUID fromClanUUID) {
        if (accepting == null || fromClanUUID == null) return false;

        UUID acceptUUID = UUID.nameUUIDFromBytes(accepting.getName().getBytes());
        Clan.DiplomacyRelation request = pendingRequests.remove(acceptUUID);
        if (request == null) return false;
        if (!request.otherClanUUID().equals(fromClanUUID)) return false;

        if (System.currentTimeMillis() > request.timestamp()) return false;

        Clan fromClan = this.plugin.getClanManager().getClanByUUID(fromClanUUID);
        if (fromClan == null) return false;

        Clan.DiplomacyType type = request.type();

        if (type == Clan.DiplomacyType.ALLY) {
            if (!canAddAlly(accepting) || !canAddAlly(fromClan)) return false;
        } else if (type == Clan.DiplomacyType.RIVAL) {
            if (!canAddRival(accepting) || !canAddRival(fromClan)) return false;
        }

        long now = System.currentTimeMillis();
        accepting.addDiplomacy(new Clan.DiplomacyRelation(fromClanUUID, type, now));
        fromClan.addDiplomacy(new Clan.DiplomacyRelation(acceptUUID, type, now));
        this.plugin.getClanManager().saveAll();
        return true;
    }

    public boolean declineRequest(Clan declining, UUID fromClanUUID) {
        if (declining == null || fromClanUUID == null) return false;
        UUID declineUUID = UUID.nameUUIDFromBytes(declining.getName().getBytes());
        Clan.DiplomacyRelation request = pendingRequests.remove(declineUUID);
        return request != null && request.otherClanUUID().equals(fromClanUUID);
    }

    public boolean removeRelation(Clan clan, UUID otherClanUUID) {
        if (clan == null || otherClanUUID == null) return false;
        clan.removeDiplomacy(otherClanUUID);
        Clan otherClan = this.plugin.getClanManager().getClanByUUID(otherClanUUID);
        if (otherClan != null) {
            UUID clanUUID = UUID.nameUUIDFromBytes(clan.getName().getBytes());
            otherClan.removeDiplomacy(clanUUID);
        }
        this.plugin.getClanManager().saveAll();
        return true;
    }

    public List<Clan> getAllies(Clan clan) {
        if (clan == null) return List.of();
        List<Clan> allies = new ArrayList<>();
        for (Clan.DiplomacyRelation rel : clan.getDiplomacy()) {
            if (rel.type() != Clan.DiplomacyType.ALLY) continue;
            Clan other = this.plugin.getClanManager().getClanByUUID(rel.otherClanUUID());
            if (other != null) allies.add(other);
        }
        return allies;
    }

    public List<Clan> getRivals(Clan clan) {
        if (clan == null) return List.of();
        List<Clan> rivals = new ArrayList<>();
        for (Clan.DiplomacyRelation rel : clan.getDiplomacy()) {
            if (rel.type() != Clan.DiplomacyType.RIVAL) continue;
            Clan other = this.plugin.getClanManager().getClanByUUID(rel.otherClanUUID());
            if (other != null) rivals.add(other);
        }
        return rivals;
    }

    public boolean canAddAlly(Clan clan) {
        if (clan == null) return false;
        int max = this.plugin.getConfig().getInt("diplomacia.max-aliados", 3);
        long current = clan.getDiplomacy().stream()
                .filter(r -> r.type() == Clan.DiplomacyType.ALLY)
                .count();
        return current < max;
    }

    public boolean canAddRival(Clan clan) {
        if (clan == null) return false;
        int max = this.plugin.getConfig().getInt("diplomacia.max-rivais", 3);
        long current = clan.getDiplomacy().stream()
                .filter(r -> r.type() == Clan.DiplomacyType.RIVAL)
                .count();
        return current < max;
    }

    public Map<UUID, Clan.DiplomacyRelation> getPendingRequests() {
        return Map.copyOf(this.pendingRequests);
    }
}
