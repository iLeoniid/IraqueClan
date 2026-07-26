package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.ClanUtils;
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
        super(player, "&8&l\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550 Logs do Cl\u00e3o \u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557", 54, MenuType.SIMPLE);
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
            this.addBackButton(49, p -> new ClanMenu(this.plugin, p).openMenu());
            return;
        }

        this.registerButton(4, new MenuButton(
                Material.BOOK,
                "&7&l\u2554\u2550\u2550 Hist\u00f3rico \u2550\u2550\u2557",
                List.of(
                        "",
                        " &7Total de logs: &f" + clan.getLogs().size(),
                        ""
                ),
                p -> {}
        ));

        List<Clan.ClanLog> logs = clan.getLogs();

        if (logs.isEmpty()) {
            this.registerButton(22, new MenuButton(
                    Material.PAPER,
                    "&7\u2554\u2550\u2550 Nenhum Log \u2550\u2550\u2557",
                    List.of(
                            "",
                            " &7Nenhuma atividade registrada.",
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
                        actionColor = "&c";
                        break;
                    case "join":
                    case "member-join":
                        mat = Material.LIME_DYE;
                        actionColor = "&a";
                        break;
                    case "leave":
                    case "member-leave":
                        mat = Material.RED_DYE;
                        actionColor = "&c";
                        break;
                    case "promote":
                        mat = Material.LIME_DYE;
                        actionColor = "&b";
                        break;
                    case "demote":
                        mat = Material.RED_DYE;
                        actionColor = "&e";
                        break;
                    case "bank-deposit":
                        mat = Material.EMERALD;
                        actionColor = "&a";
                        break;
                    case "bank-withdraw":
                        mat = Material.REDSTONE;
                        actionColor = "&c";
                        break;
                    case "upgrade":
                        mat = Material.DIAMOND;
                        actionColor = "&b";
                        break;
                    case "war-win":
                        mat = Material.GOLD_INGOT;
                        actionColor = "&6";
                        break;
                    case "war-loss":
                        mat = Material.BARRIER;
                        actionColor = "&c";
                        break;
                    default:
                        mat = Material.PAPER;
                        actionColor = "&7";
                        break;
                }

                this.registerButton(slot, new MenuButton(
                        mat,
                        actionColor + log.action(),
                        List.of(
                                "",
                                " &7Jogador: &f" + player,
                                " &7Detalhes: &f" + log.details(),
                                " &7Hora: &7" + time,
                                ""
                        ),
                        p -> {}
                ));

                slot++;
            }
        }

        this.registerButton(49, new MenuButton(
                Material.LAVA_BUCKET,
                "&c&lLimpar Logs",
                List.of(
                        "",
                        " &7Limpa todo o hist\u00f3rico",
                        " &c\u26a0 Irrevers\u00edvel!",
                        ""
                ),
                p -> {
                    this.plugin.getClanManager().clearLogs(p.getUniqueId());
                    p.sendMessage("&aLogs limpos!");
                    this.updateMenu();
                }
        ));

        this.addBackButton(50, p -> new ClanMenu(this.plugin, p).openMenu());
    }
}
