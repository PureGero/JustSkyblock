package just.skyblock.generator.end;

import just.skyblock.generator.CenteredLocationBasedIslandGenerator;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class EndMainIslandGenerator extends CenteredLocationBasedIslandGenerator {

    @Override
    public Biome getBiome() {
        return Biome.THE_END;
    }

    @Override
    public void generate(Block center, Random random) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -1; z <= 1; z++) {
                    center.getRelative(x, y, z).setType(y == -2 ? Material.OBSIDIAN : Material.END_STONE);
                    center.getRelative(x + 3, y, z).setType(Material.END_STONE);
                    center.getRelative(x, y, z + 3).setType(Material.END_STONE);
                }
            }
        }

        // Tree
        center.getWorld().generateTree(center.getRelative(-1, 1, 4).getLocation(), TreeType.CHORUS_PLANT);


    }

    @Override
    public boolean isIslandChunk(World world, int cx, int cz) {
        return Math.floorMod(cx, 96) == 47 && Math.floorMod(cz, 96) == 47;
    }
}
