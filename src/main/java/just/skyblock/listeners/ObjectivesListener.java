package just.skyblock.listeners;

import just.skyblock.Objective;
import just.skyblock.Skyblock;
import just.skyblock.SkyblockPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Cake;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
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

import java.util.HashMap;

public class ObjectivesListener implements org.bukkit.event.Listener {
    private SkyblockPlugin plugin;

    public ObjectivesListener(SkyblockPlugin plugin) {
        this.plugin = plugin;
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
        if (e.getBlock().getWorld() == plugin.world) {
            Skyblock skyblock = Skyblock.load(e.getPlayer().getUniqueId());
            skyblock.blocksPlaced += 1;
            Objective.blocks(skyblock);

            if (e.getBlockPlaced().getType() == Material.COMPARATOR) {
                Objective.PLACE_COMPARATOR.give(skyblock);
            }

            if (e.getBlockPlaced().getType() == Material.DIAMOND_BLOCK) {
                Objective.PLACE_DIAMOND_BLOCK.give(skyblock);
            }

            if (e.getBlockPlaced().getY() == 255) {
                Objective.REACH_BUILD_HEIGHT_LIMIT.give(skyblock);
            }

            if (getSapling(e.getBlockPlaced().getType()) >= 0) {
                skyblock.saplings |= (1 << getSapling(e.getItemInHand().getType()));
                if (skyblock.saplings == 63) {
                    Objective.PLACE_EACH_SAPLING.give(skyblock);
                }
            }
        }
        if(e.getBlockPlaced().getType() == Material.TORCH) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                Player player = (Player) e.getPlayer();
                if(player.getStatistic(Statistic.USE_ITEM, Material.TORCH) >= 1000) {
                    Objective.PLACE_1000_TORCHES.give(player);
                }
            });
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
                Skyblock skyblock = Skyblock.get(e.getEntity().getLocation());
                if (skyblock != null) {
                    for (Player player : skyblock.getPlayers()) {
                        Objective.EXPLODE_CREEPER.give(player);
                    }
                }
            }
        }

        if (e.getEntityType() == EntityType.WITHER) {
            Skyblock skyblock = Skyblock.get(e.getEntity().getLocation());
            if (skyblock != null) {
                for (Player player : skyblock.getPlayers()) {
                    Objective.KILL_WITHER.give(player);
                }
            }
        }

        if (e.getEntityType() == EntityType.ENDER_DRAGON) {
            for (Player player : plugin.enderDragonFight.getPlayers()) {
                Skyblock.load(player).enderDragonsKilled++;
                Objective.KILL_ENDER_DRAGON.give(player);
            }
        }

		if(e.getEntity().getType() == EntityType.BAT) {
			if(e.getEntity().getLastDamageCause().getCause() == DamageCause.DROWNING) {
                Player player = null;
                double area = 100*100;
                for(Player a : e.getEntity().getWorld().getPlayers())
                    if(a.getLocation().distanceSquared(e.getEntity().getLocation()) < area){
                        area = a.getLocation().distanceSquared(e.getEntity().getLocation());
                        player = a;
                    }
                if(player != null){
                	Objective.DROWN_BAT.give(player);
                }
			}
		}
	   if(e.getEntityType() == EntityType.PANDA && e.getEntity().getKiller() != null) {
	       Player player = (Player) e.getEntity().getKiller();
	       if(player.getStatistic(Statistic.KILL_ENTITY, EntityType.PANDA) >= 100) {
	           Objective.KILL_100_PANDAS.give(player);
	       }
	   }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
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
            Objective.STRUCK_BY_LIGHTNING.give(e.getEntity());
        }
        if(e.getCause() == DamageCause.VOID) {
            if(e.getEntity().getVehicle() != null && e.getEntity().getVehicle().getType() == EntityType.PIG) {
                for(Entity passenger : e.getEntity().getVehicle().getPassengers()) {
                    Objective.RIDE_PIG_INTO_VOID.give(passenger);
                }
            }
        }
        if (e.getCause() == DamageCause.POISON) {
            if (e.getEntity().getType() == EntityType.WITCH) {
                Player player = null;
                double area = 100*100;
                for(Player a : e.getEntity().getWorld().getPlayers())
                    if(a.getLocation().distanceSquared(e.getEntity().getLocation()) < area){
                        area = a.getLocation().distanceSquared(e.getEntity().getLocation());
                        player = a;
                    }
                if(player != null){
                    Objective.POISON_WITCH.give(player);
                }
            }
        }
        if (e.getCause() == DamageCause.ENTITY_ATTACK) {
            EntityDamageEvent d = e.getEntity().getLastDamageCause();
            if (d instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent b = (EntityDamageByEntityEvent) d;
                if (b.getDamager() instanceof Player) {
                    Player player = (Player) b.getDamager();
                    if (player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_SWORD)
                            && player.getInventory().getItemInMainHand().getEnchantments().isEmpty() == false) {
                            if (player.getInventory().getItemInOffHand().getType().equals(Material.DIAMOND_SWORD)
                                    && player.getInventory().getItemInOffHand().getEnchantments().isEmpty() == false) {
                                Objective.DUAL_WIELD_SWORDS.give(player);
                            }
                    } 
                }
            }
        }
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent e) {
        if (e.getBrokenItem().getType() == Material.DIAMOND_PICKAXE
                || e.getBrokenItem().getType() == Material.DIAMOND_SHOVEL
                || e.getBrokenItem().getType() == Material.DIAMOND_SWORD
                || e.getBrokenItem().getType() == Material.DIAMOND_HOE
                || e.getBrokenItem().getType() == Material.DIAMOND_AXE) {
            Objective.BREAK_DIAMOND_TOOL.give(e.getPlayer());
        }
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent e) {
        if (e.getVehicle().getType() == EntityType.BOAT) {
            Skyblock skyblock = Skyblock.get(e.getVehicle().getLocation());
            if (skyblock != null) {
                for (Player player : skyblock.getPlayers()) {
                    Objective.PLACE_BOAT.give(player);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        int c = 0;
        for (ItemStack i : e.getInventory().getContents()) {
            if (i != null && i.getType() == Material.COBBLESTONE) {
                c += i.getAmount();
            }
        }

        if (c >= 27 * 64) {
            Objective.FILL_CHEST_WITH_COBBLE.give(e.getPlayer());
        }

        if (c >= 2 * 27 * 64) {
            Objective.FILL_DOUBLE_CHEST_WITH_COBBLE.give(e.getPlayer());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK
                && e.getClickedBlock().getType() == Material.JUKEBOX
                && e.getItem() != null
                && e.getItem().getType().name().contains("MUSIC_DISC")) {
            Objective.PLAY_MUSIC_DISC.give(e.getPlayer());
        }
        if (e.getClickedBlock() != null 
                && e.getClickedBlock().getType() == Material.CAKE 
                && ((Cake) e.getClickedBlock().getBlockData()).getBites() == 6) {
            Objective.EAT_CAKE.give(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerExpLevelChange(PlayerExpChangeEvent e) {
        if (e.getPlayer().getLevel() >= 100) {
            Objective.LEVEL_100.give(e.getPlayer());
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

                if (item.getType() == Material.FISHING_ROD && !item.getEnchantments().isEmpty() && !rod.getEnchantments().isEmpty()) {
                    Objective.REEL_ENCHANTED_FISHING_ROD_WITH_ENCHANTED_FISHING_ROD.give(e.getPlayer());
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
            Objective.SLEEP.give(e.getPlayer());
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        if (e.getRecipe().getResult().getType() == Material.ENCHANTING_TABLE) {
            Objective.CRAFT_ENCHANTING_TABLE.give(e.getWhoClicked());
        }
        if(e.getCurrentItem().getType() == Material.MAP) {
            Objective.CRAFT_MAP.give(e.getWhoClicked());   
        }
    }

    @EventHandler
    public void onEntityTame(EntityTameEvent e) {
        if (e.getEntity().getType() == EntityType.CAT || e.getEntity().getType() == EntityType.OCELOT) {
            Objective.TAME_CAT.give((Player) e.getOwner());
        }
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent e) {
        if (e.getEntity() instanceof Player &&
                e.getCause() == EntityPotionEffectEvent.Cause.BEACON &&
                e.getNewEffect() != null &&
                (e.getNewEffect().getAmplifier() >= 1 || e.getNewEffect().getType() == PotionEffectType.REGENERATION)
        ) {
            Objective.ACTIVATE_FULL_POWER_BEACON.give(e.getEntity());
        }
    }

    private HashMap<Player, Integer> massiveSlaughterMap = new HashMap<>();
    private long lastMassiveSlaughterClear = 0;

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent e) {
        if (e.getEntity().getLastDamageCause() != null &&
                e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent &&
                ((EntityDamageByEntityEvent) e.getEntity().getLastDamageCause()).getDamager() instanceof Player) {

            Player player = (Player) ((EntityDamageByEntityEvent) e.getEntity().getLastDamageCause()).getDamager();

            if (lastMassiveSlaughterClear < System.currentTimeMillis() - 100) {
                lastMassiveSlaughterClear = System.currentTimeMillis();
                massiveSlaughterMap.clear();
            }

            int slaughterCount = massiveSlaughterMap.getOrDefault(player, 0) + 1;

            massiveSlaughterMap.put(player, slaughterCount);

            if (slaughterCount >= 8) {
                Objective.KILL_8_MOBS_AT_ONCE.give(player);
            }

            if (slaughterCount >= 20) {
                Objective.KILL_20_MOBS_AT_ONCE.give(player);
            }
        }
        if(e.getEntityType() == EntityType.TURTLE 
                && e.getEntity().getKiller() != null 
                && e.getEntity().getKiller().getInventory().getItemInMainHand().getType().equals(Material.TRIDENT)) {
            Objective.KILL_TURTLE_WITH_TRIDENT.give(e.getEntity().getKiller());
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
            Objective.FULL_ENCHANTED_DIAMOND_ARMOUR.give(playerInventory.getHolder());
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
            Objective.EAT_PUFFERFISH.give(e.getPlayer());
        }
    }
    
    @EventHandler
    public void onClickEntity(PlayerInteractEntityEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.NAME_TAG)) { 
            if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("Scarface")) {
                if (e.getRightClicked().getType() == EntityType.PARROT) {
                    Objective.NAME_PARROT.give(e.getPlayer());
                }
            }   
        }
        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.BAKED_POTATO)) {   
            if(e.getRightClicked().getType() == EntityType.VILLAGER && ((Ageable) e.getRightClicked()).isAdult() != true ) {
                Objective.FEED_BABY_VILLAGER_POTATO.give(e.getPlayer());
            }           
        }
    }
    
    private HashMap<Player, Integer> fallingStartYs = new HashMap<>();
    private HashMap<Player, Location> swimmingStartZ = new HashMap<>();
    
    @EventHandler
    public void onFall(PlayerMoveEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getPlayer().getVelocity().getY() < -0.1) {
            
            if (fallingStartYs.containsKey(player)) {
            }
            else {
                fallingStartYs.put(player, player.getLocation().getBlockY());
            }
        }

        else {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
            if(event.getPlayer().isDead() == true) {
                fallingStartYs.remove(player);
            }
            else if (fallingStartYs.containsKey(player)) {
                int startY = fallingStartYs.remove(player);
                int currentY = player.getLocation().getBlockY();
                if((startY-currentY >= 255)) {
                    Objective.FALL_256_BLOCKS.give(player);
                }
            }
            }); 
        }
        if (event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.FROSTED_ICE) {
            if (event.getPlayer().getInventory().getBoots().getEnchantments().containsKey(Enchantment.FROST_WALKER)) {
                Objective.FROST_WALKING.give(player);
            }
        }
        if(player.isSwimming() == true) {
            if (swimmingStartZ.containsKey(player)) {
            }
            else {
                swimmingStartZ.put(player, player.getLocation());
                }
        }
        
        else if (swimmingStartZ.containsKey(player)) {
            Location startZ = swimmingStartZ.remove(player);
            if((player.getLocation().distance(startZ) >= 100)) {
                Objective.SWIM_100_BLOCKS.give(player);
            }
        }
    }
    
    @EventHandler
    public void onClickInventory(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof WanderingTrader && e.getWhoClicked() instanceof Player) {
            if(e.getSlot() == 2 && e.getCurrentItem().getType() != Material.AIR) {
                Objective.TRADE_WITH_TRADER.give(e.getWhoClicked());
            }
        }
    }
    
    private static final Material[] DOORS = new Material[] {
            Material.IRON_DOOR,
            Material.OAK_DOOR,
            Material.SPRUCE_DOOR,
            Material.BIRCH_DOOR,
            Material.JUNGLE_DOOR,
            Material.ACACIA_DOOR,
            Material.DARK_OAK_DOOR
    };
    
    private static int getDoor(Material m) {
        for (int i = 0; i < DOORS.length; i++)
            if (m.equals(DOORS[i]))
                return i;
        return -1;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Skyblock skyblockdata = Skyblock.load(e.getPlayer().getUniqueId());
        if(e.getBlock().getType() != null && getDoor(e.getBlock().getType()) >= 0) {
                skyblockdata.doorsBroken |= (1 << getDoor(e.getBlock().getType()));
                if(skyblockdata.doorsBroken == 127){
                    Objective.BREAK_ALL_DOORS.give(e.getPlayer());
                }
        }
        Player player = (Player) e.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if(player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE) >= 1) {
                Objective.MINE_DIAMOND_ORE.give(player);
            }
        });    
    }

}
