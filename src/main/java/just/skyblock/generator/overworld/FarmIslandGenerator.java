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

        for (int i = -1; i <= 1; i++) {
            for (int k = -1; k <= 1; k++) {
                for (int j = -2; j <= 0; j++) {
                    center.getRelative(i, j, k).setType(j == 0 ? Material.FARMLAND : Material.DIRT);
                }

                if (i == 0 && k == 0) {
                    center.getRelative(i, 0, k).setType(Material.WATER);
                    center.getRelative(i, 1, k).setType(Material.OAK_FENCE);
                    center.getRelative(i, 2, k).setType(Material.TORCH);
                } else {
                    center.getRelative(i, 1, k).setType(crops[random.nextInt(crops.length)]);
                }
            }
        }
    }

}
