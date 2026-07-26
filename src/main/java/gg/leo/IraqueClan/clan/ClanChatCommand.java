package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public class ClanChatCommand implements ClanSubCommand {
    private static final Set<UUID> CHAT_ENABLED = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final IraqueClan plugin;

    public ClanChatCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.not-in-clan"));
            return;
        }
        UUID uuid = player.getUniqueId();
        if (CHAT_ENABLED.contains(uuid)) {
            CHAT_ENABLED.remove(uuid);
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("chat.disabled"));
        } else {
            CHAT_ENABLED.add(uuid);
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("chat.enabled"));
        }
    }

    public static boolean isChatEnabled(UUID playerUuid) {
        return CHAT_ENABLED.contains(playerUuid);
    }

    public static Set<UUID> getChatEnabledPlayers() {
        return Collections.unmodifiableSet(CHAT_ENABLED);
    }
}
