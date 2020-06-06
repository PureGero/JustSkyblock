package just.skyblock.generator;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Chest;

import java.util.Random;

public class GeneratorUtils {

    private static BlockFace[] blockFaces4 = {
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.NORTH,
            BlockFace.SOUTH
    };

    public static BlockData randomChestFacing(Random random) {
        Chest chest = (Chest) Material.CHEST.createBlockData();

        chest.setFacing(blockFaces4[random.nextInt(blockFaces4.length)]);

        return chest;
    }

}
