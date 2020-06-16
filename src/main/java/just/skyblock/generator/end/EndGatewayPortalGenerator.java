package just.skyblock.generator.end;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class EndGatewayPortalGenerator extends EnderDragonIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.THE_END;
    }

    @Override
    public void generate(Block center, Random random) {
        center.getRelative(0, 2, 0).setType(Material.BEDROCK);
        center.getRelative(0, 0, 0).setType(Material.END_GATEWAY);
        center.getRelative(0, -2, 0).setType(Material.BEDROCK);

        for (int[] i : new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            center.getRelative(i[0], 1, i[1]).setType(Material.BEDROCK);
            center.getRelative(i[0], -1, i[1]).setType(Material.BEDROCK);
        }
    }

    @Override
    public boolean isIslandChunk(World world, int cx, int cz) {
        return 100 >> 4 == cx && 8 >> 4 == cz;
    }

    @Override
    public Block getCenterBlockLocation(Chunk chunk) {
        return chunk.getBlock(100 & 0xF, 50, 8 & 0xF);
    }

}
