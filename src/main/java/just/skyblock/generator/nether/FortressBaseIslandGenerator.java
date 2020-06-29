package just.skyblock.generator.nether;

import just.skyblock.generator.BaseIslandGenerator;
import net.minecraft.server.v1_16_R1.Chunk;
import net.minecraft.server.v1_16_R1.StructureGenerator;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R1.CraftChunk;

public abstract class FortressBaseIslandGenerator extends BaseIslandGenerator {

    public static boolean isInFortressStructure(Block center) {
        Chunk chunk = ((CraftChunk) center.getChunk()).getHandle();

        return !chunk.b(StructureGenerator.FORTRESS).isEmpty();
    }

}
