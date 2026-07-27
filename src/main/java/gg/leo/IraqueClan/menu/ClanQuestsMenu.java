package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.ItemBuilder;
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
        super(player, " Quests do Cl ", 45, MenuType.SIMPLE);
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
            this.addBackButton(40, p -> new ClanMenu(this.plugin, p).openMenu());
            return;
        }

        this.registerButton(4, new MenuButton(
                Material.NAME_TAG,
                "&#FFFF55&lQuests Ativas",
                List.of(
                        "",
                        " &#AAAAAAQuests ativas: &#FFFFFF" + clan.getActiveQuests().size(),
                        " &#AAAAAAComplete para ganhar recompensas",
                        ""
                ),
                p -> {}
        ));

        Map<String, Clan.ClanQuest> quests = clan.getActiveQuests();

        if (quests.isEmpty()) {
            this.registerButton(22, new MenuButton(
                    Material.PAPER,
                    "&#AAAAAANenhuma Quest",
                    List.of(
                            "",
                            " &#AAAAAANenhuma quest ativa no momento.",
                            " &#AAAAAAComplete atividades para receber",
                            " &#AAAAAAquests automaticamente.",
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
                lore.add(" &#AAAAAATipo: &#FFFFFF" + quest.type());
                lore.add(" &#AAAAAAProgresso: " + progressBar);
                lore.add(" &#FFFFFF" + quest.current() + "/" + quest.required());
                lore.add("");
                lore.add(" &#AAAAAARecompensa:");
                lore.add("   &#FFFF55XP: &#55FFFF" + quest.rewardXP());
                lore.add("   &#AAAAAADinheiro: &#55FF55$" + String.format("%.2f", quest.rewardMoney()));
                lore.add("");

                if (complete) {
                    lore.add(" &#55FF55\u2714 Completa! Clique para resgatar!");
                } else {
                    lore.add(" &#AAAAAA" + String.format("%.0f", (quest.current() * 100.0 / quest.required())) + "% conclu\u00eddo");
                }
                lore.add("");

                this.registerButton(slot, new MenuButton(
                        complete ? Material.LIME_STAINED_GLASS_PANE : Material.ORANGE_STAINED_GLASS_PANE,
                        (complete ? "&#55FF55&l" : "&#FFFF55&l") + quest.id(),
                        lore,
                        complete
                                ? p -> {
                                    this.plugin.getClanManager().completeQuest(p.getUniqueId(), quest.id());
                                    p.sendMessage(ItemBuilder.color("&#55FF55Quest completada! Recompensas recebidas!"));
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
        StringBuilder bar = new StringBuilder("&#AAAAAA[");
        for (int i = 0; i < bars; i++) {
            bar.append(i < filled ? "&#55FF55\u2588" : "&#555555\u2591");
        }
        bar.append("&#AAAAAA]");
        return bar.toString();
    }
}
