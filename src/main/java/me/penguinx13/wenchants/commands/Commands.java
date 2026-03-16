package me.penguinx13.wenchants.commands;

import java.util.Map;
import me.penguinx13.wapi.commands.annotations.Arg;
import me.penguinx13.wapi.commands.annotations.Min;
import me.penguinx13.wapi.commands.annotations.RootCommand;
import me.penguinx13.wapi.commands.annotations.SubCommand;
import me.penguinx13.wapi.enchants.CustomEnchant;
import me.penguinx13.wapi.enchants.storage.EnchantStorage;
import me.penguinx13.wapi.enchants.util.LoreUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Demonstrates how to apply and inspect custom enchants through commands.
 */
@RootCommand("wenchants")
public final class Commands {

    private final EnchantStorage enchantStorage;
    private final CustomEnchant boerEnchant;

    public Commands(final EnchantStorage enchantStorage,
                                 final CustomEnchant boerEnchant) {
        this.enchantStorage = enchantStorage;
        this.boerEnchant = boerEnchant;
    }

    @SubCommand(value = "enchant add boer", playerOnly = true)
    public void addBoer(final Player sender, @Arg("level") @Min(1) final int level) {
        if (level > boerEnchant.getMaxLevel()) {
            sender.sendMessage("§cМаксимальный уровень: "
                    + boerEnchant.getMaxLevel());
            return;
        }

        final ItemStack item = sender.getInventory().getItemInMainHand();
        try {
            enchantStorage.addEnchant(item, boerEnchant, level);
            LoreUtil.updateLore(item, enchantStorage.getEnchants(item));
            sender.sendMessage("§aНаложено зачарование Boer " + level + ".");
        } catch (IllegalArgumentException ex) {
            sender.sendMessage("§cНе удалось наложить зачарование: " + ex.getMessage());
        }
    }

    @SubCommand(value = "enchant remove boer", playerOnly = true)
    public void removeBoer(final Player sender) {
        final ItemStack item = sender.getInventory().getItemInMainHand();
        enchantStorage.removeEnchant(item, boerEnchant);
        LoreUtil.updateLore(item, enchantStorage.getEnchants(item));
        sender.sendMessage("§eBoer удалён с предмета.");
    }

    @SubCommand(value = "enchant list", playerOnly = true)
    public void listMainHandEnchants(final Player sender) {
        final ItemStack item = sender.getInventory().getItemInMainHand();
        final Map<CustomEnchant, Integer> enchants = enchantStorage.getEnchants(item);
        if (enchants.isEmpty()) {
            sender.sendMessage("§7На предмете нет кастомных зачарований.");
            return;
        }
        sender.sendMessage("§6Кастомные зачарования предмета:");
        for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
            sender.sendMessage(" §e- §f" + entry.getKey().getId() + " " + entry.getValue());
        }
    }
}
