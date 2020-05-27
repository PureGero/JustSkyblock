package just.skyblock;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Generator extends BlockPopulator {
    SkyblockPlugin skyblock;
    public Generator(SkyblockPlugin b){
        skyblock = b;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        try {
            if (world == skyblock.world) {
                if ((chunk.getX() & 31) == 15 && (chunk.getZ() & 31) == 15) {
                    if (empty(chunk)) {
                        System.out.println("Generating main island @ " + chunk);
                        genMainIsland(chunk);
                    }
                }
                if ((chunk.getX() & 31) == 13 && (chunk.getZ() & 31) == 15) {
                    if (empty(chunk)) {
                        System.out.println("Generating sand island @ " + chunk);
                        genSandIsland(chunk);
                    }
                }
                if ((chunk.getX() & 31) == 17 && (chunk.getZ() & 31) == 15) {
                    if (empty(chunk)) {
                        System.out.println("Generating farm island @ " + chunk);
                        genFarmIsland(chunk);
                    }
                }
                if ((chunk.getX() & 31) == 15 && (chunk.getZ() & 31) == 13) {
                    if (empty(chunk)) {
                        System.out.println("Generating jungle island @ " + chunk);
                        genJungleIsland(chunk);
                    }
                }
                if ((chunk.getX() & 31) == 15 && (chunk.getZ() & 31) == 17) {
                    if (empty(chunk)) {
                        System.out.println("Generating stone island @ " + chunk);
                        genStoneIsland(chunk);
                    }
                }
            } else if (world == skyblock.nether) {
                if ((chunk.getX() & 0x1F) == 0xF && (chunk.getZ() & 0x1F) == 0xF) {
                    if (empty(chunk)) {
                        System.out.println("Generating nether island @ " + chunk);
                        genNetherIsland(chunk);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean empty(Chunk c) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 60; y < 66; y++) {
                    if (c.getBlock(x, y, z).getType() != Material.AIR) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public void genMainIsland(Chunk c){
        // Biome
        for(int i=0;i<16;i++)
            for(int j=0;j<16;j++)
                c.getWorld().setBiome(i | (c.getX() << 4), j | (c.getZ() << 4), Biome.SNOWY_TAIGA);
        
        // Skyblock
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                for(int k=0;k<3;k++){
                    c.getBlock(7+i, 62+j, 7+k).setType(j==2?Material.GRASS_BLOCK:Material.DIRT);
                    c.getBlock(10+i, 62+j, 7+k).setType(j==2?Material.GRASS_BLOCK:Material.DIRT);
                    c.getBlock(7+i, 62+j, 10+k).setType(j==2?Material.GRASS_BLOCK:Material.DIRT);
                }
        c.getBlock(8, 62, 8).setType(Material.BEDROCK);
        
        // Tree
        for(int i=0;i<3;i++)
            for(int j=0;j<2;j++){
                c.getBlock(6+i, 70+j, 12).setType(Material.OAK_LEAVES);
                c.getBlock(7, 70+j, 11+i).setType(Material.OAK_LEAVES);
            }
        c.getBlock(6, 70, 11).setType(Material.OAK_LEAVES);
        for(int i=-2;i<=2;i++)
            for(int j=-2;j<=2;j++)
                for(int k=0;k<2;k++)
                    c.getBlock(7+i, 68+k, 12+j).setType(Material.OAK_LEAVES);
        c.getBlock(5, 68, 14).setType(Material.AIR);
        c.getBlock(9, 69, 10).setType(Material.AIR);
        for(int i=0;i<6;i++)
            c.getBlock(7, 65+i, 12).setType(Material.OAK_LOG);
        
        // Chest
        c.getBlock(12, 65, 8).setType(Material.CHEST);
        Chest h = (Chest) c.getBlock(12, 65, 8).getState();
        Directional direction = (Directional) h.getBlockData();
        direction.setFacing(BlockFace.WEST);
        h.setBlockData(direction);
        for(Material m : new Material[]{
                Material.LAVA_BUCKET, Material.ICE
        })
        h.getInventory().addItem(new ItemStack(m,1));
    }
    
    public void genSandIsland(Chunk c){
        // Biome
        for(int i=0;i<16;i++)
            for(int j=0;j<16;j++)
                c.getWorld().setBiome(i | (c.getX() << 4), j | (c.getZ() << 4), Biome.DESERT);
        
        // Skyblock
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                for(int k=0;k<3;k++)
                    c.getBlock(7+i, 62+j, 7+k).setType(j==0?Material.SANDSTONE:Material.SAND);
        c.getBlock(7, 65, 7).setType(Material.CACTUS);
        c.getBlock(8, 64, 8).setType(Material.WATER);
        c.getBlock(8, 65, 9).setType(Material.SUGAR_CANE);
    }

    public void genJungleIsland(Chunk c){
        // Biome
        for(int i=0;i<16;i++)
            for(int j=0;j<16;j++)
                c.getWorld().setBiome(i | (c.getX() << 4), j | (c.getZ() << 4), Biome.JUNGLE);
        
        // Skyblock
        for(int i=0;i<3;i++)
            for(int k=0;k<3;k++){
                c.getBlock(7+i, 62, 7+k).setType(Material.STONE);
                c.getBlock(7+i, 63, 7+k).setType(Material.DIRT);
                c.getBlock(7+i, 64, 7+k).setType(Material.GRASS_BLOCK);
                if(i < 2 && k < 2){
                    c.getBlock(7+i, 65, 7+k).setType(Material.JUNGLE_SAPLING);
                }
            }
        
        c.getBlock(9, 65, 7).setType(Material.MELON);
        c.getBlock(7, 65, 9).setType(Material.BAMBOO_SAPLING);
    }

    public void genStoneIsland(Chunk c){
        // Biome
        for(int i=0;i<16;i++)
            for(int j=0;j<16;j++)
                c.getWorld().setBiome(i | (c.getX() << 4), j | (c.getZ() << 4), Biome.MOUNTAINS);
        
        // Skyblock
        for(int i=0;i<3;i++)
            for(int k=0;k<3;k++)
                for(int j=0;j<3;j++)
                    c.getBlock(7+i, 62+j, 7+k).setType(Material.STONE);
        
        c.getBlock(8, 64, 8).setType(Material.LAVA);
        c.getBlock(9, 65, 9).setType(Material.PUMPKIN);
        c.getBlock(7, 65, 8).setType(Material.RED_MUSHROOM);
        c.getBlock(8, 65, 7).setType(Material.BROWN_MUSHROOM);
    }

    public void genFarmIsland(Chunk c){
        // Biome
        for(int i=0;i<16;i++)
            for(int j=0;j<16;j++)
                c.getWorld().setBiome(i | (c.getX() << 4), j | (c.getZ() << 4), Biome.PLAINS);
        
        // Skyblock
        Random r = new Random(31416);
        Material[] crops = new Material[]{
                Material.WHEAT, Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS
        };
        for(int i=0;i<3;i++)
            for(int k=0;k<3;k++){
                for(int j=0;j<3;j++)
                    c.getBlock(7+i, 62+j, 7+k).setType(j==2?Material.FARMLAND:Material.DIRT);
                if(i == 1 && k == 1){
                    c.getBlock(7+i, 62+2, 7+k).setType(Material.GRASS_BLOCK);
                    c.getBlock(7+i, 62+3, 7+k).setType(Material.OAK_FENCE);
                    c.getBlock(7+i, 62+4, 7+k).setType(Material.TORCH);
                }else
                    c.getBlock(7+i, 62+3, 7+k).setType(crops[r.nextInt(crops.length)]);
            }
        
        
    }

    private void genNetherIsland(Chunk c) {
        // Skyblock
        for(int i = 0; i < 3; i++)
            for(int k = 0; k < 5; k++)
                for(int j = 0; j < 3; j++)
                    c.getBlock(7 + i, 62 + j, 7 + k).setType(Material.NETHERRACK);
        c.getBlock(8, 62, 8).setType(Material.BEDROCK);

        // Nether brick
        for (int i = 0; i < 3; i++)
            c.getBlock(7 + i, 64, 7).setType(Material.NETHER_BRICKS);

        // Portal Frame
        for(int i = 0; i < 3; i++)
            for(int k = 0; k < 4; k++)
                c.getBlock(7 + i, 64 + k, 8).setType(Material.OBSIDIAN);

        // Portal
        for(int k = 0; k < 2; k++)
            c.getBlock(8, 65 + k, 8).setType(Material.NETHER_PORTAL);

        c.getBlock(8, 64, 10).setType(Material.LAVA);
        c.getBlock(9, 64, 11).setType(Material.SOUL_SAND);
        c.getBlock(9, 65, 11).setType(Material.NETHER_WART);
        c.getBlock(7, 64, 11).setType(Material.NETHER_QUARTZ_ORE);
        c.getBlock(7, 67, 7).setType(Material.GLOWSTONE);
    }
}
