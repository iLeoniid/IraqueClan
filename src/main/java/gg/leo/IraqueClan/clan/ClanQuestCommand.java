package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class ClanQuestCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanQuestCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-in-clan"));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-quest"));
            return;
        }
        String sub = args[1].toLowerCase();
        switch (sub) {
            case "list", "lista" -> handleList(player, clan);
            case "refresh", "atualizar" -> handleRefresh(player, clan);
            default -> player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-quest"));
        }
    }

    private void handleList(Player player, Clan clan) {
        Map<String, Clan.ClanQuest> quests = clan.getActiveQuests();
        if (quests.isEmpty()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("quest.no-active"));
            return;
        }
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("quest.list-header"));
        for (Clan.ClanQuest quest : quests.values()) {
            String status = quest.isComplete() ? "&#55FF55✓" : "&#FF5555✗";
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("quest.list-entry")
                    .replace("{name}", quest.id())
                    .replace("{current}", String.valueOf(quest.current()))
                    .replace("{required}", String.valueOf(quest.required()))
                    .replace("{status}", status));
        }
    }

    private void handleRefresh(Player player, Clan clan) {
        if (!clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        UUID uuid = player.getUniqueId();
        for (String questId : new java.util.ArrayList<>(clan.getActiveQuests().keySet())) {
            this.plugin.getClanManager().completeQuest(uuid, questId);
        }
        this.plugin.getClanManager().addLog(uuid, "QUEST_REFRESHED", "Quests atualizadas pelo líder");
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("quest.refreshed"));
        this.plugin.sendDiscordMessage(player.getName() + " atualizou as quests do clã " + clan.getName());
    }
}
