package just.skyblock.generator.nether;

import just.skyblock.generator.BaseIslandGenerator;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class NetherrackIslandGenerator extends BaseIslandGenerator {

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

        center.getRelative(0, 0, 0).setType(Material.LAVA);
    }

}
