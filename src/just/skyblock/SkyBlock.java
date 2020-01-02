package just.skyblock;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class SkyBlock extends JavaPlugin implements TabCompleter{
    public static SkyBlock skyblock = null;
    public World world = null;
    public World nether = null;
    public World lobby = null;
    
    @Override
    public void onEnable(){
        skyblock = this;
        lobby = getServer().getWorlds().get(0);
        //world = getServer().createWorld(new WorldCreator("skyblock").type(WorldType.FLAT).generatorSettings("2;0;1;"));
        world = getServer().createWorld(new WorldCreator("skyblock").generator(new ChunkGenerator() {
            @Override
            public ChunkData generateChunkData(World w, Random r, int x, int z, BiomeGrid biome) {
                // Do nothing
                return createChunkData(w);
            }

            @Override
            public List<BlockPopulator> getDefaultPopulators(World world) {
                return Arrays.asList(new Generator(SkyBlock.this));
            }
        }));
        world.setKeepSpawnInMemory(false);
        nether = getServer().createWorld(new WorldCreator("skyblock_nether").environment(World.Environment.NETHER).generator(new ChunkGenerator() {
            @Override
            public ChunkData generateChunkData(World w, Random r, int x, int z, BiomeGrid biome) {
                // Do nothing
                return createChunkData(w);
            }

            @Override
            public List<BlockPopulator> getDefaultPopulators(World world) {
                return Arrays.asList(new Generator(SkyBlock.this));
            }
        }));
        nether.setKeepSpawnInMemory(false);
        getServer().getPluginManager().registerEvents(new Listener(this), this);
        getCommand("skyblock").setTabCompleter(this);
        getCommand("slimechunk").setExecutor(new SlimeChunkExecuter(this));
        Crate.islandCrateTicker();
        Shop.load();
        Island.disposeAll(); // If we dont reference the Island class at least once, onDisable will fail if no Islands get loaded
        for(Player p : Bukkit.getOnlinePlayers()){
            Rank.giveRank(p, Island.load(p.getUniqueId()).getRank());
        }
        getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable(){
            public void run(){
                Island.saveAll();
            }
        }, 10*60*20L, 10*60*20L);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable(){
            public void run(){
                for(Player p : Bukkit.getOnlinePlayers()){
                    Island i = Island.load(p.getUniqueId());
                    i.ontime += 10;
                    Objective.ontime(i);
                }
            }
        }, 10*20, 10*20);
    }

    @Override
    public void onDisable(){
        Island.disposeAll();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command c, final String l, final String[] a){
        if(c.getName().equals("spawn")){
            Player p = (Player) sender;
            p.teleport(getServer().getWorlds().get(0).getSpawnLocation().add(0.5, 0.5, 0.5));
            return true;
        }
        if(c.getName().equals("money")){
            Player p = (Player) sender;
            Island i = Island.load(p.getUniqueId());
            p.sendMessage(ChatColor.YELLOW + "You have " + i.coins + " coins!");
            return true;
        }
        if(c.getName().equals("pay")){
            Player p = (Player) sender;
            Island i = Island.load(p.getUniqueId());
            if (a.length < 2) {
                p.sendMessage(ChatColor.RED + "Usage: /" + l + " <player> <amount>");
                return false;
            }
            OfflinePlayer other = Bukkit.getOfflinePlayer(a[0]);
            int amount = Integer.parseInt(a[1]);
            p.sendMessage(ChatColor.YELLOW + "You have " + i.coins + " coins!");
            return true;
        }
        if(c.getName().equals("rank")){
            Player p = (Player) sender;
            Island i = Island.load(p.getUniqueId());
            p.sendMessage("Your rank is: " + i.getRank().color + i.getRank().prefix);
            p.sendMessage("You have completed " + Objective.completed(i) + " objectives.");
            p.sendMessage("See details with " + ChatColor.BOLD + "/objectives");
            return true;
        }
        if(c.getName().equals("ranks")){
            sender.sendMessage(ChatColor.GOLD + "The ranks are as following:");
            sender.sendMessage(ChatColor.DARK_PURPLE + "[SkyCrafter] " + ChatColor.WHITE + "The beginner's rank");
            sender.sendMessage(ChatColor.YELLOW + "[SkyBuilder] " + ChatColor.GRAY + "2 objectives");
            sender.sendMessage(ChatColor.GOLD + "[SkyPrentice] " + ChatColor.WHITE + "4 objectives");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "[SkyWalker] " + ChatColor.GRAY + "8 objectives");
            sender.sendMessage(ChatColor.GREEN + "[SkyMaster] " + ChatColor.WHITE + "16 objectives");
            sender.sendMessage(ChatColor.AQUA + "[SkyLord] " + ChatColor.GRAY + "32 objectives");
            sender.sendMessage(ChatColor.DARK_RED + "[SkyOverlord] " + ChatColor.WHITE + "64 objectives");
        }
        if(c.getName().equals("objectives")){
            Player p = (Player) sender;
            Island i = Island.load(p.getUniqueId());
            int k = 0;
            if(a.length > 0)
                try{
                    k = Integer.parseInt(a[0]);
                }catch(Exception e){
                    k = -1;
                }
            p.sendMessage(ChatColor.DARK_GREEN + " --- --- " + ChatColor.GREEN + "Your Objectives" + ChatColor.DARK_GREEN + " --- --- ");
            if(k == 0){
                p.sendMessage(ChatColor.DARK_GREEN + "Hover to see objective info!");
                Objective.sendProgress(p, i);
                p.sendMessage(ChatColor.GREEN + "View all with " + ChatColor.BOLD + "/objectives 1");
            }else if(k < 0 || k > 1+Objective.length()/10){
                p.sendMessage(ChatColor.RED + "Invalid page");
            }else{
                p.sendMessage(ChatColor.DARK_GREEN + "View the next page with /objectives " + (k+1));
                for(int j=k*10-10;j<k*10&&j<Objective.length();j++){
                    if(Objective.has(i, j)){
                        p.spigot().sendMessage(new ComponentBuilder("[\u2713] " + Objective.getName(j))
                                .color(ChatColor.GREEN)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder(Objective.getDesc(j)).create()))
                                .create());
                    }else{
                        p.spigot().sendMessage(new ComponentBuilder("[\u2715] " + Objective.getName(j))
                                .color(ChatColor.RED)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder(Objective.getDesc(j)).create()))
                                .create());
                    }
                }
            }
            return true;
        }
        if(c.getName().equals("skyblock")){
            
            
            final Player p = (Player) sender;
            if(a.length == 0){
                Island i = Island.load(p.getUniqueId());
                i.spawn(p);
                return true;
            }else if(a.length == 1){
                if(a[0].equalsIgnoreCase("help") || a[0].equalsIgnoreCase("?"))
                    sendHelp(p,l);
                else if(a[0].equalsIgnoreCase("reset")){
                    Island i = Island.load(p.getUniqueId());
                    if(i.resetCount >= 3 && i.lastReset > System.currentTimeMillis()-60*60*1000L){
                        p.sendMessage(ChatColor.RED + "You can only reset your skyblock once every hour.");
                    }else if(i.lastResetRequest > System.currentTimeMillis()-30*1000L){
                        ArrayList<Player> ps = i.getPlayers();
                        i.reset();
                        for(Player r : ps){
                            i.spawn(r);
                            if(r != p)
                                r.sendMessage(ChatColor.YELLOW + "The owner has reset this skyblock.");
                        }
                        //i.spawn(p);
                        p.sendMessage(ChatColor.GREEN + "Your skyblock has been reset.");
                        //p.sendMessage(ChatColor.GREEN + "Forgot something? Go back to your skyblock with "
                        //        + ChatColor.DARK_GREEN + "/" + l + " old");
                    }else{
                        p.sendMessage(ChatColor.YELLOW + "Are you sure you want to reset your skyblock?");
                        p.sendMessage(ChatColor.YELLOW + "If so, do /" + l + " reset again!");
                        i.lastResetRequest = System.currentTimeMillis();
                    }
                }/*else if(a[0].equalsIgnoreCase("old")){
                    p.sendMessage(ChatColor.RED + "WARNING: This skyblock will be deleted soon!");
                    p.sendMessage(ChatColor.RED + "Please remove all the stuff you want to keep from this skyblock.");
                    Island.load(p.getUniqueId()).spawnOld(p);
                }*/else
                    getServer().getScheduler().runTaskAsynchronously(this, new Runnable(){
                        public void run(){
                            OfflinePlayer o = getServer().getOfflinePlayer(a[0]);
                            if(o.getFirstPlayed() > 0){
                                final Island i = Island.load(o.getUniqueId());
                                if(i.allowed.contains(p.getUniqueId())){
                                    getServer().getScheduler().runTask(SkyBlock.this, new Runnable(){
                                        public void run(){
                                            i.spawn(p);
                                        }
                                    });
                                    Objective.visitAnotherPlot(Island.load(p.getUniqueId()));
                                }else{
                                    p.sendMessage(ChatColor.RED + o.getName() + " has not added you to their skyblock!");
                                    Island.safeDispose(o.getUniqueId());
                                }
                            }else{
                                sendHelp(p,l);
                            }
                        }
                    });
            }else if(a.length == 2){
                if(a[0].equalsIgnoreCase("add") || a[0].equalsIgnoreCase("trust")){
                    getServer().getScheduler().runTaskAsynchronously(this, new Runnable(){
                        public void run(){
                            OfflinePlayer o = getServer().getOfflinePlayer(a[1]);
                            if(o.getFirstPlayed() > 0){
                                Island i = Island.load(p.getUniqueId());
                                if(i.allowed.contains(o.getUniqueId()))
                                    p.sendMessage(ChatColor.RED + o.getName() + " has already been added to your skyblock!");
                                else{
                                    Objective.addToPlot(i);
                                    i.allowed.add(o.getUniqueId());
                                    p.sendMessage(ChatColor.GREEN + o.getName() + " has been added to your skyblock!");
                                }
                            }else{
                                p.sendMessage(ChatColor.RED + o.getName() + " has never played on this server!");
                            }
                        }
                    });
                }else if(a[0].equalsIgnoreCase("remove") || a[0].equalsIgnoreCase("untrust")){
                    getServer().getScheduler().runTaskAsynchronously(this, new Runnable(){
                        public void run(){
                            OfflinePlayer o = getServer().getOfflinePlayer(a[1]);
                            if(o.getFirstPlayed() > 0){
                                Island i = Island.load(p.getUniqueId());
                                if(i.allowed.contains(o.getUniqueId())){
                                    i.allowed.remove(o.getUniqueId());
                                    final Player p2 = getServer().getPlayer(o.getUniqueId());
                                    if(p2 != null && i.inIsland(p2.getLocation())){
                                        p2.sendMessage(ChatColor.RED + "You have been removed from this skyblock.");
                                        getServer().getScheduler().runTask(SkyBlock.this, new Runnable(){
                                            public void run(){
                                                p2.teleport(lobby.getSpawnLocation().add(0.5, 0.5, 0.5));
                                            }
                                        });
                                    }
                                    p.sendMessage(ChatColor.GREEN + o.getName() + " has been removed from your skyblock!");
                                }else{
                                    p.sendMessage(ChatColor.RED + o.getName() + " has been not been added to your skyblock!");
                                }
                            }else{
                                p.sendMessage(ChatColor.RED + o.getName() + " has never played on this server!");
                            }
                        }
                    });
                }else if(a[0].equalsIgnoreCase("op") && p.hasPermission("skyblock.admin")){
                    getServer().getScheduler().runTaskAsynchronously(this, new Runnable(){
                        public void run(){
                            final OfflinePlayer o = getServer().getOfflinePlayer(a[1]);
                            if(o.getFirstPlayed() > 0){
                                final Island i = Island.load(o.getUniqueId());
                                getServer().getScheduler().runTask(SkyBlock.this, new Runnable(){
                                    public void run(){
                                        i.spawn(p);
                                        Island.safeDispose(o.getUniqueId());
                                    }
                                });
                            }else{
                                p.sendMessage(ChatColor.RED + o.getName() + " has never played on this server!");
                            }
                        }
                    });
                }else sendHelp(p,l);
            }else if(a.length == 3){
                if(sender.hasPermission("skyblock.admin") && a[0].equalsIgnoreCase("giveloot")){
                    final int j = Integer.parseInt(a[2]);
                    getServer().getScheduler().runTaskAsynchronously(this, new Runnable(){
                        public void run(){
                            OfflinePlayer o = getServer().getOfflinePlayer(a[1]);
                            if(o.getFirstPlayed() > 0){
                                final Island i = Island.load(o.getUniqueId());
                                i.crates += j;
                                Island.safeDispose(o.getUniqueId());
                                p.sendMessage(ChatColor.GREEN + "Given " + o.getName() + " " + j + " loot boxes");
                                Player p = getServer().getPlayer(o.getUniqueId());
                                if(p != null)
                                    p.sendMessage(ChatColor.YELLOW + "You have been given " + j + " loot boxes to open!");
                            }else{
                                p.sendMessage(ChatColor.RED + o.getName() + " has never played on this server!");
                            }
                        }
                    });
                }else sendHelp(p,l);
            }else sendHelp(p,l);
            return false;
        }
        return false;
    }
    public void sendHelp(CommandSender p, String l){
        p.sendMessage(ChatColor.DARK_GREEN + " --- " + ChatColor.GREEN + "Help: SkyBlock" + ChatColor.DARK_GREEN + " --- ");
        p.sendMessage(ChatColor.GREEN + "/"+l + ChatColor.DARK_GREEN + ": Go to your skyblock");
        p.sendMessage(ChatColor.GREEN + "/"+l+" <player>" + ChatColor.DARK_GREEN + ": Go to player's skyblock");
        p.sendMessage(ChatColor.GREEN + "/"+l+" add <player>" + ChatColor.DARK_GREEN + ": Add a player to your skyblock");
        p.sendMessage(ChatColor.GREEN + "/"+l+" remove <player>" + ChatColor.DARK_GREEN + ": Remove a player from your skyblock");
        p.sendMessage(ChatColor.GREEN + "/"+l+" reset" + ChatColor.DARK_GREEN + ": Reset your skyblock");
        p.sendMessage(ChatColor.GREEN + "/spawn" + ChatColor.DARK_GREEN + ": Return to spawn");
        if(p.hasPermission("skyblock.admin")){
            p.sendMessage(ChatColor.GREEN + "/"+l+" op <player>" + ChatColor.DARK_GREEN + ": Go to a player's skyblock even if you aren't added");
            p.sendMessage(ChatColor.GREEN + "/"+l+" giveloot <player> <amount>" + ChatColor.DARK_GREEN + ": Give a player amount of loot boxes");
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String l, String[] args){
        if(command.getName().equals("skyblock")){
            ArrayList<String> ret = new ArrayList<String>();
            if(args.length == 1){
                for(String s : new String[]{
                        "add", "remove", "reset"
                })
                    if(s.startsWith(args[0].toLowerCase()))
                        ret.add(s);
                for(Player p : getServer().getOnlinePlayers()){
                    if(p.getName().toLowerCase().startsWith(args[0].toLowerCase())){
                        Island i = Island.load(p.getUniqueId());
                        if(i.allowed.contains(((Player)sender).getUniqueId()))
                            ret.add(p.getName());
                    }
                }
            }else if(args.length == 2){
                if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("trust")){
                    Island i = Island.load(((Player)sender).getUniqueId());
                    for(Player p : getServer().getOnlinePlayers()){
                        if(p.getName().toLowerCase().startsWith(args[1].toLowerCase())
                                && !i.allowed.contains(p.getUniqueId()))
                            ret.add(p.getName());
                    }
                }else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("untrust")){
                    for(UUID uuid : Island.load(((Player)sender).getUniqueId()).allowed){
                        OfflinePlayer p = getServer().getOfflinePlayer(uuid);
                        if(p.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                            ret.add(p.getName());
                    }
                }
            }
            return ret;
        }
        return null;
    }
}
