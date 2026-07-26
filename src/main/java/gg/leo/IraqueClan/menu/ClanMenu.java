package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.menu.leaderboard.LeaderboardMainMenu;
import gg.leo.IraqueClan.utils.ClanUtils;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanMenu extends BaseMenu {
    private final IraqueClan plugin;

    public ClanMenu(IraqueClan plugin, Player player) {
        super(player, "&8&lHub do Cl\u00e3o", 54, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&8");

        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(this.player.getUniqueId());
        boolean hasClan = clan != null;

        if (!hasClan) {
            buildNoClanMenu();
        } else {
            buildClanMenu(clan);
        }

        this.registerButton(52, new MenuButton(
                Material.COMMAND_BLOCK,
                "&#ffd166&lComandos",
                List.of(
                        "",
                        " &e/clan criar &7<nome> <tag>",
                        " &e/clan convidar &7<jogador>",
                        " &e/clan aceitar",
                        " &e/clan sair",
                        " &e/clan expulsar &7<jogador>",
                        " &e/clan promover &7<jogador>",
                        " &e/clan rebaixar &7<jogador>",
                        " &e/clan banco &7<depositar|sacar>",
                        " &e/clan casa &7<set|tp|list>",
                        " &e/clan mail &7<enviar|ler>",
                        " &e/clan loja",
                        " &e/clan guerras",
                        ""
                ),
                p -> p.closeInventory()
        ));

        this.registerButton(53, new MenuButton(
                Material.BARRIER,
                "&c&lFechar",
                List.of("", " &7Fechar este menu", ""),
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
                        " &7Crie ou entre em um para come\u00e7ar!",
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
                        " &7Use: &e/clan criar <nome> <tag>",
                        " &7Custo: &#ffd166500 &7\u00a7",
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
                        " &7Use: &e/clan aceitar",
                        ""
                ),
                p -> {
                    p.closeInventory();
                    p.sendMessage("&7Use &e/clan aceitar &7para aceitar um convite.");
                }
        ));

        this.registerButton(31, new MenuButton(
                Material.NETHER_STAR,
                "&#ffd166&lLeaderboard",
                List.of(
                        "",
                        " &7Veja o ranking de todos os cl\u00e3s",
                        " &7Kills, n\u00edveis, banco e mais",
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
        statusLore.add(" &#06d6a0Voc\u00ea est\u00e1 no cl\u00e3o &f" + clan.getName());
        statusLore.add(" &7Tag: " + clan.getFormattedTag());
        statusLore.add(" &7L\u00edder: &f" + ClanUtils.getPlayerName(clan.getLeader()));
        statusLore.add(" &7Membros: &f" + clan.getMemberCount() + "/" + clan.getMaxMembers());
        statusLore.add(" &7N\u00edvel: &#ffd166" + clan.getLevel() + " &7| XP: &#4ecdc4" + clan.getXp());
        statusLore.add(" &7Kills: &#ef476f" + clan.getTotalKills() + " &7| Mortes: &4" + clan.getDeaths() + " &7| KDR: &#ffd166" + String.format("%.2f", kdr));
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
                        " &7Detalhes completos do seu cl\u00e3o",
                        " &7Nome: &f" + clan.getName(),
                        " &7Tag: " + clan.getFormattedTag(),
                        " &7Criado: &f" + sdf.format(new Date(clan.getCreatedAt())),
                        ""
                ),
                p -> new ClanInfoMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(12, new MenuButton(
                Material.ENDER_CHEST,
                "&#4ecdc4&lBanco do Cl\u00e3o",
                List.of(
                        "",
                        " &7Saldo atual: &#06d6a0$" + String.format("%.2f", clan.getBank()),
                        " &7Fa\u00e7a dep\u00f3sitos e saques",
                        ""
                ),
                p -> new ClanBankMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(13, new MenuButton(
                Material.PLAYER_HEAD,
                "&#a8dadc&lMembros",
                List.of(
                        "",
                        " &7Veja todos os &f" + clan.getMemberCount() + " &7membros",
                        " &7Clique para abrir a lista completa",
                        ""
                ),
                p -> new ClanMembersMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(14, new MenuButton(
                Material.RED_BED,
                "&#ef476f&lHomes",
                List.of(
                        "",
                        " &7Casas do cl\u00e3o: &f" + clan.getHomeCount() + "/" + clan.getMaxHomes(),
                        " &7Use: &e/clan casa set <nome>",
                        " &7Use: &e/clan casa tp <nome>",
                        ""
                ),
                p -> p.closeInventory()
        ));

        this.registerButton(15, new MenuButton(
                Material.NETHER_STAR,
                "&#ffd166&lLeaderboard",
                List.of(
                        "",
                        " &7Veja o ranking de todos os cl\u00e3s",
                        " &7Kills, n\u00edveis, banco e mais",
                        ""
                ),
                p -> new LeaderboardMainMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(29, new MenuButton(
                Material.WRITABLE_BOOK,
                "&#ff6b6b&lMail do Cl\u00e3o",
                List.of(
                        "",
                        " &7Mensagens pendentes: &f" + clan.getMailCount(),
                        " &7Envie e leia mensagens do cl\u00e3o",
                        ""
                ),
                p -> new ClanMailsMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(31, new MenuButton(
                Material.NAME_TAG,
                "&#ffd166&lQuests",
                List.of(
                        "",
                        " &7Quests ativas: &f" + clan.getActiveQuests().size(),
                        " &7Complete quests para ganhar",
                        " &7XP e dinheiro pro cl\u00e3o",
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
                            " &7Modifique tag, cor, MOTD",
                            " &7e prefer\u00eancias do cl\u00e3o",
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
                        " &7Conquistas desbloqueadas: &f" + clan.getAchievements().size(),
                        " &7Complete objetivos para desbloquear",
                        ""
                ),
                p -> new ClanAchievementsMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(40, new MenuButton(
                Material.BARRIER,
                "&#ef476f&lSair do Cl\u00e3o",
                List.of(
                        "",
                        " &7Voc\u00ea sair\u00e1 do cl\u00e3o &f" + clan.getName(),
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
                        " &7Aliados: &#06d6a0" + getDiplomacyCount(clan, Clan.DiplomacyType.ALLY),
                        " &7Rivais: &#ef476f" + getDiplomacyCount(clan, Clan.DiplomacyType.RIVAL),
                        ""
                ),
                p -> new ClanDiplomacyMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(42, new MenuButton(
                Material.PLAYER_HEAD,
                "&#4ecdc4&lPerfil",
                List.of(
                        "",
                        " &7Veja o perfil completo",
                        " &7do seu cl\u00e3o",
                        ""
                ),
                p -> new ClanProfileMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(43, new MenuButton(
                Material.BOOK,
                "&7&lLogs",
                List.of(
                        "",
                        " &7Veja o hist\u00f3rico de",
                        " &7atividades do cl\u00e3o",
                        ""
                ),
                p -> new ClanLogsMenu(this.plugin, p).openMenu()
        ));

        this.registerButton(51, new MenuButton(
                Material.ZOMBIE_HEAD,
                "&#a8dadc&lChat",
                List.of(
                        "",
                        " &7Toggle do chat do cl\u00e3o",
                        " &7MotD: &f" + (clan.getMotd().isEmpty() ? "Nenhuma" : clan.getMotd()),
                        ""
                ),
                p -> p.closeInventory()
        ));
    }

    private int getDiplomacyCount(Clan clan, Clan.DiplomacyType type) {
        return (int) clan.getDiplomacy().stream()
                .filter(d -> d.type() == type)
                .count();
    }
}
