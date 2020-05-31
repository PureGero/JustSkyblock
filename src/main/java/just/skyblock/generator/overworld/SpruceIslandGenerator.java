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
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, -2, z).setType(Material.STONE);
                center.getRelative(x, -1, z).setType(Material.DIRT);
                center.getRelative(x, 0, z).setType(Material.GRASS_BLOCK);
                center.getRelative(x, 1, z).setType(Material.SNOW);
            }
        }

        int treeX = random.nextInt(3) - 1;
        int treeZ = random.nextInt(3) - 1;

        center.getRelative(treeX, 1, treeZ).setType(Material.AIR);
        center.getWorld().generateTree(center.getRelative(treeX, 1, treeZ).getLocation(), TreeType.REDWOOD);

        Block berrybush;

        do { // Find free location for berry bush
            berrybush = center.getRelative(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
        } while (berrybush.getType() != Material.SNOW);

        berrybush.setType(Material.SWEET_BERRY_BUSH);
    }
}
