package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.ItemBuilder;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanBankMenu extends BaseMenu {
    private final IraqueClan plugin;

    public ClanBankMenu(IraqueClan plugin, Player player) {
        super(player, "&#555555&lBanco do Cl\u00e3o", 27, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&#555555");

        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(this.player.getUniqueId());
        if (clan == null) {
            this.registerButton(13, new MenuButton(
                    Material.BARRIER,
                    "&#FF5555&lNenhum cl\u00e3o encontrado",
                    List.of("", " &#AAAAAAVoc\u00ea n\u00e3o est\u00e1 em um cl\u00e3o", ""),
                    p -> {}
            ));
            this.addBackButton(22, p -> new ClanMenu(this.plugin, p).openMenu());
            return;
        }

        boolean isLeader = clan.getLeader().equals(this.player.getUniqueId());

        this.registerButton(11, new MenuButton(
                Material.NETHERITE_INGOT,
                "&#ffd166&lSaldo do Cl\u00e3o",
                List.of(
                        "",
                        " &#06d6a0&l$" + String.format("%.2f", clan.getBank()),
                        "",
                        " &#AAAAAAEste \u00e9 o saldo total",
                        " &#AAAAAAdo banco do seu cl\u00e3o",
                        ""
                ),
                p -> {}
        ));

        this.registerButton(13, new MenuButton(
                Material.HOPPER,
                "&#06d6a0&lDepositar",
                List.of(
                        "",
                        " &#AAAAAADeposite dinheiro no banco",
                        " &#AAAAAAdo seu cl\u00e3o",
                        "",
                        " &#AAAAAAUse: &#FFFF55/clan banco depositar <valor>",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage(ItemBuilder.color("&#AAAAAAUse &#FFFF55/clan banco depositar <valor> &#AAAAAApara depositar."));
                }
        ));

        this.registerButton(15, new MenuButton(
                Material.DROPPER,
                isLeader ? "&#ef476f&lSacar" : "&#555555&lSacar",
                isLeader
                        ? List.of(
                                "",
                                " &#AAAAAASaque dinheiro do banco",
                                " &#AAAAAAdo seu cl\u00e3o",
                                "",
                                " &#AAAAAAUse: &#FFFF55/clan banco sacar <valor>",
                                ""
                        )
                        : List.of(
                                "",
                                " &#ef476fApenas o l\u00edder pode sacar",
                                ""
                        ),
                isLeader
                        ? p -> {
                            p.closeInventory();
                            p.sendMessage(ItemBuilder.color("&#AAAAAAUse &#FFFF55/clan banco sacar <valor> &#AAAAAApara sacar."));
                        }
                        : p -> {}
        ));

        this.registerButton(21, new MenuButton(
                Material.BARRIER,
                "&#AAAAAA&lTransa\u00e7\u00f5es",
                List.of(
                        "",
                        " &#AAAAAAVeja o hist\u00f3rico de",
                        " &#AAAAAAtransa\u00e7\u00f5es do banco",
                        " &#AAAAAA(use &#FFFF55/clan logs &#AAAAAApara ver)",
                        ""
                ),
                p -> new ClanLogsMenu(this.plugin, p).openMenu()
        ));

        this.addBackButton(22, p -> new ClanMenu(this.plugin, p).openMenu());
    }
}
