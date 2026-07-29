package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.utils.ItemBuilder;
import java.util.Map;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;

public class ClanColorCommand implements ClanSubCommand {
    private static final Pattern CLEAN_HEX = Pattern.compile("(?i)^[0-9A-F]{3,6}$");

    private static final Map<String, String> NAMED_COLORS = Map.ofEntries(
        Map.entry("black", "000000"),
        Map.entry("dark_blue", "0000AA"),
        Map.entry("dark_green", "00AA00"),
        Map.entry("dark_aqua", "00AAAA"),
        Map.entry("dark_red", "AA0000"),
        Map.entry("dark_purple", "AA00AA"),
        Map.entry("gold", "FFAA00"),
        Map.entry("gray", "AAAAAA"),
        Map.entry("dark_gray", "555555"),
        Map.entry("blue", "5555FF"),
        Map.entry("green", "55FF55"),
        Map.entry("aqua", "55FFFF"),
        Map.entry("red", "FF5555"),
        Map.entry("light_purple", "FF55FF"),
        Map.entry("yellow", "FFFF55"),
        Map.entry("white", "FFFFFF"),
        Map.entry("orange", "FF8800"),
        Map.entry("pink", "FFAABB"),
        Map.entry("lime", "88FF00"),
        Map.entry("cyan", "00FFFF"),
        Map.entry("brown", "8B4513"),
        Map.entry("maroon", "800000"),
        Map.entry("navy", "000080"),
        Map.entry("teal", "008080"),
        Map.entry("purple", "800080"),
        Map.entry("olive", "808000"),
        Map.entry("coral", "FF7F50"),
        Map.entry("salmon", "FA8072"),
        Map.entry("tomato", "FF6347"),
        Map.entry("indigo", "4B0082"),
        Map.entry("violet", "EE82EE")
    );

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
        String raw = args[1].strip();
        String hex = normalizeHex(raw);
        if (hex == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("color.invalid"));
            return;
        }
        String hexColor = "&#" + hex;
        boolean changed = this.plugin.getClanManager().changeColor(player.getUniqueId(), hexColor);
        if (changed) {
            player.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("color.changed")
                    .replace("{color}", hexColor + clan.getTag())));
            this.plugin.getClanManager().addLog(player.getUniqueId(), "COLOR_CHANGED", "Cor alterada para " + hexColor);
            this.plugin.sendDiscordMessage(player.getName() + " alterou a cor do clã " + clan.getName());
        }
    }

    private String normalizeHex(String raw) {
        String s = raw
                .replace("&#", "")
                .replace("&x", "")
                .replace("<#", "")
                .replace(">", "")
                .replace("0x", "")
                .replace("0X", "")
                .replace("#", "")
                .replace(" ", "");
        if (s.isEmpty()) return null;
        String lower = s.toLowerCase();
        if (NAMED_COLORS.containsKey(lower)) return NAMED_COLORS.get(lower);
        if (!CLEAN_HEX.matcher(s).matches()) return null;
        if (s.length() == 3) {
            return "" + s.charAt(0) + s.charAt(0)
                     + s.charAt(1) + s.charAt(1)
                     + s.charAt(2) + s.charAt(2);
        }
        if (s.length() == 6) return s;
        return null;
    }
}
