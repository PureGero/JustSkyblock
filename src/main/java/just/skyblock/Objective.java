package just.skyblock;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public enum Objective {
    PLAY_1_HOUR("Join the Club", "Play 1 hour", 1),
    OBJECTIVE2("I bet you can't make it to 5", "Play 2 hours", 2),
    OBJECTIVE3("Remember my name", "Play 5 hours", 5),
    OBJECTIVE4("Get some sleep", "Play 10 hours", 10),
    OBJECTIVE5("Sweet 15", "Play 15 hours", 15),
    OBJECTIVE6("72000", "Play 20 hours", 20),
    OBJECTIVE7("A day well spent", "Play 25 hours", 25),
    OBJECTIVE8("You're an addict bro", "Play 30 hours", 30),
    ADD_TO_SKYBLOCK("Friends", "Add someone to your skyblock"),
    VISIT_ANOTHER_SKYBLOCK("Bestfriends", "Visit another player's skyblock"),
    KILL_1_SHOP("MURDERER", "Kill a shop"),
    PLACE_100_BLOCKS("C", "Place 100 blocks", 100),
    OBJECTIVE13("Making progress", "Place 500 blocks", 500),
    OBJECTIVE14("I am the one who blocks", "Place 1000 blocks", 1000),
    OBJECTIVE15("Professional", "Place 2000 blocks", 2000),
    OBJECTIVE16("Empire", "Place 5000 blocks", 5000),
    SELL_100_COBBLE("Starting strong", "Sell 100 cobble", 100),
    OBJECTIVE18("Now we're talking", "Sell 200 cobble", 200),
    OBJECTIVE19("Businessmen", "Sell 500 cobble", 500),
    OBJECTIVE20("$$$", "Sell 1000 cobble", 1000),
    KILL_2_SHOPS("DOUBLE HOMICIDE", "Kill 2 shops at once"),
    KILL_3_SHOPS("MASS MURDER", "Kill 3 shops at once"),
    VOTE_ONCE("Thank you!", "Vote once", 1),
    OBJECTIVE24(":D", "Vote twice", 2),
    OBJECTIVE25("You're too kind", "Vote 10 times", 10),
    OBJECTIVE26("What a legend", "Vote 20 times", 20),
    OBJECTIVE27("Give that man a cookie", "Vote 30 times", 30),
    OBJECTIVE28("You really like me?", "Vote 40 times", 40),
    OBJECTIVE29("We love you", "Vote 50 times", 50),
    OPEN_2_LOOTBOXES("Free stuff :O", "Open 2 lootboxes", 2),
    OBJECTIVE31("That's just crate", "Open 10 lootboxes", 10),
    OBJECTIVE32("Rolling in it", "Open 20 lootboxes", 20),
    OBJECTIVE33("Open 40 lootboxes", "Open 40 lootboxes", 40),
    OBJECTIVE34("Open 70 lootboxes", "Open 70 lootboxes", 70),
    OBJECTIVE35("Escobar", "Open 100 lootboxes", 100),
    KILL_10_MOBS("Get off my lawn", "Kill 10 mobs", 10),
    OBJECTIVE37("Killing it", "Kill 50 mobs", 50),
    OBJECTIVE38("What a slayer", "Kill 100 mobs", 100),
    OBJECTIVE39("Warrier", "Kill 200 mobs", 200),
    OBJECTIVE40("Mom, get the camera!", "Kill 500 mobs", 500),
    PLACE_COMPARATOR("Redstone Expert", "Place a comparator"),
    BREAK_DIAMOND_TOOL("Diamonds aren't forever", "Break a diamond tool"),
    REACH_BUILD_HEIGHT_LIMIT("Reach for the sky", "Reach the build height limit"),
    PLACE_EACH_SAPLING("Green", "Place one of every tree sapling"),
    EXPLODE_CREEPER("His own medicine", "Kill a creeper with TNT"),
    PLACE_BOAT("Void rider", "Place a boat"),
    STRUCK_BY_LIGHTNING("Thunder", "Get struck by lightning"),
    CLICK_STAFF("Rude", "Right-click a staff"),
    SELL_SPAWN_EGG("Denied", "Try to sell a spawn egg"),
    RESET_SKYBLOCK_TWICE("Third time's the charm!", "Reset your skyblock twice"),
    FILL_CHEST_WITH_COBBLE("Cobble horder", "Fill a chest with cobblestone"),
    FILL_DOUBLE_CHEST_WITH_COBBLE("Double cobble horder", "Fill a double chest with cobblestone"),
    PLAY_MUSIC_DISC("Drop the beat", "Play a music disc"),
    ENTER_NETHER("Welcome to Hell", "Enter the nether"),
    LEVEL_100("100s Club", "Obtain 100 xp levels"),
    REEL_ENCHANTED_FISHING_ROD_WITH_ENCHANTED_FISHING_ROD("Enchanception", "Pull in an enchanted fishing rod with an enchanted fishing rod"),
    SLEEP("Sweet Dreams", "Sleep in a bed"),
    CRAFT_ENCHANTING_TABLE("Time for magic", "Craft an enchanting table"),
    TAME_CAT("Time to make some cat videos", "Tame a cat"),
    FULL_ENCHANTED_DIAMOND_ARMOUR("Fancy Suit", "Wear a full set of enchanted diamond armour"),
    ACTIVATE_FULL_POWER_BEACON("Power Overwhelming", "Activate a full power beacon"),
    KILL_8_MOBS_AT_ONCE("Massive Slaughter", "Slay 8 mobs at once with sweeping"),
    PLACE_DIAMOND_BLOCK("Nobody likes a show off", "Place a diamond block"),
    PAY_10000_COINS("Risky Transaction", "Pay another player 10,000 coins"),
    HAVE_MILLION_COINS("Millionaire", "Have 1,000,000 coins"),
    HAVE_BILLION_COINS("Billionaire", "Have 1,000,000,000 coins"),
    KILL_20_MOBS_AT_ONCE("Grim Reaper", "Slay 20 mobs at once with sweeping"),
    EAT_PUFFERFISH("Fugu Chuudoku", "Eat a pufferfish"),
    KILL_WITHER("The Beginning.", "Kill the wither"),
	DROWN_BAT("Why so serious?", "Drown a bat"),
    RIDE_PIG_INTO_VOID("Dr. Strangelove", "Ride a pig into the void"),
    POISON_WITCH("A taste of her own medicine", "Poison a Witch"),
    DUAL_WIELD_SWORDS("Akimbo", "Attack a mob with dual wield enchanted diamond swords"),
    NAME_PARROT("Say hello to my little friend", "Name a Parrot Scarface"),
    FEED_BABY_VILLAGER_POTATO("Junkfood", "Attempt to feed a Baked Potato to a Baby Villager"),
    KILL_100_PANDAS("Endangered", "Kill 100 Pandas");

    private final String name;
    private final String description;
    private final long value;

    Objective(String name, String description) {
        this.name = name;
        this.description = description;
        this.value = 0;
    }

    Objective(String name, String description, long value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public static Objective getById(int id) {
        return values()[id];
    }

    public Objective next() {
        if (this.value == 0) {
            return null; // Not in a sequence
        }

        Objective next = getById(getId() + 1);

        if (next.value < this.value) {
            return null;
        }

        return next;
    }

    public boolean has(Skyblock skyblock) {
        int id = getId();
        return skyblock.objectives.length() > id && skyblock.objectives.charAt(id) == '1';
    }

    public void give(Entity player) {
        give(Skyblock.load(player));
    }
    
    public void give(Skyblock skyblock) {
        if (has(skyblock)) {
            return;
        }

        Player p = Bukkit.getPlayer(skyblock.uuid);
        int id = getId();

        if (p != null) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            p.sendMessage(ChatColor.GOLD + "Objective Complete: " + ChatColor.GREEN + getName() + ChatColor.WHITE + " - " + getDescription());
            p.sendTitle(ChatColor.GOLD + "Objective Complete:", ChatColor.GREEN + getName() + ChatColor.WHITE + " - " + getDescription(), 10, 80, 10);

            BaseComponent[] b = new ComponentBuilder(skyblock.getRank() + " " + p.getName() + " has made the objective ")
                    .append("[" + getName() + "]")
                    .color(ChatColor.GREEN)
                    .event(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(getName() + "\n" + getDescription())
                                    .color(ChatColor.GREEN).create())
                    ).create();
            Bukkit.spigot().broadcast(b);
        }

        while (skyblock.objectives.length() <= id) {
            skyblock.objectives += '0';
        }

        skyblock.objectives = skyblock.objectives.substring(0, id) + '1' + skyblock.objectives.substring(id + 1);

        Bukkit.getScheduler().runTaskLater(SkyblockPlugin.plugin, skyblock::calcRank, 100);
    }

    public static void sendProgress(Player p, Skyblock skyblock) {
        // TODO Create a dedicated ObjectiveSequence and a dynamic way for storing objective sequence data
        Objective[] objectives = new Objective[] {PLAY_1_HOUR, PLACE_100_BLOCKS, SELL_100_COBBLE, VOTE_ONCE, OPEN_2_LOOTBOXES, KILL_10_MOBS};
        double[] values = new double[] {skyblock.ontime / 3600.0, skyblock.blocksPlaced, skyblock.cobbleSold, skyblock.votes, skyblock.cratesOpened, skyblock.mobKills};

        for (int i = 0; i < objectives.length; i++) {
            Objective objective = objectives[i];
            double value = values[i];

            while (objective != null) {
                if (!objective.has(skyblock)) {
                    p.spigot().sendMessage(new ComponentBuilder(progressBar(value / objective.getValue()) + "  ")
                            .append(objective.getName()).color(ChatColor.RED)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(objective.getDescription()).create()))
                            .append(String.format(" - %1$.0f", value / objective.getValue() * 100) + "%")
                            .create());
                    break;
                }

                Objective nextObjective = objective.next();

                if (nextObjective == null) {
                    p.spigot().sendMessage(new ComponentBuilder(progressBar(1) + "  ")
                            .append(objective.getName()).color(ChatColor.GREEN)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(objective.getDescription()).create()))
                            .create());
                }

                objective = nextObjective;
            }
        }
    }

    private static String progressBar(double p) {
        String s = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN;

        double a = 0;
        for (; a < p && a < 1; a += 0.05) {
            s += "=";
        }

        s += ChatColor.BLACK;
        for (; a < 1; a += 0.05) {
            s += "=";
        }

        s += ChatColor.DARK_GREEN + "]";
        return s;
    }

    public static int completedCount(Skyblock i) {
        int c = 0;

        for (char a : i.objectives.toCharArray()) {
            if (a == '1') {
                c++;
            }
        }

        return c;
    }

    public static void updateSequence(Objective objective, Skyblock skyblock, long value) {
        do {
            if (value >= objective.getValue()) {
                objective.give(skyblock);
            }
        } while ((objective = objective.next()) != null);
    }

    public static void ontime(Skyblock i) {
        // TODO Create a dedicated ObjectiveSequence class
        updateSequence(PLAY_1_HOUR, i, i.ontime / 60 / 60);
    }

    public static void blocks(Skyblock i) {
        // TODO Create a dedicated ObjectiveSequence class
        updateSequence(PLACE_100_BLOCKS, i, i.blocksPlaced);
    }

    public static void cobblesell(Skyblock i) {
        // TODO Create a dedicated ObjectiveSequence class
        updateSequence(SELL_100_COBBLE, i, i.cobbleSold);
    }

    public static void vote(Skyblock i) {
        // TODO Create a dedicated ObjectiveSequence class
        updateSequence(VOTE_ONCE, i, i.votes);
    }

    public static void lootboxes(Skyblock i) {
        // TODO Create a dedicated ObjectiveSequence class
        updateSequence(OPEN_2_LOOTBOXES, i, i.cratesOpened);
    }

    public static void mobs(Skyblock i) {
        // TODO Create a dedicated ObjectiveSequence class
        updateSequence(KILL_10_MOBS, i, i.mobKills);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getValue() {
        return value;
    }

    public int getId() {
        int i = 0;
        for (Objective objective : values()) {
            if (objective == this) {
                return i;
            }
            i++;
        }

        throw new RuntimeException("Could not find objective id for " + name() + "(" + getName() + "," + getDescription() + ")");
    }
}
