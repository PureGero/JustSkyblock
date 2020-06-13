package just.skyblock.listeners;

import just.skyblock.generator.SkyblockChunkGenerator;
import just.skyblock.generator.overworld.EndPortalIslandGenerator;
import org.bukkit.World;
import org.bukkit.entity.EnderSignal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EnderSignalListener implements Listener {

    public EnderSignalListener() {
    }

    @EventHandler()
    public void onEnderSignal(EntitySpawnEvent e) {
        if (e.getEntity() instanceof EnderSignal &&
                e.getLocation().getWorld() != null &&
                e.getLocation().getWorld().getGenerator() instanceof SkyblockChunkGenerator &&
                e.getLocation().getWorld().getEnvironment() == World.Environment.NORMAL) {

            ((EnderSignal) e.getEntity()).setTargetLocation(EndPortalIslandGenerator.getNearestEndIslandLocation(e.getLocation()));

        }
    }
}
