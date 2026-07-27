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

public class ClanDiplomacyMenu extends BaseMenu {
    private final IraqueClan plugin;

    public ClanDiplomacyMenu(IraqueClan plugin, Player player) {
        super(player, "&#555555&lDiplomacia", 45, MenuType.SIMPLE);
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

        boolean isLeader = clan.getLeader().equals(this.player.getUniqueId());

        this.registerButton(4, new MenuButton(
                Material.EMERALD,
                "&#06d6a0&lDiplomacia do Cl\u00e3o",
                List.of(
                        "",
                        " &#AAAAAAAliados: &#06d6a0" + countByType(clan, Clan.DiplomacyType.ALLY),
                        " &#AAAAAARivais: &#ef476f" + countByType(clan, Clan.DiplomacyType.RIVAL),
                        ""
                ),
                p -> {}
        ));

        List<Clan.DiplomacyRelation> relations = clan.getDiplomacy();

        if (relations.isEmpty()) {
            this.registerButton(22, new MenuButton(
                    Material.PAPER,
                    "&#AAAAAA&lSem rela\u00e7\u00f5es",
                    List.of(
                            "",
                            " &#AAAAAANenhuma rela\u00e7\u00e3o diplom\u00e1tica",
                            " &#AAAAAAfoi estabelecida ainda.",
                            "",
                            isLeader ? " &#AAAAAAUse comandos para gerenciar" : "",
                            ""
                    ),
                    p -> {}
            ));
        } else {
            int slot = 10;
            for (Clan.DiplomacyRelation rel : relations) {
                if (slot >= 44) break;
                if (slot % 9 == 8) slot += 2;
                if (slot >= 44) break;

                Clan otherClan = this.plugin.getClanManager().getClanByUUID(rel.otherClanUUID());
                String otherName = otherClan != null ? otherClan.getName() : "Desconhecido";
                String otherTag = otherClan != null ? otherClan.getFormattedTag() : "???";

                Material mat;
                String typeStr;
                String colorCode;
                switch (rel.type()) {
                    case ALLY:
                        mat = Material.LIME_STAINED_GLASS_PANE;
                        typeStr = "&#06d6a0Aliado";
                        colorCode = "&#06d6a0";
                        break;
                    case RIVAL:
                        mat = Material.RED_STAINED_GLASS_PANE;
                        typeStr = "&#ef476fRival";
                        colorCode = "&#ef476f";
                        break;
                    default:
                        mat = Material.YELLOW_STAINED_GLASS_PANE;
                        typeStr = "&#ffd166Neutro";
                        colorCode = "&#ffd166";
                        break;
                }

                this.registerButton(slot, new MenuButton(
                        mat,
                        colorCode + otherName + " " + otherTag,
                        List.of(
                                "",
                                " &#AAAAAAStatus: " + typeStr,
                                " &#AAAAAAMembros: &#FFFFFF" + (otherClan != null ? otherClan.getMemberCount() : "?"),
                                " &#AAAAAAKills: &#ef476f" + (otherClan != null ? otherClan.getTotalKills() : "?"),
                                ""
                        ),
                        p -> {}
                ));

                slot++;
            }
        }

        this.registerButton(38, new MenuButton(
                Material.LIME_WOOL,
                "&#06d6a0&lNovo Aliado",
                List.of(
                        "",
                        " &#AAAAAADeclare outro cl\u00e3o como aliado",
                        "",
                        isLeader ? " &#AAAAAAUse: &#FFFF55/clan diplomacia aliado <cl\u00e3o>" : " &#ef476fApenas o l\u00edder",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    if (isLeader) p.sendMessage(ItemBuilder.color("&#AAAAAAUse &#FFFF55/clan diplomacia aliado <cl\u00e3o> &#AAAAAApara declarar alian\u00e7a."));
                }
        ));

        this.registerButton(40, new MenuButton(
                Material.RED_WOOL,
                "&#ef476f&lNovo Rival",
                List.of(
                        "",
                        " &#AAAAAADeclare outro cl\u00e3o como rival",
                        "",
                        isLeader ? " &#AAAAAAUse: &#FFFF55/clan diplomacia rival <cl\u00e3o>" : " &#ef476fApenas o l\u00edder",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    if (isLeader) p.sendMessage(ItemBuilder.color("&#AAAAAAUse &#FFFF55/clan diplomacia rival <cl\u00e3o> &#AAAAAApara declarar rivalidade."));
                }
        ));

        this.registerButton(42, new MenuButton(
                Material.BARRIER,
                "&#555555&lRemover",
                List.of(
                        "",
                        " &#AAAAAARemova uma rela\u00e7\u00e3o diplom\u00e1tica",
                        "",
                        isLeader ? " &#AAAAAAUse: &#FFFF55/clan diplomacia remover <cl\u00e3o>" : " &#ef476fApenas o l\u00edder",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    if (isLeader) p.sendMessage(ItemBuilder.color("&#AAAAAAUse &#FFFF55/clan diplomacia remover <cl\u00e3o> &#AAAAAApara remover."));
                }
        ));

        this.addBackButton(44, p -> new ClanMenu(this.plugin, p).openMenu());
    }

    private int countByType(Clan clan, Clan.DiplomacyType type) {
        return (int) clan.getDiplomacy().stream()
                .filter(d -> d.type() == type)
                .count();
    }
}
