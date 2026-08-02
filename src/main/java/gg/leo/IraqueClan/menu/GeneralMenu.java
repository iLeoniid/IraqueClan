package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.menu.leaderboard.LeaderboardMainMenu;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GeneralMenu extends BaseMenu {
    private final IraqueClan plugin;

    public GeneralMenu(IraqueClan plugin, Player player) {
        super(player, "&#555555&lGeneral Menu", 54, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&#555555");

        this.registerButton(4, new MenuButton(
                Material.SUNFLOWER,
                "&#ffd166&l\u2600 General Menu \u2600",
                List.of("", " &#AAAAAAPainel de navega\u00e7\u00e3o principal", ""),
                p -> {}
        ));
        for (int slot : new int[]{1, 2, 3, 5, 6, 7}) {
            this.registerButton(slot, new MenuButton(
                    Material.NETHER_STAR,
                    "&#ffd166\u2727",
                    List.of(),
                    p -> {}
            ));
        }

        this.buildQuickAccessRow();
        this.buildModulesRow();
        this.buildExpansionRow();
        this.buildToolsRow();
        this.buildBottomBar();
    }

    private void buildQuickAccessRow() {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(this.player.getUniqueId());
        boolean hasClan = clan != null;

        this.registerButton(10, new MenuButton(
                Material.BOOK,
                "&#ffd166&lInforma\u00e7\u00e3o & Guias",
                List.of(
                        "",
                        " &#AAAAAAGuias, ajuda e informa\u00e7\u00f5es",
                        " &#AAAAAAsobre o sistema de cl\u00e3s",
                        ""
                ),
                p -> {
                    if (clan == null) {
                        p.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-clan"));
                        return;
                    }
                    new ClanInfoMenu(this.plugin, p).openMenu();
                }
        ));

        this.registerButton(11, new MenuButton(
                Material.CRAFTING_TABLE,
                "&#06d6a0&lCrafteo & Recursos",
                List.of(
                        "",
                        " &#AAAAAAAbre uma mesa de crafteo",
                        " &#AAAAAAr\u00e1pida para voc\u00ea",
                        ""
                ),
                p -> p.openWorkbench(p.getLocation(), true)
        ));

        this.registerButton(12, new MenuButton(
                Material.BARRIER,
                "&#FF5555&lFechar",
                List.of("", " &#AAAAAAFechar este menu", ""),
                Player::closeInventory
        ));

        this.registerButton(13, new MenuButton(
                Material.ANVIL,
                "&#4ecdc4&lMelhorias & Upgrades",
                List.of(
                        "",
                        " &#AAAAAACompre upgrades para o seu cl\u00e3",
                        " &#AAAAAAMais membros, casas, vault e boost",
                        ""
                ),
                p -> new ClanShopMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(14, new MenuButton(
                Material.STRING,
                "&#ef476f&lTeletransporte & Bases",
                List.of(
                        "",
                        " &#AAAAAATeleporte para as bases do cl\u00e3",
                        hasClan ? " &#AAAAAABases: &#FFFFFF" + clan.getHomeCount() : "",
                        ""
                ),
                p -> {
                    if (this.plugin.getClanManager().getClanByPlayerDirect(p.getUniqueId()) == null) {
                        p.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-clan"));
                        return;
                    }
                    p.closeInventory();
                    p.performCommand("clan homes");
                }
        ));

        this.registerButton(15, new MenuButton(
                Material.NETHER_STAR,
                "&#ffd166&lEfeitos & Conquistas",
                List.of(
                        "",
                        " &#AAAAAAConquistas e recompensas",
                        " &#AAAAAAespeciais do cl\u00e3",
                        ""
                ),
                p -> new ClanAchievementsMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(16, new MenuButton(
                Material.GRASS_BLOCK,
                "&#55ff55&lLista de Cl\u00e3s",
                List.of(
                        "",
                        " &#AAAAAANavegue por todos os cl\u00e3s",
                        " &#AAAAAAe veja os membros de cada um",
                        ""
                ),
                p -> new ClanListMenu(this.plugin, p).openMenu()
        ));
    }

    private void buildModulesRow() {
        this.registerButton(20, new MenuButton(
                Material.CHEST,
                "&#06d6a0&lBanco do Cl\u00e3",
                List.of(
                        "",
                        " &#AAAAAADep\u00f3sitos e saques do cl\u00e3",
                        ""
                ),
                p -> new ClanBankMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(21, new MenuButton(
                Material.SHULKER_BOX,
                "&#4ecdc4&lVault & Armazenamento",
                List.of(
                        "",
                        " &#AAAAAAUpgrades de armazenamento",
                        " &#AAAAAAe capacidade do vault",
                        ""
                ),
                p -> new ClanShopMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(22, new MenuButton(
                Material.REDSTONE,
                "&#ef476f&lRankings",
                List.of(
                        "",
                        " &#AAAAAAKills, n\u00edveis, banco e KDR",
                        " &#AAAAAATodos os cl\u00e3s ranqueados",
                        ""
                ),
                p -> new LeaderboardMainMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(23, new MenuButton(
                Material.NOTE_BLOCK,
                "&#ffd166&lNotifica\u00e7\u00f5es & Mail",
                List.of(
                        "",
                        " &#AAAAAAReceba e envie mensagens",
                        " &#AAAAAAentre os membros do cl\u00e3",
                        ""
                ),
                p -> new ClanMailsMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(24, new MenuButton(
                Material.PLAYER_HEAD,
                "&#ffd166&lHub do Cl\u00e3",
                List.of(
                        "",
                        " &#AAAAAAIr para o hub principal",
                        " &#AAAAAAdo sistema de cl\u00e3s",
                        ""
                ),
                p -> new ClanMenu(this.plugin, p).openMenu()
        ));
    }

    private void buildExpansionRow() {
        for (int slot = 29; slot <= 33; slot++) {
            this.registerButton(slot, new MenuButton(
                    Material.IRON_BLOCK,
                    "&#555555&lM\u00f3dulo em desenvolvimento",
                    List.of("", " &#AAAAAAEste espa\u00e7o ser\u00e1 preenchido", " &#AAAAAAem uma pr\u00f3xima atualiza\u00e7\u00e3o", ""),
                    p -> {}
            ));
        }
    }

    private void buildToolsRow() {
        this.registerButton(38, new MenuButton(
                Material.WRITABLE_BOOK,
                "&#AAAAAA&lLogs",
                List.of("", " &#AAAAAAHist\u00f3rico de atividades do cl\u00e3", ""),
                p -> new ClanLogsMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(39, new MenuButton(
                Material.RED_BED,
                "&#ef476f&lBases",
                List.of("", " &#AAAAAAGerencie as bases do cl\u00e3", ""),
                p -> {
                    if (this.plugin.getClanManager().getClanByPlayerDirect(p.getUniqueId()) == null) {
                        p.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-clan"));
                        return;
                    }
                    p.closeInventory();
                    p.performCommand("clan homes");
                }
        ));

        this.registerButton(40, new MenuButton(
                Material.MAP,
                "&#4ecdc4&lAjuda",
                List.of("", " &#AAAAAAVeja todos os comandos do cl\u00e3", ""),
                p -> {
                    p.closeInventory();
                    p.performCommand("clan ajuda");
                }
        ));

        this.registerButton(41, new MenuButton(
                Material.NAME_TAG,
                "&#ffd166&lQuests",
                List.of("", " &#AAAAAAQuests ativas do cl\u00e3", ""),
                p -> new ClanQuestsMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(42, new MenuButton(
                Material.ENDER_CHEST,
                "&#06d6a0&lPerfil",
                List.of("", " &#AAAAAAPerfil completo do seu cl\u00e3", ""),
                p -> new ClanProfileMenu(this.plugin, p).openMenu()
        ));
    }

    private void buildBottomBar() {
        this.registerButton(49, new MenuButton(
                Material.BLACK_STAINED_GLASS_PANE,
                "&#AAAAAA&lInventory",
                List.of("", " &#AAAAAASeu invent\u00e1rio continua acess\u00edvel", ""),
                p -> {}
        ));
    }
}
