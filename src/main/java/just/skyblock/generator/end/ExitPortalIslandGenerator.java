package just.skyblock.generator.end;

import just.skyblock.generator.GeneratorUtils;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

        for (double a = 0; a < 2 * Math.PI; a += 2 * Math.PI / 16.0) {
            double x = Math.cos(a) * 3.5;
            double z = Math.sin(a) * 3.5;
            center.getRelative((int) x, 1, (int) z).setType(Material.BEDROCK);
        }

        for (int y = 1; y <= 4; y++) {
            center.getRelative(0, y, 0).setType(Material.BEDROCK);
        }

        center.getRelative(1, 3, 0).setBlockData(GeneratorUtils.torchFacing(BlockFace.EAST));
        center.getRelative(-1, 3, 0).setBlockData(GeneratorUtils.torchFacing(BlockFace.WEST));
        center.getRelative(0, 3, 1).setBlockData(GeneratorUtils.torchFacing(BlockFace.SOUTH));
        center.getRelative(0, 3, -1).setBlockData(GeneratorUtils.torchFacing(BlockFace.NORTH));

        EnderDragon dragon = center.getWorld().spawn(center.getLocation().add(0, 64, 0), EnderDragon.class);
        dragon.setPhase(EnderDragon.Phase.CIRCLING);
    }

    @Override
    public boolean isIslandChunk(World world, int cx, int cz) {
        return cx == 0 && cz == 0;
    }

    @Override
    public Block getCenterBlockLocation(Chunk chunk) {
        return chunk.getBlock(0, 62, 0);
    }
}
