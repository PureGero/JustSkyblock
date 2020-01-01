package just.skyblock;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Shop implements InventoryHolder{
    private static final String SHOP_URL = "https://docs.google.com/spreadsheets/d/1KyqFLUQYXmjvSzD3TFOn1mz5yL9r-rfq1IfX6ieKzAk/export?format=csv&id=1KyqFLUQYXmjvSzD3TFOn1mz5yL9r-rfq1IfX6ieKzAk&gid=0";
    private static final String CACHE_PATH = "shop_cache.txt";
    private static File cache = null;
    public static int CHEST_SIZE = 9 * 3;
    private static Profession[] profs = new Profession[]{
        Profession.FARMER, Profession.BUTCHER, Profession.CLERIC,
        Profession.ARMORER, Profession.LIBRARIAN
    };
    
    public static ArrayList<ItemStack> items = new ArrayList<>();
    public static ArrayList<Integer> buyPrices = new ArrayList<>();
    public static ArrayList<Integer> sellPrices = new ArrayList<>();
    public static ArrayList<Double> lootBoxChances = new ArrayList<>();
    public static ArrayList<Double> lootBoxChancesRare = new ArrayList<>();
    public static Location shopSpawn = null;
    public static void load(){
        shopSpawn = SkyBlock.skyblock.lobby.getSpawnLocation();
        cache = new File(SkyBlock.skyblock.getDataFolder(), CACHE_PATH);
        cache.getParentFile().mkdirs();
        Bukkit.getScheduler().runTaskAsynchronously(SkyBlock.skyblock, () -> {
                refreshCache();
                if(downloadCache())
                    refreshCache();
                Bukkit.getScheduler().runTaskTimer(SkyBlock.skyblock, Shop::villagerChecker, 0, 2*60*20L);
        });
    }
    private static boolean downloadCache(){
        try{
            C.log("Downloading shop data...");
            long t = System.currentTimeMillis();
            URL url = new URL(SHOP_URL);
            InputStream in = url.openStream();
            Files.copy(in, cache.toPath(), StandardCopyOption.REPLACE_EXISTING);
            in.close();
            C.log("Downloaded shop data (" + (System.currentTimeMillis()-t) + "ms)");
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private static void refreshCache(){
        try{
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(cache)));
            String h = in.readLine();
            items.clear();
            buyPrices.clear();
            sellPrices.clear();
            lootBoxChances.clear();
            while((h = in.readLine()) != null){
                String[] a = h.split(",");
                if(a[0].equalsIgnoreCase("shop_spawn") && a.length >= 4){
                    try{
                        shopSpawn = new Location(SkyBlock.skyblock.lobby,Double.parseDouble(a[1]),
                                Double.parseDouble(a[2]),Double.parseDouble(a[3]));
                    }catch(Exception e){C.log("Invalid syntax: " + h);}
                }else if(a.length >= 2){
                    try{
                        String id = a[0];
                        String cost = a[1];
                        int buycoins = Integer.parseInt(cost.trim());
                        String sell = a[2];
                        int sellcoins = Integer.parseInt(sell.trim());
                        items.add(new ItemStack(Material.matchMaterial(id),1));
                        buyPrices.add(buycoins);
                        sellPrices.add(sellcoins);
                        double lbc = 1.0/buycoins;
                        if(buycoins <= 5)lbc = 0;
                        double lbr = 0;
                        if(lbc <= 1.0/99)
                            lbr = lbc;
                        if(lootBoxChances.size() == 0){
                            lootBoxChances.add(lbc);
                            lootBoxChancesRare.add(lbr);
                        }else{
                            lootBoxChances.add(lootBoxChances.get(lootBoxChances.size()-1)+lbc);
                            lootBoxChancesRare.add(lootBoxChancesRare.get(lootBoxChancesRare.size()-1)+lbr);
                        }
                    }catch(Exception e){C.log("Invalid syntax: " + h);}
                }
            }
            in.close();
            C.log("Loaded " + items.size() + " shop items.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private static void spawnVillager(int i){
        Villager v = shopSpawn.getWorld().spawn(shopSpawn, Villager.class);
        v.setAdult();
        v.setProfession(profs[i]);
        v.setVillagerLevel(5);
        v.setInvulnerable(true);
        v.setCustomName("Shop " + v.getProfession().name().toLowerCase());
        v.setCustomNameVisible(true);
    }
    
    public static void villagerChecker(){
        boolean[] found = new boolean[(items.size()-1)/CHEST_SIZE+1];
        for(Villager v : shopSpawn.getWorld().getEntitiesByClass(Villager.class)){
            int i = ArrayUtils.indexOf(profs, v.getProfession());
            if(i >= 0 && i < found.length)
                found[i] = true;
        }
        for(int i=0;i<found.length;i++){
            if(!found[i])
                spawnVillager(i);
        }
    }

    private Inventory inv;
    private Profession prof;
    private UUID uuid;
    
    public Shop(Player p, Villager v){
        uuid = p.getUniqueId();
        prof = v.getProfession();
        if (isValid()) {
            inv = Bukkit.createInventory(this, CHEST_SIZE, v.getCustomName());
            prepareItems();
            p.openInventory(inv);
        }
    }
    
    private void prepareItems(){
        inv.clear();
        Island island = Island.load(uuid);
        int index = ArrayUtils.indexOf(profs, prof);
        if (index < 0)
            return;
        int o = index*CHEST_SIZE;
        for(int i=0;i<CHEST_SIZE && i+o < items.size();i++){
            ItemStack s = items.get(i+o).clone();
            int c = buyPrices.get(i+o);
            ItemMeta m = s.getItemMeta();
            List<String> lore = m.getLore();
            if(lore == null){
                lore = new ArrayList<String>();
            }
            lore.add("");
            lore.add(ChatColor.GOLD + "" + c + " coins");
            lore.add(ChatColor.GOLD + "You have " + island.coins);
            lore.add("");
            lore.add(ChatColor.GRAY + "Click to buy 1");
            lore.add(ChatColor.GRAY + "Shift-Click to buy 64");
            m.setLore(lore);
            s.setItemMeta(m);
            inv.setItem(i, s);
        }
    }
    
    public void onClose(HumanEntity p){
        // Um, what do I need to do here? Who knows!
    }

    public void onClick(InventoryClickEvent e) {
        if(e.getClickedInventory() != null && e.getClickedInventory().equals(inv)){
            if(e.getCurrentItem() != null){
                e.setCancelled(true);
                int j = e.getRawSlot() + ArrayUtils.indexOf(profs, prof)*CHEST_SIZE;
                if(j >= items.size())return;
                ItemStack i = items.get(j);
                int c = buyPrices.get(j);
                Island is = Island.load(e.getWhoClicked().getUniqueId());
                if(e.isShiftClick()){
                    int a = 0;
                    while(a < 64){
                        if((a + 1)*c <= is.coins){
                            a += 1;
                        }else break;
                    }
                    if(a == 0){
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You do not have enough coins!");
                    }else{
                        i = i.clone();
                        i.setAmount(a);
                        HashMap<Integer,ItemStack> h = e.getWhoClicked().getInventory().addItem(i);
                        if(h.size() > 0)
                            a -= h.get(0).getAmount();
                        if(a == 0){
                            e.getWhoClicked().sendMessage(ChatColor.RED + "Your inventory is full!");
                        }else{
                            is.coins -= c*a;
                            e.getWhoClicked().sendMessage(ChatColor.GOLD + " - " + c*a + " coins");
                            prepareItems();
                        }
                    }
                }else if(e.isLeftClick()){
                    if(c > is.coins){
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You do not have enough coins!");
                    }else{
                        HashMap<Integer,ItemStack> h = e.getWhoClicked().getInventory().addItem(i.clone());
                        if(h.size() > 0){
                            e.getWhoClicked().sendMessage(ChatColor.RED + "Your inventory is full!");
                        }else{
                            is.coins -= c;
                            e.getWhoClicked().sendMessage(ChatColor.GOLD + " - " + c + " coins");
                            prepareItems();
                        }
                    }
                }
            }
        }else if(e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY){
            e.setCancelled(true);
        }
    }
    
    @Override
    public Inventory getInventory() {
        return inv;
    }

    public boolean isValid() {
        return ArrayUtils.indexOf(profs, prof) >= 0;
    }
}
