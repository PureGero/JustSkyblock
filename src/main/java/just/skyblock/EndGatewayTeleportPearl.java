package just.skyblock;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EndGatewayTeleportPearl implements Runnable {
    private final SkyblockPlugin plugin;
    private final EnderPearl pearl;

    public EndGatewayTeleportPearl(SkyblockPlugin plugin, EnderPearl pearl) {
        this.plugin = plugin;
        this.pearl = pearl;

        plugin.getServer().getScheduler().runTaskLater(plugin, this, 1);
    }

    @Override
    public void run() {
        Block block = pearl.getLocation().getBlock();

        if (block.getType() == Material.END_GATEWAY) {
            Player player = (Player) pearl.getShooter();

            player.teleport(Skyblock.load(player).getEndSpawnLocation(), PlayerTeleportEvent.TeleportCause.END_GATEWAY);

            pearl.remove();
        }

        if (pearl.isValid()) {
            plugin.getServer().getScheduler().runTaskLater(plugin, this, 1);
        }
    }
}
