package just.skyblock.generator.overworld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import just.skyblock.generator.BaseIslandGenerator;

public class RedSandIsland extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.SAVANNA;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, -2, z).setType(Material.RED_SANDSTONE);
                center.getRelative(x, -1, z).setType(Material.RED_SAND);
                center.getRelative(x, 0, z).setType(Material.RED_SAND);
            }
        }

        center.getRelative(0, 0, 0).setType(Material.DIRT);
        center.getWorld().generateTree(center.getRelative(0, 1, 0).getLocation(), TreeType.ACACIA);
         
        int x = random.nextInt(3)-1;
        int z = random.nextInt(3)-1;
        
        if (x == 0 && z == 0) {  
        }
        else { center.getRelative(x , 1, z).setType(Material.DEAD_BUSH); 
        }
    }
}
