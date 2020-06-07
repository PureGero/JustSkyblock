package just.skyblock.generator;

import just.skyblock.SkyblockPlugin;
import just.skyblock.generator.nether.*;
import just.skyblock.generator.overworld.*;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class IslandBlockPopulator extends BlockPopulator {
    private static final BaseIslandGenerator[] overworldIslandGenerators = {
            new FarmIslandGenerator(),
            new JungleIslandGenerator(),
            new SandIslandGenerator(),
            new StoneIslandGenerator(),
            new SpruceIslandGenerator(),
            new SandWellIslandGenerator(),
            new RedSandIsland(),
            new MesaIslandGenerator(),
            new FlowerIslandGenerator(),
            new IceSpikeIslandGenerator(),
            new DarkOakIslandGenerator(),
            new MushroomFieldIslandGenerator(),
            new DungeonIslandGenerator(),
            new SwampIslandGenerator()
    };

    private static final BaseIslandGenerator[] netherIslandGenerators = {
            new NetherrackIslandGenerator(),
    };

    private static final BaseIslandGenerator[] netherFortressIslandGenerators = {
            new NetherwartFortressIslandGenerator(),
    };

    private SkyblockPlugin skyblock;

    public IslandBlockPopulator(SkyblockPlugin b){
        skyblock = b;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        try {
            if (world == skyblock.world || world == skyblock.nether) {
                BaseIslandGenerator islandGenerator = getIslandGenerator(world, chunk.getX(), chunk.getZ());
                if (islandGenerator == null) {
                    return;
                } else if (islandGenerator instanceof MainIslandGenerator || islandGenerator instanceof NetherPortalIslandGenerator) {
                    islandGenerator.generate(chunk.getBlock(8, 64, 8), random);
                } else if (islandGenerator instanceof EndIslandGenerator) {
                    islandGenerator.generate(EndIslandGenerator.getNearestEndIslandLocation(chunk.getBlock(8, 64, 8).getLocation()).getBlock(), random);
                } else {
                    Block center = getIslandCenterBlock(chunk);
                    if (FortressBaseIslandGenerator.isInFortressStructure(center)) {
                        islandGenerator = getFortressIslandGenerator(world, chunk.getX(), chunk.getZ());
                    }
                    islandGenerator.generate(center, random);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private Block getIslandCenterBlock(Chunk chunk) {
        Random random = new Random(hash(chunk.getWorld().getSeed(), chunk.getX(), chunk.getZ()) ^ 569);

        return chunk.getBlock(random.nextInt(16), 64, random.nextInt(16));
    }

    public BaseIslandGenerator getIslandGenerator(World world, int x, int z) {
        Random random = new Random(hash(world.getSeed(), x, z));

        if (world == skyblock.world) {
            if (Math.floorMod(x, 96) == 47 && Math.floorMod(z, 96) == 47) {
                return new MainIslandGenerator();
            } else if (EndIslandGenerator.isEndIslandChunk(world, x, z)) {
                return new EndIslandGenerator();
            } else if ((Math.abs(Math.floorMod(x, 96) - 47) > 1 || Math.abs(Math.floorMod(z, 96) - 47) > 1)) {
                if (random.nextDouble() < 0.4) {
                    
                    double total = 0.0;
                    for (int i = 0; i < overworldIslandGenerators.length; i++) {
                        total += overworldIslandGenerators[i].getWeight();
                    }
                    Double rnd = random.nextDouble()*total;
                    int islandIndex = -1;
                    for (int i = 0; i < overworldIslandGenerators.length; i++) {
                        total -= overworldIslandGenerators[i].getWeight();
                        if (total <= rnd) {
                            islandIndex = i;
                            break;
                        }
                    }
                    return overworldIslandGenerators[islandIndex];
                    
                }
            }
        } else if (world == skyblock.nether) {
            if (Math.floorMod(x, 96) == 47 && Math.floorMod(z, 96) == 47) {
                return new NetherPortalIslandGenerator();
            } else if (FortressBaseIslandGenerator.isMainFortressChunk(world.getSeed(), x, z)) {
                return new FortressMainIslandGenerator();
            } else {
                if (random.nextDouble() < 0.6) {
                    return netherIslandGenerators[random.nextInt(netherIslandGenerators.length)];
                }
            }
        }

        return null;
    }

    public BaseIslandGenerator getFortressIslandGenerator(World world, int x, int z) {
        Random random = new Random(hash(world.getSeed(), x, z) ^ 85331);

        return netherFortressIslandGenerators[random.nextInt(netherFortressIslandGenerators.length)];
    }

    private int hash(long seed, int x, int y){
        int h = (int) seed + x*374761393 + y*668265263; //all constants are prime
        h = (h^(h >> 13))*1274126177;
        return h^(h >> 16);
    }

    /*private boolean isChunkEmpty(Chunk c) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 60; y < 100; y++) {
                    if (!c.getBlock(x, y, z).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }*/
}
