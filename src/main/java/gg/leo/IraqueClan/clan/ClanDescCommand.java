package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import org.bukkit.entity.Player;

public class ClanDescCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanDescCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-in-clan"));
            return;
        }
        if (!clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
            return;
        }
        if (args.length < 3 || !args[1].equalsIgnoreCase("set")) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-desc"));
            return;
        }
        StringBuilder text = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            if (i > 2) text.append(" ");
            text.append(args[i]);
        }
        String description = text.toString();
        int maxLength = this.plugin.getConfig().getInt("perfis.max-desc-tamanho", 200);
        if (maxLength > 0 && description.length() > maxLength) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("desc.too-long")
                    .replace("{max}", String.valueOf(maxLength)));
            return;
        }
        boolean set = this.plugin.getClanManager().setDescription(player.getUniqueId(), description);
        if (set) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("desc.set"));
            this.plugin.getClanManager().addLog(player.getUniqueId(), "DESC_SET", "Descrição atualizada");
        }
    }
}
