package just.skyblock;

import just.skyblock.objectives.Objectives;
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
	
	public static void islandCrateTicker(){
		Bukkit.getScheduler().runTaskTimer(SkyblockPlugin.plugin, new Runnable(){
			public void run(){
				for(Player p : Bukkit.getOnlinePlayers()){
					Skyblock i = Skyblock.load(p.getUniqueId());
					if(i.inIsland(p.getLocation())){
						if(i.crates > 0 || i.lastFreeCrate < today()){
							makeCrate(i);
						}else removeCrate(i);
					}
				}
			}
		}, 5*60*20, 5*60*20);
	}
	public static void newCrate(Skyblock i){
		Player p = Bukkit.getPlayer(i.uuid);
		if(p == null)return;
		if(i.inIsland(p.getLocation())){
			if(i.crates > 0 || i.lastFreeCrate < today()){
				makeCrate(i);
			}else removeCrate(i);
		}
	}
	
	public static boolean isCrate(Block b){
		if(b.getWorld() == SkyblockPlugin.plugin.world){
			Skyblock i = Skyblock.get(b.getLocation());
			return i != null && i.crateX == b.getX()
					&& i.crateY == b.getY() && i.crateZ == b.getZ();
		}
		return false;
	}
	
	public static void removeCrate(Skyblock i){
		if(i.crateX != 0){ // Crate doesnt exist
			Block b = SkyblockPlugin.plugin.world.getBlockAt(i.crateX, i.crateY, i.crateZ);
			if(b.getType() == Material.CHEST)
				b.setType(Material.AIR);
			i.crateX = 0;
			i.crateY = 0;
			i.crateZ = 0;
		}
	}
	
	public static void makeCrate(Skyblock i){
		if(i.crateX == 0){ // Crate doesnt exist
			Location il = new Location(SkyblockPlugin.plugin.world,i.x*512+256.5-8,65.5,i.z*512+256.5-8);
			while(il.getBlockY() < 256 && il.getBlock().getType() != Material.AIR){
				il = il.add(0, 1, 0);
			}
			i.crateX = il.getBlockX();
			i.crateY = il.getBlockY();
			i.crateZ = il.getBlockZ();
		}
		Block b = SkyblockPlugin.plugin.world.getBlockAt(i.crateX, i.crateY, i.crateZ);
		b.setType(Material.CHEST);
		Firework fw = (Firework) b.getWorld().spawnEntity(b.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder()
        		.withColor(Color.RED).with(Type.BALL).build();
        fwm.addEffect(effect);
        fwm.setPower(1);
        fw.setFireworkMeta(fwm); 
	}
	
	Skyblock island;
	Inventory inv = null;
	ItemStack[] item = new ItemStack[4];
	Block block;
	
	static final int[] VALUES = new int[]{5,20,50,5};
	static final int[] WORTH = new int[]{50,50,100,50};
	static final int[] POS = new int[]{10,12,14,16};
	public Crate(Player p, Block chest){
		block = chest;
		island = Skyblock.load(p.getUniqueId());
		if(island.crates > 0 || island.lastFreeCrate < today()){
			inv = Bukkit.createInventory(this, 27, "Loot Box");
			Random r = new Random(island.crateSeed);
			for(int i=0;i<POS.length;i++){
				ArrayList<Double> chances = new ArrayList<Double>();
				double lc = 0;
				for(int j=0;j<Shop.lootBoxItems.size();j++){
					int c = Shop.lootBoxValues.get(j);
					if(c >= VALUES[i]){
						lc += 1/Math.log(c);
					}
					chances.add(lc);
				}
				double c = r.nextDouble()*chances.get(chances.size()-1);
				for(int j=0;j<chances.size();j++)
					if(chances.get(j) > c){
						item[i] = Shop.lootBoxItems.get(j).clone();
						if(Shop.lootBoxValues.get(j) < WORTH[i]){
							item[i].setAmount(WORTH[i]/Shop.lootBoxValues.get(j)); // Make the value at least WORTH[i]
						}
						inv.setItem(POS[i], item[i].clone());
						break;
					}
			}
			p.openInventory(inv);
		}else{
			p.sendMessage(ChatColor.RED + "You have no more loot boxes to open!");
			p.sendMessage(ChatColor.RED + "The next daily loot box is in " + ChatColor.BOLD + nextDay());
		}
	}
	
	public void onClose(HumanEntity e){
		boolean same = true;
		for(int i=0;i<POS.length;i++){
			ItemStack s = inv.getItem(POS[i]);
			if(s == null || !s.getType().equals(item[i].getType())
					|| s.getAmount() != item[i].getAmount()){
				same = false;
				for(int j=0;j<i;j++)
					inv.setItem(POS[j], item[j].clone());
				break;
			}else
				inv.setItem(POS[i], new ItemStack(Material.AIR));
		}
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for(ItemStack i : inv.getContents()){
			if(i == null)continue;
			items.add(i.clone());
		}
		if(same){
			e.sendMessage(ChatColor.YELLOW + "You left your loot in the chest!");
			e.sendMessage(ChatColor.YELLOW + "Don't worry, it will still be there for you.");
		}else{
			if(island.lastFreeCrate < today())
				island.lastFreeCrate = today();
			else island.crates -= 1;
			island.crateSeed = new Random(island.crateSeed).nextLong();
			boolean isCrate = isCrate(block);
			if(island.crates > 0){
				e.sendMessage(ChatColor.YELLOW + "You have " + island.crates + " more loot boxes to open!");
				if(isCrate)
					e.sendMessage(ChatColor.YELLOW + "You can open them at " + ChatColor.BOLD + "/spawn");
			}else
				e.sendMessage(ChatColor.YELLOW + "You will get another free loot box in " + nextDay());
			island.cratesOpened += 1;
			//if(isCreate)
				removeCrate(island);
			Objectives.lootboxes(island);
		}
		HashMap<Integer, ItemStack> h = e.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
		for(ItemStack i : h.values()){
			if(i == null)continue;
			Item t = e.getWorld().dropItem(e.getEyeLocation(), i);
			t.setVelocity(e.getEyeLocation().getDirection().normalize().multiply(0.5));
		}
	}
	
	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	public static int today(){
		return (int) (System.currentTimeMillis()/1000/60/60/6);
	}
	public static String nextDay(){
		long t = System.currentTimeMillis()-(System.currentTimeMillis()/1000/60/60/6)*6*60*60*1000L;
		t = (1000L*60*60*6)-t; // Invert
		if(t >= 1000*60*60*2){ // More than 2 hrs
			return t/1000/60/60 + " hours";
		}
		if(t > 1000*60*60){ // 1 hr
			return t/1000/60/60 + " hour";
		}
		if(t >= 1000*60*2){ // More than 2 mins
			return t/1000/60 + " minutes";
		}
		if(t > 1000*60){ // 1 mins
			return t/1000/60 + " minute";
		}
		return t/1000 + " seconds";
	}

}
