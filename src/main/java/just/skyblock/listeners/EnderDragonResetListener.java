package just.skyblock.listeners;

import just.skyblock.SkyblockPlugin;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.File;
import java.io.IOException;

public class EnderDragonResetListener implements Listener {

    private final SkyblockPlugin plugin;

    public EnderDragonResetListener(SkyblockPlugin plugin) {
        this.plugin = plugin;

        if (needsToBeReset()) {
            resetEnderDragon();
        } else {
            createEnderDragonWorld();
        }
    }

    private World getWorld() {
        return plugin.enderDragonFight != null ? plugin.enderDragonFight : Bukkit.getWorld("skyblock_ender_dragon_fight");
    }

    private void setWorld(World world) {
        plugin.enderDragonFight = world;

        plugin.setWorldSettings(getWorld());

        getWorld().getWorldBorder().setSize(256);
    }

    private boolean needsToBeReset() {
        File resetFile = new File("skyblock_ender_dragon_fight/needs_reset.txt");

        if (resetFile.isFile()) {
            long lastMod = resetFile.lastModified();

            if (System.currentTimeMillis() > lastMod + 60 * 60 * 1000L) {
                // Reset time has passed
                return true;
            } else {
                // Schedule a reset
                plugin.getServer().getScheduler().runTaskLater(plugin, this::resetEnderDragon, (System.currentTimeMillis() - lastMod + 60 * 60 * 1000L) / 50);
            }
        }

        return false;
    }

    private void createEnderDragonWorld() {
        setWorld(plugin.getServer().createWorld(
                new WorldCreator("skyblock_ender_dragon_fight")
                        .environment(World.Environment.THE_END)
                        .generator(plugin.skyblockChunkGenerator)));
    }

    private void resetEnderDragon() {
        if (getWorld() != null) {
            for (Player player : getWorld().getPlayers()) {
                player.teleport(plugin.lobby.getSpawnLocation());
            }

            Bukkit.unloadWorld(getWorld(), false);
        }

        recursiveDelete(new File("skyblock_ender_dragon_fight"));

        createEnderDragonWorld();

        Bukkit.broadcastMessage("The Ender Dragon has been reset!");
    }

    private void recursiveDelete(File file) {
        File[] files = file.listFiles();

        if (files != null) {
            for (File f : files) {
                recursiveDelete(f);
            }
        }

        if (!file.delete()) {
            plugin.getLogger().warning("Could not delete file " + file.getPath());
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof EnderDragon && entity.getWorld().equals(getWorld())) {
            EntityDamageEvent lastDamage = entity.getLastDamageCause();

            if (lastDamage instanceof EntityDamageByEntityEvent) {
                Entity killer = getOwner(((EntityDamageByEntityEvent) lastDamage).getDamager());
                Bukkit.broadcastMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "The Ender Dragon has been slain by " + killer.getName());
            } else {
                Bukkit.broadcastMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "The Ender Dragon has been slain");
            }

            try {
                new File("skyblock_ender_dragon_fight/needs_reset.txt").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            plugin.getServer().getScheduler().runTaskLater(plugin, this::resetEnderDragon, 60 * 60 * 20);
            Bukkit.broadcastMessage("The Ender Dragon will reset in 1 hour!");

            for (Player player : getWorld().getPlayers()) {
                player.sendMessage("!!! Do not leave anything important here as it will get deleted !!!");
            }

            getWorld().getBlockAt(0, 67, 0).setType(Material.DRAGON_EGG);
        }
    }

    private Entity getOwner(Entity damager) {
        if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Entity) {
            return getOwner((Entity) ((Projectile) damager).getShooter());
        }

        if (damager instanceof Tameable && ((Tameable) damager).getOwner() instanceof Entity) {
            return getOwner((Entity) ((Tameable) damager).getOwner());
        }

        return damager;
    }

}
