package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.utils.ClanUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public final class InviteMessenger {
    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private static final String NEON_GREEN = "#00e676";
    private static final String NEON_RED = "#ff1744";
    private static final String NEON_CYAN = "#00e5ff";
    private static final String GREEN_PROMPT = "#00ff41";
    private static final String GRAY = "#9e9e9e";
    private static final String WHITE = "#ffffff";

    private InviteMessenger() {}

    public static String tagColor(Clan clan) {
        String c = clan.getTagColor();
        if (c == null || c.isEmpty()) return "#f1faee";
        String clean = c.replace("&", "").replace("#", "").trim();
        return clean.length() == 6 ? "#" + clean : "#f1faee";
    }

    private static String tagComponent(Clan clan) {
        return "<color:" + tagColor(clan) + "><bold>[" + clan.getTag() + "]</bold></color>";
    }

    public static void sendInviteMessage(Player target, Clan clan, ClanInvite invite, long timeoutMillis) {
        int seconds = (int) Math.ceil(invite.getRemainingMillis(timeoutMillis) / 1000.0);
        target.sendMessage(MINI.deserialize(buildInvite(clan, seconds)));
    }

    private static String buildInvite(Clan clan, int seconds) {
        String bar = "▄".repeat(58);
        String barBottom = "▀".repeat(58);
        String clanName = clan.getName();
        return "\n<color:" + NEON_GREEN + ">" + bar + "</color>\n"
                + "  <color:" + GREEN_PROMPT + "><bold>❯</bold></color> " + tagComponent(clan)
                + " <color:" + WHITE + ">convidou você!</color>\n"
                + "  <color:" + GREEN_PROMPT + "><bold>❯</bold></color> <color:" + WHITE + ">Entrar no clã</color> "
                + "<color:" + NEON_CYAN + ">" + clanName + "</color> <color:" + GRAY + ">?</color>\n"
                + "  <color:" + GREEN_PROMPT + "><bold>❯</bold></color> <color:" + GRAY + ">o convite expira em "
                + "<color:" + NEON_CYAN + ">" + seconds + "s</color></color>\n"
                + "<color:" + NEON_GREEN + ">" + barBottom + "</color>\n"
                + buttons(clanName);
    }

    private static String buttons(String clanName) {
        return "  "
                + "<click:run_command:'/clan aceitar " + clanName + "'>"
                + "<hover:show_text:'<color:" + NEON_GREEN + "><bold>Entrar no clã</bold></color>'>"
                + "<color:" + NEON_GREEN + "><bold>[ACEITAR]</bold></color></hover></click>"
                + "   "
                + "<click:run_command:'/clan recusar " + clanName + "'>"
                + "<hover:show_text:'<color:" + NEON_RED + "><bold>Recusar o convite</bold></color>'>"
                + "<color:" + NEON_RED + "><bold>[RECUSAR]</bold></color></hover></click>"
                + "   "
                + "<click:run_command:'/clan convites'>"
                + "<hover:show_text:'<color:" + NEON_CYAN + "><bold>Ver todos os convites pendentes</bold></color>'>"
                + "<color:" + NEON_CYAN + "><bold>[VER TODOS]</bold></color></hover></click>";
    }

    public static void sendWelcome(Player player, Clan clan) {
        String bar = "▄".repeat(50);
        String barBottom = "▀".repeat(50);
        String msg = "\n<color:" + NEON_GREEN + ">" + bar + "</color>\n"
                + "  <color:" + GREEN_PROMPT + "><bold>❯</bold></color> <color:" + WHITE + ">Bem-vindo ao clã</color> "
                + tagComponent(clan) + "!\n"
                + "  <color:" + GREEN_PROMPT + "><bold>❯</bold></color> <color:" + GRAY + ">Membros: "
                + "<color:" + NEON_CYAN + ">" + clan.getMemberCount() + "/" + clan.getMaxMembers()
                + "</color> · Líder: <color:" + NEON_CYAN + ">" + ClanUtils.getPlayerName(clan.getLeader())
                + "</color></color>\n"
                + "  <color:" + GREEN_PROMPT + "><bold>❯</bold></color> <color:" + GRAY + ">Use </color>"
                + "<color:" + NEON_CYAN + ">/clan</color> <color:" + GRAY + ">para abrir o hub do clã</color>\n"
                + "<color:" + NEON_GREEN + ">" + barBottom + "</color>";
        player.sendMessage(MINI.deserialize(msg));
    }

    public static void sendInviteList(Player player, IraqueClan plugin, List<ClanInvite> invites) {
        player.sendMessage(MINI.deserialize(
                "<color:" + NEON_GREEN + "><bold>▄▄▄▄▄▄▄▄▄▄ CONVITES PENDENTES ▄▄▄▄▄▄▄▄▄▄</bold></color>"));
        if (invites.isEmpty()) {
            player.sendMessage(MINI.deserialize(
                    "  <color:" + GRAY + ">Nenhum convite pendente.</color>"));
            return;
        }
        long timeout = plugin.getConfigManager().getInviteTimeoutMillis();
        for (ClanInvite invite : invites) {
            Clan clan = plugin.getClanManager().getClan(invite.clanName()).orElse(null);
            String tag = clan != null ? tagComponent(clan)
                    : "<color:#ffffff>[" + invite.clanName() + "]</color>";
            int seconds = (int) Math.ceil(invite.getRemainingMillis(timeout) / 1000.0);
            String line = "  <color:" + GREEN_PROMPT + "><bold>❯</bold></color> " + tag
                    + " <click:run_command:'/clan aceitar " + invite.clanName() + "'>"
                    + "<hover:show_text:'<color:" + NEON_GREEN + "><bold>Aceitar</bold></color>'>"
                    + "<color:" + NEON_GREEN + ">[✓]</color></hover></click>"
                    + " <click:run_command:'/clan recusar " + invite.clanName() + "'>"
                    + "<hover:show_text:'<color:" + NEON_RED + "><bold>Recusar</bold></color>'>"
                    + "<color:" + NEON_RED + ">[✗]</color></hover></click>"
                    + " <color:" + GRAY + ">" + seconds + "s</color>";
            player.sendMessage(MINI.deserialize(line));
        }
        player.sendMessage(MINI.deserialize(
                "  <color:" + GRAY + ">Clique em </color><color:" + NEON_GREEN + ">[✓]</color>"
                        + "<color:" + GRAY + "> ou use </color><color:" + NEON_CYAN + ">/clan aceitar <clan></color>"));
    }
}
