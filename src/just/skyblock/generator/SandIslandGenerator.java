package just.skyblock.generator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import java.util.Random;

public class SandIslandGenerator implements IIslandGenerator {

    @Override
    public void generate(Chunk c, Random random) {
        // Biome
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                c.getWorld().setBiome(i | (c.getX() << 4), j | (c.getZ() << 4), Biome.DESERT);
            }
        }

        // Skyblock
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    c.getBlock(7 + i, 62 + j, 7 + k).setType(j == 0 ? Material.SANDSTONE : Material.SAND);
                }
            }
        }

        c.getBlock(7, 65, 7).setType(Material.CACTUS);
        c.getBlock(8, 64, 8).setType(Material.WATER);
        c.getBlock(8, 65, 9).setType(Material.SUGAR_CANE);
    }

}
