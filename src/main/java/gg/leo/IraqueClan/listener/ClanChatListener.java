package gg.leo.IraqueClan.listener;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.clan.ClanChatCommand;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ClanChatListener implements Listener {
    private final IraqueClan plugin;

    public ClanChatListener(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(uuid);
        if (clan != null) {
            clan.setLastActiveTime(System.currentTimeMillis());
        }
        if (ClanChatCommand.isChatEnabled(uuid) && clan != null) {
            event.setCancelled(true);
            String tag = clan.getFormattedTag();
            String format = this.plugin.getConfigManager().getMessage("chat.format")
                    .replace("{tag}", tag)
                    .replace("{player}", player.getName())
                    .replace("{message}", event.getMessage());
            String colored = this.plugin.getConfigManager().translate(format);
            for (UUID memberUuid : clan.getMembers().keySet()) {
                Player member = this.plugin.getServer().getPlayer(memberUuid);
                if (member != null && member.isOnline()) {
                    member.sendMessage(colored);
                }
            }
        }
    }
}
