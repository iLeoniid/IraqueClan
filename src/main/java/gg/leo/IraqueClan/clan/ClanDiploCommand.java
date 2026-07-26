package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;

public class ClanDiploCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanDiploCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-in-clan"));
            return;
        }
        if (!clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("diplo.not-leader"));
            return;
        }
        if (args.length < 2) {
            showCurrentRelations(player, clan);
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-diplo"));
            return;
        }
        String sub = args[1].toLowerCase();
        switch (sub) {
            case "aliado", "ally" -> handleSetRelation(player, clan, args, Clan.DiplomacyType.ALLY);
            case "rival" -> handleSetRelation(player, clan, args, Clan.DiplomacyType.RIVAL);
            case "neutro", "neutral" -> handleSetRelation(player, clan, args, Clan.DiplomacyType.NEUTRAL);
            case "remover", "remove" -> handleRemove(player, clan, args);
            default -> player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-diplo"));
        }
    }

    private void showCurrentRelations(Player player, Clan clan) {
        List<Clan.DiplomacyRelation> relations = clan.getDiplomacy();
        long allyCount = relations.stream().filter(d -> d.type() == Clan.DiplomacyType.ALLY).count();
        long rivalCount = relations.stream().filter(d -> d.type() == Clan.DiplomacyType.RIVAL).count();
        int maxAllies = this.plugin.getConfig().getInt("diplomacia.max-aliados", 3);
        int maxRivals = this.plugin.getConfig().getInt("diplomacia.max-rivais", 3);
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("diplo.current-allies")
                .replace("{allies}", allyCount + "/" + maxAllies));
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("diplo.current-rivals")
                .replace("{rivals}", rivalCount + "/" + maxRivals));
    }

    private void handleSetRelation(Player player, Clan clan, String[] args, Clan.DiplomacyType type) {
        if (args.length < 3) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-diplo"));
            return;
        }
        String targetClanName = args[2];
        Optional<Clan> targetOpt = this.plugin.getClanManager().getClan(targetClanName);
        if (targetOpt.isEmpty()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("diplo.target-not-found"));
            return;
        }
        Clan targetClan = targetOpt.get();
        if (clan.getName().equalsIgnoreCase(targetClan.getName())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("diplo.self"));
            return;
        }
        UUID requesterUuid = player.getUniqueId();
        UUID targetUuid = UUID.nameUUIDFromBytes(targetClan.getName().getBytes());
        if (type == Clan.DiplomacyType.ALLY) {
            long currentAllies = clan.getDiplomacy().stream()
                    .filter(d -> d.type() == Clan.DiplomacyType.ALLY).count();
            int maxAllies = this.plugin.getConfig().getInt("diplomacia.max-aliados", 3);
            if (currentAllies >= maxAllies) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("diplo.max-allies")
                        .replace("{max}", String.valueOf(maxAllies)));
                return;
            }
        }
        if (type == Clan.DiplomacyType.RIVAL) {
            long currentRivals = clan.getDiplomacy().stream()
                    .filter(d -> d.type() == Clan.DiplomacyType.RIVAL).count();
            int maxRivals = this.plugin.getConfig().getInt("diplomacia.max-rivais", 3);
            if (currentRivals >= maxRivals) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("diplo.max-rivals")
                        .replace("{max}", String.valueOf(maxRivals)));
                return;
            }
        }
        boolean added = this.plugin.getClanManager().addRelation(requesterUuid, targetUuid, type);
        if (added) {
            String typeName = switch (type) {
                case ALLY -> "aliado";
                case RIVAL -> "rival";
                case NEUTRAL -> "neutro";
            };
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("diplo.request-sent")
                    .replace("{type}", typeName)
                    .replace("{clan}", targetClan.getName()));
            for (UUID uuid : targetClan.getMembers().keySet()) {
                Player member = this.plugin.getServer().getPlayer(uuid);
                if (member != null && member.isOnline()) {
                    member.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("diplo.request-received")
                            .replace("{type}", typeName)
                            .replace("{clan}", clan.getName()));
                }
            }
            this.plugin.getClanManager().addLog(requesterUuid, "DIPLOMACY",
                    "Relação " + typeName + " com " + targetClan.getName());
            this.plugin.sendDiscordMessage(clan.getName() + " estabeleceu relação " + typeName + " com " + targetClan.getName());
        }
    }

    private void handleRemove(Player player, Clan clan, String[] args) {
        if (args.length < 3) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-diplo-remove"));
            return;
        }
        String targetClanName = args[2];
        Optional<Clan> targetOpt = this.plugin.getClanManager().getClan(targetClanName);
        if (targetOpt.isEmpty()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("diplo.target-not-found"));
            return;
        }
        Clan targetClan = targetOpt.get();
        UUID targetUuid = UUID.nameUUIDFromBytes(targetClan.getName().getBytes());
        boolean removed = this.plugin.getClanManager().removeRelation(player.getUniqueId(), targetUuid);
        if (removed) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("diplo.removed")
                    .replace("{clan}", targetClan.getName()));
            this.plugin.getClanManager().addLog(player.getUniqueId(), "DIPLO_REMOVED",
                    "Relação removida com " + targetClan.getName());
        }
    }
}
