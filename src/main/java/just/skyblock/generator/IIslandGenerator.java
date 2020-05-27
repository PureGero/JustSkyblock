package just.skyblock.generator;

import org.bukkit.Chunk;

import java.util.Random;

public interface IIslandGenerator {

    public void generate(Chunk chunk, Random random);

}
