package just.skyblock.objectives;

import just.skyblock.Skyblock;
import just.skyblock.SkyblockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class ObjectivesListener implements org.bukkit.event.Listener {
    SkyblockPlugin skyblock;

    public ObjectivesListener(SkyblockPlugin b) {
        skyblock = b;
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
            Objectives.blocks(i);

            if (e.getBlockPlaced().getType() == Material.COMPARATOR) {
                Objectives.placeComparator(i);
            }

            if (e.getBlockPlaced().getType() == Material.DIAMOND_BLOCK) {
                Objectives.placeDiamondBlock(i);
            }

            if (e.getBlockPlaced().getY() == 255) {
                Objectives.reachTop(i);
            }

            if (getSapling(e.getBlockPlaced().getType()) >= 0) {
                i.saplings |= (1 << getSapling(e.getItemInHand().getType()));
                if (i.saplings == 63) {
                    Objectives.placeSaplings(i);
                }
            }
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
                    Objectives.mobs(i);
                }
            }

            if (e.getEntityType() == EntityType.CREEPER && d.getCause() == DamageCause.ENTITY_EXPLOSION) {
                Skyblock skyblock = Skyblock.get(e.getEntity().getLocation());
                if (skyblock != null) {
                    for (Player player : skyblock.getPlayers()) {
                        Skyblock skyblockMember = Skyblock.load(player.getUniqueId());
                        Objectives.explodeCreeper(skyblockMember);
                    }
                }
            }
        }

        if (e.getEntityType() == EntityType.WITHER) {
            Skyblock skyblock = Skyblock.get(e.getEntity().getLocation());
            if (skyblock != null) {
                for (Player player : skyblock.getPlayers()) {
                    Skyblock skyblockMember = Skyblock.load(player.getUniqueId());
                    Objectives.killWither(skyblockMember);
                }
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
                    Objectives.mobs(i);
                }
            }
        }

        if (e.getCause() == DamageCause.LIGHTNING && e.getEntity() instanceof Player) {
            Objectives.lightningStruck(Skyblock.load(e.getEntity().getUniqueId()));
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
            Objectives.breakDiamond(i);
        }
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent e) {
        if (e.getVehicle().getType() == EntityType.BOAT) {
            Skyblock skyblock = Skyblock.get(e.getVehicle().getLocation());
            if (skyblock != null) {
                for (Player player : skyblock.getPlayers()) {
                    Skyblock skyblockMember = Skyblock.load(player.getUniqueId());
                    Objectives.placeBoat(skyblockMember);
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
            Objectives.fillChestCobble(Skyblock.load(e.getPlayer().getUniqueId()));
        }

        if (c >= 2 * 27 * 64) {
            Objectives.fillDoubleChestCobble(Skyblock.load(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK
                && e.getClickedBlock().getType() == Material.JUKEBOX
                && e.getItem() != null
                && e.getItem().getType().name().contains("MUSIC_DISC")) {
            Objectives.musicDisc(Skyblock.load(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onPlayerExpLevelChange(PlayerExpChangeEvent e) {
        if (e.getPlayer().getLevel() >= 100) {
            Objectives.exp100(Skyblock.load(e.getPlayer().getUniqueId()));
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
                    Objectives.echantedRod(Skyblock.load(e.getPlayer().getUniqueId()));
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
            Objectives.sleep(Skyblock.load(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        if (e.getRecipe().getResult().getType() == Material.ENCHANTING_TABLE) {
            Objectives.enchantingTable(Skyblock.load(e.getWhoClicked().getUniqueId()));
        }
    }

    @EventHandler
    public void onEntityTame(EntityTameEvent e) {
        if (e.getEntity().getType() == EntityType.CAT || e.getEntity().getType() == EntityType.OCELOT) {
            Objectives.tameCat(Skyblock.load(e.getOwner().getUniqueId()));
        }
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent e) {
        if (e.getEntity() instanceof Player &&
                e.getCause() == EntityPotionEffectEvent.Cause.BEACON &&
                e.getNewEffect() != null &&
                (e.getNewEffect().getAmplifier() >= 1 || e.getNewEffect().getType() == PotionEffectType.REGENERATION)
        ) {
            Objectives.fullPowerBeacon(Skyblock.load(e.getEntity().getUniqueId()));
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
                Objectives.kill8MobsAtOnce(Skyblock.load(uuid));
            }

            if (slaughterCount >= 20) {
                Objectives.kill20MobsAtOnce(Skyblock.load(uuid));
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
            Objectives.enchantedDiamondArmour(Skyblock.load(playerInventory.getHolder().getUniqueId()));
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
            Objectives.eatPufferfish(Skyblock.load(e.getPlayer().getUniqueId()));
        }
    }
}
