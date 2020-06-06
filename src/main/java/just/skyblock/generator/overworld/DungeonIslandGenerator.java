package just.skyblock.generator.overworld;

import just.skyblock.generator.BaseIslandGenerator;
import just.skyblock.generator.GeneratorUtils;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTables;

import java.util.Random;

public class DungeonIslandGenerator extends BaseIslandGenerator {

    private static final EntityType[] spawnerTypes = {
            EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER
    };

    @Override
    public Biome getBiome() {
        return Biome.MOUNTAINS;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -2; z <= 2; z++) {
                    center.getRelative(x, y, z).setType(random.nextDouble() < 0.2 ? Material.MOSSY_COBBLESTONE: Material.COBBLESTONE);
                }
            }
        }

        center.getRelative(0, 1, 0).setType(Material.SPAWNER);

        CreatureSpawner spawner = (CreatureSpawner) center.getRelative(0, 1, 0).getState();
        spawner.setSpawnedType(spawnerTypes[random.nextInt(spawnerTypes.length)]);
        spawner.update();

        for (int i = 0; i < 2; i++) {
            Block chestBlock;

            switch(random.nextInt(4)) {
                case 0:
                    chestBlock = center.getRelative(random.nextInt(5) - 2, 1, 2);
                    break;
                case 1:
                    chestBlock = center.getRelative(random.nextInt(5) - 2, 1, -2);
                    break;
                case 2:
                    chestBlock = center.getRelative(2, 1, random.nextInt(5) - 2);
                    break;
                case 3:
                default:
                    chestBlock = center.getRelative(-2, 1, random.nextInt(5) - 2);
                    break;
            }

            chestBlock.setBlockData(GeneratorUtils.randomChestFacing(random));

            Chest chest = (Chest) chestBlock.getState();
            chest.setLootTable(LootTables.SIMPLE_DUNGEON.getLootTable());
            chest.update();
        }
    }

    @Override
    public double getWeight() {
        return 0.1;
    }

}
