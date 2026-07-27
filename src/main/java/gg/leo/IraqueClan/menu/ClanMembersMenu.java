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

public class ClanMembersMenu extends BaseMenu {
    private final IraqueClan plugin;

    public ClanMembersMenu(IraqueClan plugin, Player player) {
        super(player, "&#555555&lMembros do Cl\u00e3o", 54, MenuType.SIMPLE);
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
            this.addBackButton(49, p -> new ClanMenu(this.plugin, p).openMenu());
            return;
        }

        this.registerButton(4, new MenuButton(
                Material.PAPER,
                "&#ffd166&lMembros de &#FFFFFF" + clan.getName(),
                List.of(
                        "",
                        " &#AAAAAATotal: &#FFFFFF" + clan.getMemberCount() + "/" + clan.getMaxMembers(),
                        " &#AAAAAAL\u00edder: &#ffd166" + ClanUtils.getPlayerName(clan.getLeader()),
                        ""
                ),
                p -> {}
        ));

        List<Map.Entry<UUID, ClanRole>> memberList = new ArrayList<>(clan.getMembers().entrySet());
        int slot = 10;
        for (int i = 0; i < memberList.size() && slot < 45; i++) {
            Map.Entry<UUID, ClanRole> entry = memberList.get(i);
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
            if (uuid.equals(clan.getLeader())) {
                lore.add(" &#ffd166L\u00edder do cl\u00e3o");
            }
            lore.add("");

            this.registerButton(slot, new MenuButton(
                    Material.PLAYER_HEAD,
                    roleColor + roleName + " &#FFFFFF" + playerName,
                    lore,
                    p -> {}
            ));

            slot++;
            if (slot % 9 == 8) {
                slot += 2;
            }
            if (slot >= 45) break;
        }

        this.addBackButton(49, p -> new ClanMenu(this.plugin, p).openMenu());
    }
}
