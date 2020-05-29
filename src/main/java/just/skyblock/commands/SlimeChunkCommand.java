package just.skyblock.commands;

import just.skyblock.Skyblock;
import just.skyblock.SkyblockPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class SlimeChunkCommand implements CommandExecutor {

    public SlimeChunkCommand(SkyblockPlugin plugin) {
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
            return false;
        }

        Player player = (Player) sender;

        Skyblock skyblock = Skyblock.load(player.getUniqueId());

        Location slimeChunk = getClosestSlimeChunk(skyblock.getSpawnLocation());

        if (slimeChunk == null) {
            player.sendMessage(ChatColor.RED + "You have no slime chunks in your island, that's very unlucky.");
            return false;
        }

        int cx = slimeChunk.getBlockX() >> 4;
        int cz = slimeChunk.getBlockZ() >> 4;
        int fromx = slimeChunk.getBlockX();
        int fromy = 0;
        int fromz = slimeChunk.getBlockZ();
        int tox = slimeChunk.getBlockX() + 0xF;
        int toy = 40;
        int toz = slimeChunk.getBlockZ() + 0xF;

        player.sendMessage(String.format(ChatColor.GREEN + "Your slime chunk is %d,%d (from %d,%d,%d to %d,%d,%d)",
                cx, cz, fromx, fromy, fromz, tox, toy, toz));

        return true;
    }

    private Location getClosestSlimeChunk(Location location) {
        long seed = location.getWorld().getSeed();
        int cx = location.getBlockX() >> 4;
        int cz = location.getBlockZ() >> 4;
        for (int r = 0; r < 16; r++) {
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (isSlimeChunk(seed, cx + x, cz + z)) {
                        return new Location(location.getWorld(), (cx + x) << 4, 0, (cz + z) << 4);
                    }
                }
            }
        }
        return null; // Extremely unlikely this will be reached
    }

    private boolean isSlimeChunk(long seed, int cx, int cz) {
        Random rnd = new Random(
                seed +
                (cx * cx * 0x4c1906) +
                (cx * 0x5ac0db) +
                (cz * cz) * 0x4307a7L +
                (cz * 0x5f24f) ^ 0x3ad8025f
        );
        return rnd.nextInt(10) == 0;
    }
}
