package just.skyblock.generator;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class MainIslandGenerator extends BaseIslandGenerator {

    @Override
    public void generate(Block center, Random random) {
        // Biome
        setBiome(center, Biome.SNOWY_TAIGA);

        // Skyblock
        for (int i = -1; i <= 1; i++) {
            for (int j = -2; j <= 0; j++) {
                for (int k = -1; k <= 1; k++) {
                    center.getRelative(i, j, k).setType(j == 0 ? Material.GRASS_BLOCK : Material.DIRT);
                    center.getRelative(i + 3, j, k).setType(j == 0 ? Material.GRASS_BLOCK : Material.DIRT);
                    center.getRelative(i, j, k + 3).setType(j == 0 ? Material.GRASS_BLOCK : Material.DIRT);
                }
            }
        }
        center.getRelative(0, -2, 0).setType(Material.BEDROCK);

        // Tree
        center.getWorld().generateTree(center.getRelative(-1, 1, 4).getLocation(), TreeType.TREE);

        // Chest
        Block chest = center.getRelative(4, 1, 0);
        chest.setType(Material.CHEST);

        Chest chestState = (Chest) chest.getState();
        Directional direction = (Directional) chestState.getBlockData();
        direction.setFacing(BlockFace.WEST);
        chestState.setBlockData(direction);

        for (Material itemType : new Material[]{
                Material.LAVA_BUCKET,
                Material.ICE
        }) {
            chestState.getInventory().addItem(new ItemStack(itemType));
        }
    }

}
