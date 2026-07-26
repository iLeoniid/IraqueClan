package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.ClanUtils;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanInfoMenu extends BaseMenu {
    private final IraqueClan plugin;

    public ClanInfoMenu(IraqueClan plugin, Player player) {
        super(player, "&8&lInfo do Cl\u00e3o", 27, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&8");

        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(this.player.getUniqueId());
        if (clan == null) {
            this.registerButton(13, new MenuButton(
                    Material.BARRIER,
                    "&c&lNenhum cl\u00e3o encontrado",
                    List.of("", " &7Voc\u00ea ainda n\u00e3o est\u00e1 em nenhum cl\u00e3o", ""),
                    p -> {}
            ));
            this.addBackButton(22, p -> new ClanMenu(this.plugin, p).openMenu());
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String leaderName = ClanUtils.getPlayerName(clan.getLeader());
        double kdr = clan.getKDR();

        this.registerButton(10, new MenuButton(
                Material.NAME_TAG,
                "&#ffd166&lNome",
                List.of("", " &f" + clan.getName(), ""),
                p -> {}
        ));

        this.registerButton(11, new MenuButton(
                Material.BOOK,
                "&#ffd166&lTag",
                List.of("", " &f" + clan.getFormattedTag(), ""),
                p -> {}
        ));

        this.registerButton(12, new MenuButton(
                Material.NETHERITE_INGOT,
                "&#ffd166&lL\u00edder",
                List.of("", " &f" + leaderName, ""),
                p -> {}
        ));

        this.registerButton(14, new MenuButton(
                Material.PLAYER_HEAD,
                "&#a8dadc&lMembros",
                List.of("", " &f" + clan.getMemberCount() + "/" + clan.getMaxMembers(), ""),
                p -> new ClanMembersMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(15, new MenuButton(
                Material.IRON_SWORD,
                "&#ef476f&lKills / KDR",
                List.of(
                        "",
                        " &7Kills: &#ef476f" + clan.getTotalKills(),
                        " &7Mortes: &4" + clan.getDeaths(),
                        " &7KDR: &#ffd166" + String.format("%.2f", kdr),
                        ""
                ),
                p -> {}
        ));

        this.registerButton(16, new MenuButton(
                Material.GOLD_INGOT,
                "&#ffd166&lGuerra",
                List.of(
                        "",
                        " &7Vit\u00f3rias: &#06d6a0" + clan.getWarWins(),
                        " &7Derrotas: &#ef476f" + clan.getWarLosses(),
                        " &7Empates: &7" + clan.getWarDraws(),
                        ""
                ),
                p -> {}
        ));

        this.registerButton(19, new MenuButton(
                Material.EXPERIENCE_BOTTLE,
                "&#06d6a0&lN\u00edvel / XP",
                List.of(
                        "",
                        " &7N\u00edvel: &#ffd166" + clan.getLevel(),
                        " &7XP Total: &#4ecdc4" + clan.getXp(),
                        " &7Pr\u00f3ximo n\u00edvel: &f" + this.plugin.getClanManager().calculateRequiredXP(clan.getLevel() + 1),
                        ""
                ),
                p -> {}
        ));

        this.registerButton(21, new MenuButton(
                Material.ENDER_CHEST,
                "&#4ecdc4&lBanco",
                List.of("", " &7Saldo: &#06d6a0$" + String.format("%.2f", clan.getBank()), ""),
                p -> new ClanBankMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(23, new MenuButton(
                Material.CLOCK,
                "&#ffd166&lCriado em",
                List.of("", " &f" + sdf.format(new Date(clan.getCreatedAt())), ""),
                p -> {}
        ));

        this.registerButton(25, new MenuButton(
                Material.PAPER,
                "&7&lDescri\u00e7\u00e3o",
                List.of(
                        "",
                        " &f" + (clan.getDescription().isEmpty() ? "(Sem descri\u00e7\u00e3o)" : clan.getDescription()),
                        ""
                ),
                p -> {}
        ));

        this.addBackButton(22, p -> new ClanMenu(this.plugin, p).openMenu());
    }
}
