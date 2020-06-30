package just.skyblock.generator.nether;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import just.skyblock.generator.BaseIslandGenerator;

public class SoulSandValleyGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.SOUL_SAND_VALLEY;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, -2, z).setType(Material.SOUL_SOIL);
                center.getRelative(x, -1, z).setType(Material.SOUL_SOIL);
                center.getRelative(x, 0, z).setType(Material.SOUL_SOIL);
            }
        }
        
        for (int i = 0; i <= 3; i++) {
            int x = random.nextInt(3) - 1;
            int z = random.nextInt(3) - 1;
            
            center.getRelative(x, 0, z).setType(Material.BONE_BLOCK);
            center.getRelative(x, 1, z).setType(Material.BONE_BLOCK);
            center.getRelative(x, 1, z).setType(Material.BONE_BLOCK);
            
            if (i == 0) {
                center.getRelative(x, 2, z).setType(Material.BONE_BLOCK);
            }
            
        }
        
        for (int i = 0; i <= 2; i++) {
            int x = random.nextInt(3) - 1;
            int z = random.nextInt(3) - 1;
            
            if (center.getRelative(x, 1, z).getType().equals(Material.AIR)) {
                center.getRelative(x, 1, z).setType(Material.SOUL_FIRE);
            }
        }

    }
}