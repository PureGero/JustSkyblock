package just.skyblock.generator.overworld;

import just.skyblock.generator.BaseIslandGenerator;
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
    public Biome getBiome() {
        return Biome.SNOWY_TAIGA;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -1; z <= 1; z++) {
                    center.getRelative(x, y, z).setType(y == 0 ? Material.GRASS_BLOCK : Material.DIRT);
                    center.getRelative(x + 3, y, z).setType(y == 0 ? Material.GRASS_BLOCK : Material.DIRT);
                    center.getRelative(x, y, z + 3).setType(y == 0 ? Material.GRASS_BLOCK : Material.DIRT);
                }
            }
        }
        center.getRelative(0, -2, 0).setType(Material.BEDROCK);

        // Tree
        center.getWorld().generateTree(center.getRelative(-1, 1, 4).getLocation(), TreeType.TREE);

        // Chest
        Directional directionalChest = (Directional) Material.CHEST.createBlockData();
        directionalChest.setFacing(BlockFace.WEST);

        Block chest = center.getRelative(4, 1, 0);
        chest.setBlockData(directionalChest);

        Chest chestState = (Chest) chest.getState();

        for (Material itemType : new Material[]{
                Material.LAVA_BUCKET,
                Material.ICE
        }) {
            chestState.getInventory().addItem(new ItemStack(itemType));
        }
    }

}
