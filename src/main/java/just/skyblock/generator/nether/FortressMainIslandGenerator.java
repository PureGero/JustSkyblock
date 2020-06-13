package just.skyblock.generator.nether;

import just.skyblock.generator.LocationBasedIslandGenerator;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;

import java.util.Random;

public class FortressMainIslandGenerator extends LocationBasedIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.NETHER;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -1; z <= 1; z++) {
                    center.getRelative(x, y, z).setType(Material.NETHER_BRICKS);
                }
            }
        }

        center.getRelative(0, 0, 0).setType(Material.LAVA);
        center.getRelative(0, 1, 0).setType(Material.AIR);

        createNetherFortress(center.getWorld(), center.getX(), center.getY(), center.getZ(), 64, 16);
    }

    /**
     * @param bukkitWorld The world to create the fortress in
     * @param x The block X
     * @param y The block Y
     * @param z The block Z
     * @param r The radius in the xz plane
     * @param rh The radius in the y plane (height radius)
     */
    private void createNetherFortress(org.bukkit.World bukkitWorld, int x, int y, int z, int r, int rh) {
        World world = ((CraftWorld) bukkitWorld).getHandle();

        WorldGenNether.a fortressStructureStart = new WorldGenNether.a(WorldGenerator.NETHER_BRIDGE, x >> 4, z >> 4, new StructureBoundingBox(x - r, y - rh, z - r, x + r, y + rh, z + r), 1, bukkitWorld.getSeed());
        fortressStructureStart.a(null, null, x >> 4, z >> 4, null);

        world.getChunkAt(x >> 4, z >> 4, ChunkStatus.EMPTY, false).a("Fortress", fortressStructureStart);

        StructureBoundingBox fortressBoundingBox = fortressStructureStart.c();

        for (int i = fortressBoundingBox.a >> 4; i <= fortressBoundingBox.d >> 4; i ++) {
            for (int j = fortressBoundingBox.c >> 4; j <= fortressBoundingBox.f >> 4; j ++) {
                world.getChunkAt(i, j, ChunkStatus.EMPTY, false).a("Fortress", ChunkCoordIntPair.pair(x >> 4, z >> 4));
            }
        }
    }

    @Override
    public boolean isIslandChunk(org.bukkit.World world, int cx, int cz) {
        int bx = cx >> 4;
        int bz = cz >> 4;

        Random random = new Random((bx ^ bz << 4) ^ world.getSeed());
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
