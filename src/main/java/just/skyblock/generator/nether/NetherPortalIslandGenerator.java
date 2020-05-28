package just.skyblock.generator.nether;

import just.skyblock.generator.BaseIslandGenerator;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class NetherPortalIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.NETHER;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -2; j <= 0; j++) {
                for (int k = -1; k <= 1; k++) {
                    center.getRelative(i, j, k).setType(Material.NETHERRACK);
                }
            }
        }

        center.getRelative(0, -2, 0).setType(Material.BEDROCK);

        // Portal Frame
        for(int i = -1; i <= 1; i++) {
            for (int k = 0; k < 4; k++) {
                center.getRelative(i, k, 0).setType(Material.OBSIDIAN);
            }
        }

        // Portal
        for(int k = 0; k < 2; k++) {
            center.getRelative(0, 1 + k, 0).setType(Material.NETHER_PORTAL);
        }
    }
}
