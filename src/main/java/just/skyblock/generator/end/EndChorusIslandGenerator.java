package just.skyblock.generator.end;

import just.skyblock.generator.BaseIslandGenerator;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class EndChorusIslandGenerator extends BaseIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.END_HIGHLANDS;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -1; z <= 1; z++) {
                    center.getRelative(x, y, z).setType(Material.END_STONE);
                }
            }
        }

        // Chorus
        center.getWorld().generateTree(center.getRelative(random.nextInt(3) - 1, 1, random.nextInt(3) - 1).getLocation(), TreeType.CHORUS_PLANT);
    }

}
