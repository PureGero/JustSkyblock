package just.skyblock;

import net.minecraft.server.v1_15_R1.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_15_R1.WorldBorder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import puregero.network.VoteEvent;

import java.util.HashMap;
import java.util.UUID;

public class Listener implements org.bukkit.event.Listener {
    SkyblockPlugin skyblock;

    public Listener(SkyblockPlugin b) {
        skyblock = b;
    }

    @EventHandler
    public void onVote(VoteEvent e) {
        e.getPlayer().sendMessage(ChatColor.GREEN + "Thank you for voting for us at " + e.getWebsite() + ".");
        e.getPlayer().sendMessage(ChatColor.GREEN + "You have earnt a lootbox as a reward!");

        final Skyblock i = Skyblock.load(e.getPlayer().getUniqueId());
        i.crates += 1;
        i.votes += 1;
        Objective.vote(i);

        Bukkit.getScheduler().runTask(SkyblockPlugin.skyblock, () -> {
            Crate.newCrate(i); // Run in sync
        });
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
        skyblock.getServer().getScheduler().runTask(skyblock, () -> Skyblock.safeDispose(e.getPlayer().getUniqueId()));
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

    private static final Material[] SAPLINGS = new Material[]{
            Material.OAK_SAPLING,
            Material.SPRUCE_SAPLING,
            Material.BIRCH_SAPLING,
            Material.JUNGLE_SAPLING,
            Material.ACACIA_SAPLING,
            Material.DARK_OAK_SAPLING
    };

    private static int getSapling(Material m) {
        for (int i = 0; i < SAPLINGS.length; i++)
            if (m.equals(SAPLINGS[i]))
                return i;
        return -1;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlaceMonitor(BlockPlaceEvent e) {
        if (e.getBlock().getWorld() == skyblock.world) {
            Skyblock i = Skyblock.load(e.getPlayer().getUniqueId());
            i.blocksPlaced += 1;
            Objective.blocks(i);

            if (e.getBlockPlaced().getType() == Material.COMPARATOR) {
                Objective.placeComparator(i);
            }

            if (e.getBlockPlaced().getType() == Material.DIAMOND_BLOCK) {
                Objective.placeDiamondBlock(i);
            }

            if (e.getBlockPlaced().getY() == 255) {
                Objective.reachTop(i);
            }

            if (getSapling(e.getBlockPlaced().getType()) >= 0) {
                i.saplings |= (1 << getSapling(e.getItemInHand().getType()));
                if (i.saplings == 63) {
                    Objective.placeSaplings(i);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent e) {
        if (e.getBlock().getWorld() == skyblock.world) {
            if (e.getBlock().getType() == Material.CHEST
                    && Crate.isCrate(e.getBlock())) {
                e.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(skyblock, () -> {
                    Skyblock i = Skyblock.get(e.getBlock().getLocation());
                    if (i != null)
                        Crate.removeCrate(i);
                    e.getBlock().setType(Material.AIR); // Just incase doesnt work
                }, 1);
            } else {
                Bukkit.getScheduler().runTaskLater(skyblock, () -> {
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
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().toLowerCase().startsWith("/tpa")) {

            e.setCancelled(true);
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
        WorldBorder worldBorder = new WorldBorder();
        worldBorder.world = ((CraftWorld) e.getTo().getWorld()).getHandle();

        if (e.getTo().getWorld() == SkyblockPlugin.skyblock.lobby) {
            worldBorder.setSize(6000000);
            worldBorder.setCenter(0, 0);
        } else if (world == SkyblockPlugin.skyblock.world || world == SkyblockPlugin.skyblock.nether) {
            int x = ((e.getTo().getBlockX() >> 9) << 9) + 256;
            int z = ((e.getTo().getBlockZ() >> 9) << 9) + 256;
            worldBorder.setSize(512);
            worldBorder.setCenter(x, z);
        }

        PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE);
        CraftPlayer player = (CraftPlayer) e.getPlayer();

        player.getHandle().playerConnection.sendPacket(packet);

        // Send it again after any world loading
        Bukkit.getScheduler().runTaskLater(SkyblockPlugin.skyblock, () -> {
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
    public void onEntityDeath(EntityDeathEvent e) {
        EntityDamageEvent d = e.getEntity().getLastDamageCause();

        if (d != null) {
            if (d instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent b = (EntityDamageByEntityEvent) d;
                if (b.getDamager() instanceof Player) {
                    Skyblock i = Skyblock.load(b.getDamager().getUniqueId());
                    i.mobKills += 1;
                    Objective.mobs(i);
                }
            }

            if (e.getEntityType() == EntityType.CREEPER && d.getCause() == DamageCause.ENTITY_EXPLOSION) {
                Skyblock i = Skyblock.get(e.getEntity().getLocation());
                if (i != null) {
                    for (Player p : Bukkit.getOnlinePlayers())
                        if (i.inIsland(p.getLocation())) {
                            Skyblock j = Skyblock.load(p.getUniqueId());
                            Objective.explodeCreeper(j);
                        }
                }
            }
        }

        if (e.getEntityType() == EntityType.WITHER) {
            Skyblock i = Skyblock.get(e.getEntity().getLocation());
            if (i != null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (i.inIsland(p.getLocation())) {
                        Skyblock j = Skyblock.load(p.getUniqueId());
                        Objective.killWither(j);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage2(EntityDamageEvent e) {
        if (e.getCause() == DamageCause.VOID) {
            EntityDamageEvent d = e.getEntity().getLastDamageCause();
            if (d instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent b = (EntityDamageByEntityEvent) d;
                if (b.getDamager() instanceof Player) {
                    Skyblock i = Skyblock.load(b.getDamager().getUniqueId());
                    i.mobKills += 1;
                    Objective.mobs(i);
                }
            }
        }

        if (e.getCause() == DamageCause.LIGHTNING && e.getEntity() instanceof Player) {
            Objective.lightningStruck(Skyblock.load(e.getEntity().getUniqueId()));
        }
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent e) {
        if (e.getBrokenItem().getType() == Material.DIAMOND_PICKAXE
                || e.getBrokenItem().getType() == Material.DIAMOND_SHOVEL
                || e.getBrokenItem().getType() == Material.DIAMOND_SWORD
                || e.getBrokenItem().getType() == Material.DIAMOND_HOE
                || e.getBrokenItem().getType() == Material.DIAMOND_AXE) {
            Skyblock i = Skyblock.load(e.getPlayer().getUniqueId());
            Objective.breakDiamond(i);
        }
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent e) {
        if (e.getVehicle().getType() == EntityType.BOAT) {
            Skyblock i = Skyblock.get(e.getVehicle().getLocation());
            if (i != null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (i.inIsland(p.getLocation())) {
                        Skyblock j = Skyblock.load(p.getUniqueId());
                        Objective.placeBoat(j);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose2(InventoryCloseEvent e) {
        int c = 0;
        for (ItemStack i : e.getInventory().getContents()) {
            if (i != null && i.getType() == Material.COBBLESTONE) {
                c += i.getAmount();
            }
        }

        if (c >= 27 * 64) {
            Objective.fillChestCobble(Skyblock.load(e.getPlayer().getUniqueId()));
        }

        if (c >= 2 * 27 * 64) {
            Objective.fillDoubleChestCobble(Skyblock.load(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onInteract2(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK
                && e.getClickedBlock().getType() == Material.JUKEBOX
                && e.getItem() != null
                && e.getItem().getType().name().contains("MUSIC_DISC")) {
            Objective.musicDisc(Skyblock.load(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (from.getWorld() == skyblock.world && to.getWorld().getEnvironment() == World.Environment.NETHER) {
            e.setCancelled(true);
            e.getPlayer().teleport(Skyblock.get(from).getNetherSpawnLocation());
            Objective.enterNether(Skyblock.load(e.getPlayer().getUniqueId())); // Enter Nether Objective
        } else if (from.getWorld() == skyblock.nether && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
            e.setCancelled(true);
            e.getPlayer().teleport(Skyblock.get(from).getSpawnLocation());
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (from.getWorld() == skyblock.world && to.getWorld().getEnvironment() == World.Environment.NETHER) {
            e.setCancelled(true);
            e.getEntity().teleport(Skyblock.get(from).getNetherSpawnLocation());
        } else if (from.getWorld() == skyblock.nether && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
            e.setCancelled(true);
            e.getEntity().teleport(Skyblock.get(from).getSpawnLocation());
        }
    }

    @EventHandler
    public void onPlayerExpLevelChange(PlayerExpChangeEvent e) {
        if (e.getPlayer().getLevel() >= 100) {
            Objective.exp100(Skyblock.load(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        if (e.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Entity caught = e.getCaught();
            if (caught instanceof Item) {
                ItemStack item = ((Item) caught).getItemStack();
                ItemStack rod = e.getPlayer().getInventory().getItemInMainHand();

                if (rod.getType() != Material.FISHING_ROD) {
                    rod = e.getPlayer().getInventory().getItemInOffHand();
                    if (rod.getType() != Material.FISHING_ROD) {
                        return;
                    }
                }

                if (item.getType() == Material.FISHING_ROD && item.getEnchantments().size() > 0 && rod.getEnchantments().size() > 0) {
                    Objective.echantedRod(Skyblock.load(e.getPlayer().getUniqueId()));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerSleep(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK
                && e.getClickedBlock().getType().name().contains("_BED")
                && (e.getClickedBlock().getWorld().isThundering()
                || (e.getClickedBlock().getWorld().getTime() > 12541
                && e.getClickedBlock().getWorld().getTime() < 23458))) {
            Objective.sleep(Skyblock.load(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        if (e.getRecipe().getResult().getType() == Material.ENCHANTING_TABLE) {
            Objective.enchantingTable(Skyblock.load(e.getWhoClicked().getUniqueId()));
        }
    }

    @EventHandler
    public void onEntityTame(EntityTameEvent e) {
        if (e.getEntity().getType() == EntityType.CAT || e.getEntity().getType() == EntityType.OCELOT) {
            Objective.tameCat(Skyblock.load(e.getOwner().getUniqueId()));
        }
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent e) {
        if (e.getEntity() instanceof Player &&
                e.getCause() == EntityPotionEffectEvent.Cause.BEACON &&
                e.getNewEffect() != null &&
                (e.getNewEffect().getAmplifier() >= 1 || e.getNewEffect().getType() == PotionEffectType.REGENERATION)
        ) {
            Objective.fullPowerBeacon(Skyblock.load(e.getEntity().getUniqueId()));
        }
    }

    private HashMap<UUID, Integer> massiveSlaughterMap = new HashMap<>();
    private long lastMassiveSlaughterClear = 0;

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent e) {
        if (e.getEntity().getLastDamageCause() != null &&
                e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent &&
                ((EntityDamageByEntityEvent) e.getEntity().getLastDamageCause()).getDamager() instanceof Player) {

            Player player = (Player) ((EntityDamageByEntityEvent) e.getEntity().getLastDamageCause()).getDamager();
            UUID uuid = player.getUniqueId();

            if (lastMassiveSlaughterClear < System.currentTimeMillis() - 100) {
                lastMassiveSlaughterClear = System.currentTimeMillis();
                massiveSlaughterMap.clear();
            }

            int slaughterCount = massiveSlaughterMap.getOrDefault(uuid, 0) + 1;

            massiveSlaughterMap.put(uuid, slaughterCount);

            if (slaughterCount >= 8) {
                Objective.kill8MobsAtOnce(Skyblock.load(uuid));
            }

            if (slaughterCount >= 20) {
                Objective.kill20MobsAtOnce(Skyblock.load(uuid));
            }
        }
    }

    @EventHandler
    public void onFullEnchantedDiamondArmour(InventoryCloseEvent e) {
        PlayerInventory playerInventory = e.getPlayer().getInventory();
        boolean hasFullEnchantedDiamondArmour =
                isEnchantedDiamond(playerInventory.getHelmet()) &&
                isEnchantedDiamond(playerInventory.getChestplate()) &&
                isEnchantedDiamond(playerInventory.getLeggings()) &&
                isEnchantedDiamond(playerInventory.getBoots());
        if (hasFullEnchantedDiamondArmour) {
            Objective.enchantedDiamondArmour(Skyblock.load(playerInventory.getHolder().getUniqueId()));
        }
    }

    private boolean isEnchantedDiamond(ItemStack item) {
        return item != null &&
                item.getType().name().contains("DIAMOND") &&
                !item.getEnchantments().isEmpty();
    }

    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.PUFFERFISH) {
            Objective.eatPufferfish(Skyblock.load(e.getPlayer().getUniqueId()));
        }
    }


    // # --- --- --- #
    // |    SPAWN    |
    // # --- --- --- #

    @EventHandler
    public void onBlockPlaceSpawn(BlockPlaceEvent e) {
        if (e.getBlock().getWorld() == skyblock.lobby) {
            if (!e.getPlayer().hasPermission("skyblock.admin")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreakSpawn(final BlockBreakEvent e) {
        if (e.getBlock().getWorld() == skyblock.lobby) {
            if (!e.getPlayer().hasPermission("skyblock.admin")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getEntity().getWorld() == skyblock.lobby) {
            if (e.getCause() == DamageCause.VOID) {
                e.getEntity().setFallDistance(0);
                e.getEntity().teleport(skyblock.lobby.getSpawnLocation().add(0.5, 0.5, 0.5));
                Bukkit.getScheduler().runTaskLater(skyblock, () ->
                        e.getEntity().teleport(skyblock.lobby.getSpawnLocation().add(0.5, 0.5, 0.5)),
                1);
            }
            e.setCancelled(true);
        }

        if (e.getEntity().getType() == EntityType.VILLAGER && e.getEntity().getWorld() == skyblock.lobby) {
            if (e.getCause() == DamageCause.VOID) {
                Player p = null;
                double d = 200 * 200;

                for (Player a : e.getEntity().getWorld().getPlayers()) {
                    if (a.getLocation().distanceSquared(e.getEntity().getLocation()) < d) {
                        d = a.getLocation().distanceSquared(e.getEntity().getLocation());
                        p = a;
                    }
                }

                if (p != null) {
                    int c = 0;
                    for (Entity a : e.getEntity().getLocation().getChunk().getEntities()) {
                        if (a instanceof Villager && a.getLocation().getY() <= 0) {
                            c += 1;
                        }
                    }
                    Objective.killShop(Skyblock.load(p.getUniqueId()), c);
                }
            }

            Bukkit.getScheduler().runTaskLater(skyblock, () -> Shop.villagerChecker(), 20);
        }

        if (e instanceof EntityDamageByEntityEvent && e.getEntity().hasPermission("skyblock.admin")) {
            Entity d = ((EntityDamageByEntityEvent) e).getDamager();
            if (d instanceof Player) {
                Objective.punchStaff(Skyblock.load(d.getUniqueId()));
            }
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        e.getPlayer().sendMessage(ChatColor.BLUE + "Welcome back to " + ChatColor.AQUA + ChatColor.BOLD + "Just Skyblock"
                + ChatColor.BLUE + ", " + e.getPlayer().getName() + "!");
        e.getPlayer().sendMessage(ChatColor.AQUA + "/skyblock" + ChatColor.BLUE + " to start your own skyblock!");

        Rank.giveRank(e.getPlayer(), Skyblock.load(e.getPlayer().getUniqueId()).getRank());

        if (e.getPlayer().getWorld() == skyblock.lobby) {
            e.getPlayer().teleport(skyblock.lobby.getSpawnLocation().add(0.5, 0.5, 0.5));
            Bukkit.getScheduler().runTaskLater(skyblock, () ->
                    e.getPlayer().teleport(skyblock.lobby.getSpawnLocation().add(0.5, 0.5, 0.5)),
            1);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType() == Material.CHEST) {
                if (e.getPlayer().getWorld() == skyblock.lobby
                        || Crate.isCrate(e.getClickedBlock())) {
                    new Crate(e.getPlayer(), e.getClickedBlock());
                    e.setCancelled(true);
                }
            }
        }
        if (e.getPlayer().getWorld() == skyblock.lobby &&
                (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (!e.getPlayer().hasPermission("skyblock.admin")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBucketEmtpty(final PlayerBucketEmptyEvent e) {
        if (e.getPlayer().getWorld() == skyblock.lobby) {
            if (!e.getPlayer().hasPermission("skyblock.admin")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getPlayer().getWorld() == skyblock.lobby) {
            if (e.getRightClicked().getType() == EntityType.VILLAGER) {
                Shop shop = new Shop(e.getPlayer(), (Villager) e.getRightClicked());
                if (shop.isValid()) {
                    e.setCancelled(true);
                }
            }
        }

        if (e.getRightClicked().hasPermission("skyblock.admin") || e.getRightClicked().hasPermission("pure.helper")) {
            Objective.punchStaff(Skyblock.load(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof Crate) {
            ((Crate) e.getInventory().getHolder()).onClose(e.getPlayer());
        } else if (e.getInventory().getHolder() instanceof Shop) {
            ((Shop) e.getInventory().getHolder()).onClose(e.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof Shop) {
            ((Shop) e.getInventory().getHolder()).onClick(e);
        }
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent e) {
        if (e.getItemDrop().getWorld() == skyblock.lobby) {
            ItemStack i = e.getItemDrop().getItemStack();
            for (ItemStack s : Shop.sellItems) {
                if (s.getType().equals(i.getType())) {
                    new SellItem(e.getPlayer(), e.getItemDrop());
                    break;
                }
            }
        }
    }

    private class SellItem implements Runnable {
        Item item;
        Player player;
        Location last;

        public SellItem(Player p, Item i) {
            player = p;
            item = i;
            last = item.getLocation();
            Bukkit.getScheduler().runTaskLater(skyblock, this, 4);
        }

        @Override
        public void run() {
            if (!item.isValid() || item.getLocation().getBlockY() < 3) {
                return;
            }

            Block b = item.getLocation().getBlock();

            for (int j = 0; j < 2; j++) {
                if (b.getType() == Material.HOPPER) {
                    ItemStack i = item.getItemStack();
                    if (i.getType().name().contains("SPAWN_EGG") || i.getType() == Material.COW_SPAWN_EGG) { // if SPAWN_EGG changes, this'll detect it
                        Objective.sellSpawnEgg(Skyblock.load(player.getUniqueId()));
                    } else {
                        for (int k = 0; k < Shop.sellItems.size(); k++) {
                            if (Shop.sellItems.get(k).getType() == i.getType()
                                    && Shop.sellItems.get(k).getDurability() == i.getDurability()) {

                                // Sold
                                Skyblock is = Skyblock.load(player.getUniqueId());
                                int coins = i.getAmount() * Shop.sellPrices.get(k);
                                is.coins += coins;
                                player.sendMessage(ChatColor.GOLD + " + " + coins + " coins");
                                if (i.getType() == Material.COBBLESTONE) {
                                    is.cobbleSold += i.getAmount();
                                    Objective.cobblesell(is);
                                }

                                item.remove();
                            }
                        }
                    }
                    return;
                }
                b = b.getRelative(0, -1, 0);
            }

            if (item.getLocation().distanceSquared(last) < 0.01) { // Item hasnt moved
                return;
            }

            last = item.getLocation();
            Bukkit.getScheduler().runTaskLater(skyblock, this, 4);
        }

    }
}
