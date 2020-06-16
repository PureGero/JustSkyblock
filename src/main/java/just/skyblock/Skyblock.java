package just.skyblock;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;

public class Skyblock {
    private static HashMap<UUID, Skyblock> cache = new HashMap<>();

    public UUID uuid;
    public Temp temp;

    public String rank = null;

    public int x = 0;
    public int z = 0;
    public int oldx = Integer.MAX_VALUE;
    public int oldz = Integer.MAX_VALUE;
    public int size = 0; // 0 = 512x512, 1 = 1536x1536

    public String lworld = null;
    public double lx = 0;
    public double ly = 0;
    public double lz = 0;
    public float lyaw = 0;
    public float lpitch = 0;
    public boolean teleportToLastPos = false;

    public int update = 0;
    public long ontime = 0; // In seconds
    public long ontime_cumulative = 0; // Not used
    public long creation = System.currentTimeMillis();
    public int lastFreeCrate = 0;
    public long lastReset = 0;
    public int resetCount = 0;
    public long lastResetRequest = 0;
    public int coins = 0;
    public int votes = 0;

    public int crates = 0;
    public long crateSeed = System.currentTimeMillis();
    public int cratesOpened = 0;
    public int crateX = 0;
    public int crateY = 0;
    public int crateZ = 0;

    // Objective Stuff
    public String objectives = "";
    public int blocksPlaced = 0;
    public int mobKills = 0;
    public int cobbleSold = 0;
    public int saplings = 0;
    public int doorsBroken = 0;
    public int enderDragonsKilled = 0;

    /** I have allowed these players access to my skyblock */
    public HashSet<UUID> allowed = new HashSet<>();

    /** These players have allowed me to access their skyblock. Do not use this
     *  set for a definite answer that I have access to their skyblock, use
     *  their {@code allowed} instead. */
    public HashSet<UUID> allowedMe = new HashSet<>();

    private Skyblock() {
    }

    public static Skyblock get(Location l) {
        if (l.getWorld() != SkyblockPlugin.plugin.world && l.getWorld() != SkyblockPlugin.plugin.nether && l.getWorld() != SkyblockPlugin.plugin.end) return null;
        int x = (int) Math.floor(l.getBlockX() / 512.0);
        int z = (int) Math.floor(l.getBlockZ() / 512.0);
        int x2 = (int) Math.floor(l.getBlockX() / 1536.0);
        int z2 = (int) Math.floor(l.getBlockZ() / 1536.0);
        for (Skyblock i : cache.values()) {
            if ((i.x == x && i.z == z && i.size == 0) ||
                    (i.x == x2 && i.z == z2 && i.size == 1)) {
                return i;
                
            }
        }
        return null;
    }

    public static Skyblock load(Entity player) {
        return load(player.getUniqueId());
    }

