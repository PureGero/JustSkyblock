package just.skyblock.generator.overworld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import just.skyblock.generator.BaseIslandGenerator;

public class  DarkOakIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.DARK_FOREST;
    }

    TreeType[] ForestTrees = new TreeType[] {TreeType.DARK_OAK,
            TreeType.RED_MUSHROOM,
            TreeType.BROWN_MUSHROOM};
    
    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, -2, z).setType(Material.STONE);
                center.getRelative(x, -1, z).setType(Material.DIRT);
                center.getRelative(x, 0, z).setType(Material.GRASS_BLOCK);
            }
        }
        
        int treeX = random.nextInt(2);
        int treeZ = random.nextInt(2);

        center.getWorld().generateTree(center.getRelative(treeX - 1, 1, treeZ - 1).getLocation(), 
                ForestTrees[random.nextInt((ForestTrees.length))]);
    
        
        Material[] flowers = new Material[] {Material.DANDELION,
                Material.POPPY, 
                Material.BLUE_ORCHID,
                Material.ALLIUM, 
                Material.AZURE_BLUET, 
                Material.RED_TULIP,
                Material.ORANGE_TULIP,
                Material.WHITE_TULIP,
                Material.PINK_TULIP,
                Material.OXEYE_DAISY,
                Material.CORNFLOWER,
                Material.LILY_OF_THE_VALLEY,
                Material.SUNFLOWER,
                Material.LILAC,
                Material.ROSE_BUSH,
                Material.PEONY,
                Material.BROWN_MUSHROOM,
                Material.RED_MUSHROOM,
                Material.TALL_GRASS};
                 
        for (int i = 0; i <= 3; i++) {
            int x = random.nextInt(3)-1;
            int z = random.nextInt(3)-1;
            if (center.getRelative(x, 1, z).getType() == Material.AIR) {
                center.getRelative(x, 1, z).setType(flowers[random.nextInt((flowers.length))]);;
            }
        } 
    }
}