package just.skyblock.generator.end;

import just.skyblock.generator.GeneratorUtils;
import just.skyblock.generator.LocationBasedIslandGenerator;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ElytraIslandGenerator extends LocationBasedIslandGenerator {

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

        // Chest
        Block chestBlock = center.getRelative(0, 1, 0);

        chestBlock.setBlockData(GeneratorUtils.randomChestFacing(random));

        Chest chest = (Chest) chestBlock.getState();
        chest.getInventory().setItem(13, new ItemStack(Material.ELYTRA));

        // Shulker
        do {
            Block shulker = center.getRelative(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);

            if (shulker.isEmpty()) {
                center.getWorld().spawnEntity(shulker.getLocation(), EntityType.SHULKER);
                break;
            }
        } while (true);
    }

    @Override
    public Block getCenterBlockLocation(Chunk chunk) {
        return getNearestElytraIslandLocation(chunk.getBlock(8, 64, 8).getLocation()).getBlock();
    }

    @Override
    public boolean isIslandChunk(World world, int cx, int cz) {
        Location location = getNearestElytraIslandLocation(new Location(world, cx << 4, 64, cz << 4));

        return location.getBlockX() >> 4 == cx && location.getBlockZ() >> 4 == cz;
    }

    private static Location getNearestElytraIslandLocation(Location location) {
        int rx = location.getBlockX() >> 9;
        int rz = location.getBlockZ() >> 9;

        long seed = rx ^ ((long) rz << 32L) ^ 5294699836227729L;

        if (location.getWorld() != null) {
            seed ^= location.getWorld().getSeed();
        }

        Random random = new Random(seed);

        int x = random.nextInt(512) | (rx << 9);
        int z = random.nextInt(512) | (rz << 9);

        return new Location(location.getWorld(), x + 0.5, 64, z + 0.5);
    }
}
