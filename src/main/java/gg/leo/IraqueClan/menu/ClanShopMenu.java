package gg.leo.IraqueClan.menu;

import gg.leo.IraqueClan.IraqueClan;
import gg.leo.IraqueClan.clan.Clan;
import gg.leo.IraqueClan.utils.ItemBuilder;
import gg.leo.IraqueClan.utils.menu.BaseMenu;
import gg.leo.IraqueClan.utils.menu.MenuButton;
import gg.leo.IraqueClan.utils.menu.MenuType;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanShopMenu extends BaseMenu {
    private final IraqueClan plugin;

    private static final String[][] UPGRADES = {
            {"limite-membros", "Limite de Membros", "Aumenta o limite de membros do cl\u00e3o", Material.NETHERITE_CHESTPLATE.name()},
            {"casas-extras", "Casas Extras", "Desbloqueia casas adicionais", Material.RED_BED.name()},
            {"xp-boost", "Boost de XP", "Ganhe mais XP em atividades", Material.EXPERIENCE_BOTTLE.name()},
            {"drop-boost", "Boost de Drop", "Chance aumentada de drops", Material.DIAMOND.name()},
            {"guerreiro", "Guerreiro", "B\u00f4nus de dano em guerras", Material.IRON_SWORD.name()},
            {"fortaleza", "Fortaleza", "Defesa extra em guerras", Material.SHIELD.name()},
            {"diplomacia", "Diplomacia", "Permite mais alian\u00e7as", Material.EMERALD.name()},
            {"tesouro", "Tesouro", "Reduz pre\u00e7os da loja", Material.GOLD_INGOT.name()}
    };

    public ClanShopMenu(IraqueClan plugin, Player player) {
        super(player, "&#555555&lLoja de Upgrades", 45, MenuType.SIMPLE);
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
            this.addBackButton(40, p -> new ClanMenu(this.plugin, p).openMenu());
            return;
        }

        boolean isLeader = clan.getLeader().equals(this.player.getUniqueId());

        this.registerButton(4, new MenuButton(
                Material.EMERALD,
                "&#06d6a0&lLoja de Upgrades",
                List.of(
                        "",
                        " &#AAAAAASaldo: &#06d6a0$" + String.format("%.2f", clan.getBank()),
                        " &#AAAAAAL\u00edder pode comprar upgrades",
                        ""
                ),
                p -> {}
        ));

        int slot = 10;
        for (String[] upgrade : UPGRADES) {
            if (slot >= 45) break;
            if (slot % 9 == 8) slot += 2;
            if (slot >= 45) break;

            String upgradeId = upgrade[0];
            String upgradeName = upgrade[1];
            String description = upgrade[2];
            Material mat = Material.getMaterial(upgrade[3]) != null ? Material.getMaterial(upgrade[3]) : Material.PAPER;

            int currentLevel = clan.getUpgradeLevel(upgradeId);
            boolean maxed = currentLevel >= 5;

            double basePrice = this.plugin.getConfig().getDouble("loja.upgrades." + upgradeId + ".preco-base", 1000);
            double multiplier = this.plugin.getConfig().getDouble("loja.upgrades." + upgradeId + ".multiplicador-preco", 1.5);
            double nextPrice = basePrice * Math.pow(multiplier, currentLevel);

            List<String> lore = List.of(
                    "",
                    " &#AAAAAA" + description,
                    "",
                    " &#AAAAAAN\u00edvel: " + getLevelBar(currentLevel, 5),
                    " &#FFFFFF" + currentLevel + "/5",
                    "",
                    maxed ? " &#06d6a0N\u00edvel m\u00e1ximo!" : " &#AAAAAAPr\u00f3ximo: &#ffd166$" + String.format("%.2f", nextPrice),
                    "",
                    !isLeader ? " &#ef476fApenas o l\u00edder pode comprar" : (maxed ? "" : " &#06d6a0Clique para comprar!")
            );

            this.registerButton(slot, new MenuButton(
                    mat,
                    (maxed ? "&#06d6a0" : "&#ffd166") + upgradeName,
                    lore,
                    isLeader && !maxed
                            ? p -> {
                                boolean success = this.plugin.getClanManager().purchaseUpgrade(p.getUniqueId(), upgradeId);
                                if (success) {
                                    p.sendMessage(ItemBuilder.color("&#55FF55Upgrade &#FFFF55" + upgradeName + " &#55FF55comprado com sucesso!"));
                                    this.updateMenu();
                                } else {
                                    p.sendMessage(ItemBuilder.color("&#FF5555Saldo insuficiente ou erro ao comprar!"));
                                }
                            }
                            : p -> {}
            ));

            slot++;
        }

        this.addBackButton(40, p -> new ClanMenu(this.plugin, p).openMenu());
    }

    private String getLevelBar(int current, int max) {
        StringBuilder bar = new StringBuilder("&#AAAAAA[");
        for (int i = 0; i < max; i++) {
            bar.append(i < current ? "&#06d6a0\u2588" : "&#555555\u2591");
        }
        bar.append("&#AAAAAA]");
        return bar.toString();
    }
}
