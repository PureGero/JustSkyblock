package just.skyblock;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.inventory.ItemStack;
import puregero.network.VoteEvent;

public class Listener implements org.bukkit.event.Listener{
    SkyBlock skyblock;
    public Listener(SkyBlock b){
        skyblock = b;
    }

    @EventHandler
    public void onVote(VoteEvent e) {
        e.getPlayer().sendMessage(ChatColor.GREEN + "Thank you for voting for us at " + e.getWebsite() + ".");
        e.getPlayer().sendMessage(ChatColor.GREEN + "You have earnt a lootbox as a reward!");
        final Island i = Island.load(e.getPlayer().getUniqueId());
        i.crates += 1;
        i.votes += 1;
        Bukkit.getScheduler().runTask(SkyBlock.skyblock, () -> {
            Crate.newCrate(i); // Run in sync
        });
        Objective.vote(i);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void chat(AsyncPlayerChatEvent e){
        Rank c = Island.load(e.getPlayer().getUniqueId()).getRank();
        if(c != null && c.color != null){
            e.setFormat(e.getFormat().replaceAll("\\%1\\$s", c.color + "["+c.prefix+"] "+"\\%1\\$s"+ChatColor.RESET));
            //e.setFormat(e.getFormat().replaceAll("\\%2\\$s", c.chatcolor+"\\%2\\$s"));
        }
    }
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e){
        skyblock.getServer().getScheduler().runTask(skyblock, new Runnable(){
            public void run(){
                Island.safeDispose(e.getPlayer().getUniqueId());
            }
        });
    }
    
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e){
        if(e.getEntity() instanceof Monster){
            for(Entity y : e.getLocation().getChunk().getEntities()){
                if(y instanceof Monster){
                    if(y.getLocation().distanceSquared(e.getEntity().getLocation()) <= 5){
                        e.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }
    
    private static final Material[] SAPLINGS = new Material[] {
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
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onBlockPlaceMonitor(BlockPlaceEvent e){
        if(e.getBlock().getWorld() == skyblock.world){
            Island i = Island.load(e.getPlayer().getUniqueId());
            i.blocksPlaced += 1;
            Objective.blocks(i);
            if(e.getBlockPlaced().getType() == Material.COMPARATOR)
                Objective.placeComparator(i);
            if(e.getBlockPlaced().getY() == 255){
                Objective.reachTop(i);
            }
            if(e.getItemInHand() != null && getSapling(e.getItemInHand().getType()) >= 0){
                i.saplings |= (1 << getSapling(e.getItemInHand().getType()));
                if(i.saplings == 63){
                    Objective.placeSaplings(i);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent e){
        if(e.getBlock().getWorld() == skyblock.world){
            if(e.getBlock().getType() == Material.CHEST
                    && Crate.isCrate(e.getBlock())){
                e.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(skyblock, new Runnable(){
                    public void run(){
                        Island i = Island.get(e.getBlock().getLocation());
                        if(i != null)
                            Crate.removeCrate(i);
                        e.getBlock().setType(Material.AIR); // Just incase doesnt work
                    }
                }, 1);
            } else {
                Bukkit.getScheduler().runTaskLater(skyblock, new Runnable(){
                    public void run(){
                        Location loc = e.getBlock().getLocation().add(0.5, 0.5, 0.5);
                        for (Item i : loc.getWorld().getEntitiesByClass(Item.class)) {
                            if (i.getLocation().distanceSquared(loc) < 0.25) { // Squared more efficient
                                i.setPickupDelay(0);
                                i.teleport(e.getPlayer().getEyeLocation());
                            }
                        }
                    }
                }, 1);
            }
        }
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        if(e.getMessage().toLowerCase().startsWith("/tpa")){
            e.getPlayer().sendMessage(ChatColor.YELLOW + "/tpa is currently not avaliable.");
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Add someone to your skyblock with /s add <player>");
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e){
        Island i = Island.load(e.getPlayer().getUniqueId());
        if(i.inIsland(e.getFrom()) && !i.inIsland(e.getTo())){
            i.lx = e.getFrom().getX();
            i.ly = e.getFrom().getY();
            i.lz = e.getFrom().getZ();
            i.lyaw = e.getFrom().getYaw();
            i.lpitch = e.getFrom().getPitch();
        }else
        if(!i.inIsland(e.getFrom()) && i.inIsland(e.getTo()) && i.lx != 0 && i.lz != 0){
            e.setTo(new Location(e.getTo().getWorld(),i.lx,i.ly,i.lz,i.lyaw,i.lpitch));
            e.getPlayer().sendMessage(ChatColor.GOLD + "Teleporting to previous location...");
            i.lx = 0;
            i.lz = 0;
        }
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        EntityDamageEvent d = e.getEntity().getLastDamageCause();
        if(d instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent b = (EntityDamageByEntityEvent) d;
            if(b.getDamager() instanceof Player){
                Island i = Island.load(b.getDamager().getUniqueId());
                i.mobKills += 1;
                Objective.mobs(i);
            }
        }
        if(e.getEntityType() == EntityType.CREEPER && d.getCause() == DamageCause.ENTITY_EXPLOSION){
            Island i = Island.get(e.getEntity().getLocation());
            if(i != null){
                for(Player p : Bukkit.getOnlinePlayers())
                    if(i.inIsland(p.getLocation())){
                        Island j = Island.load(p.getUniqueId());
                        Objective.explodeCreeper(j);
                    }
            }
        }
    }
    @EventHandler
    public void onEntityDamage2(EntityDamageEvent e){
        if(e.getCause() == DamageCause.VOID){
            EntityDamageEvent d = e.getEntity().getLastDamageCause();
            if(d instanceof EntityDamageByEntityEvent){
                EntityDamageByEntityEvent b = (EntityDamageByEntityEvent) d;
                if(b.getDamager() instanceof Player){
                    Island i = Island.load(b.getDamager().getUniqueId());
                    i.mobKills += 1;
                    Objective.mobs(i);
                }
            }
        }
        if(e.getCause() == DamageCause.LIGHTNING && e.getEntity() instanceof Player){
            Objective.lightningStruck(Island.load(e.getEntity().getUniqueId()));
        }
    }
    
    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent e){
        if(e.getBrokenItem().getType() == Material.DIAMOND_PICKAXE
                || e.getBrokenItem().getType() == Material.DIAMOND_SHOVEL
                || e.getBrokenItem().getType() == Material.DIAMOND_SWORD
                || e.getBrokenItem().getType() == Material.DIAMOND_HOE
                || e.getBrokenItem().getType() == Material.DIAMOND_AXE){
            Island i = Island.load(e.getPlayer().getUniqueId());
            Objective.breakDiamond(i);
        }
    }
    
    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent e){
        if(e.getVehicle().getType() == EntityType.BOAT){
            Island i = Island.get(e.getVehicle().getLocation());
            if(i != null){
                for(Player p : Bukkit.getOnlinePlayers())
                    if(i.inIsland(p.getLocation())){
                        Island j = Island.load(p.getUniqueId());
                        Objective.placeBoat(j);
                    }
            }
        }
    }

    @EventHandler
    public void onInventoryClose2(InventoryCloseEvent e){
        int c = 0;
        for(ItemStack i : e.getInventory().getContents()){
            if(i != null && i.getType() == Material.COBBLESTONE)
                c += i.getAmount();
        }
        if(c >= 27*64)
            Objective.fillChestCobble(Island.load(e.getPlayer().getUniqueId()));
        if(c >= 2*27*64)
            Objective.fillDoubleChestCobble(Island.load(e.getPlayer().getUniqueId()));
    }
    
    @EventHandler
    public void onInteract2(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(e.getClickedBlock().getType() == Material.JUKEBOX
                    && e.getItem() != null
                    && e.getItem().getType().name().contains("RECORD")){
                Objective.musicDisc(Island.load(e.getPlayer().getUniqueId()));
            }
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (from.getWorld() == skyblock.world && to.getWorld().getEnvironment() == World.Environment.NETHER) {
            e.setCancelled(true);
            e.getPlayer().teleport(Island.get(from).getNetherSpawnLocation());
        } else if (from.getWorld() == skyblock.nether && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
            e.setCancelled(true);
            e.getPlayer().teleport(Island.get(from).getSpawnLocation());
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (from.getWorld() == skyblock.world && to.getWorld().getEnvironment() == World.Environment.NETHER) {
            e.setCancelled(true);
            e.getEntity().teleport(Island.get(from).getNetherSpawnLocation());
        } else if (from.getWorld() == skyblock.nether && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
            e.setCancelled(true);
            e.getEntity().teleport(Island.get(from).getSpawnLocation());
        }
    }
    
    
    
    
    // # --- --- --- #
    // |    SPAWN    |
    // # --- --- --- #

    @EventHandler
    public void onBlockPlaceSpawn(BlockPlaceEvent e){
        if (e.getBlock().getWorld() == skyblock.lobby) {
            if(!e.getPlayer().hasPermission("skyblock.admin")){
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
    public void onEntityDamage(final EntityDamageEvent e){
        if(e.getEntity() instanceof Player && e.getEntity().getWorld() == skyblock.lobby){
            if(e.getCause() == DamageCause.VOID){
                e.getEntity().setFallDistance(0);
                e.getEntity().teleport(skyblock.lobby.getSpawnLocation().add(0.5, 0.5, 0.5));
                Bukkit.getScheduler().runTaskLater(skyblock, new Runnable(){
                    public void run(){
                        e.getEntity().teleport(skyblock.lobby.getSpawnLocation().add(0.5, 0.5, 0.5));
                    }
                }, 1);
            }
            e.setCancelled(true);
        }
        if(e.getEntity().getType() == EntityType.VILLAGER && e.getEntity().getWorld() == skyblock.lobby){
            if(e.getCause() == DamageCause.VOID){
                Player p = null;
                double d = 200*200;
                for(Player a : e.getEntity().getWorld().getPlayers())
                    if(a.getLocation().distanceSquared(e.getEntity().getLocation()) < d){
                        d = a.getLocation().distanceSquared(e.getEntity().getLocation());
                        p = a;
                    }
                if(p != null){
                    int c = 0;
                    for(Entity a : e.getEntity().getLocation().getChunk().getEntities())
                        if(a instanceof Villager && a.getLocation().getY() <= 0)
                            c += 1;
                    Objective.killShop(Island.load(p.getUniqueId()),c);
                }
            }
            Bukkit.getScheduler().runTaskLater(skyblock, new Runnable(){
                public void run(){
                    Shop.villagerChecker();
                }
            }, 20);
        }
        if(e instanceof EntityDamageByEntityEvent && e.getEntity().hasPermission("skyblock.admin")){
            Entity d = ((EntityDamageByEntityEvent)e).getDamager();
            if(d instanceof Player){
                Objective.punchStaff(Island.load(d.getUniqueId()));
            }
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e){
        e.getPlayer().sendMessage(ChatColor.BLUE + "Welcome back to " + ChatColor.AQUA + ChatColor.BOLD + "Just Skyblock"
                + ChatColor.BLUE + ", " + e.getPlayer().getName() + "!");
        e.getPlayer().sendMessage(ChatColor.AQUA + "/skyblock" + ChatColor.BLUE + " to start your own skyblock!");
        Rank.giveRank(e.getPlayer(), Island.load(e.getPlayer().getUniqueId()).getRank());
        if(e.getPlayer().getWorld() == skyblock.lobby){
            e.getPlayer().teleport(skyblock.lobby.getSpawnLocation().add(0.5, 0.5, 0.5));
            Bukkit.getScheduler().runTaskLater(skyblock, new Runnable(){
                public void run(){
                    e.getPlayer().teleport(skyblock.lobby.getSpawnLocation().add(0.5, 0.5, 0.5));
                }
            }, 1);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(e.getClickedBlock().getType() == Material.CHEST){
                if(e.getPlayer().getWorld() == skyblock.lobby
                        || Crate.isCrate(e.getClickedBlock())){
                    new Crate(e.getPlayer(),e.getClickedBlock());
                    e.setCancelled(true);
                }
            }
        }
        if(e.getPlayer().getWorld() == skyblock.lobby &&
                (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)){
            if(!e.getPlayer().hasPermission("skyblock.admin"))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmtpty(final PlayerBucketEmptyEvent e){
        if(e.getPlayer().getWorld() == skyblock.lobby)
            if(!e.getPlayer().hasPermission("skyblock.admin")){
                e.setCancelled(true);
            }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e){
        if(e.getPlayer().getWorld() == skyblock.lobby){
            if(e.getRightClicked().getType() == EntityType.VILLAGER){
                Shop shop = new Shop(e.getPlayer(),(Villager) e.getRightClicked());
                if (shop.isValid())
                    e.setCancelled(true);
            }
        }
        if(e.getRightClicked().hasPermission("skyblock.admin")){
            Objective.punchStaff(Island.load(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(e.getInventory().getHolder() instanceof Crate){
            ((Crate)e.getInventory().getHolder()).onClose(e.getPlayer());
        }else if(e.getInventory().getHolder() instanceof Shop){
            ((Shop)e.getInventory().getHolder()).onClose(e.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getInventory().getHolder() instanceof Shop){
            ((Shop)e.getInventory().getHolder()).onClick(e);
        }
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent e){
        if(e.getItemDrop().getWorld() == skyblock.lobby){
            ItemStack i = e.getItemDrop().getItemStack();
            for(ItemStack s : Shop.items)
                if(s.getType().equals(i.getType())){
                    new SellItem(e.getPlayer(), e.getItemDrop());
                    break;
                }
        }
    }

    private class SellItem implements Runnable{
        Item item;
        Player player;
        Location last;

        public SellItem(Player p, Item i){
            player = p;
            item = i;
            last = item.getLocation();
            Bukkit.getScheduler().runTaskLater(skyblock, this, 4);
        }

        @Override
        public void run() {
            if(!item.isValid() || item.getLocation().getBlockY() < 3)return;
            Block b = item.getLocation().getBlock();
            for(int j=0;j<2;j++){
                if(b.getType() == Material.HOPPER){
                    ItemStack i = item.getItemStack();
                    if(i.getType().name().contains("SPAWN_EGG") || i.getType() == Material.COW_SPAWN_EGG) // if SPAWN_EGG changes, this'll detect it
                        Objective.sellSpawnEgg(Island.load(player.getUniqueId()));
                    else
                        for(int k=0;k<Shop.items.size();k++){
                            if(Shop.items.get(k).getType() == i.getType()
                                    && Shop.items.get(k).getDurability() == i.getDurability()){
                                
                                // Sold
                                Island is = Island.load(player.getUniqueId());
                                int coins = i.getAmount()*Shop.sellPrices.get(k);
                                is.coins += coins;
                                player.sendMessage(ChatColor.GOLD + " + " + coins + " coins");
                                if(i.getType() == Material.COBBLESTONE){
                                    is.cobbleSold += i.getAmount();
                                    Objective.cobblesell(is);
                                }
                                
                                item.remove();
                            }
                        }
                    return;
                }
                b = b.getRelative(0, -1, 0);
            }
            if(item.getLocation().distanceSquared(last) < 0.01){ // Item hasnt moved
                return;
            }
            last = item.getLocation();
            Bukkit.getScheduler().runTaskLater(skyblock, this, 4);
        }
        
    }
}
