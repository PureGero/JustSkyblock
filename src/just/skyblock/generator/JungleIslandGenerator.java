package just.skyblock.generator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;

import java.util.Random;

public class JungleIslandGenerator implements IIslandGenerator {

    @Override
    public void generate(Chunk chunk, Random random) {
        // Biome
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunk.getWorld().setBiome(i | (chunk.getX() << 4), j | (chunk.getZ() << 4), Biome.JUNGLE);
            }
        }

        // Skyblock
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                chunk.getBlock(7 + i, 62, 7 + k).setType(Material.STONE);
                chunk.getBlock(7 + i, 63, 7 + k).setType(Material.DIRT);
                chunk.getBlock(7 + i, 64, 7 + k).setType(Material.GRASS_BLOCK);
            }
        }

        chunk.getWorld().generateTree(chunk.getBlock(7, 65, 7).getLocation(), TreeType.JUNGLE);

        chunk.getBlock(9, 65, 7).setType(Material.MELON);
        chunk.getBlock(7, 65, 9).setType(Material.BAMBOO_SAPLING);
    }

}
