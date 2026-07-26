package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.entity.Player;

public class ClanDelHomeCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanDelHomeCommand(IraqueClan plugin) {
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
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-delhome"));
            return;
        }
        String homeName = args[1].toLowerCase();
        if (clan.getHome(homeName) == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.not-found")
                    .replace("{name}", homeName));
            return;
        }
        boolean removed = this.plugin.getClanManager().removeHome(player.getUniqueId(), homeName);
        if (removed) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.deleted")
                    .replace("{name}", homeName));
            this.plugin.getClanManager().addLog(player.getUniqueId(), "HOME_DELETED", "Base removida: " + homeName);
        }
    }
}
