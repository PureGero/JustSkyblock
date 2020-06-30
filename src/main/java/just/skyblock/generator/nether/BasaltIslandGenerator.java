package just.skyblock.generator.nether;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import just.skyblock.generator.BaseIslandGenerator;

public class BasaltIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.BASALT_DELTAS;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, -2, z).setType(Material.BASALT);
                center.getRelative(x, -1, z).setType(Material.BASALT);
                center.getRelative(x, 0, z).setType(Material.BASALT);
            }
        }
        
        for (int i = 0; i <= 9; i++) {
            int x = random.nextInt(3) - 1;
            int z = random.nextInt(3) - 1;
            
            if (center.getRelative(x, 1, z).getType().equals(Material.AIR)) {
                for (int y = 1; y <= random.nextInt(9); y++) {
                    center.getRelative(x, y, z).setType(Material.BASALT);
                }
            }
        }

    }
}