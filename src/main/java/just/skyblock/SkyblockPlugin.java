package just.skyblock;

import just.skyblock.commands.*;
import just.skyblock.generator.IslandGenerator;
import just.skyblock.objectives.Objectives;
import just.skyblock.objectives.ObjectivesListener;
import org.bukkit.*;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class SkyblockPlugin extends JavaPlugin {
    public static SkyblockPlugin skyblock = null;
    public World world = null;
    public World nether = null;
    public World lobby = null;

    @Override
    public void onEnable() {
        skyblock = this;
        lobby = getServer().getWorlds().get(0);
        lobby.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        //world = getServer().createWorld(new WorldCreator("skyblock").type(WorldType.FLAT).generatorSettings("2;0;1;"));
        world = getServer().createWorld(new WorldCreator("skyblock").generator(new ChunkGenerator() {
            @Override
            public ChunkData generateChunkData(World w, Random r, int x, int z, BiomeGrid biome) {
                // Do nothing
                return createChunkData(w);
            }

            @Override
            public List<BlockPopulator> getDefaultPopulators(World world) {
                return Arrays.asList(new IslandGenerator(SkyblockPlugin.this));
            }
        }));
        world.setKeepSpawnInMemory(false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        nether = getServer().createWorld(new WorldCreator("skyblock_nether").environment(World.Environment.NETHER).generator(new ChunkGenerator() {
            @Override
            public ChunkData generateChunkData(World w, Random r, int x, int z, BiomeGrid biome) {
                // Do nothing
                return createChunkData(w);
            }

            @Override
            public List<BlockPopulator> getDefaultPopulators(World world) {
                return Arrays.asList(new IslandGenerator(SkyblockPlugin.this));
            }
        }));
        nether.setKeepSpawnInMemory(false);
        nether.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        getServer().setSpawnRadius(0);
        Crate.islandCrateTicker();
        Shop.load();
        Skyblock.disposeAll(); // If we dont reference the Skyblock class at least once, onDisable will fail if no Islands get loaded
        for (Player p : Bukkit.getOnlinePlayers()) {
            Rank.giveRank(p, Skyblock.load(p.getUniqueId()).getRank());
        }
        getServer().getScheduler().runTaskTimerAsynchronously(this, Skyblock::saveAll, 10 * 60 * 20L, 10 * 60 * 20L);
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Skyblock i = Skyblock.load(p.getUniqueId());
                i.ontime += 10;
                Objectives.ontime(i);
                if (i.coins >= 1000000) {
                    Objectives.millionCoins(i);
                }
                if (i.coins >= 1000000000) {
                    Objectives.billionCoins(i);
                }
            }
        }, 10 * 20, 10 * 20);

        registerCommands();
        registerTabCompleters();
        registerListeners();
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
    }

    @Override
    public void onDisable() {
        Skyblock.disposeAll();
    }
}
