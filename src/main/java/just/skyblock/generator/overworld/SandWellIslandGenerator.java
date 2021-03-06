package just.skyblock.generator.overworld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.Slab;
import org.bukkit.loot.LootTables;

import just.skyblock.generator.BaseIslandGenerator;
import just.skyblock.generator.GeneratorUtils;

public class SandWellIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.DESERT;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                center.getRelative(x, -2, z).setType(Material.SANDSTONE);
                center.getRelative(x, -1, z).setType(Material.SAND);
                center.getRelative(x, 0, z).setType(Material.SAND);
            }
        }

        for (int x = -1; x<= 1; x=x+2) {
            for (int z = -1; z<= 1; z=z+2) {
                center.getRelative(x, 2, z).setType(Material.SANDSTONE);
                center.getRelative(x, 3, z).setType(Material.SANDSTONE);
            }
        }
        
        int arr[] = {-2, -1, 1, 2};
        for (int x : arr) {
            for (int z : arr) {
                center.getRelative(x, 1, z).setType(Material.SANDSTONE);
            }
        }
        
        
        Slab slab = (Slab) Material.SANDSTONE_SLAB.createBlockData();
        slab.setType(Slab.Type.TOP);
        
        for(int x = -1; x <= 1; x++) {
            for(int z = -1; z <= 1; z++) {
                if(Math.abs(x) == Math.abs(z)) {
                    continue;
                }
                center.getRelative(x, 3, z).setBlockData(slab);
                center.getRelative(x, 0, z).setType(Material.RED_SAND);
                center.getRelative(x, -1, z).setType(Material.RED_SAND);
            }
        }
        
        center.getRelative(0, 3, 0).setBlockData(slab);
        center.getRelative(0, 4, 0).setType(Material.SANDSTONE_SLAB);
        
        center.getRelative(2, 1, 0).setType(Material.SANDSTONE_SLAB);
        center.getRelative(-2, 1, 0).setType(Material.SANDSTONE_SLAB);
        center.getRelative(0, 1, 2).setType(Material.SANDSTONE_SLAB);
        center.getRelative(0, 1, -2).setType(Material.SANDSTONE_SLAB);
        
        center.getRelative(0, 0, 0).setType(Material.WATER);

        center.getRelative(0, -1, 0).setBlockData(GeneratorUtils.randomChestFacing(random));

        Chest chest = (Chest) center.getRelative(0, -1, 0).getState();
        chest.setLootTable(LootTables.DESERT_PYRAMID.getLootTable());
        chest.update();
        
    }

    @Override
    public double getWeight() {
        return 0.1;
    }
}
