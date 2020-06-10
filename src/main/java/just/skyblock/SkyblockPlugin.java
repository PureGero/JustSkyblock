package just.skyblock;

import just.skyblock.commands.*;
import just.skyblock.dragonfight.DragonHijacker;
import just.skyblock.generator.SkyblockChunkGenerator;
import just.skyblock.listeners.*;
import org.bukkit.*;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyblockPlugin extends JavaPlugin {
    public static SkyblockPlugin plugin = null;
    public World world = null;
    public World nether = null;
    public World end = null;
    public World lobby = null;

    @Override
    public void onEnable() {
        plugin = this;

        registerWorlds();

        Crate.islandCrateTicker();
        Shop.load();
        Skyblock.disposeAll(); // If we dont reference the Skyblock class at least once, onDisable will fail if no Islands get loaded

        for (Player player : Bukkit.getOnlinePlayers()) {
            Skyblock skyblock = Skyblock.load(player);

            Rank.giveRank(player, skyblock.getRank());

            if (skyblock.teleportToLastPos && skyblock.lworld != null) {
                player.teleport(new Location(Bukkit.getWorld(skyblock.lworld), skyblock.lx, skyblock.ly, skyblock.lz, skyblock.lyaw, skyblock.lpitch));

                skyblock.teleportToLastPos = false;
                skyblock.lx = 0;
                skyblock.lz = 0;
            }
        }

        getServer().getScheduler().runTaskTimerAsynchronously(this, Skyblock::saveAll, 10 * 60 * 20L, 10 * 60 * 20L);
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Skyblock i = Skyblock.load(p.getUniqueId());
                i.ontime += 10;
                Objective.ontime(i);
                if (i.coins >= 1000000) {
                    Objective.HAVE_BILLION_COINS.give(i);
                }
                if (i.coins >= 1000000000) {
                    Objective.HAVE_BILLION_COINS.give(i);
                }
            }
        }, 10 * 20, 10 * 20);

        registerCommands();
        registerTabCompleters();
        registerListeners();
        registerExtras();
    }


    private void registerWorlds() {
        lobby = getServer().getWorlds().get(0);

        SkyblockChunkGenerator skyblockChunkGenerator = new SkyblockChunkGenerator(this);

        world = getServer().createWorld(new WorldCreator("skyblock").generator(skyblockChunkGenerator));
        nether = getServer().createWorld(new WorldCreator("skyblock_nether").environment(World.Environment.NETHER).generator(skyblockChunkGenerator));
        end = getServer().createWorld(new WorldCreator("skyblock_the_end").environment(World.Environment.THE_END));

        for (World w : new World[] {lobby, world, nether, end}) {
            if (w != lobby) {
                w.setKeepSpawnInMemory(false);
            }

            w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            w.setDifficulty(Difficulty.NORMAL);
        }

        lobby.setKeepSpawnInMemory(true);

        getServer().setSpawnRadius(0);
    }

    private void registerCommands() {
        getCommand("money").setExecutor(new MoneyCommand(this));
        getCommand("objectives").setExecutor(new ObjectivesCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("rank").setExecutor(new RankCommand(this));
        getCommand("ranks").setExecutor(new RanksCommand(this));
        getCommand("skyblock").setExecutor(new SkyblockCommand(this));
        getCommand("slimechunk").setExecutor(new SlimeChunkCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("tpa").setExecutor(new TpaCommand(this));
    }

    private void registerTabCompleters() {
        getCommand("skyblock").setTabCompleter((TabCompleter) getCommand("skyblock").getExecutor());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ObjectivesListener(this), this);
        getServer().getPluginManager().registerEvents(new SkyblockListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new UsernameCacheListener(this), this);
        getServer().getPluginManager().registerEvents(new EnderSignalListener(), this);

        try {
            if (Class.forName("puregero.network.VoteEvent") != null) {
                getServer().getPluginManager().registerEvents(new VoteListener(this), this);
            }
        } catch (ClassNotFoundException e) {
            getLogger().info("puregero.network.VoteEvent could not be found and was not registered.");
        }
    }
    private void registerExtras() {
        new DragonHijacker(this);
    }

    @Override
    public void onDisable() {
        unregisterWorlds();

        Skyblock.disposeAll();
    }

    private void unregisterWorlds() {
        for (World w : new World[] {world, nether, end}) {
            for (Player player : w.getPlayers()) {
                Skyblock skyblock = Skyblock.load(player);
                Location location = player.getLocation();
                player.teleport(lobby.getSpawnLocation().add(0.5, 0.5, 0.5));

                skyblock.lworld = location.getWorld().getName();
                skyblock.lx = location.getX();
                skyblock.ly = location.getY();
                skyblock.lz = location.getZ();
                skyblock.lyaw = location.getYaw();
                skyblock.lpitch = location.getPitch();
                skyblock.teleportToLastPos = true;
            }

            Bukkit.unloadWorld(w, true);
        }
    }
}
