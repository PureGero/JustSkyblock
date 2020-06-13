package just.skyblock.generator.end;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;

import java.util.Random;

public class ExitPortalIslandGenerator extends EnderDragonIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.THE_END;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x + 1, 0, z).setType(Material.BEDROCK);
                center.getRelative(x - 1, 0, z).setType(Material.BEDROCK);
                center.getRelative(x, 0, z + 1).setType(Material.BEDROCK);
                center.getRelative(x, 0, z - 1).setType(Material.BEDROCK);
            }
        }

        EnderDragon dragon = center.getWorld().spawn(center.getLocation().add(0, 64, 0), EnderDragon.class);
        dragon.setPhase(EnderDragon.Phase.CIRCLING);
    }

    @Override
    public boolean isIslandChunk(World world, int cx, int cz) {
        return cx == 0 && cz == 0;
    }

    @Override
    public Block getCenterBlockLocation(Chunk chunk) {
        return chunk.getBlock(0, 64, 0);
    }
}
