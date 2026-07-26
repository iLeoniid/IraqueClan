package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.List;
import org.bukkit.entity.Player;

public class ClanTagCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanTagCommand(IraqueClan plugin) {
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
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-tag"));
            return;
        }
        String newTag = args[1];
        int minLength = this.plugin.getConfig().getInt("criacao.min-tag-tamanho", 2);
        int maxLength = this.plugin.getConfigManager().getMaxTagLength();
        if (newTag.length() < minLength) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.tag-too-short")
                    .replace("{min}", String.valueOf(minLength)));
            return;
        }
        if (newTag.length() > maxLength) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.tag-too-long")
                    .replace("{max}", String.valueOf(maxLength)));
            return;
        }
        if (this.plugin.getClanManager().tagExists(newTag)) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.tag-taken"));
            return;
        }
        List<String> blockedTags = this.plugin.getConfig().getStringList("criacao.tags-bloqueadas");
        if (blockedTags.stream().anyMatch(bt -> bt.equalsIgnoreCase(newTag))) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.tag-blocked"));
            return;
        }
        boolean changed = this.plugin.getClanManager().changeTag(player.getUniqueId(), newTag);
        if (changed) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("tag.changed")
                    .replace("{tag}", newTag));
            this.plugin.getClanManager().addLog(player.getUniqueId(), "TAG_CHANGED", "Tag alterada para " + newTag);
            this.plugin.sendDiscordMessage(player.getName() + " alterou a tag do clã " + clan.getName() + " para " + newTag);
        }
    }
}
