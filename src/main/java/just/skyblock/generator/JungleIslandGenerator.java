package just.skyblock.generator;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class JungleIslandGenerator extends BaseIslandGenerator {

    @Override
    public void generate(Block center, Random random) {
        // Biome
        setBiome(center, Biome.JUNGLE);

        // Skyblock
        for (int i = -1; i <= 1; i++) {
            for (int k = -1; k <= 1; k++) {
                center.getRelative(i, -2, k).setType(Material.STONE);
                center.getRelative(i, -1, k).setType(Material.DIRT);
                center.getRelative(i, 0, k).setType(Material.GRASS_BLOCK);
            }
        }

        int treeX = random.nextInt(2);
        int treeZ = random.nextInt(2);

        center.getWorld().generateTree(center.getRelative(treeX - 1, 1, treeZ - 1).getLocation(), TreeType.JUNGLE);

        if (random.nextBoolean()) {
            center.getRelative(((treeX + 2) % 3) - 1, 1, treeZ - 1).setType(Material.MELON);
        } else {
            center.getRelative(treeX - 1, 1, ((treeZ + 2) % 3) - 1).setType(Material.MELON);
        }

        center.getRelative(((treeX + 2) % 3) - 1, 1, ((treeZ + 2) % 3) - 1).setType(Material.BAMBOO_SAPLING);
    }

}
