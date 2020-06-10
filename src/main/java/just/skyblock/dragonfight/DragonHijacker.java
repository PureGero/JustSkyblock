package just.skyblock.dragonfight;

import just.skyblock.SkyblockPlugin;
import net.minecraft.server.v1_15_R1.DragonControllerPhase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;

import java.util.Collection;

public class DragonHijacker implements Runnable, Listener {
    private final SkyblockPlugin plugin;

    public DragonHijacker(SkyblockPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getScheduler().runTaskTimer(plugin, this, 20L, 20L);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDragonPhaseChange(EnderDragonChangePhaseEvent event) {
        Bukkit.broadcastMessage("Ender dragon has changed from phase " + event.getCurrentPhase() + " to " + event.getNewPhase());
    }

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            Collection<EnderDragon> dragons = world.getEntitiesByClass(EnderDragon.class);

            for (EnderDragon dragon : dragons) {
                if (!(((CraftEntity) dragon).getHandle() instanceof SkyblockEnderDragon)) {
                    hijackDragon(dragon);
                }
            }
        }
    }

    private void hijackDragon(EnderDragon dragon) {
        plugin.getLogger().info("Hijacking dragon " + dragon);

        CraftWorld world = (CraftWorld) dragon.getWorld();

        SkyblockEnderDragon enderDragon = new SkyblockEnderDragon(world.getHandle(), new Location(world, 1000, 0, 0));

        enderDragon.setHealth((float) dragon.getHealth());
        enderDragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.HOLDING_PATTERN);

        enderDragon.getBukkitEntity().teleport(dragon.getLocation());

        world.getHandle().addEntity(enderDragon);

        dragon.remove();
    }
}
