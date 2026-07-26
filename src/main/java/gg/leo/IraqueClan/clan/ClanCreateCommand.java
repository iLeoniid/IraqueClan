package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanCreateCommand implements CommandExecutor {
    private final IraqueClan plugin;

    public ClanCreateCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }
        if (!player.hasPermission("iraqueclan.create")) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return true;
        }
        if (this.plugin.getClanManager().isPlayerInClan(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.already-in-clan"));
            return true;
        }
        if (args.length < 3) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-create"));
            return true;
        }
        String clanName = args[1];
        String tag = args[2];
        if (clanName.length() > this.plugin.getConfigManager().getMaxNameLength()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.name-too-long")
                    .replace("{max}", String.valueOf(this.plugin.getConfigManager().getMaxNameLength())));
            return true;
        }
        if (tag.length() > this.plugin.getConfigManager().getMaxTagLength()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.tag-too-long")
                    .replace("{max}", String.valueOf(this.plugin.getConfigManager().getMaxTagLength())));
            return true;
        }
        if (this.plugin.getClanManager().clanNameExists(clanName)) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.name-taken"));
            return true;
        }
        if (this.plugin.getClanManager().tagExists(tag)) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.tag-taken"));
            return true;
        }
        boolean created = this.plugin.getClanManager().createClan(clanName, tag, "&#f1faee", player.getUniqueId());
        if (created) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.created")
                    .replace("{clan}", clanName));
        } else {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.create-error"));
        }
        return true;
    }
}
