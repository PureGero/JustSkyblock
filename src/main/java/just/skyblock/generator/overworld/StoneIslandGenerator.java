package just.skyblock.generator.overworld;

import just.skyblock.generator.BaseIslandGenerator;
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
        for (int x = -1; x <= 1; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -1; z <= 1; z++) {
                    center.getRelative(x, y, z).setType(Material.STONE);
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
