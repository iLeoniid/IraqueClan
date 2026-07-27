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
        super(player, "&#555555&lRanking de KDR", 54, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&#555555");

        this.registerButton(4, new MenuButton(
                Material.ARROW,
                "&#a8dadc&lRanking de KDR",
                List.of("", " &#AAAAAACl\u00e3s com melhor KDR", " &#AAAAAA(kills / mortes)", ""),
                p -> {}
        ));

        List<Clan> clans = this.plugin.getClanManager().getAllClans().stream()
                .filter(c -> c.getDeaths() > 0 || c.getTotalKills() > 0)
                .sorted(Comparator.comparingDouble(Clan::getKDR).reversed())
                .collect(Collectors.toList());
        String[] medals = {"&#ffd166&l#1", "&#f1faee&l#2", "&#ef476f&l#3"};

        for (int i = 0; i < Math.min(clans.size(), 45); i++) {
            Clan clan = clans.get(i);
            String prefix = i < 3 ? medals[i] + " " : "&#AAAAAA#" + (i + 1) + " ";
            boolean isMyClan = clan.isMember(this.player.getUniqueId());
            String highlight = isMyClan ? " &#55FF55\u2714" : "";
            Material mat = i == 0 ? Material.NETHERITE_INGOT : i == 1 ? Material.DIAMOND : i == 2 ? Material.GOLD_INGOT : Material.PAPER;

            this.registerButton(i + 10, new MenuButton(
                    mat,
                    prefix + clan.getFormattedTag() + highlight,
                    List.of(
                            "",
                            " &#AAAAAAKDR: &#FFAA00&l" + String.format("%.2f", clan.getKDR()),
                            " &#AAAAAAKills: &#FF5555" + clan.getTotalKills(),
                            " &#AAAAAAMortes: &#AA0000" + clan.getDeaths(),
                            " &#AAAAAAMembros: &#FFFFFF" + clan.getMemberCount(),
                            isMyClan ? " &#55FF55\u2714 Seu cl\u00e3o!" : "",
                            ""
                    ),
                    p -> {}
            ));
        }

        if (clans.isEmpty()) {
            this.registerButton(22, new MenuButton(
                    Material.BARRIER,
                    "&#FF5555&lNenhum cl\u00e3o encontrado",
                    List.of("", " &#AAAAAAN\u00e3o h\u00e1 dados de KDR ainda.", ""),
                    p -> {}
            ));
        }

        this.addBackButton(49, p -> new LeaderboardMainMenu(this.plugin, p).openMenu());
    }
}
