package just.skyblock.generator;

import org.bukkit.World;

public abstract class LocationBasedIslandGenerator extends BaseIslandGenerator {

    public abstract boolean isIslandChunk(World world, int cx, int cz);

    @Override
    public double getWeight() {
        // Location based islands shouldn't generate randomly, 0 weight
        return 0;
    }

}
