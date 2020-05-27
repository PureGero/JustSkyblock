package just.skyblock.generator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class MainIslandGenerator implements IIslandGenerator {

    @Override
    public void generate(Chunk c, Random random) {
        // Biome
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                c.getWorld().setBiome(i | (c.getX() << 4), j | (c.getZ() << 4), Biome.SNOWY_TAIGA);
            }
        }

        // Skyblock
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    c.getBlock(7 + i, 62 + j, 7 + k).setType(j == 2 ? Material.GRASS_BLOCK : Material.DIRT);
                    c.getBlock(10 + i, 62 + j, 7 + k).setType(j == 2 ? Material.GRASS_BLOCK : Material.DIRT);
                    c.getBlock(7 + i, 62 + j, 10 + k).setType(j == 2 ? Material.GRASS_BLOCK : Material.DIRT);
                }
            }
        }
        c.getBlock(8, 62, 8).setType(Material.BEDROCK);

        // Tree
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                c.getBlock(6 + i, 70 + j, 12).setType(Material.OAK_LEAVES);
                c.getBlock(7, 70 + j, 11 + i).setType(Material.OAK_LEAVES);
            }
        }
        c.getBlock(6, 70, 11).setType(Material.OAK_LEAVES);
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                for (int k = 0; k < 2; k++) {
                    c.getBlock(7 + i, 68 + k, 12 + j).setType(Material.OAK_LEAVES);
                }
            }
        }

        c.getBlock(5, 68, 14).setType(Material.AIR);
        c.getBlock(9, 69, 10).setType(Material.AIR);

        for (int i = 0; i < 6; i++) {
            c.getBlock(7, 65 + i, 12).setType(Material.OAK_LOG);
        }

        // Chest
        c.getBlock(12, 65, 8).setType(Material.CHEST);
        Chest h = (Chest) c.getBlock(12, 65, 8).getState();
        Directional direction = (Directional) h.getBlockData();
        direction.setFacing(BlockFace.WEST);
        h.setBlockData(direction);

        for (Material m : new Material[]{
                Material.LAVA_BUCKET,
                Material.ICE
        }) {
            h.getInventory().addItem(new ItemStack(m, 1));
        }
    }

}
