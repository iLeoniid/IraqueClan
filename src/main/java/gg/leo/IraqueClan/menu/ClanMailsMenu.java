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
        super(player, "&#555555&lMail do Cl\u00e3o", 54, MenuType.SIMPLE);
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
                Material.WRITABLE_BOOK,
                "&#ff6b6b&lMails do Cl\u00e3o",
                List.of(
                        "",
                        " &#AAAAAAMensagens: &#FFFFFF" + clan.getMailCount(),
                        "",
                        " &#AAAAAAEnvie: &#FFFF55/clan mail enviar <mensagem>",
                        " &#AAAAAALimpar: &#FFFF55/clan mail limpar",
                        ""
                ),
                p -> {}
        ));

        List<Clan.ClanMail> mails = clan.getMails();

        if (mails.isEmpty()) {
            this.registerButton(22, new MenuButton(
                    Material.PAPER,
                    "&#AAAAAA&lNenhuma Mail",
                    List.of(
                            "",
                            " &#AAAAAANenhuma mensagem no momento.",
                            " &#AAAAAAEnvie uma com &#FFFF55/clan mail enviar <msg>",
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
                lore.add(" &#AAAAAADe: &#FFFFFF" + senderName);
                lore.add(" &#AAAAAAHora: &#AAAAAA" + timeStr);
                lore.add("");
                lore.add(" &#FFFFFF\"" + truncate(mail.message(), 40) + "\"");
                lore.add("");

                this.registerButton(slot, new MenuButton(
                        Material.PAPER,
                        "&#ffd166Mail de &#FFFFFF" + senderName,
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
                        " &#AAAAAARemove todas as mails",
                        " &#ef476fEsta a\u00e7\u00e3o \u00e9 irrevers\u00edvel!",
                        ""
                ),
                p -> {
                    this.plugin.getClanManager().clearMails(p.getUniqueId());
                    p.sendMessage("&#55FF55Todas as mails foram limpas!");
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
