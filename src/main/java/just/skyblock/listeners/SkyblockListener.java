package just.skyblock.listeners;

import just.skyblock.*;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.PacketPlayOutWorldBorder;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;

public class SkyblockListener implements Listener {
    private SkyblockPlugin plugin;

    public SkyblockListener(SkyblockPlugin plugin) {
        this.plugin = plugin;
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
    public void onBlockBreak(BlockBreakEvent e) {
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
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void pickupBlockDropsInstantly(BlockBreakEvent e) {
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

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Skyblock skyblock = Skyblock.load(e.getPlayer());

        if (skyblock.inIsland(e.getFrom()) && !skyblock.inIsland(e.getTo())) {
            if (e.getTo().getWorld() == plugin.enderDragonFight) {
                skyblock.lx = 0;
                skyblock.lz = 0;
            } else {
                skyblock.lworld = e.getFrom().getWorld().getName();
                skyblock.lx = e.getFrom().getX();
                skyblock.ly = e.getFrom().getY();
                skyblock.lz = e.getFrom().getZ();
                skyblock.lyaw = e.getFrom().getYaw();
                skyblock.lpitch = e.getFrom().getPitch();
            }

        } else if (!skyblock.inIsland(e.getFrom()) && skyblock.inIsland(e.getTo()) && skyblock.lx != 0 && skyblock.lz != 0) {
            if (skyblock.lworld != null && Bukkit.getWorld(skyblock.lworld) != null) {
                e.setTo(new Location(Bukkit.getWorld(skyblock.lworld), skyblock.lx, skyblock.ly, skyblock.lz, skyblock.lyaw, skyblock.lpitch));
                e.getPlayer().sendMessage(ChatColor.GOLD + "Teleporting to previous location...");
            }

            skyblock.lx = 0;
            skyblock.lz = 0;
            skyblock.teleportToLastPos = false;
        }

        if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            e.setCancelled(true);

            if (skyblock.enderDragonsKilled == 0) {
                e.getPlayer().sendMessage(ChatColor.RED + "You must kill the ender dragon before using the end gateway.");
                e.getPlayer().playSound(e.getFrom(), Sound.BLOCK_ANVIL_LAND, 1f, 1f);
                return;
            }

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                e.getPlayer().teleport(skyblock.getEndSpawnLocation());
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinUpdateWorldBorder(PlayerJoinEvent e) {
        updateWorldBorder(e.getPlayer(), e.getPlayer().getLocation());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void playerTeleportUpdateWorldBorder(PlayerTeleportEvent e) {
        updateWorldBorder(e.getPlayer(), e.getTo());
    }

    private void updateWorldBorder(Player bukkitPlayer, Location location) {
        World world = location.getWorld();
        net.minecraft.server.v1_16_R1.WorldBorder worldBorder = new net.minecraft.server.v1_16_R1.WorldBorder();
        worldBorder.world = ((CraftWorld) location.getWorld()).getHandle();

        Skyblock skyblock = Skyblock.get(location);

        if (skyblock == null) {
            worldBorder.setSize(world.getWorldBorder().getSize());
            worldBorder.setCenter(0, 0);
        } else if (skyblock.inSmallIsland(location)) {
            int x = ((location.getBlockX() >> 9) << 9) | 256;
            int z = ((location.getBlockZ() >> 9) << 9) | 256;
            worldBorder.setSize(512);
            worldBorder.setCenter(x, z);
        } else {
            int x = skyblock.x * 1536 + 768;
            int z = skyblock.z * 1536 + 768;
            worldBorder.setSize(1536);
            worldBorder.setCenter(x, z);
        }

        PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE);
        CraftPlayer player = (CraftPlayer) bukkitPlayer;

        player.getHandle().playerConnection.sendPacket(packet);

        // Send it again after any world loading
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.getHandle().playerConnection.sendPacket(packet), 1L);
    }

    @EventHandler
    public void preventPlayerMovingBetweenIslands(PlayerMoveEvent e) {
        Skyblock from = Skyblock.get(e.getFrom());
        Skyblock to = Skyblock.get(e.getTo());

        if (e.getFrom().getWorld() == e.getTo().getWorld()
                && to != from
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
            e.setTo(new Location(plugin.nether, from.getX(), from.getY(), from.getZ()));
            Objective.ENTER_NETHER.give(e.getPlayer()); // Enter Nether Objective
        } else if (from.getWorld() == plugin.world && to.getWorld().getEnvironment() == World.Environment.THE_END) {
            e.setTo(new Location(plugin.enderDragonFight, 100, 50, 0));
        } else if (from.getWorld() == plugin.nether) {
            e.setTo(new Location(plugin.world, from.getX(), from.getY(), from.getZ()));
        }
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent e) {
        for (BlockState state : e.getBlocks()) {
            state.update(true, false);
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (from.getWorld() == plugin.world && to.getWorld().getEnvironment() == World.Environment.NETHER) {
            e.setTo(new Location(plugin.nether, from.getX(), from.getY(), from.getZ()));
        } else if (from.getWorld() == plugin.world && to.getWorld().getEnvironment() == World.Environment.THE_END) {
            e.setTo(new Location(plugin.enderDragonFight, 100, 50, 0));
        } else if (from.getWorld() == plugin.nether) {
            e.setTo(new Location(plugin.world, from.getX(), from.getY(), from.getZ()));
        }
    }

    @EventHandler
    public void onProjectileThrow(ProjectileLaunchEvent e) {
        if (e.getEntity() instanceof EnderPearl && e.getEntity().getWorld() == plugin.enderDragonFight) {
            EnderPearl pearl = (EnderPearl) e.getEntity();
            new EndGatewayTeleportPearl(plugin, pearl);
        }
    }

    @EventHandler
    public void onWalkIntoEndGateway(PlayerMoveEvent e) {
        if (e.getTo() != null && (e.getTo().getBlockX() != e.getFrom().getBlockX() || e.getTo().getBlockZ() != e.getFrom().getBlockZ())
                && e.getTo().getBlock().getType() == Material.END_GATEWAY) {
            e.getPlayer().teleport(Skyblock.load(e.getPlayer()).getEndSpawnLocation(), PlayerTeleportEvent.TeleportCause.END_GATEWAY);
        }
    }
}
