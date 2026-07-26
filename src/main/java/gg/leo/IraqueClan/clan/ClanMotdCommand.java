package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.entity.Player;

public class ClanMotdCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanMotdCommand(IraqueClan plugin) {
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
            String currentMotd = this.plugin.getClanManager().getMOTD(player.getUniqueId());
            if (currentMotd == null || currentMotd.isEmpty()) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("motd.empty"));
            } else {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("motd.current")
                        .replace("{motd}", currentMotd));
            }
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-motd"));
            return;
        }
        String sub = args[1].toLowerCase();
        switch (sub) {
            case "set", "definir" -> handleSet(player, clan, args);
            case "clear", "limpar" -> handleClear(player);
            default -> player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-motd"));
        }
    }

    private void handleSet(Player player, Clan clan, String[] args) {
        if (!clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        if (args.length < 3) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-motd-set"));
            return;
        }
        StringBuilder text = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            if (i > 2) text.append(" ");
            text.append(args[i]);
        }
        String motd = text.toString();
        int maxLength = this.plugin.getConfig().getInt("motd.max-tamanho-motd", 200);
        if (motd.length() > maxLength) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("motd.too-long")
                    .replace("{max}", String.valueOf(maxLength)));
            return;
        }
        boolean set = this.plugin.getClanManager().setMOTD(player.getUniqueId(), motd);
        if (set) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("motd.set"));
            this.plugin.getClanManager().addLog(player.getUniqueId(), "MOTD_SET", "MOTD atualizado");
        }
    }

    private void handleClear(Player player) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null || !clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        boolean cleared = this.plugin.getClanManager().clearMOTD(player.getUniqueId());
        if (cleared) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("motd.cleared"));
            this.plugin.getClanManager().addLog(player.getUniqueId(), "MOTD_CLEARED", "MOTD limpo");
        }
    }
}
