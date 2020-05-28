package just.skyblock;

import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Crate implements InventoryHolder {

    private static final int[] VALUES = new int[] {5, 20, 50, 5};
    private static final int[] WORTH = new int[] {50, 50, 100, 50};
    private static final int[] POS = new int[] {10, 12, 14, 16};
    private Skyblock skyblock;
    private Inventory inventory = null;
    private ItemStack[] items = new ItemStack[4];
    private Block chest;

    public Crate(Player player, Block chest) {
        this.chest = chest;
        this.skyblock = Skyblock.load(player.getUniqueId());

        if (skyblock.crates > 0 || skyblock.lastFreeCrate < today()) {
            inventory = Bukkit.createInventory(this, 27, "Loot Box");
            Random random = new Random(skyblock.crateSeed);

            for (int i = 0; i < POS.length; i++) {
                ArrayList<Double> chances = new ArrayList<>();

                double lc = 0;
                for (int j = 0; j < Shop.lootBoxItems.size(); j++) {
                    int c = Shop.lootBoxValues.get(j);
                    if (c >= VALUES[i]) {
                        lc += 1 / Math.log(c);
                    }
                    chances.add(lc);
                }

                double c = random.nextDouble() * chances.get(chances.size() - 1);
                for (int j = 0; j < chances.size(); j++) {
                    if (chances.get(j) > c) {
                        items[i] = Shop.lootBoxItems.get(j).clone();
                        if (Shop.lootBoxValues.get(j) < WORTH[i]) {
                            items[i].setAmount(WORTH[i] / Shop.lootBoxValues.get(j)); // Make the value at least WORTH[i]
                        }
                        inventory.setItem(POS[i], items[i].clone());
                        break;
                    }
                }
            }

            player.openInventory(inventory);
        } else {
            player.sendMessage(ChatColor.RED + "You have no more loot boxes to open!");
            player.sendMessage(ChatColor.RED + "The next daily loot box is in " + ChatColor.BOLD + nextDay());
        }
    }

    public static void islandCrateTicker() {
        Bukkit.getScheduler().runTaskTimer(SkyblockPlugin.plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Skyblock skyblock = Skyblock.load(player.getUniqueId());

                if (skyblock.inIsland(player.getLocation())) {
                    if (skyblock.crates > 0 || skyblock.lastFreeCrate < today()) {
                        makeCrate(skyblock);
                    } else {
                        removeCrate(skyblock);
                    }
                }
            }
        }, 5 * 60 * 20, 5 * 60 * 20);
    }

    public static void newCrate(Skyblock skyblock) {
        Player player = Bukkit.getPlayer(skyblock.uuid);

        if (player == null) {
            return;
        }

        if (skyblock.inIsland(player.getLocation())) {
            if (skyblock.crates > 0 || skyblock.lastFreeCrate < today()) {
                makeCrate(skyblock);
            } else {
                removeCrate(skyblock);
            }
        }
    }

    public static boolean isCrate(Block b) {
        if (b.getWorld() == SkyblockPlugin.plugin.world) {
            Skyblock skyblock = Skyblock.get(b.getLocation());
            return skyblock != null &&
                    skyblock.crateX == b.getX() &&
                    skyblock.crateY == b.getY() &&
                    skyblock.crateZ == b.getZ();
        }

        return false;
    }

    public static void removeCrate(Skyblock skyblock) {
        if (skyblock.crateX != 0) { // Crate exists
            Block chest = SkyblockPlugin.plugin.world.getBlockAt(skyblock.crateX, skyblock.crateY, skyblock.crateZ);

            if (chest.getType() == Material.CHEST) {
                chest.setType(Material.AIR);
            }

            skyblock.crateX = 0;
            skyblock.crateY = 0;
            skyblock.crateZ = 0;
        }
    }

    public static void makeCrate(Skyblock skyblock) {
        if (skyblock.crateX == 0) { // Crate doesnt exist
            Location crateLocation = new Location(SkyblockPlugin.plugin.world, skyblock.x * 512 + 256.5 - 8, 65.5, skyblock.z * 512 + 256.5 - 8);

            while (crateLocation.getBlockY() < 256 && crateLocation.getBlock().getType() != Material.AIR) {
                crateLocation = crateLocation.add(0, 1, 0);
            }

            skyblock.crateX = crateLocation.getBlockX();
            skyblock.crateY = crateLocation.getBlockY();
            skyblock.crateZ = crateLocation.getBlockZ();
        }

        Block chest = SkyblockPlugin.plugin.world.getBlockAt(skyblock.crateX, skyblock.crateY, skyblock.crateZ);
        chest.setType(Material.CHEST);

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.RED)
                .with(Type.BALL)
                .build();

        Firework firework = (Firework) chest.getWorld().spawnEntity(chest.getLocation(), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
    }

    public static int today() {
        return (int) (System.currentTimeMillis() / 1000 / 60 / 60 / 6);
    }

    public static String nextDay() {
        long t = System.currentTimeMillis() - (System.currentTimeMillis() / 1000 / 60 / 60 / 6) * 6 * 60 * 60 * 1000L;
        t = (1000L * 60 * 60 * 6) - t; // Invert

        if (t >= 1000 * 60 * 60 * 2) { // More than 2 hrs
            return t / 1000 / 60 / 60 + " hours";
        }
        if (t > 1000 * 60 * 60) { // 1 hr
            return t / 1000 / 60 / 60 + " hour";
        }
        if (t >= 1000 * 60 * 2) { // More than 2 mins
            return t / 1000 / 60 + " minutes";
        }
        if (t > 1000 * 60) { // 1 min
            return t / 1000 / 60 + " minute";
        }
        return t / 1000 + " seconds";
    }

    public void onClose(HumanEntity e) {
        boolean same = true;
        for (int i = 0; i < POS.length; i++) {
            ItemStack s = inventory.getItem(POS[i]);
            if (s == null ||
                    !s.getType().equals(items[i].getType()) ||
                    s.getAmount() != items[i].getAmount()
            ) {
                same = false;
                for (int j = 0; j < i; j++) {
                    inventory.setItem(POS[j], items[j].clone());
                }
                
                break;
            } else {
                inventory.setItem(POS[i], new ItemStack(Material.AIR));
            }
        }

        ArrayList<ItemStack> items = new ArrayList<>();
        for (ItemStack i : inventory.getContents()) {
            if (i == null) {
                continue;
            }
            items.add(i.clone());
        }

        if (same) {
            e.sendMessage(ChatColor.YELLOW + "You left your loot in the chest!");
            e.sendMessage(ChatColor.YELLOW + "Don't worry, it will still be there for you.");
        } else {
            if (skyblock.lastFreeCrate < today()) {
                skyblock.lastFreeCrate = today();
            } else {
                skyblock.crates -= 1;
            }

            skyblock.crateSeed = new Random(skyblock.crateSeed).nextLong();
            boolean isCrate = isCrate(chest);

            if (skyblock.crates > 0) {
                e.sendMessage(ChatColor.YELLOW + "You have " + skyblock.crates + " more loot boxes to open!");
                if (isCrate) {
                    e.sendMessage(ChatColor.YELLOW + "You can open them at " + ChatColor.BOLD + "/spawn");
                }
            } else {
                e.sendMessage(ChatColor.YELLOW + "You will get another free loot box in " + nextDay());
            }

            skyblock.cratesOpened += 1;

            removeCrate(skyblock);
            Objective.lootboxes(skyblock);
        }

        HashMap<Integer, ItemStack> h = e.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
        for (ItemStack i : h.values()) {
            if (i == null) {
                continue;
            }

            Item t = e.getWorld().dropItem(e.getEyeLocation(), i);
            t.setVelocity(e.getEyeLocation().getDirection().normalize().multiply(0.5));
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

}
