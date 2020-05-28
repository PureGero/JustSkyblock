package just.skyblock.listeners;

import just.skyblock.*;
import just.skyblock.Objectives;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class SpawnListener implements org.bukkit.event.Listener {
    SkyblockPlugin skyblock;

    public SpawnListener(SkyblockPlugin b) {
        skyblock = b;
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
    public void onBlockBreakSpawn(BlockBreakEvent e) {
        if (e.getBlock().getWorld() == skyblock.lobby) {
            if (!e.getPlayer().hasPermission("skyblock.admin")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
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
                    Objectives.killShop(Skyblock.load(p.getUniqueId()), c);
                }
            }

            Bukkit.getScheduler().runTaskLater(skyblock, Shop::villagerChecker, 20);
        }

        if (e instanceof EntityDamageByEntityEvent && e.getEntity().hasPermission("skyblock.admin")) {
            Entity d = ((EntityDamageByEntityEvent) e).getDamager();
            if (d instanceof Player) {
                Objectives.punchStaff(Skyblock.load(d.getUniqueId()));
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
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
    public void onBucketEmtpty(PlayerBucketEmptyEvent e) {
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
            Objectives.punchStaff(Skyblock.load(e.getPlayer().getUniqueId()));
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
    public void onPlayerDropItem(PlayerDropItemEvent e) {
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
                        Objectives.sellSpawnEgg(Skyblock.load(player.getUniqueId()));
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
                                    Objectives.cobblesell(is);
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
