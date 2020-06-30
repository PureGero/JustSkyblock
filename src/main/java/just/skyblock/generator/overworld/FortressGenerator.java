package just.skyblock.generator.overworld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.loot.LootTables;

import just.skyblock.generator.BaseIslandGenerator;
import just.skyblock.generator.GeneratorUtils;

public class FortressGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.NETHER_WASTES;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                center.getRelative(x, -2, z).setType(Material.NETHER_BRICKS);
                center.getRelative(x, -1, z).setType(Material.NETHER_BRICKS);
                center.getRelative(x, 0, z).setType(Material.NETHER_BRICKS);
                center.getRelative(x, 3, z).setType(Material.NETHER_BRICKS);
            }
        }
        
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, 0, z).setType(Material.LAVA);
            }
        }
        
        center.getRelative(1, 1, 2).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(1, 1, -2).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(-1, 1, 2).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(-1, 1, -2).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(2, 1, 1).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(2, 1, -1).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(-2, 1, 1).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(-2, 1, -1).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(1, 2, 2).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(1, 2, -2).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(-1, 2, 2).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(-1, 2, -2).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(2, 2, 1).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(2, 2, -1).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(-2, 2, 1).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(-2, 2, -1).setType(Material.NETHER_BRICK_FENCE);
        
        center.getRelative(2, 1, 2).setType(Material.NETHER_BRICKS);
        center.getRelative(2, 1, -2).setType(Material.NETHER_BRICKS);
        center.getRelative(-2, 1, 2).setType(Material.NETHER_BRICKS);
        center.getRelative(-2, 1, -2).setType(Material.NETHER_BRICKS);
        center.getRelative(2, 2, 2).setType(Material.NETHER_BRICKS);
        center.getRelative(2, 2, -2).setType(Material.NETHER_BRICKS);
        center.getRelative(-2, 2, 2).setType(Material.NETHER_BRICKS);
        center.getRelative(-2, 2, -2).setType(Material.NETHER_BRICKS);
        center.getRelative(0, 1, 2).setType(Material.NETHER_BRICKS);
        center.getRelative(0, 1, -2).setType(Material.NETHER_BRICKS);
        center.getRelative(2, 1, 0).setType(Material.NETHER_BRICKS);
        center.getRelative(-2, 1, 0).setType(Material.NETHER_BRICKS);
        center.getRelative(0, 2, 2).setType(Material.NETHER_BRICKS);
        center.getRelative(0, 2, -2).setType(Material.NETHER_BRICKS);
        center.getRelative(2, 2, 0).setType(Material.NETHER_BRICKS);
        center.getRelative(-2, 2, 0).setType(Material.NETHER_BRICKS);
        
        center.getRelative(0, 4, 2).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(0, 4, -2).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(2, 4, 0).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(-2, 4, 0).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(2, 4, 2).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(2, 4, -2).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(-2, 4, 2).setType(Material.NETHER_BRICK_FENCE);
        center.getRelative(-2, 4, -2).setType(Material.NETHER_BRICK_FENCE);
        
        center.getRelative(1, 4, 2).setType(Material.NETHER_BRICKS);
        center.getRelative(1, 4, -2).setType(Material.NETHER_BRICKS);
        center.getRelative(-1, 4, 2).setType(Material.NETHER_BRICKS);
        center.getRelative(-1, 4, -2).setType(Material.NETHER_BRICKS);
        center.getRelative(2, 4, 1).setType(Material.NETHER_BRICKS);
        center.getRelative(2, 4, -1).setType(Material.NETHER_BRICKS);
        center.getRelative(-2, 4, 1).setType(Material.NETHER_BRICKS);
        center.getRelative(-2, 4, -1).setType(Material.NETHER_BRICKS);
        
        if (random.nextInt(3) == 0) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    center.getRelative(x, 3, z).setType(Material.SOUL_SAND);
                    center.getRelative(x, 4, z).setType(Material.NETHER_WART);
                }
            }
        }
        
        if (random.nextInt(5) == 0) {
            center.getRelative(0, -1, 0).setBlockData(GeneratorUtils.randomChestFacing(random));
            
            Chest chest = (Chest) center.getRelative(0, -1, 0).getState();
            chest.setLootTable(LootTables.NETHER_BRIDGE.getLootTable());
            chest.update();
        }

    }
    
    @Override
    public double getWeight() {
        return 5;
    }
}