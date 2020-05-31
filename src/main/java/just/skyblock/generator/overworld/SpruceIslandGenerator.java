package just.skyblock.generator.overworld;

import just.skyblock.generator.BaseIslandGenerator;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class SpruceIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.SNOWY_TAIGA;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int i = -1; i <= 1; i++) {
            for (int k = -1; k <= 1; k++) {
                center.getRelative(i, -2, k).setType(Material.STONE);
                center.getRelative(i, -1, k).setType(Material.DIRT);
                center.getRelative(i, 0, k).setType(Material.GRASS_BLOCK);
                center.getRelative(i, 1, k).setType(Material.SNOW);
            }
        }

        int treeX = random.nextInt(3);
        int treeZ = random.nextInt(3);

        center.getRelative(treeX-1, 1, treeZ-1).setType(Material.AIR);
        center.getWorld().generateTree(center.getRelative(treeX - 1, 1, treeZ - 1).getLocation(), TreeType.REDWOOD);

        if (random.nextBoolean()) {
            center.getRelative(((treeX + 2) % 3) - 1, 1, treeZ - 1).setType(Material.SWEET_BERRY_BUSH);
        } else {
            center.getRelative(treeX - 1, 1, ((treeZ + 2) % 3) - 1).setType(Material.SWEET_BERRY_BUSH);
        }
    }
}
