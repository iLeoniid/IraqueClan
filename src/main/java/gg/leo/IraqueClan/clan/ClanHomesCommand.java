package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.Map;
import org.bukkit.entity.Player;

public class ClanHomesCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanHomesCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-in-clan"));
            return;
        }
        Map<String, Clan.SimpleLocation> homes = this.plugin.getClanManager().getHomes(player.getUniqueId());
        if (homes.isEmpty()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.no-homes"));
            return;
        }
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.list-header")
                .replace("{clan}", clan.getName()));
        for (Map.Entry<String, Clan.SimpleLocation> entry : homes.entrySet()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("home.list-entry")
                    .replace("{name}", entry.getKey())
                    .replace("{world}", entry.getValue().getWorld()));
        }
    }
}
