package just.skyblock.generator;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class StoneIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.SNOWY_MOUNTAINS;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -2; j <= 0; j++) {
                for (int k = -1; k <= 1; k++) {
                    center.getRelative(i, j, k).setType(Material.STONE);
                }
            }
        }

        center.getRelative(0, 0, 0).setType(Material.LAVA);

        do {
            center.getRelative(random.nextInt(3) - 1, 1, random.nextInt(3) - 1).setType(Material.PUMPKIN);
            center.getRelative(random.nextInt(3) - 1, 1, random.nextInt(3) - 1).setType(Material.RED_MUSHROOM);
            center.getRelative(random.nextInt(3) - 1, 1, random.nextInt(3) - 1).setType(Material.BROWN_MUSHROOM);
        } while (random.nextDouble() < 0.5);

        center.getRelative(0, 1, 0).setType(Material.AIR);
    }

}
