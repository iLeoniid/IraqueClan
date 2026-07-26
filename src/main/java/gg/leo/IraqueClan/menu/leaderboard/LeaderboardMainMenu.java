package gg.leo.IraqueClan.menu.leaderboard;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.menu.ClanMenu;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LeaderboardMainMenu extends BaseMenu {
    private final IraqueClan plugin;

    public LeaderboardMainMenu(IraqueClan plugin, Player player) {
        super(player, "&8&lLeaderboard", 27, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&8");

        this.registerButton(10, new MenuButton(
                Material.IRON_SWORD,
                "&#ef476f&lKills",
                List.of(
                        "",
                        " &7Ranking de kills totais",
                        " &7de todos os cl\u00e3s",
                        ""
                ),
                p -> new KillsLeaderboard(this.plugin, p).openMenu()
        ));

        this.registerButton(11, new MenuButton(
                Material.PLAYER_HEAD,
                "&#4ecdc4&lMembros",
                List.of(
                        "",
                        " &7Ranking por n\u00famero",
                        " &7de membros por cl\u00e3o",
                        ""
                ),
                p -> new MembersLeaderboard(this.plugin, p).openMenu()
        ));

        this.registerButton(12, new MenuButton(
                Material.CLOCK,
                "&#ffd166&lTempo",
                List.of(
                        "",
                        " &7Ranking por tempo",
                        " &7de exist\u00eancia dos cl\u00e3s",
                        ""
                ),
                p -> new TimeLeaderboard(this.plugin, p).openMenu()
        ));

        this.registerButton(14, new MenuButton(
                Material.EXPERIENCE_BOTTLE,
                "&#06d6a0&lN\u00edvel",
                List.of(
                        "",
                        " &7Ranking por n\u00edvel",
                        " &7do cl\u00e3o",
                        ""
                ),
                p -> new LevelLeaderboard(this.plugin, p).openMenu()
        ));

        this.registerButton(15, new MenuButton(
                Material.NETHERITE_INGOT,
                "&#ffd166&lBanco",
                List.of(
                        "",
                        " &7Ranking por saldo",
                        " &7do banco do cl\u00e3o",
                        ""
                ),
                p -> new BankLeaderboard(this.plugin, p).openMenu()
        ));

        this.registerButton(16, new MenuButton(
                Material.ARROW,
                "&#a8dadc&lKDR",
                List.of(
                        "",
                        " &7Ranking por KDR",
                        " &7(kills / mortes)",
                        ""
                ),
                p -> new KdrLeaderboard(this.plugin, p).openMenu()
        ));

        this.addBackButton(22, p -> new ClanMenu(this.plugin, p).openMenu());
    }
}
