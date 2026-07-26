package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanIconCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanIconCommand(IraqueClan plugin) {
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
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-icon"));
            return;
        }
        String materialName = args[1].toUpperCase();
        try {
            Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("icon.invalid"));
            return;
        }
        boolean set = this.plugin.getClanManager().setIcon(player.getUniqueId(), materialName);
        if (set) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("icon.set")
                    .replace("{material}", materialName));
            this.plugin.getClanManager().addLog(player.getUniqueId(), "ICON_SET", "Ícone alterado para " + materialName);
        }
    }
}
