package just.skyblock.generator.nether;

import just.skyblock.generator.BaseIslandGenerator;
import net.minecraft.server.v1_15_R1.Chunk;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;

import java.util.Random;

public abstract class FortressBaseIslandGenerator extends BaseIslandGenerator {

    public static boolean isInFortressStructure(Block center) {
        Chunk chunk = ((CraftChunk) center.getChunk()).getHandle();

        return !chunk.b("Fortress").isEmpty();
    }

}
