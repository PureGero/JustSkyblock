package just.skyblock.generator.overworld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTables;

import just.skyblock.generator.BaseIslandGenerator;
import just.skyblock.generator.GeneratorUtils;

public class BastionGenerator extends BaseIslandGenerator {
    
    Material[] blackstone = new Material[] {Material.POLISHED_BLACKSTONE_BRICKS,
                                        Material.CRACKED_POLISHED_BLACKSTONE_BRICKS};
    
    LootTables[] lootTable = new LootTables[] {LootTables.BASTION_BRIDGE,
                                                LootTables.BASTION_TREASURE,
                                                LootTables.BASTION_OTHER,
                                                LootTables.BASTION_HOGLIN_STABLE};

    @Override
    public Biome getBiome() {
        return Biome.NETHER_WASTES;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, -1, z).setType(blackstone[random.nextInt((blackstone.length))]);
                center.getRelative(x, 0, z).setType(blackstone[random.nextInt((blackstone.length))]);
                center.getRelative(x, 1, z).setType(blackstone[random.nextInt((blackstone.length))]);
                center.getRelative(x, 5, z).setType(blackstone[random.nextInt((blackstone.length))]);
            }
        }

        center.getRelative(-1, -2, -1).setType(blackstone[random.nextInt((blackstone.length))]);
        center.getRelative(1, -2, 0).setType(blackstone[random.nextInt((blackstone.length))]);
        
        center.getRelative(-1, -1, 1).setType(Material.AIR);
        center.getRelative(1, -1, -1).setType(Material.AIR);
     
        center.getRelative(0, 1, 0).setType(Material.AIR);
        center.getRelative(1, 1, 0).setType(Material.AIR);
        center.getRelative(1, 1, 1).setType(Material.AIR);
        
        center.getRelative(-1, 2, -1).setType(blackstone[random.nextInt((blackstone.length))]);
        center.getRelative(-1, 2, 0).setType(blackstone[random.nextInt((blackstone.length))]);
        center.getRelative(0, 2, -1).setType(blackstone[random.nextInt((blackstone.length))]);
        
        center.getRelative(-1, 3, -1).setType(blackstone[random.nextInt((blackstone.length))]);
        center.getRelative(1, 3, 1).setType(blackstone[random.nextInt((blackstone.length))]);
        
        center.getRelative(1, 4, 1).setType(blackstone[random.nextInt((blackstone.length))]);
        center.getRelative(1, 4, 0).setType(blackstone[random.nextInt((blackstone.length))]);
        center.getRelative(0, 4, 1).setType(blackstone[random.nextInt((blackstone.length))]);
        
        center.getRelative(-1, 5, 1).setType(Material.AIR);
        center.getRelative(1, 5, -1).setType(Material.AIR);
        
        if (random.nextInt(2) == 0) {
            center.getRelative(0, 4, 0).setType(Material.LANTERN);
        }
        else {
            center.getRelative(0, 5, 0).setType(Material.LAVA);
            center.getRelative(0, 0, 0).setType(Material.AIR);
        }
        
        int rnd = random.nextInt(3);
        
        if (rnd == 0) {
            center.getRelative(1, 6, 1).setType(blackstone[random.nextInt((blackstone.length))]);
            center.getRelative(1, 6, 0).setType(blackstone[random.nextInt((blackstone.length))]);
            center.getRelative(0, 6, 1).setType(blackstone[random.nextInt((blackstone.length))]);
            center.getRelative(0, 6, 0).setType(blackstone[random.nextInt((blackstone.length))]);
            
            center.getRelative(1, 7, 1).setType(blackstone[random.nextInt((blackstone.length))]);
            center.getRelative(1, 7, 0).setType(blackstone[random.nextInt((blackstone.length))]);
            
            center.getRelative(1, 8, 1).setType(blackstone[random.nextInt((blackstone.length))]);
            
            center.getRelative(0, 7, 0).setBlockData(GeneratorUtils.randomChestFacing(random));
            
            Chest chest = (Chest) center.getRelative(0, 7, 0).getState();
            chest.setLootTable(lootTable[random.nextInt((lootTable.length))].getLootTable());
            chest.update();
        }
        
        if (rnd == 1) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    center.getRelative(x, 6, z).setType(blackstone[random.nextInt((blackstone.length))]);
                }
            }
            center.getRelative(0, 5, -2).setType(blackstone[random.nextInt((blackstone.length))]);
            center.getRelative(-2, 5, 1).setType(blackstone[random.nextInt((blackstone.length))]);
            center.getRelative(2, 5, -1).setType(blackstone[random.nextInt((blackstone.length))]);
            center.getRelative(0, 5, 2).setType(blackstone[random.nextInt((blackstone.length))]);
            
            center.getRelative(0, 7, 0).setType(Material.SPAWNER);

            CreatureSpawner spawner = (CreatureSpawner) center.getRelative(0, 7, 0).getState();
            spawner.setSpawnedType(EntityType.MAGMA_CUBE);
            spawner.update();
        }
        
        if (rnd == 2) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    center.getRelative(x, 6, z).setType(blackstone[random.nextInt((blackstone.length))]);
                }
            }
            
            center.getRelative(1, 7, 1).setType(blackstone[random.nextInt((blackstone.length))]);
            center.getRelative(-1, 7, -1).setType(blackstone[random.nextInt((blackstone.length))]);
            
            for (int i = 0; i <= 4; i++) {
                int x = random.nextInt(3)-1;
                int z = random.nextInt(3)-1;
                center.getRelative(x, 7, z).setType(Material.GOLD_BLOCK);
            }
            
            for (int i = 0; i <= 3; i++) {
                int x = random.nextInt(3)-1;
                int z = random.nextInt(3)-1;
                if (center.getRelative(x, 7, z).getType() != Material.AIR) {
                    center.getRelative(x, 8, z).setType(Material.GOLD_BLOCK);
                }  
            }
        }
        

    }
    
    @Override
    public double getWeight() {
        return 5;
    }
}
















