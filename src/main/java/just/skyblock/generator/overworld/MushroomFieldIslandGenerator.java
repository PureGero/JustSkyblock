package just.skyblock.generator.overworld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import just.skyblock.generator.BaseIslandGenerator;

public class  MushroomFieldIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.MUSHROOM_FIELDS;
    }

    TreeType[] ForestTrees = new TreeType[] {TreeType.RED_MUSHROOM,
            TreeType.BROWN_MUSHROOM};
    
    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, -2, z).setType(Material.DIRT);
                center.getRelative(x, -1, z).setType(Material.DIRT);
                center.getRelative(x, 0, z).setType(Material.MYCELIUM);
            }
        }
        
        int treeX = random.nextInt(2);
        int treeZ = random.nextInt(2);

        center.getWorld().generateTree(center.getRelative(treeX - 1, 1, treeZ - 1).getLocation(), 
                ForestTrees[random.nextInt((ForestTrees.length))]);
    
        
        Material[] mushrooms = new Material[] {Material.BROWN_MUSHROOM,                
                Material.RED_MUSHROOM,};
                 
        for (int i = 0; i <= 3; i++) {
            int x = random.nextInt(3)-1;
            int z = random.nextInt(3)-1;
            if (center.getRelative(x, 1, z).getType() == Material.AIR) {
                center.getRelative(x, 1, z).setType(mushrooms[random.nextInt((mushrooms.length))]);;
            }
        } 
    }
    @Override
    public double getWeight() {
        return 0.1;
    }
}