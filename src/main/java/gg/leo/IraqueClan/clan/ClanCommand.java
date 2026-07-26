package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.menu.ClanMenu;
import gg.leo.IraqueClan.war.WarAcceptCommand;
import gg.leo.IraqueClan.war.WarCommand;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ClanCommand implements CommandExecutor, TabCompleter {
    private final IraqueClan plugin;
    private final Map<String, ClanSubCommand> subCommands;

    public ClanCommand(IraqueClan plugin) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();
        this.subCommands.put("criar", new ClanCreateSubCommand(plugin));
        this.subCommands.put("convidar", new ClanInviteSubCommand(plugin));
        this.subCommands.put("aceitar", new ClanAcceptCommand(plugin));
        this.subCommands.put("sair", new ClanLeaveCommand(plugin));
        this.subCommands.put("expulsar", new ClanKickCommand(plugin));
        this.subCommands.put("dissolver", new ClanDisbandCommand(plugin));
        this.subCommands.put("promover", new ClanPromoteCommand(plugin));
        this.subCommands.put("rebaixar", new ClanDemoteCommand(plugin));
        this.subCommands.put("guerra", new WarCommand(plugin));
        this.subCommands.put("guerraaceitar", new WarAcceptCommand(plugin));
        this.subCommands.put("guerrarecusar", new ClanWarDeclineCommand(plugin));
        this.subCommands.put("guerrarender", new ClanWarSurrenderCommand(plugin));
        this.subCommands.put("guerrastats", new ClanWarStatsCommand(plugin));
        this.subCommands.put("tag", new ClanTagCommand(plugin));
        this.subCommands.put("cor", new ClanColorCommand(plugin));
        this.subCommands.put("banco", new ClanBankCommand(plugin));
        this.subCommands.put("home", new ClanHomeCommand(plugin));
        this.subCommands.put("sethome", new ClanSetHomeCommand(plugin));
        this.subCommands.put("delhome", new ClanDelHomeCommand(plugin));
        this.subCommands.put("homes", new ClanHomesCommand(plugin));
        this.subCommands.put("mail", new ClanMailCommand(plugin));
        this.subCommands.put("motd", new ClanMotdCommand(plugin));
        this.subCommands.put("chat", new ClanChatCommand(plugin));
        this.subCommands.put("loja", new ClanShopCommand(plugin));
        this.subCommands.put("quest", new ClanQuestCommand(plugin));
        this.subCommands.put("conquista", new ClanAchievementCommand(plugin));
        this.subCommands.put("perfil", new ClanProfileCommand(plugin));
        this.subCommands.put("desc", new ClanDescCommand(plugin));
        this.subCommands.put("icon", new ClanIconCommand(plugin));
        this.subCommands.put("logs", new ClanLogsCommand(plugin));
        this.subCommands.put("top", new ClanTopCommand(plugin));
        this.subCommands.put("diplo", new ClanDiploCommand(plugin));
        this.subCommands.put("xp", new ClanXpCommand(plugin));
        this.subCommands.put("ajuda", new ClanHelpSubCommand(plugin));
        this.subCommands.put("help", new ClanHelpSubCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }
        if (args.length == 0) {
            new ClanMenu(this.plugin, player).openMenu();
            return true;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("guerra") && args.length > 1) {
            String warSub = args[1].toLowerCase();
            switch (warSub) {
                case "aceitar" -> sub = "guerraaceitar";
                case "recusar" -> sub = "guerrarecusar";
                case "render" -> sub = "guerrarender";
                case "stats" -> sub = "guerrastats";
            }
        }
        ClanSubCommand subCommand = this.subCommands.get(sub);
        if (subCommand != null) {
            subCommand.execute(player, args);
            return true;
        }
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("unknown-subcommand"));
        new ClanMenu(this.plugin, player).openMenu();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return List.of();
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            List<String> subs = new ArrayList<>(this.subCommands.keySet());
            subs.add("guerra aceitar");
            subs.add("guerra recusar");
            subs.add("guerra render");
            subs.add("guerra stats");
            for (String sub : subs) {
                if (sub.startsWith(partial)) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("guerra")) {
                String partial = args[1].toLowerCase();
                for (String warSub : List.of("aceitar", "recusar", "render", "stats")) {
                    if (warSub.startsWith(partial)) {
                        completions.add(warSub);
                    }
                }
            } else if (List.of("convidar", "expulsar", "promover", "rebaixar", "home", "sethome", "delhome", "diplo").contains(sub)) {
                String partial = args[1].toLowerCase();
                for (Player online : org.bukkit.Bukkit.getOnlinePlayers()) {
                    if (online.getName().toLowerCase().startsWith(partial)) {
                        completions.add(online.getName());
                    }
                }
            } else if (sub.equals("perfil")) {
                String partial = args[1].toLowerCase();
                for (Clan clan : this.plugin.getClanManager().getAllClans()) {
                    if (clan.getName().toLowerCase().startsWith(partial)) {
                        completions.add(clan.getName());
                    }
                }
            } else if (sub.equals("banco")) {
                String partial = args[1].toLowerCase();
                for (String action : List.of("depositar", "sacar", "saldo")) {
                    if (action.startsWith(partial)) {
                        completions.add(action);
                    }
                }
            } else if (sub.equals("mail")) {
                String partial = args[1].toLowerCase();
                for (String action : List.of("send", "read", "clear")) {
                    if (action.startsWith(partial)) {
                        completions.add(action);
                    }
                }
            } else if (sub.equals("motd")) {
                String partial = args[1].toLowerCase();
                for (String action : List.of("set", "clear")) {
                    if (action.startsWith(partial)) {
                        completions.add(action);
                    }
                }
            } else if (sub.equals("quest")) {
                String partial = args[1].toLowerCase();
                for (String action : List.of("list", "refresh")) {
                    if (action.startsWith(partial)) {
                        completions.add(action);
                    }
                }
            } else if (sub.equals("top")) {
                String partial = args[1].toLowerCase();
                for (String type : List.of("kills", "level", "bank", "kdr", "members", "time")) {
                    if (type.startsWith(partial)) {
                        completions.add(type);
                    }
                }
            } else if (sub.equals("logs")) {
                String partial = args[1].toLowerCase();
                if ("clear".startsWith(partial)) {
                    completions.add("clear");
                }
            }
        }
        return completions;
    }

    private static class ClanHelpSubCommand implements ClanSubCommand {
        private final IraqueClan plugin;

        ClanHelpSubCommand(IraqueClan plugin) {
            this.plugin = plugin;
        }

        @Override
        public void execute(Player player, String[] args) {
            new ClanMenu(this.plugin, player).openMenu();
        }
    }

    private static class ClanCreateSubCommand implements ClanSubCommand {
        private final IraqueClan plugin;

        ClanCreateSubCommand(IraqueClan plugin) {
            this.plugin = plugin;
        }

        @Override
        public void execute(Player player, String[] args) {
            if (!player.hasPermission("iraqueclan.create")) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
                return;
            }
            if (this.plugin.getClanManager().isPlayerInClan(player.getUniqueId())) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.already-in-clan"));
                return;
            }
            if (args.length < 3) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-create"));
                return;
            }
            String clanName = args[1];
            String tag = args[2];
            if (clanName.length() > this.plugin.getConfigManager().getMaxNameLength()) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.name-too-long")
                        .replace("{max}", String.valueOf(this.plugin.getConfigManager().getMaxNameLength())));
                return;
            }
            if (tag.length() > this.plugin.getConfigManager().getMaxTagLength()) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.tag-too-long")
                        .replace("{max}", String.valueOf(this.plugin.getConfigManager().getMaxTagLength())));
                return;
            }
            if (this.plugin.getClanManager().clanNameExists(clanName)) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.name-taken"));
                return;
            }
            if (this.plugin.getClanManager().tagExists(tag)) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.tag-taken"));
                return;
            }
            boolean created = this.plugin.getClanManager().createClan(clanName, tag, "&#f1faee", player.getUniqueId());
            if (created) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.created")
                        .replace("{clan}", clanName));
            } else {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.create-error"));
            }
        }
    }

    private static class ClanInviteSubCommand implements ClanSubCommand {
        private final IraqueClan plugin;

        ClanInviteSubCommand(IraqueClan plugin) {
            this.plugin = plugin;
        }

        @Override
        public void execute(Player player, String[] args) {
            if (!player.hasPermission("iraqueclan.invite")) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
                return;
            }
            Clan clan = this.plugin.getClanManager().getClanByPlayerDirect(player.getUniqueId());
            if (clan == null) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-clan"));
                return;
            }
            if (!clan.canKick(player.getUniqueId())) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("no-permission"));
                return;
            }
            if (args.length < 2) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-invite"));
                return;
            }
            Player target = org.bukkit.Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("player-not-found"));
                return;
            }
            if (this.plugin.getClanManager().isPlayerInClan(target.getUniqueId())) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.target-already-in-clan"));
                return;
            }
            if (clan.getMemberCount() >= this.plugin.getConfigManager().getMaxMembers()) {
                player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("clan.clan-full")
                        .replace("{max}", String.valueOf(this.plugin.getConfigManager().getMaxMembers())));
                return;
            }
            this.plugin.getClanManager().setPendingInvite(target.getUniqueId(), player.getUniqueId());
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.sent")
                    .replace("{player}", target.getName()));
            target.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("invite.received")
                    .replace("{player}", player.getName())
                    .replace("{clan}", clan.getName()));
        }
    }
}
