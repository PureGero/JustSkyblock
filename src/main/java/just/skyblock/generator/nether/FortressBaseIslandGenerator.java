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

    public static boolean isMainFortressChunk(long seed, int cx, int cz) {
        int bx = cx >> 4;
        int bz = cz >> 4;

        Random random = new Random((bx ^ bz << 4) ^ seed);
        random.nextInt();

        if (random.nextInt(3) != 0) {
            return false;
        }

        int h = (bx << 4) + 4 + random.nextInt(8);
        if (cx != h) {
            return false;
        }

        int i = (bz << 4) + 4 + random.nextInt(8);
        if (cz != i) {
            return false;
        }

        return true;
    }

}
