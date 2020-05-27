package just.skyblock.objectives;

import just.skyblock.Skyblock;
import just.skyblock.SkyblockPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Objectives {
    public static String[] fields = new String[]{
/*  0 */"Join the Club", "Play 1 hour",
        "I bet you can't make it to 5", "Play 2 hours",
        "Remember my name", "Play 5 hours",
        "Get some sleep", "Play 10 hours",
        "Sweet 15", "Play 15 hours",
        "72000", "Play 20 hours",
        "A day well spent", "Play 25 hours",
        "You're an addict bro", "Play 30 hours",
/*  8 */"Friends", "Add someone to your skyblock",
        "Bestfriends", "Visit another player's skyblock",
        "MURDERER", "Kill a shop",
/* 11 */"C", "Place 100 blocks",
        "Making progress", "Place 500 blocks",
        "I am the one who blocks", "Place 1000 blocks",
        "Professional", "Place 2000 blocks",
        "Empire", "Place 4000 blocks",
/* 16 */"Starting strong", "Sell 100 cobble",
        "Now we're talking", "Sell 200 cobble",
        "Businessmen", "Sell 500 cobble",
        "$$$", "Sell 1000 cobble",
/* 20 */"DOUBLE HOMICIDE", "Kill 2 shops at once",
        "MASS MURDER", "Kill 3 shops at once",
/* 22 */"Thank you!", "Vote once",
        ":D", "Vote twice",
        "You're too kind", "Vote 10 times",
        "What a legend", "Vote 20 times",
        "Give that man a cookie", "Vote 30 times",
        "You really like me?", "Vote 40 times",
        "We love you", "Vote 50 times",
/* 29 */"Free stuff :O", "Open 2 lootboxes",
        "That's just crate", "Open 10 lootboxes",
        "Rolling in it", "Open 20 lootboxes",
        "Open 40 lootboxes", "Open 40 lootboxes",
        "Open 70 lootboxes", "Open 70 lootboxes",
        "Escobar", "Open 100 lootboxes",
/* 35 */"Get off my lawn", "Kill 10 mobs",
        "Killing it", "Kill 50 mobs",
        "What a slayer", "Kill 100 mobs",
        "Warrier", "Kill 200 mobs",
        "Mom, get the camera!", "Kill 500 mobs",
/* 40 */"Redstone Expert", "Place a comparator",
        "Diamonds aren't forever", "Break a diamond tool",
        "Reach for the sky", "Reach the build height limit",
        "Green", "Place one of every tree sapling", 
        "His own medicine", "Kill a creeper with TNT", 
/* 45 */"Void rider", "Place a boat", 
        "Thunder", "Get struck by lightning", 
        "Rude", "Right-click a staff", 
        "Denied", "Try to sell a spawn egg", 
        "Third time's the charm!", "Reset your skyblock twice",
/* 50 */"Cobble horder", "Fill a chest with cobblestone", 
        "Double cobble horder", "Fill a double chest with cobblestone",
        "Drop the beat", "Play a music disc",
        "Welcome to Hell", "Enter the nether",
        "100s Club", "Obtain 100 xp levels",
/* 55 */"Enchanception", "Pull in an enchanted fishing rod with an enchanted fishing rod",
        "Sweet Dreams", "Sleep in a bed",
        "Time for magic", "Craft an enchanting table",
        "Time to make some cat videos", "Tame a cat",
        "Fancy Suit", "Wear a full set of enchanted diamond armour",
/* 60 */"Power Overwhelming", "Activate a full power beacon",
        "Massive Slaughter", "Slay 8 mobs at once with sweeping",
        "Nobody likes a show off", "Place a diamond block",
        "Risky Transaction", "Pay another player 10,000 coins",
        "Millionaire", "Have 1,000,000 coins",
/* 65 */"Billionaire", "Have 1,000,000,000 coins",
        "Grim Reaper", "Slay 20 mobs at once with sweeping",
        "Fugu Chuudoku", "Eat a pufferfish",
        "The Beginning.", "Kill the wither",
    };

    public static int length(){
        return fields.length/2;
    }
    public static String getName(int o){
        return fields[o<<1];
    }
    public static String getDesc(int o){
        return fields[(o<<1)|1];
    }
    
    public static void sendProgress(Player p, Skyblock i){
        int[][] a = new int[][]{hours,block,cobble,votes,lootboxes,mobs};
        int[] b = new int[]{0,11,16,22,29,35};
        double[] c = new double[]{i.ontime/3600.0,i.blocksPlaced,i.cobbleSold,i.votes,i.cratesOpened,i.mobKills};
        for(int j=0;j<a.length;j++){
            int[] d = a[j];
            int o = b[j];
            for(int k=0;k<=d.length;k++){
                if(k == d.length){
                    String n = getName(k+o-1);
                    p.spigot().sendMessage(new ComponentBuilder(probar(1) + "  ")
                            .append(n).color(ChatColor.GREEN)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(getDesc(k+o-1)).create()))
                            .create());
                }else if(!has(i,k+o)){
                    String n = getName(k+o);
                    p.spigot().sendMessage(new ComponentBuilder(probar(c[j]/d[k]) + "  ")
                            .append(n).color(ChatColor.RED)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(getDesc(k+o)).create()))
                                    .append(String.format(" - %1$.0f",c[j]/d[k]*100)+"%")
                            .create());
                    break;
                }
            }
        }
    }
    private static String probar(double p){
        String s = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN;
        double a=0;
        for(;a<p&&a<1;a+=0.05){
            s += "=";
        }
        s += ChatColor.BLACK;
        for(;a<1;a+=0.05){
            s += "=";
        }
        s += ChatColor.DARK_GREEN + "]";
        return s;
    }
    
    public static int completed(Skyblock i){
        int c = 0;
        for(char a : i.objectives.toCharArray())
            if(a == '1')
                c++;
        return c;
    }
    
    public static boolean has(Skyblock i, int o){
        if(i.objectives.length() <= o)
            return false;
        return i.objectives.charAt(o) == '1';
    }
    public static void give(final Skyblock i, int o){
        Player p = Bukkit.getPlayer(i.uuid);
        if(p != null){
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            p.sendMessage(ChatColor.GOLD + "Objective Complete: " + ChatColor.GREEN + getName(o) + ChatColor.WHITE + " - " + getDesc(o));
            p.sendTitle(ChatColor.GOLD + "Objective Complete:", ChatColor.GREEN + getName(o) + ChatColor.WHITE + " - " + getDesc(o), 10, 80, 10);
            BaseComponent[] b = new ComponentBuilder(i.getRank() + " " + p.getName() + " has made the objective ")
                    .append("[" + getName(o) + "]")
                    .color(ChatColor.GREEN)
                    .event(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(getName(o) + "\n" + getDesc(o))
                                    .color(ChatColor.GREEN).create())
                    ).create();
            Bukkit.spigot().broadcast(b);
        }
        while(i.objectives.length() <= o){
            i.objectives += '0';
        }
        i.objectives = i.objectives.substring(0, o) + '1' + i.objectives.substring(o+1);
        Bukkit.getScheduler().runTaskLater(SkyblockPlugin.plugin, new Runnable(){
            public void run(){
                i.calcRank();
            }
        }, 100);
    }

    private static int[] hours = new int[]{
        1,2,5,10,15,20,25,30    
    };
    public static void ontime(Skyblock i){
        long t = i.ontime;
        for(int j=0;j<hours.length;j++){
            if(t >= hours[j]*60*60 && !has(i,j))
                give(i,j);
        }
    }
    private static int[] block = new int[]{
        100, 500, 1000, 2000, 4000    
    };
    public static void blocks(Skyblock i){
        long t = i.blocksPlaced;
        for(int j=0;j<block.length;j++){
            if(t >= block[j] && !has(i,11+j))
                give(i,11+j);
        }
    }
    private static int[] cobble = new int[]{
        100, 200, 500, 1000    
    };
    public static void cobblesell(Skyblock i){
        long t = i.cobbleSold;
        for(int j=0;j<cobble.length;j++){
            if(t >= cobble[j] && !has(i,16+j))
                give(i,16+j);
        }
    }
    private static int[] votes = new int[]{
        1, 2, 10, 20, 30, 40, 50
    };
    public static void vote(Skyblock i){
        long t = i.votes;
        for(int j=0;j<votes.length;j++){
            if(t >= votes[j] && !has(i,22+j))
                give(i,22+j);
        }
    }
    private static int[] lootboxes = new int[]{
        2, 10, 20, 40, 70, 100
    };
    public static void lootboxes(Skyblock i){
        long t = i.cratesOpened;
        for(int j=0;j<lootboxes.length;j++){
            if(t >= lootboxes[j] && !has(i,29+j))
                give(i,29+j);
        }
    }
    private static int[] mobs = new int[]{
        10, 50, 100, 200, 500
    };
    public static void mobs(Skyblock i){
        long t = i.mobKills;
        for(int j=0;j<mobs.length;j++){
            if(t >= mobs[j] && !has(i,35+j))
                give(i,35+j);
        }
    }
    public static void addToPlot(Skyblock i){
        if(!has(i,8))
            give(i,8);
    }
    public static void visitAnotherPlot(Skyblock i){
        if(!has(i,9))
            give(i,9);
    }
    public static void placeComparator(Skyblock i){
        if(!has(i,40))
            give(i,40);
    }
    public static void breakDiamond(Skyblock i){
        if(!has(i,41))
            give(i,41);
    }
    public static void reachTop(Skyblock i){
        if(!has(i,42))
            give(i,42);
    }
    public static void placeSaplings(Skyblock i){
        if(!has(i,43))
            give(i,43);
    }
    public static void explodeCreeper(Skyblock i){
        if(!has(i,44))
            give(i,44);
    }
    public static void placeBoat(Skyblock i){
        if(!has(i,45))
            give(i,45);
    }
    public static void lightningStruck(Skyblock i){
        if(!has(i,46))
            give(i,46);
    }
    public static void punchStaff(Skyblock i){
        if(!has(i,47))
            give(i,47);
    }
    public static void sellSpawnEgg(Skyblock i){
        if(!has(i,48))
            give(i,48);
    }
    public static void resetSkyblock(Skyblock i){
        if(!has(i,49))
            give(i,49);
    }
    public static void fillChestCobble(Skyblock i){
        if(!has(i,50))
            give(i,50);
    }
    public static void fillDoubleChestCobble(Skyblock i){
        if(!has(i,51))
            give(i,51);
    }
    public static void musicDisc(Skyblock i){
        if(!has(i,52))
            give(i,52);
    }
    public static void enterNether(Skyblock i) {
        if (!has(i, 53))
            give(i, 53);
    }
    public static void exp100(Skyblock i) {
        if (!has(i, 54))
            give(i, 54);
    }
    public static void echantedRod(Skyblock i) {
        if (!has(i, 55))
            give(i, 55);
    }
    public static void sleep(Skyblock i) {
        if (!has(i, 56))
            give(i, 56);
    }
    public static void enchantingTable(Skyblock i) {
        if (!has(i, 57))
            give(i, 57);
    }
    public static void tameCat(Skyblock i) {
        if (!has(i, 58))
            give(i, 58);
    }
    public static void enchantedDiamondArmour(Skyblock i) {
        if (!has(i, 59))
            give(i, 59);
    }
    public static void fullPowerBeacon(Skyblock i) {
        if (!has(i, 60))
            give(i, 60);
    }
    public static void kill8MobsAtOnce(Skyblock i) {
        if (!has(i, 61))
            give(i, 61);
    }
    public static void placeDiamondBlock(Skyblock i) {
        if (!has(i, 62))
            give(i, 62);
    }
    public static void pay10000(Skyblock i) {
        if (!has(i, 63))
            give(i, 63);
    }
    public static void millionCoins(Skyblock i) {
        if (!has(i, 64))
            give(i, 64);
    }
    public static void billionCoins(Skyblock i) {
        if (!has(i, 65))
            give(i, 65);
    }
    public static void kill20MobsAtOnce(Skyblock i) {
        if (!has(i, 66))
            give(i, 66);
    }
    public static void eatPufferfish(Skyblock i) {
        if (!has(i, 67))
            give(i, 67);
    }
    public static void killWither(Skyblock i) {
        if (!has(i, 68))
            give(i, 68);
    }
    public static void killShop(Skyblock i, int c){
        if(c >= 1)
            if(!has(i,10))
                give(i,10);
        if(c >= 2)
            if(!has(i,20))
                give(i,20);
        if(c >= 3)
            if(!has(i,21))
                give(i,21);
    }
}
