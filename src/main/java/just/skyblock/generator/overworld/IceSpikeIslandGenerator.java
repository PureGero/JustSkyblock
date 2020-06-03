package just.skyblock.generator.overworld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import just.skyblock.generator.BaseIslandGenerator;

public class IceSpikeIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.ICE_SPIKES;
    }

    @Override
    public void generate(Block center, Random random) {
        int rnd = random.nextInt(6);
        
        
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = -2; y <= 13 + rnd; y++)
                    center.getRelative(x, y, z).setType(Material.PACKED_ICE);
            }
        }
        
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, 0, z).setType(Material.BLUE_ICE);
            }
        }
        
        for (int y = 1; y <= 7 + rnd; y++) {
            for (int i = 0; i <=3; i++) {
                
                int arr[]={-1,1};
                center.getRelative(arr[random.nextInt(arr.length)], y, arr[random.nextInt(arr.length)]).setType(Material.AIR);
            }
        }
        
        for (int y = 10 + rnd; y <= 11 + rnd; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (Math.abs(x) == Math.abs(z)) {
                        continue;
                    }
                    center.getRelative(x, y, z).setType(Material.PACKED_ICE);
                }
            }
        }
        
        center.getRelative(0, 9 + rnd, 2).setType(Material.PACKED_ICE);
        center.getRelative(0, 9 + rnd, -2).setType(Material.PACKED_ICE);
        center.getRelative(2, 9 + rnd, 0).setType(Material.PACKED_ICE);
        center.getRelative(-2, 9 + rnd, 0).setType(Material.PACKED_ICE);
        
        center.getRelative(1, 14 + rnd, 0).setType(Material.PACKED_ICE);
        center.getRelative(-1, 14 + rnd, 0).setType(Material.PACKED_ICE);
        center.getRelative(0, 14 + rnd, 1).setType(Material.PACKED_ICE);
        center.getRelative(0, 14 + rnd, -1).setType(Material.PACKED_ICE);
        center.getRelative(0, 14 + rnd, 0).setType(Material.PACKED_ICE);
        center.getRelative(0, 15 + rnd, 0).setType(Material.PACKED_ICE);
        center.getRelative(0, 16 + rnd, 0).setType(Material.PACKED_ICE);

    }

    @Override
    public double getWeight() {
        return 0.1;
    }
}
