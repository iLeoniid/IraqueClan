package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.clan.ClanChatCommand;
import gg.leo.IraqueClan.menu.leaderboard.LeaderboardMainMenu;
import gg.leo.IraqueClan.utils.ClanUtils;
import gg.leo.IraqueClan.utils.ItemBuilder;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanMenu extends BaseMenu {
    private final IraqueClan plugin;

    public ClanMenu(IraqueClan plugin, Player player) {
        super(player, "&#555555&lHub do Cl\u00e3o", 54, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&#555555");

        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(this.player.getUniqueId());
        boolean hasClan = clan != null;

        if (!hasClan) {
            buildNoClanMenu();
        } else {
            buildClanMenu(clan);
        }

        this.registerButton(45, new MenuButton(
                Material.COMPASS,
                "&#ffd166&lMenu Geral",
                List.of(
                        "",
                        " &#AAAAAAPainel principal de navega\u00e7\u00e3o",
                        " &#AAAAAATodos os m\u00f3dulos em um s\u00f3 lugar",
                        ""
                ),
                p -> new GeneralMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(46, new MenuButton(
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

        this.registerButton(52, new MenuButton(
                Material.COMMAND_BLOCK,
                "&#ffd166&lComandos",
                List.of(
                        "",
                        " &#FFFF55/clan criar &#AAAAAA<nome> <tag>",
                        " &#FFFF55/clan convidar &#AAAAAA<jogador>",
                        " &#FFFF55/clan aceitar",
                        " &#FFFF55/clan sair",
                        " &#FFFF55/clan expulsar &#AAAAAA<jogador>",
                        " &#FFFF55/clan promover &#AAAAAA<jogador>",
                        " &#FFFF55/clan rebaixar &#AAAAAA<jogador>",
                        " &#FFFF55/clan banco &#AAAAAA<depositar|sacar>",
                        " &#FFFF55/clan casa &#AAAAAA<set|tp|list>",
                        " &#FFFF55/clan mail &#AAAAAA<enviar|ler>",
                        " &#FFFF55/clan loja",
                        " &#FFFF55/clan guerras",
                        ""
                ),
                p -> p.closeInventory()
        ));

        this.registerButton(53, new MenuButton(
                Material.BARRIER,
                "&#FF5555&lFechar",
                List.of("", " &#AAAAAAFechar este menu", ""),
                Player::closeInventory
        ));
    }

    private void buildNoClanMenu() {

        this.registerButton(4, new MenuButton(
                Material.PAPER,
                "&#ffd166&lHub do Cl\u00e3o",
                List.of(
                        "",
                        " &#ef476fVoc\u00ea ainda n\u00e3o est\u00e1 em um cl\u00e3o",
                        " &#AAAAAACrie ou entre em um para come\u00e7ar!",
                        ""
                ),
                p -> {}
        ));

        this.registerButton(20, new MenuButton(
                Material.EMERALD_BLOCK,
                "&#06d6a0&lCriar Cl\u00e3o",
                List.of(
                        "",
                        " &#06d6a0Crie seu pr\u00f3prio cl\u00e3o!",
                        "",
                        " &#AAAAAAUse: &#FFFF55/clan criar <nome> <tag>",
                        " &#AAAAAACusto: &#ffd166500 &#AAAAAA\u00a7",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-create"));
                }
        ));

        this.registerButton(24, new MenuButton(
                Material.CHEST,
                "&#4ecdc4&lConvites Pendentes",
                List.of(
                        "",
                        " &#4ecdc4Veja os convites que voc\u00ea recebeu",
                        "",
                        " &#AAAAAAUse: &#FFFF55/clan aceitar",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage(ItemBuilder.color("&#AAAAAAUse &#FFFF55/clan aceitar &#AAAAAApara aceitar um convite."));
                }
        ));

        this.registerButton(31, new MenuButton(
                Material.NETHER_STAR,
                "&#ffd166&lLeaderboard",
                List.of(
                        "",
                        " &#AAAAAAVeja o ranking de todos os cl\u00e3s",
                        " &#AAAAAAKills, n\u00edveis, banco e mais",
                        ""
                ),
                p -> new LeaderboardMainMenu(this.plugin, p).openMenu()
        ));
    }

    private void buildClanMenu(Clan clan) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        boolean isLeader = clan.getLeader().equals(this.player.getUniqueId());
        double kdr = clan.getKDR();

        List<String> statusLore = new ArrayList<>();
        statusLore.add("");
        statusLore.add(" &#06d6a0Voc\u00ea est\u00e1 no cl\u00e3o &#FFFFFF" + clan.getName());
        statusLore.add(" &#AAAAAATag: " + clan.getFormattedTag());
        statusLore.add(" &#AAAAAAL\u00edder: &#FFFFFF" + ClanUtils.getPlayerName(clan.getLeader()));
        statusLore.add(" &#AAAAAAMembros: &#FFFFFF" + clan.getMemberCount() + "/" + clan.getMaxMembers());
        statusLore.add(" &#AAAAAAN\u00edvel: &#ffd166" + clan.getLevel() + " &#AAAAAA| XP: &#4ecdc4" + clan.getXp());
        statusLore.add(" &#AAAAAAKills: &#ef476f" + clan.getTotalKills() + " &#AAAAAA| Mortes: &#AA0000" + clan.getDeaths() + " &#AAAAAA| KDR: &#ffd166" + String.format("%.2f", kdr));
        statusLore.add("");

        this.registerButton(4, new MenuButton(
                Material.PAPER,
                "&#06d6a0&lStatus do Cl\u00e3o",
                statusLore,
                p -> new ClanInfoMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(11, new MenuButton(
                Material.BOOK,
                "&#ffd166&lInfo do Cl\u00e3o",
                List.of(
                        "",
                        " &#AAAAAADetalhes completos do seu cl\u00e3o",
                        " &#AAAAAANome: &#FFFFFF" + clan.getName(),
                        " &#AAAAAATag: " + clan.getFormattedTag(),
                        " &#AAAAAACriado: &#FFFFFF" + sdf.format(new Date(clan.getCreatedAt())),
                        ""
                ),
                p -> new ClanInfoMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(12, new MenuButton(
                Material.ENDER_CHEST,
                "&#4ecdc4&lBanco do Cl\u00e3o",
                List.of(
                        "",
                        " &#AAAAAASaldo atual: &#06d6a0$" + String.format("%.2f", clan.getBank()),
                        " &#AAAAAAFa\u00e7a dep\u00f3sitos e saques",
                        ""
                ),
                p -> new ClanBankMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(13, new MenuButton(
                Material.PLAYER_HEAD,
                "&#a8dadc&lMembros",
                List.of(
                        "",
                        " &#AAAAAAVeja todos os &#FFFFFF" + clan.getMemberCount() + " &#AAAAAAmembros",
                        " &#AAAAAAClique para abrir a lista completa",
                        ""
                ),
                p -> new ClanMembersMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(14, new MenuButton(
                Material.RED_BED,
                "&#ef476f&lHomes",
                List.of(
                        "",
                        " &#AAAAAACasas do cl\u00e3o: &#FFFFFF" + clan.getHomeCount() + "/" + clan.getMaxHomes(),
                        " &#AAAAAAUse: &#FFFF55/clan casa set <nome>",
                        " &#AAAAAAUse: &#FFFF55/clan casa tp <nome>",
                        ""
                ),
                p -> p.closeInventory()
        ));

        this.registerButton(15, new MenuButton(
                Material.NETHER_STAR,
                "&#ffd166&lLeaderboard",
                List.of(
                        "",
                        " &#AAAAAAVeja o ranking de todos os cl\u00e3s",
                        " &#AAAAAAKills, n\u00edveis, banco e mais",
                        ""
                ),
                p -> new LeaderboardMainMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(29, new MenuButton(
                Material.WRITABLE_BOOK,
                "&#ff6b6b&lMail do Cl\u00e3o",
                List.of(
                        "",
                        " &#AAAAAAMensagens pendentes: &#FFFFFF" + clan.getMailCount(),
                        " &#AAAAAAEnvie e leia mensagens do cl\u00e3o",
                        ""
                ),
                p -> new ClanMailsMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(31, new MenuButton(
                Material.NAME_TAG,
                "&#ffd166&lQuests",
                List.of(
                        "",
                        " &#AAAAAAQuests ativas: &#FFFFFF" + clan.getActiveQuests().size(),
                        " &#AAAAAAComplete quests para ganhar",
                        " &#AAAAAAXP e dinheiro pro cl\u00e3o",
                        ""
                ),
                p -> new ClanQuestsMenu(this.plugin, p).openMenu()
        ));

        if (isLeader) {
            this.registerButton(33, new MenuButton(
                    Material.REDSTONE,
                    "&#ef476f&lConfigura\u00e7\u00f5es",
                    List.of(
                            "",
                            " &#AAAAAAModifique tag, cor, MOTD",
                            " &#AAAAAAe prefer\u00eancias do cl\u00e3o",
                            " &#ef476fApenas o l\u00edder pode alterar",
                            ""
                    ),
                    p -> new ClanSettingsMenu(this.plugin, p).openMenu()
            ));
        }

        this.registerButton(39, new MenuButton(
                Material.GOLD_INGOT,
                "&#ffd166&lConquistas",
                List.of(
                        "",
                        " &#AAAAAAConquistas desbloqueadas: &#FFFFFF" + clan.getAchievements().size(),
                        " &#AAAAAAComplete objetivos para desbloquear",
                        ""
                ),
                p -> new ClanAchievementsMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(40, new MenuButton(
                Material.BARRIER,
                "&#ef476f&lSair do Cl\u00e3o",
                List.of(
                        "",
                        " &#AAAAAAVoc\u00ea sair\u00e1 do cl\u00e3o &#FFFFFF" + clan.getName(),
                        " &#ef476fVoc\u00ea perder\u00e1 todas as permiss\u00f5es",
                        ""
                ),
                p -> {
                    this.plugin.getClanManager().leaveClan(p.getUniqueId());
                    p.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.left")
                            .replace("{clan}", clan.getName()));
                    p.closeInventory();
                }
        ));

        this.registerButton(41, new MenuButton(
                Material.EMERALD,
                "&#06d6a0&lDiplomacia",
                List.of(
                        "",
                        " &#AAAAAAAliados: &#06d6a0" + getDiplomacyCount(clan, Clan.DiplomacyType.ALLY),
                        " &#AAAAAARivais: &#ef476f" + getDiplomacyCount(clan, Clan.DiplomacyType.RIVAL),
                        ""
                ),
                p -> new ClanDiplomacyMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(42, new MenuButton(
                Material.PLAYER_HEAD,
                "&#4ecdc4&lPerfil",
                List.of(
                        "",
                        " &#AAAAAAVeja o perfil completo",
                        " &#AAAAAAdo seu cl\u00e3o",
                        ""
                ),
                p -> new ClanProfileMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(43, new MenuButton(
                Material.BOOK,
                "&#AAAAAA&lLogs",
                List.of(
                        "",
                        " &#AAAAAAVeja o hist\u00f3rico de",
                        " &#AAAAAAatividades do cl\u00e3o",
                        ""
                ),
                p -> new ClanLogsMenu(this.plugin, p).openMenu()
        ));

        boolean chatEnabled = ClanChatCommand.isChatEnabled(this.player.getUniqueId());

        this.registerButton(51, new MenuButton(
                Material.ZOMBIE_HEAD,
                chatEnabled ? "&#55FF55&lChat &#55FF55[ON]" : "&#FF5555&lChat &#FF5555[OFF]",
                List.of(
                        "",
                        chatEnabled ? " &#55FF55Chat do cl\u00e3o est\u00e1 ATIVADO" : " &#FF5555Chat do cl\u00e3o est\u00e1 DESATIVADO",
                        " &#AAAAAAClique para " + (chatEnabled ? "desativar" : "ativar"),
                        "",
                        " &#AAAAAAMotD: &#FFFFFF" + (clan.getMotd().isEmpty() ? "Nenhuma" : clan.getMotd()),
                        ""
                ),
                p -> {
                    ClanChatCommand.toggleChat(p.getUniqueId());
                    if (ClanChatCommand.isChatEnabled(p.getUniqueId())) {
                        p.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("chat.enabled")));
                    } else {
                        p.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("chat.disabled")));
                    }
                    new ClanMenu(this.plugin, p).openMenu();
                }
        ));
    }

    private int getDiplomacyCount(Clan clan, Clan.DiplomacyType type) {
        return (int) clan.getDiplomacy().stream()
                .filter(d -> d.type() == type)
                .count();
    }
}
