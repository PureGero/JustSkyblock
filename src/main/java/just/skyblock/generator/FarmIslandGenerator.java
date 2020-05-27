package just.skyblock.generator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import java.util.Random;

public class FarmIslandGenerator implements IIslandGenerator {

    @Override
    public void generate(Chunk c, Random random) {
        // Biome
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                c.getWorld().setBiome(i | (c.getX() << 4), j | (c.getZ() << 4), Biome.PLAINS);
            }
        }

        // Skyblock
        Material[] crops = new Material[]{
                Material.WHEAT, Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS
        };

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                for (int j = 0; j < 3; j++)
                    c.getBlock(7 + i, 62 + j, 7 + k).setType(j == 2 ? Material.FARMLAND : Material.DIRT);
                if (i == 1 && k == 1) {
                    c.getBlock(7 + i, 62 + 2, 7 + k).setType(Material.GRASS_BLOCK);
                    c.getBlock(7 + i, 62 + 3, 7 + k).setType(Material.OAK_FENCE);
                    c.getBlock(7 + i, 62 + 4, 7 + k).setType(Material.TORCH);
                } else {
                    c.getBlock(7 + i, 62 + 3, 7 + k).setType(crops[random.nextInt(crops.length)]);
                }
            }
        }
    }

}
