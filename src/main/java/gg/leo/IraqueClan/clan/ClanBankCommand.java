package gg.leo.IraqueClan.clan;

import gg.leo.IraqueClan.IraqueClan;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ClanBankCommand implements ClanSubCommand {
    private final IraqueClan plugin;

    public ClanBankCommand(IraqueClan plugin) {
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
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-bank"));
            return;
        }
        String sub = args[1].toLowerCase();
        switch (sub) {
            case "saldo", "balance" -> handleBalance(player, clan);
            case "depositar", "deposit" -> handleDeposit(player, clan, args);
            case "sacar", "withdraw" -> handleWithdraw(player, clan, args);
            default -> player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-bank"));
        }
    }

    private Economy getEconomy() {
        RegisteredServiceProvider<Economy> provider = this.plugin.getServer().getServicesManager()
                .getRegistration(Economy.class);
        return provider != null ? provider.getProvider() : null;
    }

    private void handleBalance(Player player, Clan clan) {
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("bank.balance")
                .replace("{balance}", String.format("%.2f", clan.getBank())));
    }

    private void handleDeposit(Player player, Clan clan, String[] args) {
        if (args.length < 3) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-deposit"));
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-deposit"));
            return;
        }
        if (amount <= 0) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-deposit"));
            return;
        }
        Economy eco = getEconomy();
        if (eco == null) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("economy-no-vault"));
            return;
        }
        if (!eco.has(player, amount)) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("bank.not-enough-player")
                    .replace("{amount}", String.format("%.2f", amount)));
            return;
        }
        eco.withdrawPlayer(player, amount);
        this.plugin.getClanManager().depositClan(player.getUniqueId(), amount);
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("bank.deposit")
                .replace("{amount}", String.format("%.2f", amount)));
        this.plugin.getClanManager().addLog(player.getUniqueId(), "DEPOSIT", "Depositou $" + String.format("%.2f", amount));
        for (java.util.UUID uuid : clan.getMembers().keySet()) {
            if (uuid.equals(player.getUniqueId())) continue;
            Player member = this.plugin.getServer().getPlayer(uuid);
            if (member != null && member.isOnline()) {
                member.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("bank.deposit-received")
                        .replace("{player}", player.getName())
                        .replace("{amount}", String.format("%.2f", amount)));
            }
        }
    }

    private void handleWithdraw(Player player, Clan clan, String[] args) {
        if (args.length < 3) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-withdraw"));
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-withdraw"));
            return;
        }
        if (amount <= 0) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("usage-withdraw"));
            return;
        }
        if (!clan.hasBank(amount)) {
            player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("bank.insufficient")
                    .replace("{balance}", String.format("%.2f", clan.getBank())));
            return;
        }
        this.plugin.getClanManager().withdrawClan(player.getUniqueId(), amount);
        Economy eco = getEconomy();
        if (eco != null) {
            eco.depositPlayer(player, amount);
        }
        player.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("bank.withdraw")
                .replace("{amount}", String.format("%.2f", amount)));
        this.plugin.getClanManager().addLog(player.getUniqueId(), "WITHDRAWAL", "Sacou $" + String.format("%.2f", amount));
        for (java.util.UUID uuid : clan.getMembers().keySet()) {
            if (uuid.equals(player.getUniqueId())) continue;
            Player member = this.plugin.getServer().getPlayer(uuid);
            if (member != null && member.isOnline()) {
                member.sendMessage(this.plugin.getConfigManager().getPrefixedMessage("bank.withdraw-received")
                        .replace("{player}", player.getName())
                        .replace("{amount}", String.format("%.2f", amount)));
            }
        }
    }
}
