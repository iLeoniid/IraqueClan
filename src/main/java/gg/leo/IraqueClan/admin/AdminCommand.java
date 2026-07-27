package gg.leo.IraqueClan.admin;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.clan.ClanManager;
import gg.leo.IraqueClan.utils.ItemBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final IraqueClan plugin;
    private final Map<UUID, Long> pendingDisbands = new HashMap<>();
    private static final long CONFIRM_TIMEOUT_MS = 30_000;

    private static final List<String> SUBCOMMANDS = List.of(
            "reload", "save", "inspect", "vault", "eco",
            "addxp", "setlevel", "setleader", "forcejoin",
            "disband", "rename", "setholo", "endseason"
    );

    public AdminCommand(IraqueClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("iraqueclan.admin")) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("no-permission")));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin")));
            return true;
        }
        String sub = args[0].toLowerCase();
        return switch (sub) {
            case "reload" -> handleReload(sender);
            case "save" -> handleSave(sender);
            case "inspect" -> handleInspect(sender, args);
            case "vault" -> handleVault(sender, args);
            case "eco" -> handleEco(sender, args);
            case "addxp" -> handleAddXP(sender, args);
            case "setlevel" -> handleSetLevel(sender, args);
            case "setleader" -> handleSetLeader(sender, args);
            case "forcejoin" -> handleForceJoin(sender, args);
            case "disband" -> handleDisband(sender, args);
            case "rename" -> handleRename(sender, args);
            case "setholo" -> handleSetHolo(sender, args);
            case "endseason" -> handleEndSeason(sender);
            default -> {
                sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin")));
                yield true;
            }
        };
    }

    private boolean handleReload(CommandSender sender) {
        this.plugin.getConfigManager().reloadAll();
        this.plugin.getClanManager().saveAll();
        sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("admin.reloaded")));
        return true;
    }

    private boolean handleSave(CommandSender sender) {
        this.plugin.getClanManager().saveAll();
        sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("admin.saved")));
        return true;
    }

    private boolean handleInspect(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin-inspect")));
            return true;
        }
        String clanName = args[1];
        ClanManager cm = this.plugin.getClanManager();
        var clanOpt = cm.getClan(clanName);
        if (clanOpt.isEmpty()) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("admin.no-clan")));
            return true;
        }
        Clan clan = clanOpt.get();
        sender.sendMessage(ItemBuilder.color(
                this.plugin.getConfigManager().getPrefixedMessage("admin.inspecting")
                        .replace("{clan}", clan.getName())
        ));
        sender.sendMessage(ItemBuilder.color("&#FFFF55Nome: &#FFFFFF" + clan.getName()));
        sender.sendMessage(ItemBuilder.color("&#FFFF55Tag: &#FFFFFF" + clan.getTag()));
        sender.sendMessage(ItemBuilder.color("&#FFFF55Lider: &#FFFFFF" + Bukkit.getOfflinePlayer(clan.getLeader()).getName()));
        sender.sendMessage(ItemBuilder.color("&#FFFF55Level: &#FFFFFF" + clan.getLevel()));
        sender.sendMessage(ItemBuilder.color("&#FFFF55XP: &#FFFFFF" + clan.getXp()));
        sender.sendMessage(ItemBuilder.color("&#FFFF55Membros: &#FFFFFF" + clan.getMemberCount() + "/" + clan.getMaxMembers()));
        sender.sendMessage(ItemBuilder.color("&#FFFF55Banco: &#FFFFFF$" + String.format("%.2f", clan.getBank())));
        sender.sendMessage(ItemBuilder.color("&#FFFF55Kills: &#FFFFFF" + clan.getTotalKills() + " &#AAAAAA| &#FFFF55Mortes: &#FFFFFF" + clan.getDeaths()));
        sender.sendMessage(ItemBuilder.color("&#FFFF55KDR: &#FFFFFF" + clan.getKDR()));
        return true;
    }

    private boolean handleVault(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin-vault")));
            return true;
        }
        String clanName = args[1];
        var clanOpt = this.plugin.getClanManager().getClan(clanName);
        if (clanOpt.isEmpty()) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("admin.no-clan")));
            return true;
        }
        Clan clan = clanOpt.get();
        sender.sendMessage(ItemBuilder.color(
                this.plugin.getConfigManager().getPrefixedMessage("admin.vault-opened")
                        .replace("{clan}", clan.getName())
        ));
        sender.sendMessage(ItemBuilder.color("&#FFFF55Saldo: &#FFFFFF$" + String.format("%.2f", clan.getBank())));
        return true;
    }

    private boolean handleEco(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin-eco")));
            return true;
        }
        String clanName = args[1];
        String action = args[2].toLowerCase();
        double amount;
        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ItemBuilder.color("&#FF5555Valor invalido."));
            return true;
        }
        ClanManager cm = this.plugin.getClanManager();
        var clanOpt = cm.getClan(clanName);
        if (clanOpt.isEmpty()) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("admin.no-clan")));
            return true;
        }
        Clan clan = clanOpt.get();
        switch (action) {
            case "set" -> {
                clan.setBank(amount);
                cm.saveAll();
                sender.sendMessage(ItemBuilder.color(
                        this.plugin.getConfigManager().getPrefixedMessage("admin.eco-set")
                                .replace("{clan}", clan.getName())
                                .replace("{amount}", String.format("%.2f", amount))
                ));
            }
            case "add" -> {
                clan.addBank(amount);
                cm.saveAll();
                sender.sendMessage(ItemBuilder.color(
                        this.plugin.getConfigManager().getPrefixedMessage("admin.eco-added")
                                .replace("{clan}", clan.getName())
                                .replace("{amount}", String.format("%.2f", amount))
                ));
            }
            case "remove" -> {
                if (!clan.removeBank(amount)) {
                    sender.sendMessage(ItemBuilder.color("&#FF5555Banco do clã insuficiente."));
                    return true;
                }
                cm.saveAll();
                sender.sendMessage(ItemBuilder.color(
                        this.plugin.getConfigManager().getPrefixedMessage("admin.eco-removed")
                                .replace("{clan}", clan.getName())
                                .replace("{amount}", String.format("%.2f", amount))
                ));
            }
            default -> sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin-eco")));
        }
        return true;
    }

    private boolean handleAddXP(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin-xp")));
            return true;
        }
        String clanName = args[1];
        long amount;
        try {
            amount = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ItemBuilder.color("&#FF5555Valor invalido."));
            return true;
        }
        ClanManager cm = this.plugin.getClanManager();
        var clanOpt = cm.getClan(clanName);
        if (clanOpt.isEmpty()) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("admin.no-clan")));
            return true;
        }
        cm.forceAddXP(clanName, amount);
        cm.saveAll();
        sender.sendMessage(ItemBuilder.color(
                this.plugin.getConfigManager().getPrefixedMessage("admin.xp-set")
                        .replace("{clan}", clanName)
                        .replace("{amount}", String.valueOf(amount))
        ));
        return true;
    }

    private boolean handleSetLevel(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin-level")));
            return true;
        }
        String clanName = args[1];
        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ItemBuilder.color("&#FF5555Valor invalido."));
            return true;
        }
        ClanManager cm = this.plugin.getClanManager();
        var clanOpt = cm.getClan(clanName);
        if (clanOpt.isEmpty()) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("admin.no-clan")));
            return true;
        }
        cm.forceSetLevel(clanName, level);
        cm.saveAll();
        sender.sendMessage(ItemBuilder.color(
                this.plugin.getConfigManager().getPrefixedMessage("admin.level-set")
                        .replace("{clan}", clanName)
                        .replace("{level}", String.valueOf(level))
        ));
        return true;
    }

    private boolean handleSetLeader(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin-leader")));
            return true;
        }
        String clanName = args[1];
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("admin.no-player")));
            return true;
        }
        ClanManager cm = this.plugin.getClanManager();
        var clanOpt = cm.getClan(clanName);
        if (clanOpt.isEmpty()) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("admin.no-clan")));
            return true;
        }
        Clan clan = clanOpt.get();
        if (!clan.isMember(target.getUniqueId())) {
            sender.sendMessage(ItemBuilder.color("&#FF5555Este jogador nao e membro do clã."));
            return true;
        }
        UUID oldLeader = clan.getLeader();
        clan.setMemberRole(oldLeader, gg.leo.IraqueClan.clan.role.ClanRole.SUB_LIDER);
        clan.setMemberRole(target.getUniqueId(), gg.leo.IraqueClan.clan.role.ClanRole.LIDER);
        clan.setLeader(target.getUniqueId());
        cm.saveAll();
        sender.sendMessage(ItemBuilder.color(
                this.plugin.getConfigManager().getPrefixedMessage("admin.leader-set")
                        .replace("{clan}", clanName)
                        .replace("{player}", target.getName())
        ));
        return true;
    }

    private boolean handleForceJoin(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin-forcejoin")));
            return true;
        }
        String clanName = args[1];
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("admin.no-player")));
            return true;
        }
        ClanManager cm = this.plugin.getClanManager();
        if (cm.isPlayerInClan(target.getUniqueId())) {
            sender.sendMessage(ItemBuilder.color("&#FF5555Este jogador ja esta em um clã."));
            return true;
        }
        boolean success = cm.forceJoin(target.getUniqueId(), clanName);
        if (!success) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("admin.no-clan")));
            return true;
        }
        sender.sendMessage(ItemBuilder.color(
                this.plugin.getConfigManager().getPrefixedMessage("admin.force-joined")
                        .replace("{player}", target.getName())
                        .replace("{clan}", clanName)
        ));
        target.sendMessage(ItemBuilder.color(
                this.plugin.getConfigManager().getPrefixedMessage("admin.force-join-target")
                        .replace("{clan}", clanName)
        ));
        return true;
    }

    private boolean handleDisband(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin-disband")));
            return true;
        }
        String clanName = args[1];
        ClanManager cm = this.plugin.getClanManager();
        var clanOpt = cm.getClan(clanName);
        if (clanOpt.isEmpty()) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("admin.no-clan")));
            return true;
        }
        boolean requireConfirm = this.plugin.getConfig().getBoolean("admin.exigir-confirmacao-desmanchar", true);
        if (requireConfirm && (args.length < 3 || !args[2].equalsIgnoreCase("confirmar"))) {
            sender.sendMessage(ItemBuilder.color(
                    this.plugin.getConfigManager().getPrefixedMessage("admin.confirm-disband")
                            .replace("{clan}", clanName)
            ));
            return true;
        }
        cm.disbandClan(clanName);
        sender.sendMessage(ItemBuilder.color(
                this.plugin.getConfigManager().getPrefixedMessage("admin.disband-forced")
                        .replace("{clan}", clanName)
        ));
        return true;
    }

    private boolean handleRename(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin-rename")));
            return true;
        }
        String oldName = args[1];
        String newName = args[2];
        ClanManager cm = this.plugin.getClanManager();
        boolean success = cm.renameClan(oldName, newName);
        if (!success) {
            sender.sendMessage(ItemBuilder.color("&#FF5555Nao foi possivel renomear. Verifique se o nome antigo existe e o novo nao esta em uso."));
            return true;
        }
        sender.sendMessage(ItemBuilder.color(
                this.plugin.getConfigManager().getPrefixedMessage("admin.renamed")
                        .replace("{old}", oldName)
                        .replace("{new}", newName)
        ));
        return true;
    }

    private boolean handleSetHolo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin-holo")));
            return true;
        }
        String type = args[1].toLowerCase();
        if (!List.of("kills", "level", "bank").contains(type)) {
            sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("usage-admin-holo")));
            return true;
        }
        sender.sendMessage(ItemBuilder.color(
                this.plugin.getConfigManager().getPrefixedMessage("admin.holo-created")
                        .replace("{type}", type)
        ));
        return true;
    }

    private boolean handleEndSeason(CommandSender sender) {
        sender.sendMessage(ItemBuilder.color(this.plugin.getConfigManager().getPrefixedMessage("admin.season-ended")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("iraqueclan.admin")) {
            return List.of();
        }
        if (args.length == 1) {
            return filterStartsWith(SUBCOMMANDS, args[0]);
        }
        ClanManager cm = this.plugin.getClanManager();
        List<String> clanNames = cm.getAllClans().stream().map(Clan::getName).toList();
        return switch (args[0].toLowerCase()) {
            case "inspect", "vault", "addxp", "setlevel", "setleader", "forcejoin", "disband" -> {
                if (args.length == 2) {
                    yield filterStartsWith(clanNames, args[1]);
                }
                if (args.length == 3 && args[0].equalsIgnoreCase("eco")) {
                    yield filterStartsWith(List.of("add", "remove", "set"), args[2]);
                }
                if (args.length == 3 && (args[0].equalsIgnoreCase("setleader") || args[0].equalsIgnoreCase("forcejoin"))) {
                    yield null;
                }
                if (args.length == 4 && args[0].equalsIgnoreCase("eco")) {
                    yield List.of("<quantia>");
                }
                yield List.of();
            }
            case "rename" -> {
                if (args.length == 2) {
                    yield filterStartsWith(clanNames, args[1]);
                }
                if (args.length == 3) {
                    yield List.of("<novo-nome>");
                }
                yield List.of();
            }
            case "setholo" -> {
                if (args.length == 2) {
                    yield filterStartsWith(List.of("kills", "level", "bank"), args[1]);
                }
                yield List.of();
            }
            case "eco" -> {
                if (args.length == 2) {
                    yield filterStartsWith(clanNames, args[1]);
                }
                if (args.length == 3) {
                    yield filterStartsWith(List.of("add", "remove", "set"), args[2]);
                }
                if (args.length == 4) {
                    yield List.of("<quantia>");
                }
                yield List.of();
            }
            default -> List.of();
        };
    }

    private List<String> filterStartsWith(List<String> options, String input) {
        if (input == null || input.isEmpty()) return options;
        String lower = input.toLowerCase();
        return options.stream().filter(s -> s.toLowerCase().startsWith(lower)).toList();
    }
}
