package just.skyblock.generator.overworld;

import just.skyblock.generator.LocationBasedIslandGenerator;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.EndPortalFrame;

import java.util.Random;

public class EndPortalIslandGenerator extends LocationBasedIslandGenerator {

    private static final Material[] strongholdBlocks = {
            Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS, Material.CRACKED_STONE_BRICKS
    };

    @Override
    public Biome getBiome() {
        return Biome.SNOWY_MOUNTAINS;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -2; x <= 2; x++) {
            for (int y = -3; y <= -2; y++) {
                for (int z = -2; z <= 2; z++) {
                    center.getRelative(x, y, z).setType(strongholdBlocks[random.nextInt(strongholdBlocks.length)]);
                }
            }
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, -2, z).setType(Material.LAVA);
            }
        }

        boolean[] eyes = generateEyes(random);

        int j = 0;

        for (int i = -1; i <= 1; i++) {
            center.getRelative(2, 0, i).setBlockData(getEndPortalFrame(BlockFace.WEST, eyes[j++]));
            center.getRelative(-2, 0, i).setBlockData(getEndPortalFrame(BlockFace.EAST, eyes[j++]));
            center.getRelative(i, 0, 2).setBlockData(getEndPortalFrame(BlockFace.NORTH, eyes[j++]));
            center.getRelative(i, 0, -2).setBlockData(getEndPortalFrame(BlockFace.SOUTH, eyes[j++]));
        }
    }

    private boolean[] generateEyes(Random random) {
        boolean[] eyes = new boolean[12];

        int eyeCount = 0;

        for (int i = 0; i < eyes.length; i++) {
            eyes[i] = random.nextBoolean();

            if (eyes[i]) {
                eyeCount++;
            }
        }

        if (eyeCount == eyes.length) {
            // All the eyes are true, set one to false
            eyes[random.nextInt(eyes.length)] = false;
        }

        return eyes;
    }

    private BlockData getEndPortalFrame(BlockFace face, boolean hasEye) {
        EndPortalFrame endPortalFrame = (EndPortalFrame) Material.END_PORTAL_FRAME.createBlockData();
        endPortalFrame.setFacing(face);
        endPortalFrame.setEye(hasEye);
        return endPortalFrame;
    }

    @Override
    public Block getCenterBlockLocation(Chunk chunk) {
        return getNearestEndIslandLocation(chunk.getBlock(8, 64, 8).getLocation()).getBlock();
    }

    @Override
    public boolean isIslandChunk(World world, int cx, int cz) {
        Location location = getNearestEndIslandLocation(new Location(world, cx << 4, 64, cz << 4));

        return location.getBlockX() >> 4 == cx && location.getBlockZ() >> 4 == cz;
    }

    public static Location getNearestEndIslandLocation(Location location) {
        int rx = location.getBlockX() >> 9;
        int rz = location.getBlockZ() >> 9;

        long seed = rx ^ ((long) rz << 32L);

        if (location.getWorld() != null) {
            seed ^= location.getWorld().getSeed();
        }

        Random random = new Random(seed);

        int x = random.nextInt(512) | (rx << 9);
        int z = random.nextInt(512) | (rz << 9);

        return new Location(location.getWorld(), x + 0.5, 64, z + 0.5);
    }
}
