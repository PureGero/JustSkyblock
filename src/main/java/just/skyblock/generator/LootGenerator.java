package just.skyblock.generator;

import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.MinecraftKey;
import net.minecraft.server.v1_15_R1.TileEntityChest;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;

public enum LootGenerator {
    // Find loot table values in net.minecraft.server.LootTables.java
    DESERT_PYRAMID("chests/desert_pyramid");

    private final String key;

    LootGenerator(String key) {
        this.key = key;
    }

    public static void generateLoot(Block chest, long seed, LootGenerator lootGenerator) {
        TileEntityChest tileEntity = (TileEntityChest) ((CraftWorld) chest.getWorld()).getHandle().getTileEntity(new BlockPosition(chest.getX(), chest.getY(), chest.getZ()));

        if (tileEntity == null) {
            throw new IllegalArgumentException("Block must be a Chest!");
        }

        tileEntity.setLootTable(lootGenerator.getMinecraftKey(), seed);
    }

    private MinecraftKey getMinecraftKey() {
        return new MinecraftKey(key);
    }
}
