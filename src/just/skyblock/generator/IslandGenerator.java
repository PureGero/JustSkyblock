package just.skyblock.generator;

import just.skyblock.SkyblockPlugin;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class IslandGenerator extends BlockPopulator {
    private static final IIslandGenerator[] overworldIslandGenerators = {
            new FarmIslandGenerator(),
            new JungleIslandGenerator(),
            new SandIslandGenerator(),
            new StoneIslandGenerator()
    };

    SkyblockPlugin skyblock;
    public IslandGenerator(SkyblockPlugin b){
        skyblock = b;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        try {
            if (world == skyblock.world) {
                if ((chunk.getX() & 31) == 15 && (chunk.getZ() & 31) == 15) {
                    if (isChunkEmpty(chunk)) {
                        new MainIslandGenerator().generate(chunk, random);
                    }
                } else if (random.nextDouble() < 0.4 && (Math.abs((chunk.getX() & 31) - 15) > 1 || Math.abs((chunk.getZ() & 31) - 15) > 1)){
                    if (isChunkEmpty(chunk)) {
                        generateRandomIsland(chunk, random);
                    }
                }
            } else if (world == skyblock.nether) {
                if ((chunk.getX() & 0x1F) == 0xF && (chunk.getZ() & 0x1F) == 0xF) {
                    if (isChunkEmpty(chunk)) {
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

    private void generateRandomIsland(Chunk chunk, Random random) {
        IIslandGenerator islandGenerator = overworldIslandGenerators[random.nextInt(overworldIslandGenerators.length)];

        islandGenerator.generate(chunk, random);
    }

    private boolean isChunkEmpty(Chunk c) {
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
