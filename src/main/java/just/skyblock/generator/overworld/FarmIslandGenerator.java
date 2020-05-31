package just.skyblock.generator.overworld;

import just.skyblock.generator.BaseIslandGenerator;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class FarmIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.PLAINS;
    }

    @Override
    public void generate(Block center, Random random) {
        Material[] crops = new Material[]{
                Material.WHEAT, Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS
        };

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = -2; y <= 0; y++) {
                    center.getRelative(x, y, z).setType(y == 0 ? Material.FARMLAND : Material.DIRT);
                }

                if (x == 0 && z == 0) {
                    center.getRelative(x, 0, z).setType(Material.WATER);
                    center.getRelative(x, 1, z).setType(Material.OAK_FENCE);
                    center.getRelative(x, 2, z).setType(Material.TORCH);
                } else {
                    center.getRelative(x, 1, z).setType(crops[random.nextInt(crops.length)]);
                }
            }
        }
    }

}
