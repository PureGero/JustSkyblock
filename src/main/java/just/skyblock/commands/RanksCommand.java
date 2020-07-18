package just.skyblock.commands;

import just.skyblock.Rank;
import just.skyblock.SkyblockPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RanksCommand implements CommandExecutor {

    public RanksCommand(SkyblockPlugin plugin) {
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
            return false;
        }

        sender.sendMessage(ChatColor.GOLD + "The ranks are as following:");

        int i = 0;
        for (Rank rank : Rank.all) {
            String objectives = rank.objectives + " objectives";

            if (rank.objectives == 0) {
                objectives = "The beginner's rank";
            }

            sender.sendMessage(rank.color + "[" + rank.prefix + "] " + (i++ % 2 == 0 ? ChatColor.WHITE : ChatColor.GRAY) + objectives);
        }

        return true;
    }
}
