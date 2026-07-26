package gg.leo.IraqueClan.menu.leaderboard;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LevelLeaderboard extends BaseMenu {
    private final IraqueClan plugin;

    public LevelLeaderboard(IraqueClan plugin, Player player) {
        super(player, "&8&lRanking de N\u00edvel", 54, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&8");

        this.registerButton(4, new MenuButton(
                Material.EXPERIENCE_BOTTLE,
                "&#06d6a0&lRanking de N\u00edvel",
                List.of("", " &7Cl\u00e3s com maior n\u00edvel", ""),
                p -> {}
        ));

        List<Clan> clans = this.plugin.getClanManager().getClansSortedByLevel();
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
                            " &7N\u00edvel: &e&l" + clan.getLevel(),
                            " &7XP: &b" + clan.getXp(),
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
                    List.of("", " &7N\u00e3o h\u00e1 cl\u00e3s registrados ainda.", ""),
                    p -> {}
            ));
        }

        this.addBackButton(49, p -> new LeaderboardMainMenu(this.plugin, p).openMenu());
    }
}
