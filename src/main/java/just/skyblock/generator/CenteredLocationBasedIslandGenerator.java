package just.skyblock.generator;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

public abstract class CenteredLocationBasedIslandGenerator extends LocationBasedIslandGenerator {

    @Override
    public Block getCenterBlockLocation(Chunk chunk) {
        return chunk.getBlock(8, 64, 8);
    }

}
