package just.skyblock.generator.overworld;

import java.util.Random;

import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import just.skyblock.generator.BaseIslandGenerator;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;

public class CrimsonForestGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.CRIMSON_FOREST;
    }

    @Override
    public void generate(Block center, Random random) {
        
        Material[] plants = new Material[] {Material.CRIMSON_ROOTS,
                                            Material.CRIMSON_FUNGUS};
        
        Material[] ore = new Material[] {Material.NETHER_QUARTZ_ORE,
                                        Material.GLOWSTONE,
                                        Material.ANCIENT_DEBRIS,
                                        Material.NETHER_GOLD_ORE};
        
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                center.getRelative(x, -2, z).setType(Material.NETHERRACK);
                center.getRelative(x, -1, z).setType(Material.NETHERRACK);
                center.getRelative(x, 0, z).setType(Material.CRIMSON_NYLIUM);
            }
        }
        
        if (random.nextInt(3) + 1 == 1) {
            int treeX = random.nextInt(3) - 1;
            int treeZ = random.nextInt(3) - 1;
            
            center.getRelative(treeX, 1, treeZ).setType(Material.AIR);

            //center.getWorld().generateTree(center.getRelative(treeX, 1, treeZ).getLocation(), TreeType.REDWOOD);
            WorldServer world = ((CraftWorld) center.getWorld()).getHandle();
            BlockPosition pos = new BlockPosition(center.getX(), center.getY() + 1, center.getZ());

            WorldGenerator.HUGE_FUNGUS.generate(world, world.getStructureManager(), world.getChunkProvider().getChunkGenerator(), new Random(), pos, WorldGenFeatureHugeFungiConfiguration.c);
        }
        
        for (int i = 0; i < 4; i++) {
            int x = random.nextInt(3)-1;
            int z = random.nextInt(3)-1;
            
            if (center.getRelative(x, 1, z).getType().equals(Material.AIR)) {
                center.getRelative(x, 1, z).setType(plants[random.nextInt((plants.length))]);
            }
        }
        
        if (random.nextInt(3) + 1 == 1) {
            for (int i = 0; i < 4; i++) {
                int x = random.nextInt(3)-1;
                int z = random.nextInt(3)-1;
                
                if (center.getRelative(x, 1, z).getType().equals(Material.AIR)) {
                    center.getRelative(x, 0, z).setType(Material.NETHER_WART_BLOCK);
                }
            }
        }

        if (random.nextInt(3) + 1 == 1) {
            int x = random.nextInt(3)-1;
            int z = random.nextInt(3)-1;
            int oreindex = random.nextInt((ore.length));
            
            center.getRelative(x, random.nextInt(2)-2, z).setType(ore[oreindex]);
            
            for (int i = 0; i < 7; i++) {
                int xore = x + random.nextInt(3)-1;
                int zore = z + random.nextInt(3)-1;
                
                if (xore <= 1 && xore >= -1 && zore <= 1 && zore >= -1) {
                    center.getRelative(xore, random.nextInt(2)-2, zore).setType(ore[oreindex]);
                }
                
            }
        }
        
    }
    
    @Override
    public double getWeight() {
        return 5;
    }
}
