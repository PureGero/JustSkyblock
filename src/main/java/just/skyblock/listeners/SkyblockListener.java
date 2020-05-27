package just.skyblock.listeners;

import just.skyblock.Crate;
import just.skyblock.Rank;
import just.skyblock.Skyblock;
import just.skyblock.SkyblockPlugin;
import just.skyblock.objectives.Objectives;
import net.minecraft.server.v1_15_R1.PacketPlayOutWorldBorder;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.*;

public class SkyblockListener implements Listener {
    private SkyblockPlugin plugin;

    public SkyblockListener(SkyblockPlugin b) {
        plugin = b;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void chat(AsyncPlayerChatEvent e) {
        Rank c = Skyblock.load(e.getPlayer().getUniqueId()).getRank();

        if (c != null && c.color != null) {
            e.setFormat(e.getFormat().replaceAll("\\%1\\$s", c.color + "[" + c.prefix + "] " + "\\%1\\$s" + ChatColor.RESET));
            //e.setFormat(e.getFormat().replaceAll("\\%2\\$s", c.chatcolor+"\\%2\\$s"));
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        // Remove player's skyblock data from memory
        plugin.getServer().getScheduler().runTask(plugin, () -> Skyblock.safeDispose(e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        /*f(e.getEntity() instanceof Monster){
            for(Entity y : e.getLocation().getChunk().getEntities()){
                if(y instanceof Monster){
                    if(y.getLocation().distanceSquared(e.getEntity().getLocation()) <= 5){
                        e.setCancelled(true);
                        break;
                    }
                }
            }
        }*/

        if (e.getEntityType() == EntityType.PIG_ZOMBIE && e.getLocation().getWorld().getEnvironment() == World.Environment.NETHER) {
            if (Math.random() < 0.01) { // Replace pig zombie with a wither skeleton
                e.setCancelled(true);
                e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.WITHER_SKELETON);
            } else if (Math.random() < 0.05) { // Replace pig zombie with a blaze
                e.setCancelled(true);
                e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.BLAZE);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent e) {
        if (e.getBlock().getWorld() == plugin.world) {
            if (e.getBlock().getType() == Material.CHEST
                    && Crate.isCrate(e.getBlock())) {
                e.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Skyblock i = Skyblock.get(e.getBlock().getLocation());
                    if (i != null)
                        Crate.removeCrate(i);
                    e.getBlock().setType(Material.AIR); // Just incase doesnt work
                }, 1);
            } else {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Location loc = e.getBlock().getLocation().add(0.5, 0.5, 0.5);
                    for (Item i : loc.getWorld().getEntitiesByClass(Item.class)) {
                        if (i.getLocation().distanceSquared(loc) < 0.25) { // Squared more efficient
                            i.setPickupDelay(0);
                            i.teleport(e.getPlayer().getEyeLocation());
                        }
                    }
                }, 1);
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Skyblock i = Skyblock.load(e.getPlayer().getUniqueId());
        if (i.inIsland(e.getFrom()) && !i.inIsland(e.getTo())) {
            i.lx = e.getFrom().getX();
            i.ly = e.getFrom().getY();
            i.lz = e.getFrom().getZ();
            i.lyaw = e.getFrom().getYaw();
            i.lpitch = e.getFrom().getPitch();
        } else if (!i.inIsland(e.getFrom()) && i.inIsland(e.getTo()) && i.lx != 0 && i.lz != 0) {
            e.setTo(new Location(e.getTo().getWorld(), i.lx, i.ly, i.lz, i.lyaw, i.lpitch));
            e.getPlayer().sendMessage(ChatColor.GOLD + "Teleporting to previous location...");
            i.lx = 0;
            i.lz = 0;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void playerTeleportUpdateWorldBorder(PlayerTeleportEvent e) {
        World world = e.getTo().getWorld();
        net.minecraft.server.v1_15_R1.WorldBorder worldBorder = new net.minecraft.server.v1_15_R1.WorldBorder();
        worldBorder.world = ((CraftWorld) e.getTo().getWorld()).getHandle();

        if (e.getTo().getWorld() == plugin.lobby) {
            worldBorder.setSize(6000000);
            worldBorder.setCenter(0, 0);
        } else if (world == plugin.world || world == plugin.nether) {
            int x = ((e.getTo().getBlockX() >> 9) << 9) + 256;
            int z = ((e.getTo().getBlockZ() >> 9) << 9) + 256;
            worldBorder.setSize(512);
            worldBorder.setCenter(x, z);
        }

        PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE);
        CraftPlayer player = (CraftPlayer) e.getPlayer();

        player.getHandle().playerConnection.sendPacket(packet);

        // Send it again after any world loading
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.getHandle().playerConnection.sendPacket(packet);
        }, 1L);
    }

    @EventHandler
    public void preventPlayerMovingBetweenIslands(PlayerMoveEvent e) {
        int fromrx = e.getFrom().getBlockX() >> 4 >> 5;
        int fromrz = e.getFrom().getBlockZ() >> 4 >> 5;
        int torx = e.getTo().getBlockX() >> 4 >> 5;
        int torz = e.getTo().getBlockZ() >> 4 >> 5;

        if (e.getFrom().getWorld() == e.getTo().getWorld()
                && (fromrx != torx || fromrz != torz)
                && e.getTo().distanceSquared(e.getFrom()) < 64) {
            e.getPlayer().sendMessage(ChatColor.RED + "You have reached the limit of your skyblock world");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (from.getWorld() == plugin.world && to.getWorld().getEnvironment() == World.Environment.NETHER) {
            e.setCancelled(true);
            e.getPlayer().teleport(Skyblock.get(from).getNetherSpawnLocation());
            Objectives.enterNether(Skyblock.load(e.getPlayer().getUniqueId())); // Enter Nether Objective
        } else if (from.getWorld() == plugin.nether && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
            e.setCancelled(true);
            e.getPlayer().teleport(Skyblock.get(from).getSpawnLocation());
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (from.getWorld() == plugin.world && to.getWorld().getEnvironment() == World.Environment.NETHER) {
            e.setCancelled(true);
            e.getEntity().teleport(Skyblock.get(from).getNetherSpawnLocation());
        } else if (from.getWorld() == plugin.nether && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
            e.setCancelled(true);
            e.getEntity().teleport(Skyblock.get(from).getSpawnLocation());
        }
    }
}
