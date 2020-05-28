package just.skyblock.generator.nether;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class NetherwartFortressIslandGenerator extends FortressBaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.NETHER;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int i = -1; i <= 1; i++) {
            for (int k = -1; k <= 1; k++) {
                center.getRelative(i, -2, k).setType(Material.NETHER_BRICKS);
                center.getRelative(i, -1, k).setType(Material.NETHER_BRICKS);
                center.getRelative(i, 0, k).setType(Material.SOUL_SAND);
                center.getRelative(i, 1, k).setType(Material.NETHER_WART);
            }
        }

        center.getRelative(0, 0, 0).setType(Material.LAVA);
        center.getRelative(0, 1, 0).setType(Material.AIR);
    }

}
