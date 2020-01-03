package just.skyblock;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Island {
    private static HashMap<UUID,Island> cache = new HashMap<UUID,Island>();

    public UUID uuid;
    public Temp temp;

    public String rank = null;
    
    public int x = 0;
    public int z = 0;
    public int oldx = Integer.MAX_VALUE;
    public int oldz = Integer.MAX_VALUE;
    
    public double lx = 0;
    public double ly = 0;
    public double lz = 0;
    public float lyaw = 0;
    public float lpitch = 0;
    
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
    
    public ArrayList<UUID> allowed = new ArrayList<UUID>();
    
    public boolean inIsland(Location l){
        return l.getWorld() == SkyBlock.skyblock.world && 
            Math.floor(l.getBlockX()/512.0) == x && 
            Math.floor(l.getBlockZ()/512.0) == z;
    }
    private Island(){}

    public Location getSpawnLocation(){
        Location il = new Location(SkyBlock.skyblock.world,x*512+256.5-8,65.5,z*512+256.5-8);
        while(il.getBlockY() < 256 && (il.getBlock().getType() != Material.AIR
                || il.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR)){
            il = il.add(0, 1, 0);
        }
        return il;
    }

    public Location getNetherSpawnLocation(){
        Location il = new Location(SkyBlock.skyblock.nether,x*512+256.5-8,65.5,z*512+256.5-8);
        while(il.getBlockY() < 256 && (il.getBlock().getType() != Material.AIR
                || il.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR)){
            il = il.add(0, 1, 0);
        }
        return il;
    }

    public void spawn(Player p){
        p.teleport(getSpawnLocation());
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,20*10,100));
    }
    
    public ArrayList<Player> getPlayers(){
        ArrayList<Player> a = new ArrayList<Player>();
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.getWorld() == SkyBlock.skyblock.world){
                if(inIsland(p.getLocation()))
                    a.add(p);
            }
        }
        return a;
    }
    
    public void reset(){
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
        new File(SkyBlock.skyblock.world.getWorldFolder(),
                "region/r." + (int)Math.floor(oldx/512.0)
                + "." + (int)Math.floor(oldz/512.0) + ".mca")
                    .deleteOnExit(); // Delete region file once server has exited
        if(resetCount >= 2)
            Objective.resetSkyblock(this);
    }
    
    public void calcRank(){
        int rank = (int)(Math.log(Objective.completed(this)+0.1 /*Stop 0*/)/Math.log(2));
        if(rank < 0)rank = 0;
        if(rank >= Rank.ordered.length)rank = Rank.ordered.length-1;
        Rank r = Rank.ordered[rank];
        Player p = Bukkit.getPlayer(uuid);
        if(temp.rank != null && !r.name.equals(temp.rank.name)){
            if(p != null){
                if(this.rank == null)Rank.giveRank(p, r);
                p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                p.sendMessage(ChatColor.GREEN + "You have ranked up to " + r.color + r.prefix);
                p.sendTitle(r.color + r.prefix, ChatColor.GREEN + "You have ranked up!", 10, 80, 10);
            }
        }
        temp.rank = r;
    }
    public Rank getRank(){
        if(rank != null)
            return Rank.getRank(rank);
        if(temp.rank == null)
            calcRank();
        return temp.rank;
    }
    
    public static Island get(Location l){
        if (l.getWorld() != SkyBlock.skyblock.world && l.getWorld() != SkyBlock.skyblock.nether) return null;
        int x = (int) Math.floor(l.getBlockX()/512.0);
        int z = (int) Math.floor(l.getBlockZ()/512.0);
        for (Island i : cache.values()) {
            if (i.x == x && i.z == z) {
                return i;
            }
        }
        return null;
    }
    
    public static Island load(UUID u){
        if(cache.containsKey(u))
            return cache.get(u);
        String s = u.toString().toLowerCase();
        Island c = new Island();
        c.uuid = u;
        c.temp = new Temp(c.uuid);
        File f = new File(SkyBlock.skyblock.getDataFolder(), "skyblocks/" + s + ".json");
        try{
            if(f.isFile()){
                JSONObject o = (JSONObject) new JSONParser().parse(new String(Files.readAllBytes(f.toPath())));
                
                for(Field d : c.getClass().getFields()){
                    if(o.containsKey(d.getName()))
                    try{
                        //if(d.getAnnotation(Data.class) != null){
                            if(d.getType() == int.class)d.setInt(c, (int) (long) o.get(d.getName()));
                            if(d.getType() == long.class)d.setLong(c, (long) o.get(d.getName()));
                            if(d.getType() == double.class)d.setDouble(c, (double) o.get(d.getName()));
                            if(d.getType() == float.class)d.setFloat(c, (float) (double) o.get(d.getName()));
                            if(d.getType() == String.class)d.set(c, o.get(d.getName()));
                        //}
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                
                try{
                    for(Object a : (JSONArray)o.get("allowed"))
                        c.allowed.add(UUID.fromString((String)a));
                }catch(Exception e){e.printStackTrace();}
                
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        c.checkUpdates();
        
        cache.put(u, c);
        return c;
    }
    
    public void checkUpdates(){
        if(update == 0){ // First time loading island
            try{
                FileInputStream fin = new FileInputStream(new File(SkyBlock.skyblock.getDataFolder(),"nextskyblock.csv"));
                byte[] b = new byte[4096];
                int i = fin.read(b);
                fin.close();
                String[] a = new String(b,0,i).split(",");
                x = Integer.parseInt(a[0]);
                z = Integer.parseInt(a[1]);
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                int x = this.x;
                int y = this.z;
                if(x == y)
                    if(x >= 0)
                        x++;
                    else
                        y++;
                else if(y == -x)
                    if(y < 0)
                        x--;
                    else
                        x++;
                else if(Math.abs(y) < Math.abs(x))
                    if(x > 0)
                        y--;
                    else
                        y++;
                else
                    if(y > 0)
                        x++;
                    else
                        x--;
                SkyBlock.skyblock.getDataFolder().mkdirs();
                FileOutputStream fout = new FileOutputStream(new File(SkyBlock.skyblock.getDataFolder(),"nextskyblock.csv"));
                fout.write((x + "," + y + ",1," + uuid + "\r\n").getBytes());
                fout.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            update = 2;
        }
        if (update == 1) {
            SkyBlock.skyblock.world.getBlockAt(x*512+256-9,64,z*512+256-39).setType(Material.GRASS_BLOCK);
            SkyBlock.skyblock.world.getBlockAt(x*512+256-9,64,z*512+256-39).setType(Material.BAMBOO_SAPLING);
            update = 2;
        }
    }

    public static void saveAll(){
        for(Island i : cache.values()){
            i.save();
            if(safeDispose(i)){
                cache.remove(i.uuid);
            }
        }
    }
    public static void disposeAll(){
        for(Island i : cache.values())
            i.save();
        cache.clear();
    }
    public static void safeDispose(UUID uuid){
        if(cache.containsKey(uuid)){
            Island i = cache.get(uuid);
            if(safeDispose(i)){
                i.save();
                cache.remove(uuid);
            }
        }
    }
    private static boolean safeDispose(Island i){
        if(Bukkit.getPlayer(i.uuid) != null)return false; // Player is online
        for(UUID u : i.allowed){
            if(Bukkit.getPlayer(u) != null)return false; // Player is online
        }
        return true;
    }
    public void dispose(){
        save();
        cache.remove(uuid);
    }
    
    public void save(){
        String s = uuid.toString().toLowerCase();
        final File f = new File(SkyBlock.skyblock.getDataFolder(), "skyblocks/" + s + ".json");
        f.getParentFile().mkdirs();
        
        JSONObject o = new JSONObject();
        o.put("uuid", uuid.toString());
        
        JSONArray a = new JSONArray();
        for(UUID u : allowed)a.add(u.toString());
        o.put("allowed", a);
        
        for(Field d : getClass().getFields()){
            try{
                //System.out.println(d.getName() + " @" + d.getAnnotation(Data.class));
                //if(d.getAnnotation(Data.class) != null){
                    if(d.getType() == int.class)o.put(d.getName(), d.getInt(this));
                    if(d.getType() == long.class)o.put(d.getName(), d.getLong(this));
                    if(d.getType() == float.class)o.put(d.getName(), d.getFloat(this));
                    if(d.getType() == double.class)o.put(d.getName(), d.getDouble(this));
                    if(d.getType() == String.class)o.put(d.getName(), d.get(this));
                //}
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        final byte[] b = o.toString().getBytes();
        if(SkyBlock.skyblock.isEnabled() && Bukkit.isPrimaryThread()) // Run async
            Bukkit.getScheduler().runTaskAsynchronously(SkyBlock.skyblock, new Runnable(){
                public void run(){
                    try {
                        Files.write(f.toPath(), b);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        else
            try {
                Files.write(f.toPath(), b);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
