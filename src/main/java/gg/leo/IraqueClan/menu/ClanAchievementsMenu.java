package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanAchievementsMenu extends BaseMenu {
    private final IraqueClan plugin;

    private static final String[][] ACHIEVEMENTS = {
            {"primeiro-kill", "\u2694 Primeiro Kill", "Consiga seu primeiro kill como cl\u00e3o", Material.IRON_SWORD.name()},
            {"100-kills", "\u2694 100 Kills", "Alcance 100 kills totais", Material.DIAMOND_SWORD.name()},
            {"1000-kills", "\u2694 1000 Kills", "Alcance 1000 kills totais", Material.NETHERITE_SWORD.name()},
            {"membros-5", "\u2605 5 Membros", "Tenha 5 membros no cl\u00e3o", Material.PLAYER_HEAD.name()},
            {"membros-10", "\u2605 10 Membros", "Tenha 10 membros no cl\u00e3o", Material.PLAYER_HEAD.name()},
            {"nivel-10", "\u2b50 N\u00edvel 10", "Alcance o n\u00edvel 10", Material.EXPERIENCE_BOTTLE.name()},
            {"nivel-50", "\u2b50 N\u00edvel 50", "Alcance o n\u00edvel 50", Material.EXPERIENCE_BOTTLE.name()},
            {"banco-10k", "\u2726 Banco 10K", "Tenha $10.000 no banco", Material.GOLD_INGOT.name()},
            {"banco-100k", "\u2726 Banco 100K", "Tenha $100.000 no banco", Material.NETHERITE_INGOT.name()},
            {"guerreiro-1", "\u2620 Guerreiro", "Ven\u00e7a sua primeira guerra", Material.SHIELD.name()},
            {"guerreiro-10", "\u2620 Veterano", "Ven\u00e7a 10 guerras", Material.NETHERITE_CHESTPLATE.name()},
            {"aliado-1", "\u2764 Primeira Alian\u00e7a", "Tenha seu primeiro aliado", Material.EMERALD.name()},
            {"velho-clan", "\u231b Veterano", "Exist\u00e1 por 30 dias", Material.CLOCK.name()},
            {"kdr-2", "\u2742 KDR Duplo", "Tenha KDR de 2.0 ou mais", Material.ARROW.name()},
            {"kdr-5", "\u2742 KDR Mestre", "Tenha KDR de 5.0 ou mais", Material.TRIDENT.name()}
    };

    public ClanAchievementsMenu(IraqueClan plugin, Player player) {
        super(player, "&8&l\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550 Conquistas \u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557", 45, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&8\u2591");

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

        this.registerButton(4, new MenuButton(
                Material.GOLD_INGOT,
                "&6&l\u2554\u2550\u2550 Conquistas \u2550\u2550\u2557",
                List.of(
                        "",
                        " &7Desbloqueadas: &a" + clan.getAchievements().size() + "/" + ACHIEVEMENTS.length,
                        ""
                ),
                p -> {}
        ));

        int slot = 10;
        for (String[] ach : ACHIEVEMENTS) {
            if (slot >= 44) break;
            if (slot % 9 == 8) slot += 2;
            if (slot >= 44) break;

            String id = ach[0];
            String name = ach[1];
            String description = ach[2];
            Material lockedMat = Material.RED_STAINED_GLASS_PANE;
            Material unlockedMat = Material.GOLD_INGOT;

            boolean unlocked = clan.hasAchievement(id);

            this.registerButton(slot, new MenuButton(
                    unlocked ? unlockedMat : lockedMat,
                    unlocked ? "&a\u2714 &f" + name : "&c\u2716 &8" + name,
                    List.of(
                            "",
                            " &7" + description,
                            "",
                            unlocked ? " &a\u2714 Desbloqueado!" : " &c\u2716 Bloqueado",
                            ""
                    ),
                    p -> {}
            ));

            slot++;
        }

        this.addBackButton(44, p -> new ClanMenu(this.plugin, p).openMenu());
    }
}
