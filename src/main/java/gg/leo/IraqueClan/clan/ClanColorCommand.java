package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.entity.Player;

public class ClanColorCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanColorCommand(IraqueClan plugin) {
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
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-color"));
            return;
        }
        String hexColor = args[1];
        if (!hexColor.matches("^&#([0-9A-Fa-f]{6})$")) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("color.invalid"));
            return;
        }
        boolean changed = this.plugin.getClanManager().changeColor(player.getUniqueId(), hexColor);
        if (changed) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("color.changed")
                    .replace("{color}", hexColor));
            this.plugin.getClanManager().addLog(player.getUniqueId(), "COLOR_CHANGED", "Cor alterada para " + hexColor);
            this.plugin.sendDiscordMessage(player.getName() + " alterou a cor do clã " + clan.getName());
        }
    }
}
