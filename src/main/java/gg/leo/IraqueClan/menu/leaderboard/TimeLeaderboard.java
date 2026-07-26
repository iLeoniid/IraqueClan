package gg.leo.IraqueClan.menu.leaderboard;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TimeLeaderboard extends BaseMenu {
    private final IraqueClan plugin;

    public TimeLeaderboard(IraqueClan plugin, Player player) {
        super(player, "&8&lTempo de Exist\u00eancia", 54, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&8");

        this.registerButton(4, new MenuButton(
                Material.CLOCK,
                "&#ffd166&lRanking por Tempo",
                List.of("", " &7Cl\u00e3s mais antigos", ""),
                p -> {}
        ));

        List<Clan> clans = this.plugin.getClanManager().getClansSortedByTime();
        String[] medals = {"&#ffd166&l#1", "&#f1faee&l#2", "&#ef476f&l#3"};

        for (int i = 0; i < Math.min(clans.size(), 45); i++) {
            Clan clan = clans.get(i);
            String prefix = i < 3 ? medals[i] + " " : "&7#" + (i + 1) + " ";
            boolean isMyClan = clan.isMember(this.player.getUniqueId());
            String highlight = isMyClan ? " &a\u2714" : "";
            Material mat = i == 0 ? Material.NETHERITE_INGOT : i == 1 ? Material.DIAMOND : i == 2 ? Material.GOLD_INGOT : Material.PAPER;
            long daysSinceCreation = (System.currentTimeMillis() - clan.getCreatedAt()) / 86400000L;

            this.registerButton(i + 10, new MenuButton(
                    mat,
                    prefix + clan.getFormattedTag() + highlight,
                    List.of(
                            "",
                            " &7Dias ativo: &e" + daysSinceCreation,
                            " &7Membros: &f" + clan.getMemberCount(),
                            " &7Kills: &c" + clan.getTotalKills(),
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
