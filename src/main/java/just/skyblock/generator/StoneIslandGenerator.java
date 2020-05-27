package just.skyblock.generator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import java.util.Random;

public class StoneIslandGenerator implements IIslandGenerator {

    @Override
    public void generate(Chunk c, Random random) {
        // Biome
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                c.getWorld().setBiome(i | (c.getX() << 4), j | (c.getZ() << 4), Biome.MOUNTAINS);
            }
        }

        // Skyblock
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                for (int j = 0; j < 3; j++) {
                    c.getBlock(7 + i, 62 + j, 7 + k).setType(Material.STONE);
                }
            }
        }

        c.getBlock(8, 64, 8).setType(Material.LAVA);
        c.getBlock(9, 65, 9).setType(Material.PUMPKIN);
        c.getBlock(7, 65, 8).setType(Material.RED_MUSHROOM);
        c.getBlock(8, 65, 7).setType(Material.BROWN_MUSHROOM);
    }

}
