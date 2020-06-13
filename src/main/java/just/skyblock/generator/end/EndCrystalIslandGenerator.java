package just.skyblock.generator.end;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;

import java.util.Random;

public class EndCrystalIslandGenerator extends EnderDragonIslandGenerator {

    private static final int CRYSTAL_COUNT = 10;
    private static final int CRYSTAL_RADIUS = 42;

    private static Location[] crystalLocations = new Location[CRYSTAL_COUNT];

    static {
        for (int i = 0; i < CRYSTAL_COUNT; i++) {
            double angle = Math.PI * 2 / CRYSTAL_COUNT * i;

            double x = CRYSTAL_RADIUS * Math.cos(angle);
            double y = 64;
            double z = CRYSTAL_RADIUS * Math.sin(angle);

            crystalLocations[i] = new Location(null, x, y, z);
        }
    }

    @Override
    public Biome getBiome() {
        return Biome.THE_END;
    }

    @Override
    public void generate(Block center, Random random) {
        
        
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                center.getRelative(x, -2, z).setType(Material.END_STONE);
                center.getRelative(x, -1, z).setType(Material.END_STONE);
                center.getRelative(x, 0, z).setType(Material.END_STONE);
            }
        }

        int rnd = random.nextInt(10);
        
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) == 2 && Math.abs(z) == 2) {
                }
                else {
                    for (int y = 1; y <= 23 + rnd; y++)
                        center.getRelative(x, y, z).setType(Material.OBSIDIAN);
                }
            }
        }
     
        center.getRelative(0, 24 + rnd, 0).setType(Material.BEDROCK);

        EnderCrystal enderCrystal = center.getWorld().spawn(center.getRelative(0, 25 + rnd, 0).getLocation().add(0.5, 0, 0.5), EnderCrystal.class);
        enderCrystal.setShowingBottom(true);

    }

    @Override
    public boolean isIslandChunk(World world, int cx, int cz) {
        for (Location crystalLocation : crystalLocations) {
            if (cx == crystalLocation.getBlockX() >> 4 && cz == crystalLocation.getBlockZ() >> 4) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Block getCenterBlockLocation(Chunk chunk) {
        for (Location crystalLocation : crystalLocations) {
            if (chunk.getX() == crystalLocation.getBlockX() >> 4 && chunk.getZ() == crystalLocation.getBlockZ() >> 4) {
                return chunk.getBlock(crystalLocation.getBlockX() & 0xF, crystalLocation.getBlockY(), crystalLocation.getBlockZ() & 0xF);
            }
        }

        // Hopefully someone will notice something's up if the crystal spawn at y=32
        return chunk.getBlock(8, 32, 8);
    }
}
