package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.bukkit.entity.Player;

public class ClanMailCommand implements ClanSubCommand {
    private final IraqueClan plugin;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ClanMailCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-in-clan"));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-mail"));
            return;
        }
        String sub = args[1].toLowerCase();
        switch (sub) {
            case "read", "ler" -> handleRead(player, clan);
            case "send", "enviar" -> handleSend(player, clan, args);
            case "clear", "limpar" -> handleClear(player, clan);
            default -> player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-mail"));
        }
    }

    @SuppressWarnings("deprecation")
    private void handleRead(Player player, Clan clan) {
        List<Clan.ClanMail> mails = this.plugin.getClanManager().getMails(player.getUniqueId());
        if (mails.isEmpty()) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("mail.empty"));
            return;
        }
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("mail.read"));
        for (Clan.ClanMail mail : mails) {
            String senderName = "Desconhecido";
            if (mail.sender() != null) {
                Player senderPlayer = this.plugin.getServer().getPlayer(mail.sender());
                if (senderPlayer != null) {
                    senderName = senderPlayer.getName();
                } else {
                    senderName = this.plugin.getServer().getOfflinePlayer(mail.sender()).getName();
                }
            }
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("mail.read-entry")
                    .replace("{sender}", senderName)
                    .replace("{message}", mail.message())
                    .replace("{time}", this.dateFormat.format(new Date(mail.timestamp()))));
        }
    }

    private void handleSend(Player player, Clan clan, String[] args) {
        if (args.length < 3) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-mail-send"));
            return;
        }
        StringBuilder message = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            if (i > 2) message.append(" ");
            message.append(args[i]);
        }
        String text = message.toString();
        int maxLength = this.plugin.getConfig().getInt("correio.max-tamanho-mensagem", 100);
        if (text.length() > maxLength) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("mail.too-long")
                    .replace("{max}", String.valueOf(maxLength)));
            return;
        }
        int maxMails = this.plugin.getConfig().getInt("correio.max-correios-armazenados", 50);
        if (clan.getMailCount() >= maxMails) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("mail.limit-reached")
                    .replace("{max}", String.valueOf(maxMails)));
            return;
        }
        this.plugin.getClanManager().addMail(player.getUniqueId(), text);
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("mail.sent"));
        for (java.util.UUID uuid : clan.getMembers().keySet()) {
            if (uuid.equals(player.getUniqueId())) continue;
            Player member = this.plugin.getServer().getPlayer(uuid);
            if (member != null && member.isOnline()) {
                member.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("mail.sent")
                        .replace("{player}", player.getName()));
            }
        }
    }

    private void handleClear(Player player, Clan clan) {
        if (!clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        this.plugin.getClanManager().clearMails(player.getUniqueId());
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("mail.cleared"));
        this.plugin.getClanManager().addLog(player.getUniqueId(), "MAIL_CLEARED", "Mail do clã limpo");
    }
}
