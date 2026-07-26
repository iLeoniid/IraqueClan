package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
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

public class ClanMailsMenu extends BaseMenu {
    private final IraqueClan plugin;

    public ClanMailsMenu(IraqueClan plugin, Player player) {
        super(player, "&8&lMail do Cl\u00e3o", 54, MenuType.SIMPLE);
        this.plugin = plugin;
    }

    @Override
    public void buildMenu() {
        this.addBorder(Material.GRAY_STAINED_GLASS_PANE, "&8");

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
                Material.WRITABLE_BOOK,
                "&#ff6b6b&lMails do Cl\u00e3o",
                List.of(
                        "",
                        " &7Mensagens: &f" + clan.getMailCount(),
                        "",
                        " &7Envie: &e/clan mail enviar <mensagem>",
                        " &7Limpar: &e/clan mail limpar",
                        ""
                ),
                p -> {}
        ));

        List<Clan.ClanMail> mails = clan.getMails();

        if (mails.isEmpty()) {
            this.registerButton(22, new MenuButton(
                    Material.PAPER,
                    "&7&lNenhuma Mail",
                    List.of(
                            "",
                            " &7Nenhuma mensagem no momento.",
                            " &7Envie uma com &e/clan mail enviar <msg>",
                            ""
                    ),
                    p -> {}
            ));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
            int slot = 10;
            for (int i = 0; i < mails.size() && slot < 45; i++) {
                Clan.ClanMail mail = mails.get(i);
                String senderName = ClanUtils.getPlayerName(mail.sender());
                String timeStr = sdf.format(new Date(mail.timestamp()));

                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(" &7De: &f" + senderName);
                lore.add(" &7Hora: &7" + timeStr);
                lore.add("");
                lore.add(" &f\"" + truncate(mail.message(), 40) + "\"");
                lore.add("");

                this.registerButton(slot, new MenuButton(
                        Material.PAPER,
                        "&#ffd166Mail de &f" + senderName,
                        lore,
                        p -> {}
                ));

                slot++;
                if (slot % 9 == 8) slot += 2;
                if (slot >= 45) break;
            }
        }

        this.registerButton(49, new MenuButton(
                Material.LAVA_BUCKET,
                "&#ef476f&lLimpar Tudo",
                List.of(
                        "",
                        " &7Remove todas as mails",
                        " &#ef476fEsta a\u00e7\u00e3o \u00e9 irrevers\u00edvel!",
                        ""
                ),
                p -> {
                    this.plugin.getClanManager().clearMails(p.getUniqueId());
                    p.sendMessage("&aTodas as mails foram limpas!");
                    this.updateMenu();
                }
        ));

        this.addBackButton(50, p -> new ClanMenu(this.plugin, p).openMenu());
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() > max ? text.substring(0, max) + "..." : text;
    }
}
