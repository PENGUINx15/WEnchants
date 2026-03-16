package me.penguinx13.wenchants.enchants;

import me.penguinx13.wapi.enchants.CustomEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class BoerEnchant extends CustomEnchant {

    public BoerEnchant() {
        super(
                "boer",
                3,
                true,
                false,
                Set.of(
                        Material.WOODEN_PICKAXE,
                        Material.STONE_PICKAXE,
                        Material.IRON_PICKAXE,
                        Material.GOLDEN_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.NETHERITE_PICKAXE
                )
        );
    }

    @Override
    public void onBlockBreak(Player player, ItemStack item, int level, Event e) {

        if (!(e instanceof BlockBreakEvent event)) {
            return;
        }

        if (player.isSneaking()) {
            return;
        }

        Block origin = event.getBlock();

        switch (level) {

            case 1 -> breakLine(player, origin, item);

            case 2 -> breakPlane(player, origin, item);

            case 3 -> breakCube(origin, item);

        }
    }

    /**
     * 1x3x1
     */
    private void breakLine(Player player, Block origin, ItemStack tool) {

        float yaw = player.getLocation().getYaw();

        boolean northSouth =
                (yaw > 315 || yaw <= 45) ||
                        (yaw > 135 && yaw <= 225);

        for (int i = -1; i <= 1; i++) {

            Block target = origin.getRelative(0, i, 0);

            breakOther(origin, target, tool);
        }
    }

    /**
     * 3x3x1
     */
    private void breakPlane(Player player, Block origin, ItemStack tool) {

        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        boolean horizontal = Math.abs(pitch) > 45;

        if (horizontal) {

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    breakOther(origin, origin.getRelative(x, 0, z), tool);
                }
            }

            return;
        }

        boolean northSouth =
                (yaw > 315 || yaw <= 45) ||
                        (yaw > 135 && yaw <= 225);

        if (northSouth) {

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    breakOther(origin, origin.getRelative(x, y, 0), tool);
                }
            }

        } else {

            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y <= 1; y++) {
                    breakOther(origin, origin.getRelative(0, y, z), tool);
                }
            }

        }
    }

    /**
     * 3x3x3
     */
    private void breakCube(Block origin, ItemStack tool) {

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {

                    breakOther(origin, origin.getRelative(x, y, z), tool);

                }
            }
        }
    }

    private void breakOther(Block origin, Block block, ItemStack item) {
        if (block.equals(origin)) {
            return;
        }

        block.breakNaturally(item);
    }
}
