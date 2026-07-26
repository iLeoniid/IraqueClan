package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanDiplomacyMenu extends BaseMenu {
    private final IraqueClan plugin;

    public ClanDiplomacyMenu(IraqueClan plugin, Player player) {
        super(player, "&8&lDiplomacia", 45, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&8");

        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(this.player.getUniqueId());
        if (clan == null) {
            this.registerButton(22, new MenuButton(
                    Material.BARRIER,
                    "&c&lNenhum cl\u00e3o encontrado",
                    List.of("", " &7Voc\u00ea n\u00e3o est\u00e1 em um cl\u00e3o", ""),
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
                        " &7Aliados: &#06d6a0" + countByType(clan, Clan.DiplomacyType.ALLY),
                        " &7Rivais: &#ef476f" + countByType(clan, Clan.DiplomacyType.RIVAL),
                        ""
                ),
                p -> {}
        ));

        List<Clan.DiplomacyRelation> relations = clan.getDiplomacy();

        if (relations.isEmpty()) {
            this.registerButton(22, new MenuButton(
                    Material.PAPER,
                    "&7&lSem rela\u00e7\u00f5es",
                    List.of(
                            "",
                            " &7Nenhuma rela\u00e7\u00e3o diplom\u00e1tica",
                            " &7foi estabelecida ainda.",
                            "",
                            isLeader ? " &7Use comandos para gerenciar" : "",
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
                                " &7Status: " + typeStr,
                                " &7Membros: &f" + (otherClan != null ? otherClan.getMemberCount() : "?"),
                                " &7Kills: &#ef476f" + (otherClan != null ? otherClan.getTotalKills() : "?"),
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
                        " &7Declare outro cl\u00e3o como aliado",
                        "",
                        isLeader ? " &7Use: &e/clan diplomacia aliado <cl\u00e3o>" : " &#ef476fApenas o l\u00edder",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    if (isLeader) p.sendMessage("&7Use &e/clan diplomacia aliado <cl\u00e3o> &7para declarar alian\u00e7a.");
                }
        ));

        this.registerButton(40, new MenuButton(
                Material.RED_WOOL,
                "&#ef476f&lNovo Rival",
                List.of(
                        "",
                        " &7Declare outro cl\u00e3o como rival",
                        "",
                        isLeader ? " &7Use: &e/clan diplomacia rival <cl\u00e3o>" : " &#ef476fApenas o l\u00edder",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    if (isLeader) p.sendMessage("&7Use &e/clan diplomacia rival <cl\u00e3o> &7para declarar rivalidade.");
                }
        ));

        this.registerButton(42, new MenuButton(
                Material.BARRIER,
                "&8&lRemover",
                List.of(
                        "",
                        " &7Remova uma rela\u00e7\u00e3o diplom\u00e1tica",
                        "",
                        isLeader ? " &7Use: &e/clan diplomacia remover <cl\u00e3o>" : " &#ef476fApenas o l\u00edder",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    if (isLeader) p.sendMessage("&7Use &e/clan diplomacia remover <cl\u00e3o> &7para remover.");
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
