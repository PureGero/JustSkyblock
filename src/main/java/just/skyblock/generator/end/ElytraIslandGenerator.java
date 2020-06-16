package just.skyblock.generator.end;

import just.skyblock.generator.LocationBasedIslandGenerator;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElytraIslandGenerator extends LocationBasedIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.END_HIGHLANDS;
    }

    @Override
    public void generate(Block center, Random random) {
        DefinedStructureManager definedStructureManager = ((CraftWorld) center.getWorld()).getHandle().r();

        List<StructurePiece> pieces = new ArrayList<>();

        WorldGenEndCityPieces.a(definedStructureManager, new BlockPosition(center.getX(), center.getY() + 1, center.getZ()), EnumBlockRotation.a(random), pieces, random);

        for (StructurePiece piece : pieces) {
            piece.a(((CraftWorld) center.getWorld()).getHandle(), null, random, new StructureBoundingBox(center.getX() - 256, center.getZ() - 256, center.getX() + 256, center.getZ() + 256), null);
        }
    }

    @Override
    public Block getCenterBlockLocation(Chunk chunk) {
        return chunk.getBlock(7, 64, 7);
    }

    @Override
    public boolean isIslandChunk(World world, int cx, int cz) {
        Location location = getNearestElytraIslandLocation(new Location(world, cx << 4, 64, cz << 4));

        return location.getBlockX() >> 4 == cx && location.getBlockZ() >> 4 == cz;
    }

    private static Location getNearestElytraIslandLocation(Location location) {
        int rx = location.getBlockX() >> 9;
        int rz = location.getBlockZ() >> 9;

        long seed = rx ^ ((long) rz << 32L) ^ 5294699836227729L;

        if (location.getWorld() != null) {
            seed ^= location.getWorld().getSeed();
        }

        Random random = new Random(seed);

        int x = random.nextInt(512) | (rx << 9);
        int z = random.nextInt(512) | (rz << 9);

        return new Location(location.getWorld(), x + 0.5, 64, z + 0.5);
    }
}
