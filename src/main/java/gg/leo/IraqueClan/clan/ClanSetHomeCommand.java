package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.entity.Player;

public class ClanSetHomeCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanSetHomeCommand(IraqueClan plugin) {
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
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-sethome"));
            return;
        }
        String homeName = args[1].toLowerCase();
        if (!clan.canAddHome()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.limit-reached")
                    .replace("{current}", String.valueOf(clan.getHomeCount()))
                    .replace("{max}", String.valueOf(clan.getMaxHomes())));
            return;
        }
        boolean added = this.plugin.getClanManager().addHome(player.getUniqueId(), homeName, player.getLocation());
        if (added) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.set")
                    .replace("{name}", homeName));
            this.plugin.getClanManager().addLog(player.getUniqueId(), "HOME_SET", "Base definida: " + homeName);
            this.plugin.sendDiscordMessage(player.getName() + " definiu a base " + homeName + " no clã " + clan.getName());
        }
    }
}
