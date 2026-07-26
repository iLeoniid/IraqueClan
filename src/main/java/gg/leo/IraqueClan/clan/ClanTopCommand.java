package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;

public class ClanTopCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanTopCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-top"));
            return;
        }
        String type = args[1].toLowerCase();
        List<Clan> sorted;
        String metricLabel;
        switch (type) {
            case "kills" -> {
                sorted = this.plugin.getClanManager().getClansSortedByKills();
                metricLabel = this.plugin.getConfigManager().getMessage("top.type-kills");
            }
            case "level", "nivel" -> {
                sorted = this.plugin.getClanManager().getClansSortedByLevel();
                metricLabel = this.plugin.getConfigManager().getMessage("top.type-level");
            }
            case "bank", "banco" -> {
                sorted = this.plugin.getClanManager().getClansSortedByBank();
                metricLabel = this.plugin.getConfigManager().getMessage("top.type-bank");
            }
            case "kdr" -> {
                sorted = this.plugin.getClanManager().getAllClans().stream()
                        .sorted(Comparator.comparingDouble(Clan::getKDR).reversed())
                        .collect(Collectors.toList());
                metricLabel = this.plugin.getConfigManager().getMessage("top.type-kdr");
            }
            default -> {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-top"));
                return;
            }
        }
        if (sorted.isEmpty()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("top.empty"));
            return;
        }
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("top.header"));
        int limit = Math.min(sorted.size(), 10);
        for (int i = 0; i < limit; i++) {
            Clan clan = sorted.get(i);
            String value;
            switch (type) {
                case "kills" -> value = String.valueOf(clan.getTotalKills());
                case "level", "nivel" -> value = String.valueOf(clan.getLevel());
                case "bank", "banco" -> value = "$" + String.format("%.2f", clan.getBank());
                case "kdr" -> value = String.format("%.2f", clan.getKDR());
                default -> value = "?";
            }
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("top.entry")
                    .replace("{rank}", String.valueOf(i + 1))
                    .replace("{clan}", clan.getName())
                    .replace("{value}", value));
        }
    }
}
