package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.clan.role.ClanRole;
import gg.leo.IraqueClan.utils.ItemBuilder;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanSettingsMenu extends BaseMenu {
    private final IraqueClan plugin;

    public ClanSettingsMenu(IraqueClan plugin, Player player) {
        super(player, "&#555555&lConfigura\u00e7\u00f5es do Cl\u00e3o", 45, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&#555555");

        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(this.player.getUniqueId());
        if (clan == null || !clan.getLeader().equals(this.player.getUniqueId())) {
            this.registerButton(22, new MenuButton(
                    Material.BARRIER,
                    "&#FF5555&lAcesso negado",
                    List.of(
                            "",
                            " &#AAAAAAApenas o l\u00edder pode alterar",
                            " &#AAAAAAas configura\u00e7\u00f5es do cl\u00e3o",
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
                        " &#AAAAAATag atual: " + clan.getFormattedTag(),
                        "",
                        " &#AAAAAAUse: &#FFFF55/clan tag <nova tag>",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage(ItemBuilder.color("&#AAAAAAUse &#FFFF55/clan tag <nova tag> &#AAAAAApara alterar a tag."));
                }
        ));

        this.registerButton(12, new MenuButton(
                Material.PAINTING,
                "&#ffd166&lMudar Cor",
                List.of(
                        "",
                        " &#AAAAAACor atual: &#FFFFFF" + (clan.getTagColor().isEmpty() ? "(padr\u00e3o)" : clan.getTagColor()),
                        "",
                        " &#AAAAAAUse: &#FFFF55/clan cor <cor hex ou c\u00f3digo>",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage(ItemBuilder.color("&#AAAAAAUse &#FFFF55/clan cor <cor> &#AAAAAApara alterar a cor."));
                }
        ));

        this.registerButton(14, new MenuButton(
                Material.WRITABLE_BOOK,
                "&#ffd166&lDescri\u00e7\u00e3o",
                List.of(
                        "",
                        " &#AAAAAAAtual: &#FFFFFF" + (clan.getDescription().isEmpty() ? "(vazio)" : clan.getDescription()),
                        "",
                        " &#AAAAAAUse: &#FFFF55/clan descricao <texto>",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage(ItemBuilder.color("&#AAAAAAUse &#FFFF55/clan descricao <texto> &#AAAAAApara definir."));
                }
        ));

        this.registerButton(16, new MenuButton(
                Material.WHITE_BANNER,
                "&#ffd166&lIcone do Cl\u00e3o",
                List.of(
                        "",
                        " &#AAAAAAIcone atual: &#FFFFFF" + clan.getIcon(),
                        "",
                        " &#AAAAAAUse: &#FFFF55/clan icone <material>",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage(ItemBuilder.color("&#AAAAAAUse &#FFFF55/clan icone <material> &#AAAAAApara alterar."));
                }
        ));

        this.registerButton(28, new MenuButton(
                Material.OAK_SIGN,
                "&#ffd166&lMOTD",
                List.of(
                        "",
                        " &#AAAAAAMensagem do dia:",
                        " &#FFFFFF" + (clan.getMotd().isEmpty() ? "(nenhuma)" : clan.getMotd()),
                        "",
                        " &#AAAAAAUse: &#FFFF55/clan motd <mensagem>",
                        " &#AAAAAAUse: &#FFFF55/clan motd limpar",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage(ItemBuilder.color("&#AAAAAAUse &#FFFF55/clan motd <mensagem> &#AAAAAApara definir."));
                }
        ));

        this.registerButton(30, new MenuButton(
                Material.RED_WOOL,
                "&#ffd166&lNomes de Cargo",
                List.of(
                        "",
                        " &#AAAAAAL\u00edder: &#ffd166" + clan.getRoleName(ClanRole.LIDER),
                        " &#AAAAAASub-L\u00edder: &#4ecdc4" + clan.getRoleName(ClanRole.SUB_LIDER),
                        " &#AAAAAAMembro: &#AAAAAA" + clan.getRoleName(ClanRole.MEMBRO),
                        "",
                        " &#AAAAAAUse: &#FFFF55/clan cargo <l\u00edder|sub|membro> <nome>",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage(ItemBuilder.color("&#AAAAAAUse &#FFFF55/clan cargo <tipo> <nome> &#AAAAAApara alterar."));
                }
        ));

        this.registerButton(32, new MenuButton(
                Material.BOOKSHELF,
                "&#ffd166&lHomes do Cl\u00e3o",
                List.of(
                        "",
                        " &#AAAAAACasas: &#FFFFFF" + clan.getHomeCount() + "/" + clan.getMaxHomes(),
                        "",
                        " &#AAAAAAUse: &#FFFF55/clan casa set <nome>",
                        " &#AAAAAAUse: &#FFFF55/clan casa tp <nome>",
                        " &#AAAAAAUse: &#FFFF55/clan casa remover <nome>",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage(ItemBuilder.color("&#AAAAAAUse &#FFFF55/clan casa <set|tp|list> &#AAAAAApara gerenciar."));
                }
        ));

        this.registerButton(34, new MenuButton(
                Material.EMERALD,
                "&#06d6a0&lLoja de Upgrades",
                List.of(
                        "",
                        " &#AAAAAAVeja e compre upgrades",
                        " &#AAAAAApara o seu cl\u00e3o",
                        ""
                ),
                p -> new ClanShopMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(38, new MenuButton(
                Material.BARRIER,
                "&#ef476f&lLimpar Logs",
                List.of(
                        "",
                        " &#AAAAAALimpa todo o hist\u00f3rico",
                        " &#AAAAAAde atividades do cl\u00e3o",
                        " &#ef476fEsta a\u00e7\u00e3o \u00e9 irrevers\u00edvel!",
                        ""
                ),
                p -> {
                    this.plugin.getClanManager().clearLogs(p.getUniqueId());
                    p.sendMessage(ItemBuilder.color("&#55FF55Logs do cl\u00e3o limpos!"));
                    p.closeInventory();
                }
        ));

        this.addBackButton(40, p -> new ClanMenu(this.plugin, p).openMenu());
    }
}
