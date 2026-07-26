package gg.leo.IraqueClan.menu.leaderboard;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KdrLeaderboard extends BaseMenu {
    private final IraqueClan plugin;

    public KdrLeaderboard(IraqueClan plugin, Player player) {
        super(player, "&8&lRanking de KDR", 54, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&8");

        this.registerButton(4, new MenuButton(
                Material.ARROW,
                "&#a8dadc&lRanking de KDR",
                List.of("", " &7Cl\u00e3s com melhor KDR", " &7(kills / mortes)", ""),
                p -> {}
        ));

        List<Clan> clans = this.plugin.getClanManager().getAllClans().stream()
                .filter(c -> c.getDeaths() > 0 || c.getTotalKills() > 0)
                .sorted(Comparator.comparingDouble(Clan::getKDR).reversed())
                .collect(Collectors.toList());
        String[] medals = {"&#ffd166&l#1", "&#f1faee&l#2", "&#ef476f&l#3"};

        for (int i = 0; i < Math.min(clans.size(), 45); i++) {
            Clan clan = clans.get(i);
            String prefix = i < 3 ? medals[i] + " " : "&7#" + (i + 1) + " ";
            boolean isMyClan = clan.isMember(this.player.getUniqueId());
            String highlight = isMyClan ? " &a\u2714" : "";
            Material mat = i == 0 ? Material.NETHERITE_INGOT : i == 1 ? Material.DIAMOND : i == 2 ? Material.GOLD_INGOT : Material.PAPER;

            this.registerButton(i + 10, new MenuButton(
                    mat,
                    prefix + clan.getFormattedTag() + highlight,
                    List.of(
                            "",
                            " &7KDR: &6&l" + String.format("%.2f", clan.getKDR()),
                            " &7Kills: &c" + clan.getTotalKills(),
                            " &7Mortes: &4" + clan.getDeaths(),
                            " &7Membros: &f" + clan.getMemberCount(),
                            isMyClan ? " &a\u2714 Seu cl\u00e3o!" : "",
                            ""
                    ),
                    p -> {}
            ));
        }

        if (clans.isEmpty()) {
            this.registerButton(22, new MenuButton(
                    Material.BARRIER,
                    "&c&lNenhum cl\u00e3o encontrado",
                    List.of("", " &7N\u00e3o h\u00e1 dados de KDR ainda.", ""),
                    p -> {}
            ));
        }

        this.addBackButton(49, p -> new LeaderboardMainMenu(this.plugin, p).openMenu());
    }
}
