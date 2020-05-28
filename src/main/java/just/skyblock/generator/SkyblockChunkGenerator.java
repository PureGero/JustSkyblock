package just.skyblock.generator;

import just.skyblock.SkyblockPlugin;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SkyblockChunkGenerator extends ChunkGenerator {
    private IslandBlockPopulator islandBlockPopulator;

    public SkyblockChunkGenerator(SkyblockPlugin plugin) {
        islandBlockPopulator = new IslandBlockPopulator(plugin);
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biomeGrid) {
        ChunkData chunkData = createChunkData(world);

        Biome biome = getDefaultBiome(world);

        BaseIslandGenerator islandGenerator = islandBlockPopulator.getIslandGenerator(world, x, z);

        if (islandGenerator != null) {
            biome = islandGenerator.getBiome();
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < chunkData.getMaxHeight(); j++) {
                for (int k = 0; k < 16; k++) {
                    biomeGrid.setBiome(i, j, k, biome);
                }
            }
        }

        return chunkData;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList(islandBlockPopulator);
    }

    private Biome getDefaultBiome(World world) {
        switch (world.getEnvironment()) {
            case NETHER:
                return Biome.NETHER;
            case THE_END:
                return Biome.THE_END;
            case NORMAL:
            default:
                return Biome.OCEAN;
        }
    }
}
