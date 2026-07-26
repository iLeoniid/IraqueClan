package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanQuestsMenu extends BaseMenu {
    private final IraqueClan plugin;

    public ClanQuestsMenu(IraqueClan plugin, Player player) {
        super(player, "&8&l\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550 Quests do Cl\u00e3o \u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557", 45, MenuType.SIMPLE);
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
                Material.NAME_TAG,
                "&e&l\u2554\u2550\u2550 Quests Ativas \u2550\u2550\u2557",
                List.of(
                        "",
                        " &7Quests ativas: &f" + clan.getActiveQuests().size(),
                        " &7Complete para ganhar recompensas",
                        ""
                ),
                p -> {}
        ));

        Map<String, Clan.ClanQuest> quests = clan.getActiveQuests();

        if (quests.isEmpty()) {
            this.registerButton(22, new MenuButton(
                    Material.PAPER,
                    "&7\u2554\u2550\u2550 Nenhuma Quest \u2550\u2550\u2557",
                    List.of(
                            "",
                            " &7Nenhuma quest ativa no momento.",
                            " &7Complete atividades para receber",
                            " &7quests automaticamente.",
                            ""
                    ),
                    p -> {}
            ));
        } else {
            int slot = 10;
            for (Map.Entry<String, Clan.ClanQuest> entry : quests.entrySet()) {
                if (slot >= 44) break;
                if (slot % 9 == 8) slot += 2;
                if (slot >= 44) break;

                Clan.ClanQuest quest = entry.getValue();
                boolean complete = quest.isComplete();
                String progressBar = getProgressBar(quest.current(), quest.required());

                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(" &7Tipo: &f" + quest.type());
                lore.add(" &7Progresso: " + progressBar);
                lore.add(" &f" + quest.current() + "/" + quest.required());
                lore.add("");
                lore.add(" &7Recompensa:");
                lore.add("   &eXP: &b" + quest.rewardXP());
                lore.add("   &7Dinheiro: &a$" + String.format("%.2f", quest.rewardMoney()));
                lore.add("");

                if (complete) {
                    lore.add(" &a\u2714 Completa! Clique para resgatar!");
                } else {
                    lore.add(" &7" + String.format("%.0f", (quest.current() * 100.0 / quest.required())) + "% conclu\u00eddo");
                }
                lore.add("");

                this.registerButton(slot, new MenuButton(
                        complete ? Material.LIME_STAINED_GLASS_PANE : Material.ORANGE_STAINED_GLASS_PANE,
                        (complete ? "&a&l" : "&e&l") + quest.id(),
                        lore,
                        complete
                                ? p -> {
                                    this.plugin.getClanManager().completeQuest(p.getUniqueId(), quest.id());
                                    p.sendMessage("&aQuest completada! Recompensas recebidas!");
                                    this.updateMenu();
                                }
                                : p -> {}
                ));

                slot++;
            }
        }

        this.addBackButton(40, p -> new ClanMenu(this.plugin, p).openMenu());
    }

    private String getProgressBar(int current, int max) {
        int bars = 20;
        int filled = max > 0 ? (int) Math.round((double) current / max * bars) : 0;
        StringBuilder bar = new StringBuilder("&7[");
        for (int i = 0; i < bars; i++) {
            bar.append(i < filled ? "&a\u2588" : "&8\u2591");
        }
        bar.append("&7]");
        return bar.toString();
    }
}
