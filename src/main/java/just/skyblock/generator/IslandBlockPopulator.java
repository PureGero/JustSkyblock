package just.skyblock.generator;

import just.skyblock.SkyblockPlugin;
import just.skyblock.generator.end.*;
import just.skyblock.generator.overworld.EndPortalIslandGenerator;
import just.skyblock.generator.nether.*;
import just.skyblock.generator.overworld.*;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class IslandBlockPopulator extends BlockPopulator {
    private static final BaseIslandGenerator[] overworldIslandGenerators = {

            // Location based
            new MainIslandGenerator(),
            new EndPortalIslandGenerator(),

            // Normal
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

            // Location based
            new NetherPortalIslandGenerator(),
            new FortressMainIslandGenerator(),

            // Normal
            new NetherrackIslandGenerator(),
    };

    private static final BaseIslandGenerator[] netherFortressIslandGenerators = {
            new NetherwartFortressIslandGenerator(),
    };

    private static final BaseIslandGenerator[] endIslandGenerators = {

            // Location based
            new EndMainIslandGenerator(),
            new ElytraIslandGenerator(),

            // Normal
            new EndBlankIslandGenerator(),
            new EndChorusIslandGenerator(),
    };

    private static final BaseIslandGenerator[] enderDragonFightIslandGenerators = {

            // Location based
            new ExitPortalIslandGenerator(),
            new EndCrystalIslandGenerator(),

            // Normal
            new EndBlankIslandGenerator(),
    };

    private SkyblockPlugin skyblock;

    public IslandBlockPopulator(SkyblockPlugin b){
        skyblock = b;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        try {
            BaseIslandGenerator islandGenerator = getIslandGenerator(world, chunk.getX(), chunk.getZ());

            if (islandGenerator != null) {
                Block center = islandGenerator.getCenterBlockLocation(chunk);

                if (!(islandGenerator instanceof LocationBasedIslandGenerator) && FortressBaseIslandGenerator.isInFortressStructure(center)) {
                    islandGenerator = getIslandGeneratorFromList(world, chunk.getX(), chunk.getZ(), netherFortressIslandGenerators,
                            new Random(GeneratorUtils.hash(world.getSeed(), chunk.getX(), chunk.getZ())));
                }

                islandGenerator.generate(center, random);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private BaseIslandGenerator getIslandGeneratorFromList(World world, int cx, int cz, BaseIslandGenerator[] generators, Random random) {
        double total = 0.0;
        for (BaseIslandGenerator generator : generators) {
            total += generator.getWeight();

            if (generator instanceof LocationBasedIslandGenerator && ((LocationBasedIslandGenerator) generator).isIslandChunk(world, cx, cz)) {
                return generator;
            }
        }

        double rnd = random.nextDouble()*total;
        int islandIndex = -1;
        for (int i = 0; i < generators.length; i++) {
            total -= generators[i].getWeight();
            if (total <= rnd) {
                islandIndex = i;
                break;
            }
        }

        return generators[islandIndex];
    }

    public BaseIslandGenerator getIslandGenerator(World world, int x, int z) {
        Random random = new Random(GeneratorUtils.hash(world.getSeed(), x, z));

        if (world == skyblock.world) {
            BaseIslandGenerator generator = getIslandGeneratorFromList(world, x, z, overworldIslandGenerators, random);

            if (generator instanceof LocationBasedIslandGenerator ||
                    ((Math.abs(Math.floorMod(x, 96) - 47) > 1 || Math.abs(Math.floorMod(z, 96) - 47) > 1) && random.nextDouble() < 0.4)) {
                return generator;
            }

        } else if (world == skyblock.nether) {
            BaseIslandGenerator generator = getIslandGeneratorFromList(world, x, z, netherIslandGenerators, random);

            if (generator instanceof LocationBasedIslandGenerator || random.nextDouble() < 0.6) {
                return generator;
            }

        } else if (world == skyblock.end) {
            BaseIslandGenerator generator = getIslandGeneratorFromList(world, x, z, endIslandGenerators, random);

            if (generator instanceof LocationBasedIslandGenerator || random.nextDouble() < 0.05) {
                return generator;
            }

        } else if (world == skyblock.enderDragonFight) {
            BaseIslandGenerator generator = getIslandGeneratorFromList(world, x, z, enderDragonFightIslandGenerators, random);

            if (generator instanceof LocationBasedIslandGenerator || random.nextDouble() < 0.1) {
                return generator;
            }
        }

        return null;
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
