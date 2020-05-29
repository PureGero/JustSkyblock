package just.skyblock.commands;

import just.skyblock.Objective;
import just.skyblock.Skyblock;
import just.skyblock.SkyblockPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {

    public RankCommand(SkyblockPlugin plugin) {
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
            return false;
        }

        Player player = (Player) sender;
        Skyblock skyblock = Skyblock.load(player.getUniqueId());

        player.sendMessage("Your rank is: " + skyblock.getRank().color + skyblock.getRank().prefix);
        player.sendMessage("You have completed " + Objective.completedCount(skyblock) + " objectives.");
        player.sendMessage("See details with " + ChatColor.BOLD + "/objectives");
        return true;
    }
}
