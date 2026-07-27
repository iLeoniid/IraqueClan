package gg.leo.IraqueClan.menu.leaderboard;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BankLeaderboard extends BaseMenu {
    private final IraqueClan plugin;

    public BankLeaderboard(IraqueClan plugin, Player player) {
        super(player, "&#555555&lRanking do Banco", 54, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&#555555");

        this.registerButton(4, new MenuButton(
                Material.NETHERITE_INGOT,
                "&#ffd166&lRanking do Banco",
                List.of("", " &#AAAAAACl\u00e3s com mais dinheiro no banco", ""),
                p -> {}
        ));

        List<Clan> clans = this.plugin.getClanManager().getClansSortedByBank();
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
                            " &#AAAAAASaldo: &#55FF55$" + String.format("%.2f", clan.getBank()),
                            " &#AAAAAAN\u00edvel: &#FFFF55" + clan.getLevel(),
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
                    List.of("", " &#AAAAAAN\u00e3o h\u00e1 cl\u00e3s registrados ainda.", ""),
                    p -> {}
            ));
        }

        this.addBackButton(49, p -> new LeaderboardMainMenu(this.plugin, p).openMenu());
    }
}
