package just.skyblock.generator;

import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public abstract class BaseIslandGenerator {

    public abstract Biome getBiome();

    public abstract void generate(Block center, Random random);

}
