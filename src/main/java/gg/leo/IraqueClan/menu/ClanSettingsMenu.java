package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.clan.role.ClanRole;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanSettingsMenu extends BaseMenu {
    private final IraqueClan plugin;

    public ClanSettingsMenu(IraqueClan plugin, Player player) {
        super(player, "&8&lConfigura\u00e7\u00f5es do Cl\u00e3o", 45, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&8");

        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(this.player.getUniqueId());
        if (clan == null || !clan.getLeader().equals(this.player.getUniqueId())) {
            this.registerButton(22, new MenuButton(
                    Material.BARRIER,
                    "&c&lAcesso negado",
                    List.of(
                            "",
                            " &7Apenas o l\u00edder pode alterar",
                            " &7as configura\u00e7\u00f5es do cl\u00e3o",
                            ""
                    ),
                    p -> {}
            ));
            this.addBackButton(40, p -> new ClanMenu(this.plugin, p).openMenu());
            return;
        }

        this.registerButton(10, new MenuButton(
                Material.NAME_TAG,
                "&#ffd166&lMudar Tag",
                List.of(
                        "",
                        " &7Tag atual: " + clan.getFormattedTag(),
                        "",
                        " &7Use: &e/clan tag <nova tag>",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage("&7Use &e/clan tag <nova tag> &7para alterar a tag.");
                }
        ));

        this.registerButton(12, new MenuButton(
                Material.PAINTING,
                "&#ffd166&lMudar Cor",
                List.of(
                        "",
                        " &7Cor atual: &f" + (clan.getTagColor().isEmpty() ? "(padr\u00e3o)" : clan.getTagColor()),
                        "",
                        " &7Use: &e/clan cor <cor hex ou c\u00f3digo>",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage("&7Use &e/clan cor <cor> &7para alterar a cor.");
                }
        ));

        this.registerButton(14, new MenuButton(
                Material.WRITABLE_BOOK,
                "&#ffd166&lDescri\u00e7\u00e3o",
                List.of(
                        "",
                        " &7Atual: &f" + (clan.getDescription().isEmpty() ? "(vazio)" : clan.getDescription()),
                        "",
                        " &7Use: &e/clan descricao <texto>",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage("&7Use &e/clan descricao <texto> &7para definir.");
                }
        ));

        this.registerButton(16, new MenuButton(
                Material.WHITE_BANNER,
                "&#ffd166&lIcone do Cl\u00e3o",
                List.of(
                        "",
                        " &7Icone atual: &f" + clan.getIcon(),
                        "",
                        " &7Use: &e/clan icone <material>",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage("&7Use &e/clan icone <material> &7para alterar.");
                }
        ));

        this.registerButton(28, new MenuButton(
                Material.OAK_SIGN,
                "&#ffd166&lMOTD",
                List.of(
                        "",
                        " &7Mensagem do dia:",
                        " &f" + (clan.getMotd().isEmpty() ? "(nenhuma)" : clan.getMotd()),
                        "",
                        " &7Use: &e/clan motd <mensagem>",
                        " &7Use: &e/clan motd limpar",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage("&7Use &e/clan motd <mensagem> &7para definir.");
                }
        ));

        this.registerButton(30, new MenuButton(
                Material.RED_WOOL,
                "&#ffd166&lNomes de Cargo",
                List.of(
                        "",
                        " &7L\u00edder: &#ffd166" + clan.getRoleName(ClanRole.LIDER),
                        " &7Sub-L\u00edder: &#4ecdc4" + clan.getRoleName(ClanRole.SUB_LIDER),
                        " &7Membro: &7" + clan.getRoleName(ClanRole.MEMBRO),
                        "",
                        " &7Use: &e/clan cargo <l\u00edder|sub|membro> <nome>",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage("&7Use &e/clan cargo <tipo> <nome> &7para alterar.");
                }
        ));

        this.registerButton(32, new MenuButton(
                Material.BOOKSHELF,
                "&#ffd166&lHomes do Cl\u00e3o",
                List.of(
                        "",
                        " &7Casas: &f" + clan.getHomeCount() + "/" + clan.getMaxHomes(),
                        "",
                        " &7Use: &e/clan casa set <nome>",
                        " &7Use: &e/clan casa tp <nome>",
                        " &7Use: &e/clan casa remover <nome>",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage("&7Use &e/clan casa <set|tp|list> &7para gerenciar.");
                }
        ));

        this.registerButton(34, new MenuButton(
                Material.EMERALD,
                "&#06d6a0&lLoja de Upgrades",
                List.of(
                        "",
                        " &7Veja e compre upgrades",
                        " &7para o seu cl\u00e3o",
                        ""
                ),
                p -> new ClanShopMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(38, new MenuButton(
                Material.BARRIER,
                "&#ef476f&lLimpar Logs",
                List.of(
                        "",
                        " &7Limpa todo o hist\u00f3rico",
                        " &7de atividades do cl\u00e3o",
                        " &#ef476fEsta a\u00e7\u00e3o \u00e9 irrevers\u00edvel!",
                        ""
                ),
                p -> {
                    this.plugin.getClanManager().clearLogs(p.getUniqueId());
                    p.sendMessage("&aLogs do cl\u00e3o limpos!");
                    p.closeInventory();
                }
        ));

        this.addBackButton(40, p -> new ClanMenu(this.plugin, p).openMenu());
    }
}
