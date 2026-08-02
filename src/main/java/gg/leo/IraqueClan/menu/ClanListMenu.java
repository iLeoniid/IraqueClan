package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.clan.role.ClanRole;
import gg.leo.IraqueClan.utils.ClanUtils;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanListMenu extends BaseMenu {
    private static final int CLAN_PAGE_SIZE = 7;
    private static final int MEMBER_PAGE_SIZE = 21;
    private static final int[] MEMBER_SLOTS = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
    private static final Material[] ICONS = {
            Material.OAK_LOG, Material.DIRT, Material.GOLD_BLOCK, Material.EMERALD_BLOCK,
            Material.IRON_BLOCK, Material.DIAMOND_BLOCK, Material.NETHERITE_BLOCK,
            Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK, Material.AMETHYST_BLOCK
    };

    private final IraqueClan plugin;
    private String selectedClan;
    private int clanPage = 1;
    private int memberPage = 1;

    public ClanListMenu(IraqueClan plugin, Player player) {
        super(player, "&#555555&lClan List &#AAAAAA| &#4ecdc4ALL", 54, MenuType.PAGINATED);
        this.plugin = plugin;
        Clan own = plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        this.selectedClan = own != null ? own.getName() : null;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&#555555");

        List<Clan> clans = new ArrayList<>(this.plugin.getClanManager().getClansSortedByMembers());
        int totalPages = Math.max(1, (int) Math.ceil(clans.size() / (double) CLAN_PAGE_SIZE));
        this.clanPage = Math.min(this.clanPage, totalPages);

        String ownClanName = this.plugin.getClanManager().getClanByPlayerDirect(this.player.getUniqueId()) != null
                ? this.plugin.getClanManager().getClanByPlayerDirect(this.player.getUniqueId()).getName() : null;

        this.registerButton(4, new MenuButton(
                Material.PAPER,
                "&#ffd166&lClan List &#AAAAAA| &#4ecdc4ALL",
                List.of(
                        "",
                        " &#AAAAAATotal de cl\u00e3s: &#FFFFFF" + clans.size(),
                        " &#AAAAAASelecione um cl\u00e3 acima para",
                        " &#AAAAAAver os membros dele",
                        ""
                ),
                p -> {}
        ));

        if (this.clanPage > 1) {
            this.registerButton(7, new MenuButton(
                    Material.ARROW,
                    "&#4ecdc4&l\u25c0 P\u00e1gina Anterior",
                    List.of("", " &#AAAAAAP\u00e1gina " + (this.clanPage - 1) + " de " + totalPages, ""),
                    p -> {
                        this.clanPage--;
                        this.updateMenu();
                    }
            ));
        }
        if (this.clanPage < totalPages) {
            this.registerButton(8, new MenuButton(
                    Material.ARROW,
                    "&#4ecdc4&lPr\u00f3xima P\u00e1gina \u25b6",
                    List.of("", " &#AAAAAAP\u00e1gina " + (this.clanPage + 1) + " de " + totalPages, ""),
                    p -> {
                        this.clanPage++;
                        this.updateMenu();
                    }
            ));
        }

        this.registerButton(16, new MenuButton(
                Material.CYAN_DYE,
                "&#4ecdc4&lCategoria: &#FFFFFFALL",
                List.of("", " &#AAAAAATodos os cl\u00e3s, sem filtro", ""),
                p -> {}
        ));

        int start = (this.clanPage - 1) * CLAN_PAGE_SIZE;
        for (int i = 0; i < CLAN_PAGE_SIZE; i++) {
            int index = start + i;
            if (index >= clans.size()) break;
            Clan clan = clans.get(index);
            boolean mine = clan.getName().equalsIgnoreCase(ownClanName != null ? ownClanName : "");
            this.registerButton(9 + i, this.buildClanIcon(clan, index, mine));
        }

        Clan selected = null;
        if (this.selectedClan != null) {
            selected = this.plugin.getClanManager().getClan(this.selectedClan).orElse(null);
        }
        if (selected == null && !clans.isEmpty()) {
            selected = clans.get(0);
            this.selectedClan = selected.getName();
        }

        if (selected != null) {
            this.buildMembers(selected);
        } else {
            this.registerButton(31, new MenuButton(
                    Material.BARRIER,
                    "&#FF5555&lNenhum cl\u00e3o encontrado",
                    List.of("", " &#AAAAAACrie um cl\u00e3o usando", " &#FFFF55/clan criar <nome> <tag>", ""),
                    p -> {}
            ));
        }

        this.buildBottomBar(selected);
    }

    private MenuButton buildClanIcon(Clan clan, int index, boolean mine) {
        String color = mine ? "&#55ff55&l" : "&#ffd166&l";
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(" &#AAAAAAL\u00edder: &#FFFFFF" + ClanUtils.getPlayerName(clan.getLeader()));
        lore.add(" &#AAAAAAMembros: &#FFFFFF" + clan.getMemberCount() + "/" + clan.getMaxMembers());
        lore.add(" &#AAAAAAN\u00edvel: &#ffd166" + clan.getLevel() + " &#AAAAAA| Kills: &#ef476f" + clan.getTotalKills());
        lore.add(" &#AAAAAABanco: &#06d6a0$" + String.format("%.2f", clan.getBank()));
        if (mine) {
            lore.add("");
            lore.add(" &#55ff55&lSEU CL\u00c3");
        }
        lore.add("");
        lore.add(" &#AAAAAAClique para ver os membros");
        lore.add("");

        return new MenuButton(
                this.getClanIcon(clan, index),
                color + clan.getFormattedTag(),
                lore,
                p -> {
                    this.selectedClan = clan.getName();
                    this.memberPage = 1;
                    this.updateMenu();
                },
                false,
                mine,
                0
        );
    }

    private Material getClanIcon(Clan clan, int index) {
        String icon = clan.getIcon();
        if (icon != null && !icon.equalsIgnoreCase("PAPER")) {
            Material m = Material.matchMaterial(icon);
            if (m != null && m.isItem()) {
                return m;
            }
        }
        return ICONS[Math.floorMod(index, ICONS.length)];
    }

    private void buildMembers(Clan clan) {
        List<Map.Entry<UUID, ClanRole>> memberList = new ArrayList<>(clan.getMembers().entrySet());
        int totalMemberPages = Math.max(1, (int) Math.ceil(memberList.size() / (double) MEMBER_PAGE_SIZE));
        this.memberPage = Math.min(this.memberPage, totalMemberPages);

        this.registerButton(13, new MenuButton(
                Material.PLAYER_HEAD,
                "&#ffd166&lMembros de &#FFFFFF" + clan.getName(),
                List.of(
                        "",
                        " &#AAAAAATag: " + clan.getFormattedTag(),
                        " &#AAAAAAMembros: &#FFFFFF" + clan.getMemberCount() + "/" + clan.getMaxMembers(),
                        " &#AAAAAAL\u00edder: &#ffd166" + ClanUtils.getPlayerName(clan.getLeader()),
                        ""
                ),
                p -> {}
        ));

        if (this.memberPage > 1) {
            this.registerButton(18, new MenuButton(
                    Material.ARROW,
                    "&#4ecdc4&l\u25c0 Membros",
                    List.of("", " &#AAAAAAP\u00e1gina " + (this.memberPage - 1) + " de " + totalMemberPages, ""),
                    p -> {
                        this.memberPage--;
                        this.updateMenu();
                    }
            ));
        }
        if (this.memberPage < totalMemberPages) {
            this.registerButton(44, new MenuButton(
                    Material.ARROW,
                    "&#4ecdc4&lMembros \u25b6",
                    List.of("", " &#AAAAAAP\u00e1gina " + (this.memberPage + 1) + " de " + totalMemberPages, ""),
                    p -> {
                        this.memberPage++;
                        this.updateMenu();
                    }
            ));
        }

        int start = (this.memberPage - 1) * MEMBER_PAGE_SIZE;
        for (int i = 0; i < MEMBER_PAGE_SIZE; i++) {
            int index = start + i;
            if (index >= memberList.size()) break;
            Map.Entry<UUID, ClanRole> entry = memberList.get(index);
            UUID uuid = entry.getKey();
            ClanRole role = entry.getValue();
            String playerName = ClanUtils.getPlayerName(uuid);
            boolean isSelf = uuid.equals(this.player.getUniqueId());

            String roleName = clan.getRoleName(role);
            String roleColor;
            switch (role) {
                case LIDER:
                    roleColor = "&#ffd166&l";
                    break;
                case SUB_LIDER:
                    roleColor = "&#4ecdc4&l";
                    break;
                default:
                    roleColor = "&#AAAAAA";
                    break;
            }

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(" &#AAAAAACargo: " + roleColor + roleName);
            if (isSelf) {
                lore.add(" &#06d6a0Este \u00e9 voc\u00ea!");
            }
            lore.add("");

            this.registerButton(MEMBER_SLOTS[i], new MenuButton(
                    Material.PLAYER_HEAD,
                    roleColor + roleName + " &#FFFFFF" + playerName,
                    lore,
                    p -> {}
            ));
        }
    }

    private void buildBottomBar(Clan selected) {
        String selectedName = selected != null ? selected.getName() : "";

        this.registerButton(45, new MenuButton(
                Material.SPYGLASS,
                "&#4ecdc4&lBuscar Cl\u00e3",
                List.of(
                        "",
                        " &#AAAAAAProcure o perfil de qualquer cl\u00e3",
                        selected != null ? " &#AAAAAAAtual: &#FFFFFF" + selected.getName() : "",
                        ""
                ),
                p -> {
                    if (selectedName.isEmpty()) {
                        p.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-profile"));
                        return;
                    }
                    p.closeInventory();
                    p.performCommand("clan perfil " + selectedName);
                }
        ));

        this.registerButton(49, new MenuButton(
                Material.BOOK,
                "&#ffd166&lInforma\u00e7\u00f5es do Cl\u00e3",
                List.of(
                        "",
                        " &#AAAAAADetalhes completos do cl\u00e3 selecionado",
                        selected != null ? " &#AAAAAANome: &#FFFFFF" + selected.getName() : "",
                        selected != null ? " &#AAAAAATag: " + selected.getFormattedTag() : "",
                        ""
                ),
                p -> {
                    if (selectedName.isEmpty()) {
                        p.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-profile"));
                        return;
                    }
                    p.closeInventory();
                    p.performCommand("clan perfil " + selectedName);
                }
        ));

        this.registerButton(50, new MenuButton(
                Material.BLACK_STAINED_GLASS_PANE,
                "&#AAAAAA&lInventory",
                List.of("", " &#AAAAAASeu invent\u00e1rio continua acess\u00edvel", ""),
                p -> {}
        ));

        this.registerButton(53, new MenuButton(
                Material.TNT,
                "&#ef476f&lSair do Cl\u00e3",
                List.of(
                        "",
                        " &#AAAAAASair do seu cl\u00e3 atual",
                        " &#ef476fN\u00e3o \u00e9 poss\u00edvel para o l\u00edder",
                        ""
                ),
                p -> {
                    Clan own = this.plugin.getClanManager().getClanByPlayerDirect(p.getUniqueId());
                    if (own == null) {
                        p.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-clan"));
                        return;
                    }
                    if (own.getLeader().equals(p.getUniqueId())) {
                        p.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.leader-cannot-leave"));
                        return;
                    }
                    this.plugin.getClanManager().leaveClan(p.getUniqueId());
                    p.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.left")
                            .replace("{clan}", own.getName()));
                    p.closeInventory();
                }
        ));
    }
}
