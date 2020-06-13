package just.skyblock.generator;

import org.bukkit.Chunk;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public abstract class BaseIslandGenerator {

    public abstract Biome getBiome();

    public abstract void generate(Block center, Random random);

    public Block getCenterBlockLocation(Chunk chunk) {
        Random random = new Random(GeneratorUtils.hash(chunk.getWorld().getSeed(), chunk.getX(), chunk.getZ()) ^ 569);

        return chunk.getBlock(random.nextInt(16), 64, random.nextInt(16));
    }

    /**
     * Get the weight of the island to be generated in the randomiser.<br/>
     * The default weight is 1.<br/>
     * A weight of 0.5 will make the island generate half as frequently,<br/>
     * while a weight of 2 will double the generation frequency.
     */
    public double getWeight() {
        return 1;
    }

}