    public static Skyblock load(UUID u) {
        if (cache.containsKey(u)) {
            return cache.get(u);
        }

        String s = u.toString().toLowerCase();
        Skyblock c = new Skyblock();
        c.uuid = u;
        c.temp = new Temp(c.uuid);
        File f = new File(SkyblockPlugin.plugin.getDataFolder(), "skyblocks/" + s + ".json");
        try {
            if (f.isFile()) {
                JSONObject o = (JSONObject) new JSONParser().parse(new String(Files.readAllBytes(f.toPath())));

                for (Field d : c.getClass().getFields()) {
                    if (o.containsKey(d.getName()))
                        try {
                            //if(d.getAnnotation(Data.class) != null){
                            if (d.getType() == boolean.class) d.setBoolean(c, (boolean) o.get(d.getName()));
                            if (d.getType() == int.class) d.setInt(c, (int) (long) o.get(d.getName()));
                            if (d.getType() == long.class) d.setLong(c, (long) o.get(d.getName()));
                            if (d.getType() == double.class) d.setDouble(c, (double) o.get(d.getName()));
                            if (d.getType() == float.class) d.setFloat(c, (float) (double) o.get(d.getName()));
                            if (d.getType() == String.class) d.set(c, o.get(d.getName()));
                            //}
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }

                try {
                    for (Object a : (JSONArray) o.get("allowed"))
                        c.allowed.add(UUID.fromString((String) a));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    for (Object a : (JSONArray) o.get("allowedMe"))
                        c.allowedMe.add(UUID.fromString((String) a));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        c.checkUpdates();

        cache.put(u, c);
        return c;
    }

    public static void saveAll() {
        for (Skyblock i : cache.values()) {
            i.save();
            if (safeDispose(i)) {
                cache.remove(i.uuid);
            }
        }
    }

    public static void disposeAll() {
        for (Skyblock i : cache.values())
            i.save();
        cache.clear();
    }

    public static void safeDispose(UUID uuid) {
        if (cache.containsKey(uuid)) {
            Skyblock i = cache.get(uuid);
            if (safeDispose(i)) {
                i.save();
                cache.remove(uuid);
            }
        }
    }

    private static boolean safeDispose(Skyblock i) {
        if (Bukkit.getPlayer(i.uuid) != null) return false; // Player is online
        for (UUID u : i.allowed) {
            if (Bukkit.getPlayer(u) != null) return false; // Player is online
        }
        return true;
    }

    public boolean inIsland(Location location) {
        if (size == 0) {
            return isSkyblockWorld(location.getWorld()) &&
                    Math.floor(location.getBlockX() / 512.0) == x &&
                    Math.floor(location.getBlockZ() / 512.0) == z;
        } else if (size == 1) {
            return isSkyblockWorld(location.getWorld()) &&
                    Math.floor(location.getBlockX() / 1536.0) == x &&
                    Math.floor(location.getBlockZ() / 1536.0) == z;
        } else {
            throw new IllegalStateException(uuid + "'s Skyblock.size == " + size + " (expected 0 or 1)");
        }
    }

    private boolean isSkyblockWorld(World world) {
        return world == getWorld() || world == getNether() || world == getEnd();
    }

    public World getWorld() {
        return SkyblockPlugin.plugin.world;
    }

    public World getNether() {
        return SkyblockPlugin.plugin.nether;
    }

    public World getEnd() {
        return SkyblockPlugin.plugin.end;
    }

    private Location getCenterLocation(World world) {
        if (size == 0) {
            return new Location(world, x * 512 + 256.5 - 8, 65.5, z * 512 + 256.5 - 8);
        } else {
            return new Location(world, x * 1536 + 768.5 - 8, 65.5, z * 1536 + 768.5 - 8);
        }
    }

    public Location getSpawnLocation() {
        Location spawnLocation = getCenterLocation(getWorld());

        while (spawnLocation.getBlockY() < 256 && (!spawnLocation.getBlock().isEmpty()
                || !spawnLocation.getBlock().getRelative(BlockFace.UP).isEmpty())) {
            spawnLocation = spawnLocation.add(0, 1, 0);
        }

        return spawnLocation;
    }

    public Location getNetherSpawnLocation() {
        Location spawnLocation = getCenterLocation(getNether());

        while (spawnLocation.getBlockY() < 256 && (!isSuitableNetherSpawnBlock(spawnLocation.getBlock())
                || !isSuitableNetherSpawnBlock(spawnLocation.getBlock().getRelative(BlockFace.UP)))) {
            spawnLocation = spawnLocation.add(0, 1, 0);
        }

        return spawnLocation;
    }

    private boolean isSuitableNetherSpawnBlock(Block block) {
        return block.isEmpty() || block.getType() == Material.NETHER_PORTAL;
    }

    public Location getEndSpawnLocation() {
        Location spawnLocation = getCenterLocation(getEnd());

        while (spawnLocation.getBlockY() < 256 && (!spawnLocation.getBlock().isEmpty()
                || !spawnLocation.getBlock().getRelative(BlockFace.UP).isEmpty())) {
            spawnLocation = spawnLocation.add(0, 1, 0);
        }

        return spawnLocation;
    }

    public void spawn(Player p) {
        p.teleport(getSpawnLocation());
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 100));
    }

    public ArrayList<Player> getPlayers() {
        ArrayList<Player> playerList = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (inIsland(player.getLocation())) {
                playerList.add(player);
            }
        }
        return playerList;
    }

    public void reset() {
        oldx = x;
        oldz = z;
        x = 0;
        z = 0;
        ontime = 0;
        ontime_cumulative = 0;
        //allowed = new ArrayList<UUID>();
        lastReset = System.currentTimeMillis();
        resetCount++;
        update = 0;
        checkUpdates();
        save();

        //new File(SkyblockPlugin.plugin.world.getWorldFolder(),
        //        "region/r." + oldx + "." + oldz + ".mca")
        //        .deleteOnExit(); // Delete region file once server has exited

        if (resetCount >= 2)
            Objective.RESET_SKYBLOCK_TWICE.give(this);
    }

    public void calcRank() {
        int rank = (int) (Math.log(Objective.completedCount(this) + 0.1 /*Stop 0*/) / Math.log(2));
        if (rank < 0) rank = 0;
        if (rank >= Rank.ordered.length) rank = Rank.ordered.length - 1;
        Rank r = Rank.ordered[rank];
        Player p = Bukkit.getPlayer(uuid);
        if (temp.rank != null && !r.name.equals(temp.rank.name)) {
            if (p != null) {
                if (this.rank == null) Rank.giveRank(p, r);
                p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                p.sendMessage(ChatColor.GREEN + "You have ranked up to " + r.color + r.prefix);
                p.sendTitle(r.color + r.prefix, ChatColor.GREEN + "You have ranked up!", 10, 80, 10);
            }
        }
        temp.rank = r;
    }

    public Rank getRank() {
        if (rank != null)
            return Rank.getRank(rank);
        if (temp.rank == null)
            calcRank();
        return temp.rank;
    }

    public void checkUpdates() {
        if (update == 0) { // First time loading island
            try {
                FileInputStream fin = new FileInputStream(new File(SkyblockPlugin.plugin.getDataFolder(), "nextskyblock.csv"));
                byte[] b = new byte[4096];
                int i = fin.read(b);
                fin.close();
                String[] a = new String(b, 0, i).split(",");
                x = Integer.parseInt(a[0]);
                z = Integer.parseInt(a[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                int x = this.x;
                int y = this.z;
                if (x == y)
                    if (x >= 0)
                        x++;
                    else
                        y++;
                else if (y == -x)
                    if (y < 0)
                        x--;
                    else
                        x++;
                else if (Math.abs(y) < Math.abs(x))
                    if (x > 0)
                        y--;
                    else
                        y++;
                else if (y > 0)
                    x++;
                else
                    x--;
                SkyblockPlugin.plugin.getDataFolder().mkdirs();
                FileOutputStream fout = new FileOutputStream(new File(SkyblockPlugin.plugin.getDataFolder(), "nextskyblock.csv"));
                fout.write((x + "," + y + ",1," + uuid + "\r\n").getBytes());
                fout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            size = 1;
            update = 3;
        }
        if (update == 1) {
            SkyblockPlugin.plugin.world.getBlockAt(x * 512 + 256 - 9, 64, z * 512 + 256 - 39).setType(Material.GRASS_BLOCK);
            SkyblockPlugin.plugin.world.getBlockAt(x * 512 + 256 - 9, 65, z * 512 + 256 - 39).setType(Material.BAMBOO_SAPLING);
            update = 2;
        }
        if (update == 2) {
            // TODO Fill empty chunks with new islands
            update = 3;
        }
    }

    public void dispose() {
        save();
        cache.remove(uuid);
    }

    @SuppressWarnings("unchecked")
	public void save() {
        String s = uuid.toString().toLowerCase();
        final File f = new File(SkyblockPlugin.plugin.getDataFolder(), "skyblocks/" + s + ".json");
        f.getParentFile().mkdirs();

        if (!f.isFile() && Bukkit.getOfflinePlayer(uuid).getFirstPlayed() == 0) {
            SkyblockPlugin.plugin.getLogger().warning("Not saving Skyblock for " + uuid + "{name: " + Bukkit.getOfflinePlayer(uuid).getName() + "} as they have never played on this server.");
            return;
        }

        JSONObject o = new JSONObject();
        o.put("uuid", uuid.toString());
        
        JSONArray a = new JSONArray();
        for (UUID u : allowed) a.add(u.toString());
        o.put("allowed", a);

        a = new JSONArray();
        for (UUID u : allowedMe) a.add(u.toString());
        o.put("allowedMe", a);

        for (Field d : getClass().getFields()) {
            try {
                //System.out.println(d.getName() + " @" + d.getAnnotation(Data.class));
                //if(d.getAnnotation(Data.class) != null){
                if (d.getType() == boolean.class) o.put(d.getName(), d.getBoolean(this));
                if (d.getType() == int.class) o.put(d.getName(), d.getInt(this));
                if (d.getType() == long.class) o.put(d.getName(), d.getLong(this));
                if (d.getType() == float.class) o.put(d.getName(), d.getFloat(this));
                if (d.getType() == double.class) o.put(d.getName(), d.getDouble(this));
                if (d.getType() == String.class) o.put(d.getName(), d.get(this));
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final byte[] b = o.toString().getBytes();

        if (SkyblockPlugin.plugin.isEnabled() && Bukkit.isPrimaryThread()) { // Run async
            Bukkit.getScheduler().runTaskAsynchronously(SkyblockPlugin.plugin, () -> {
                try {
                    Files.write(f.toPath(), b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            try {
                Files.write(f.toPath(), b);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
