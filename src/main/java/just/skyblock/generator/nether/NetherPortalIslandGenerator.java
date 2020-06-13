package just.skyblock.generator.nether;

import just.skyblock.generator.CenteredLocationBasedIslandGenerator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class NetherPortalIslandGenerator extends CenteredLocationBasedIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.NETHER;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -1; z <= 1; z++) {
                    center.getRelative(x, y, z).setType(Material.NETHERRACK);
                }
            }
        }

        center.getRelative(0, -2, 0).setType(Material.BEDROCK);

        // Portal Frame
        for(int x = -1; x <= 1; x++) {
            for (int y = 0; y < 4; y++) {
                center.getRelative(x, y, 0).setType(Material.OBSIDIAN);
            }
        }

        // Portal
        for(int y = 0; y < 2; y++) {
            center.getRelative(0, 1 + y, 0).setType(Material.NETHER_PORTAL);
        }
    }

    @Override
    public boolean isIslandChunk(World world, int cx, int cz) {
        return Math.floorMod(cx, 96) == 47 && Math.floorMod(cz, 96) == 47;
    }
}
