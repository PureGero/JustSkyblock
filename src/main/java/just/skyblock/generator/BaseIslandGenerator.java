package just.skyblock.generator;

import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public abstract class BaseIslandGenerator {

    public abstract void generate(Block center, Random random);

    /**
     * Sets the biome with a default radius of 8 (diameter of 16)
     */
    protected void setBiome(Block block, Biome biome) {
        setBiome(block, biome, 8);
    }

    protected void setBiome(Block block, Biome biome, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    block.getWorld().setBiome(block.getX() + x, block.getY() + y, block.getZ() + z, biome);
                }
            }
        }
    }

}
