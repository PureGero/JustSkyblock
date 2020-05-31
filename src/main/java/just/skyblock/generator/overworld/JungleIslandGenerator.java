package just.skyblock.generator.overworld;

import just.skyblock.generator.BaseIslandGenerator;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class JungleIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.JUNGLE;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, -2, z).setType(Material.STONE);
                center.getRelative(x, -1, z).setType(Material.DIRT);
                center.getRelative(x, 0, z).setType(Material.GRASS_BLOCK);
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
