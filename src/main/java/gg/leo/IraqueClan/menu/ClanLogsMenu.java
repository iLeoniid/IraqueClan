package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.ClanUtils;
import gg.leo.IraqueClan.utils.ItemBuilder;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanLogsMenu extends BaseMenu {
    private final IraqueClan plugin;

    public ClanLogsMenu(IraqueClan plugin, Player player) {
        super(player, " Logs do Cl ", 54, MenuType.SIMPLE);
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
                Material.BOOK,
                "&#AAAAAA&lHist\u00f3rico",
                List.of(
                        "",
                        " &#AAAAAATotal de logs: &#FFFFFF" + clan.getLogs().size(),
                        ""
                ),
                p -> {}
        ));

        List<Clan.ClanLog> logs = clan.getLogs();

        if (logs.isEmpty()) {
            this.registerButton(22, new MenuButton(
                    Material.PAPER,
                    "&#AAAAAANenhum Log",
                    List.of(
                            "",
                            " &#AAAAAANenhuma atividade registrada.",
                            ""
                    ),
                    p -> {}
            ));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
            int startIdx = Math.max(0, logs.size() - 36);
            int slot = 10;

            for (int i = logs.size() - 1; i >= startIdx && slot < 45; i--) {
                if (slot % 9 == 8) slot += 2;
                if (slot >= 45) break;

                Clan.ClanLog log = logs.get(i);
                String player = log.player() != null ? ClanUtils.getPlayerName(log.player()) : "Sistema";
                String time = sdf.format(new Date(log.timestamp()));

                Material mat;
                String actionColor;
                switch (log.action().toLowerCase()) {
                    case "kill":
                        mat = Material.IRON_SWORD;
                        actionColor = "&#FF5555";
                        break;
                    case "join":
                    case "member-join":
                        mat = Material.LIME_DYE;
                        actionColor = "&#55FF55";
                        break;
                    case "leave":
                    case "member-leave":
                        mat = Material.RED_DYE;
                        actionColor = "&#FF5555";
                        break;
                    case "promote":
                        mat = Material.LIME_DYE;
                        actionColor = "&#55FFFF";
                        break;
                    case "demote":
                        mat = Material.RED_DYE;
                        actionColor = "&#FFFF55";
                        break;
                    case "bank-deposit":
                        mat = Material.EMERALD;
                        actionColor = "&#55FF55";
                        break;
                    case "bank-withdraw":
                        mat = Material.REDSTONE;
                        actionColor = "&#FF5555";
                        break;
                    case "upgrade":
                        mat = Material.DIAMOND;
                        actionColor = "&#55FFFF";
                        break;
                    case "war-win":
                        mat = Material.GOLD_INGOT;
                        actionColor = "&#FFAA00";
                        break;
                    case "war-loss":
                        mat = Material.BARRIER;
                        actionColor = "&#FF5555";
                        break;
                    default:
                        mat = Material.PAPER;
                        actionColor = "&#AAAAAA";
                        break;
                }

                this.registerButton(slot, new MenuButton(
                        mat,
                        actionColor + log.action(),
                        List.of(
                                "",
                                " &#AAAAAAJogador: &#FFFFFF" + player,
                                " &#AAAAAADetalhes: &#FFFFFF" + log.details(),
                                " &#AAAAAAHora: &#AAAAAA" + time,
                                ""
                        ),
                        p -> {}
                ));

                slot++;
            }
        }

        this.registerButton(49, new MenuButton(
                Material.LAVA_BUCKET,
                "&#FF5555&lLimpar Logs",
                List.of(
                        "",
                        " &#AAAAAALimpa todo o hist\u00f3rico",
                        " &#FF5555\u26a0 Irrevers\u00edvel!",
                        ""
                ),
                p -> {
                    this.plugin.getClanManager().clearLogs(p.getUniqueId());
                    p.sendMessage(ItemBuilder.color("&#55FF55Logs limpos!"));
                    this.updateMenu();
                }
        ));

        this.addBackButton(50, p -> new ClanMenu(this.plugin, p).openMenu());
    }
}
