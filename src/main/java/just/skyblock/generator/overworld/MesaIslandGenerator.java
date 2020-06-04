package just.skyblock.generator.overworld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import just.skyblock.generator.BaseIslandGenerator;

public class MesaIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.BADLANDS;
    }

    @Override
    public void generate(Block center, Random random) {
        
        Material[] terracotta = new Material[] {Material.BROWN_TERRACOTTA,
                                                Material.TERRACOTTA, 
                                                Material.WHITE_TERRACOTTA,
                                                Material.RED_TERRACOTTA, 
                                                Material.ORANGE_TERRACOTTA, 
                                                Material.YELLOW_TERRACOTTA};
        int rnd = random.nextInt((terracotta.length));
        
        Material a = terracotta[rnd];
        Material b = terracotta[Math.abs(rnd-1)];
        Material c = terracotta[Math.abs(rnd-2)];
        Material d = terracotta[Math.abs(rnd-3)];
        
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, -2, z).setType(a);
                center.getRelative(x, -1, z).setType(b);
                center.getRelative(x, 0, z).setType(c);
            }
        }

        for (int i = 0; i <= 4; i++) {
            int x = random.nextInt(3)-1;
            int z = random.nextInt(3)-1;
            center.getRelative(x, 1, z).setType(d);
        }
        
        for (int i = 0; i <= 4; i++) {
            int x = random.nextInt(3)-1;
            int z = random.nextInt(3)-1;
            if (center.getRelative(x, 1, z).getType() != Material.AIR) {
                center.getRelative(x, 2, z).setType(d);
            }  
        }
    }
    
    @Override
    public double getWeight() {
        return 0.2;
    }
}
