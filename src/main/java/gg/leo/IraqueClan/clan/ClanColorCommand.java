package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.utils.ItemBuilder;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;

public class ClanColorCommand implements ClanSubCommand {
    private static final Pattern HEX_PATTERN = Pattern.compile("^#?([0-9A-Fa-f]{6})$");
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
        String raw = args[1];
        var matcher = HEX_PATTERN.matcher(raw);
        if (!matcher.matches()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("color.invalid"));
            return;
        }
        String hexColor = "&#" + matcher.group(1);
        boolean changed = this.plugin.getClanManager().changeColor(player.getUniqueId(), hexColor);
        if (changed) {
            player.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("color.changed")
                    .replace("{color}", hexColor + clan.getTag())));
            this.plugin.getClanManager().addLog(player.getUniqueId(), "COLOR_CHANGED", "Cor alterada para " + hexColor);
            this.plugin.sendDiscordMessage(player.getName() + " alterou a cor do clã " + clan.getName());
        }
    }
}
