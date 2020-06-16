package just.skyblock.listeners;

import just.skyblock.SkyblockPlugin;
import org.bukkit.boss.DragonBattle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EnderDragonResetListener implements Listener {

    private final SkyblockPlugin skyblock;

    public EnderDragonResetListener(SkyblockPlugin skyblock) {
        this.skyblock = skyblock;

        resetEnderDragon();
    }

    private void resetEnderDragon() {
        DragonBattle dragonBattle = skyblock.end.getEnderDragonBattle();

        System.out.println(dragonBattle);
    }

    @EventHandler
    public void onTeleportEvent(PlayerTeleportEvent event) {

    }

}
