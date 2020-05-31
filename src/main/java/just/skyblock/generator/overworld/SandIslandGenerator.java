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
        for (int x = -1; x <= 1; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -1; z <= 1; z++) {
                    center.getRelative(x, y, z).setType(y == -2 ? Material.SANDSTONE : Material.SAND);
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
