package just.skyblock.generator.nether;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class NetherwartFortressIslandGenerator extends FortressBaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.NETHER_WASTES;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, -2, z).setType(Material.NETHER_BRICKS);
                center.getRelative(x, -1, z).setType(Material.NETHER_BRICKS);
                center.getRelative(x, 0, z).setType(Material.SOUL_SAND);
                center.getRelative(x, 1, z).setType(Material.NETHER_WART);
            }
        }

        center.getRelative(0, 0, 0).setType(Material.LAVA);
        center.getRelative(0, 1, 0).setType(Material.AIR);
    }

}
