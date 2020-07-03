package just.skyblock.generator.overworld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.loot.LootTables;

import just.skyblock.generator.BaseIslandGenerator;
import just.skyblock.generator.GeneratorUtils;

public class WitchHuntIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.SWAMP;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                center.getRelative(x, -2, z).setType(Material.STONE);
                center.getRelative(x, -1, z).setType(Material.DIRT);
                center.getRelative(x, 0, z).setType(Material.GRASS_BLOCK);
            }
        }
        
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, 0, z).setType(Material.WATER);
            }
        }
        
        for (int y = 1; y <= 5; y++) {
            int arr[] = {-2, 2};
            for (int x : arr) {
                for (int z : arr) {
                    center.getRelative(x, y, z).setType(Material.SPRUCE_LOG);
                }
            }
        }
        
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 3; y <= 6; y = y+3) {
                    if (center.getRelative(x, y, z).getType() == Material.AIR) {
                        center.getRelative(x, y, z).setType(Material.SPRUCE_PLANKS);
                    }
                }
                
            }
        }
        
        for (int x = -3; x <= 3; x++ ) {
            for (int z = -3; z <= 3; z++) {
                if (center.getRelative(x, 6, z).getType() == Material.AIR) {
                    center.getRelative(x, 6, z).setType(Material.SPRUCE_SLAB);
                }
            }
        }

        for (int x = -2; x <= 2; x = x + 4) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, 4, z).setType(Material.SPRUCE_PLANKS);
                if (z != 0) {
                    center.getRelative(x, 5, z).setType(Material.SPRUCE_PLANKS);
                }
            }
        }
        
        for (int x = -1; x <= 1; x++) {
            for (int z = 3; z <= 4; z++) {
                center.getRelative(x, 3, z).setType(Material.SPRUCE_PLANKS);
            }
        }
        center.getRelative(2, 3, 3).setType(Material.SPRUCE_PLANKS);
        center.getRelative(-2, 3, 3).setType(Material.SPRUCE_PLANKS);
        center.getRelative(2, 4, 3).setType(Material.SPRUCE_FENCE);
        center.getRelative(-2, 4, 3).setType(Material.SPRUCE_FENCE);
        
        center.getRelative(0, 4, 2).setType(Material.SPRUCE_PLANKS);
        center.getRelative(-1, 4, 2).setType(Material.SPRUCE_PLANKS);
        center.getRelative(0, 5, 2).setType(Material.SPRUCE_PLANKS);
        
        center.getRelative(1, 4, -2).setType(Material.SPRUCE_PLANKS);
        center.getRelative(0, 4, -2).setType(Material.SPRUCE_PLANKS);
        center.getRelative(-1, 4, -2).setType(Material.SPRUCE_PLANKS);
        center.getRelative(1, 5, -2).setType(Material.SPRUCE_PLANKS);
        center.getRelative(-1, 5, -2).setType(Material.SPRUCE_PLANKS);
        
        for (int i = 0; i <= 1; i++) {
            int x = random.nextInt(2) - 1;
            int z = random.nextInt(2) - 1;
            center.getRelative(x, 1, z).setType(Material.LILY_PAD);
        }

        
        Material[] mushrooms = new Material[] {Material.BROWN_MUSHROOM,                
                Material.RED_MUSHROOM,};
                 
        for (int i = 0; i <= 2; i++) {
            int x = random.nextInt(5)-3;
            int z = random.nextInt(5)-3;
            if (center.getRelative(x, 4, z).getType() == Material.AIR && center.getRelative(x, 3, z).getType() == Material.SPRUCE_PLANKS) {
                center.getRelative(x, 4, z).setType(mushrooms[random.nextInt((mushrooms.length))]);;
            }
            if (center.getRelative(x, 5, z).getType() == Material.AIR && center.getRelative(x, 4, z).getType() == Material.SPRUCE_PLANKS) {
                center.getRelative(x, 5, z).setType(mushrooms[random.nextInt((mushrooms.length))]);;
            }
        } 
        
        center.getRelative(-1, 4, -1).setBlockData(GeneratorUtils.randomChestFacing(random));
        
        Chest chest = (Chest) center.getRelative(-1, 4, -1).getState();
        chest.setLootTable(LootTables.WOODLAND_MANSION.getLootTable());
        chest.update();
        
    }

    @Override
    public double getWeight() {
        return 0.1;
    }
}