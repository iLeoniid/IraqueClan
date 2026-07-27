package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.ClanUtils;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanProfileMenu extends BaseMenu {
    private final IraqueClan plugin;

    public ClanProfileMenu(IraqueClan plugin, Player player) {
        super(player, "&#555555&lPerfil do Cl\u00e3o", 45, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&#555555");

        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(this.player.getUniqueId());
        if (clan == null) {
            this.registerButton(22, new MenuButton(
                    Material.BARRIER,
                    "&#FF5555&lNenhum cl\u00e3o encontrado",
                    List.of("", " &#AAAAAAVoc\u00ea n\u00e3o est\u00e1 em um cl\u00e3o", ""),
                    p -> {}
            ));
            this.addBackButton(40, p -> new ClanMenu(this.plugin, p).openMenu());
            return;
        }

        Material iconMat = Material.getMaterial(clan.getIcon()) != null ? Material.getMaterial(clan.getIcon()) : Material.PAPER;
        double kdr = clan.getKDR();

        this.registerButton(4, new MenuButton(
                iconMat,
                "&#ffd166&l" + clan.getName(),
                List.of(
                        "",
                        " &#AAAAAATag: " + clan.getFormattedTag(),
                        " &#AAAAAADescri\u00e7\u00e3o: &#FFFFFF" + (clan.getDescription().isEmpty() ? "(sem)" : clan.getDescription()),
                        ""
                ),
                p -> {}
        ));

        this.registerButton(11, new MenuButton(
                Material.PLAYER_HEAD,
                "&#a8dadc&lMembros",
                List.of(
                        "",
                        " &#AAAAAATotal: &#FFFFFF" + clan.getMemberCount() + "/" + clan.getMaxMembers(),
                        " &#AAAAAAL\u00edder: &#ffd166" + ClanUtils.getPlayerName(clan.getLeader()),
                        ""
                ),
                p -> new ClanMembersMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(13, new MenuButton(
                Material.IRON_SWORD,
                "&#ef476f&lStats de Guerra",
                List.of(
                        "",
                        " &#AAAAAAKills: &#ef476f" + clan.getTotalKills(),
                        " &#AAAAAAMortes: &#AA0000" + clan.getDeaths(),
                        " &#AAAAAAKDR: &#ffd166" + String.format("%.2f", kdr),
                        "",
                        " &#AAAAAAVit\u00f3rias: &#06d6a0" + clan.getWarWins(),
                        " &#AAAAAADerrotas: &#ef476f" + clan.getWarLosses(),
                        " &#AAAAAAEmpates: &#AAAAAA" + clan.getWarDraws(),
                        ""
                ),
                p -> {}
        ));

        this.registerButton(15, new MenuButton(
                Material.EXPERIENCE_BOTTLE,
                "&#06d6a0&lN\u00edvel e Progresso",
                List.of(
                        "",
                        " &#AAAAAAN\u00edvel: &#ffd166&l" + clan.getLevel(),
                        " &#AAAAAAXP Total: &#4ecdc4" + clan.getXp(),
                        " &#AAAAAAPr\u00f3ximo: &#FFFFFF" + this.plugin.getClanManager().calculateRequiredXP(clan.getLevel() + 1),
                        "",
                        " &#AAAAAAN\u00edvel " + getLevelBar(clan.getLevel(), 100),
                        ""
                ),
                p -> {}
        ));

        this.registerButton(19, new MenuButton(
                Material.ENDER_CHEST,
                "&#4ecdc4&lBanco",
                List.of(
                        "",
                        " &#AAAAAASaldo: &#06d6a0$" + String.format("%.2f", clan.getBank()),
                        ""
                ),
                p -> new ClanBankMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(21, new MenuButton(
                Material.EMERALD,
                "&#06d6a0&lDiplomacia",
                List.of(
                        "",
                        " &#AAAAAAAliados: &#06d6a0" + countByType(clan, Clan.DiplomacyType.ALLY),
                        " &#AAAAAARivais: &#ef476f" + countByType(clan, Clan.DiplomacyType.RIVAL),
                        ""
                ),
                p -> new ClanDiplomacyMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(23, new MenuButton(
                Material.GOLD_INGOT,
                "&#ffd166&lConquistas",
                List.of(
                        "",
                        " &#AAAAAADesbloqueadas: &#FFFFFF" + clan.getAchievements().size(),
                        ""
                ),
                p -> new ClanAchievementsMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(25, new MenuButton(
                Material.CLOCK,
                "&#ffd166&lUpgrades",
                List.of(
                        "",
                        " &#AAAAAAUpgrades comprados: &#FFFFFF" + clan.getUpgrades().size(),
                        " &#AAAAAACasas: &#FFFFFF" + clan.getHomeCount() + "/" + clan.getMaxHomes(),
                        " &#AAAAAAMails: &#FFFFFF" + clan.getMailCount(),
                        ""
                ),
                p -> new ClanShopMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(31, new MenuButton(
                Material.BOOK,
                "&#AAAAAA&lDescri\u00e7\u00e3o",
                List.of(
                        "",
                        " &#FFFFFF" + (clan.getDescription().isEmpty() ? "(Sem descri\u00e7\u00e3o definida)" : clan.getDescription()),
                        ""
                ),
                p -> {}
        ));

        this.registerButton(33, new MenuButton(
                Material.OAK_SIGN,
                "&#AAAAAA&lMOTD",
                List.of(
                        "",
                        " &#FFFFFF" + (clan.getMotd().isEmpty() ? "(Sem MOTD definida)" : clan.getMotd()),
                        ""
                ),
                p -> {}
        ));

        this.addBackButton(40, p -> new ClanMenu(this.plugin, p).openMenu());
    }

    private int countByType(Clan clan, Clan.DiplomacyType type) {
        return (int) clan.getDiplomacy().stream()
                .filter(d -> d.type() == type)
                .count();
    }

    private String getLevelBar(int current, int max) {
        StringBuilder bar = new StringBuilder("&#AAAAAA[");
        int filled = Math.min(current, 20);
        for (int i = 0; i < 20; i++) {
            bar.append(i < filled ? "&#06d6a0\u2588" : "&#555555\u2591");
        }
        bar.append("&#AAAAAA]");
        return bar.toString();
    }
}
