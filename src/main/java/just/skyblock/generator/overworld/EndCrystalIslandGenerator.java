package just.skyblock.generator.overworld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;

import just.skyblock.generator.BaseIslandGenerator;

public class EndCrystalIslandGenerator extends BaseIslandGenerator {

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
        
        center.getWorld().spawnEntity(center.getRelative(0, 25 + rnd, 0).getLocation().add(0.5, 0, 0.5), EntityType.ENDER_CRYSTAL);
        center.getWorld().getEntitiesByClass(EnderCrystal.class).forEach(c -> {
            c.setShowingBottom(true);
        });

    }

    @Override
    public double getWeight() {
        return 1;
    }
}
