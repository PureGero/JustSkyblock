package just.skyblock.generator.overworld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTables;

import just.skyblock.generator.BaseIslandGenerator;
import just.skyblock.generator.GeneratorUtils;

public class FortressPlatformGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.NETHER_WASTES;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                center.getRelative(x, 0, z).setType(Material.NETHER_BRICKS);
                center.getRelative(x, 1, z).setType(Material.NETHER_BRICK_FENCE);
            }
        }
        
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, 1, z).setType(Material.AIR);
            }
        }
        
        center.getRelative(2, -1, 2).setType(Material.NETHER_BRICKS);
        center.getRelative(2, -1, -2).setType(Material.NETHER_BRICKS);
        center.getRelative(-2, -1, 2).setType(Material.NETHER_BRICKS);
        center.getRelative(-2, -1, -2).setType(Material.NETHER_BRICKS);
        center.getRelative(2, -2, 2).setType(Material.NETHER_BRICKS);
        center.getRelative(2, -2, -2).setType(Material.NETHER_BRICKS);
        center.getRelative(-2, -2, 2).setType(Material.NETHER_BRICKS);
        center.getRelative(-2, -2, -2).setType(Material.NETHER_BRICKS);
        
        Slab slab = (Slab) Material.NETHER_BRICK_SLAB.createBlockData();
        slab.setType(Slab.Type.TOP);
        center.getRelative(0, 0, 1).setBlockData(slab);
        center.getRelative(0, 0, -1).setBlockData(slab);
        center.getRelative(-1, 0, 0).setBlockData(slab);
        center.getRelative(1, 0, 0).setBlockData(slab);
        center.getRelative(0, 0, 0).setBlockData(slab);
        
        center.getRelative(1, -1, 2).setBlockData(slab);
        center.getRelative(1, -1, -2).setBlockData(slab);
        center.getRelative(-1, -1, 2).setBlockData(slab);
        center.getRelative(-1, -1, -2).setBlockData(slab);
        center.getRelative(2, -1, 1).setBlockData(slab);
        center.getRelative(-2, -1, 1).setBlockData(slab);
        center.getRelative(2, -1, -1).setBlockData(slab);
        center.getRelative(-2, -1, -1).setBlockData(slab);
        
        int rnd = random.nextInt(2);
        if (rnd == 0) {
            center.getRelative(0, 1, 0).setType(Material.SPAWNER);

            CreatureSpawner spawner = (CreatureSpawner) center.getRelative(0, 1, 0).getState();
            spawner.setSpawnedType(EntityType.BLAZE);
            spawner.update();
        }
        
        if (rnd == 1) {
            center.getRelative(0, 1, 0).setBlockData(GeneratorUtils.randomChestFacing(random));
            
            Chest chest = (Chest) center.getRelative(0, 1, 0).getState();
            chest.setLootTable(LootTables.NETHER_BRIDGE.getLootTable());
            chest.update();
        }

    }
    
    @Override
    public double getWeight() {
        return 5;
    }
}