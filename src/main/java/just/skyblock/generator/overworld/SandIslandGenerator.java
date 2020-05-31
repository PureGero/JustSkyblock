package just.skyblock.generator.overworld;

import just.skyblock.generator.BaseIslandGenerator;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class SandIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.DESERT;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -2; j <= 0; j++) {
                for (int k = -1; k <= 1; k++) {
                    center.getRelative(i, j, k).setType(j == -2 ? Material.SANDSTONE : Material.SAND);
                }
            }
        }

        center.getRelative(0, 0, 0).setType(Material.WATER);

        Material[] sandBlocks = new Material[] {
                Material.CACTUS,
                Material.SUGAR_CANE,
                Material.DEAD_BUSH
        };

        do {
            center.getRelative(random.nextInt(3) - 1, 1, random.nextInt(3) - 1).setType(sandBlocks[random.nextInt(sandBlocks.length)]);
        } while (random.nextDouble() < 0.75);

        center.getRelative(0, 1, 0).setType(Material.AIR);
    }

}
