package just.skyblock.commands;

import just.skyblock.Skyblock;
import just.skyblock.SkyblockPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {

    public MoneyCommand(SkyblockPlugin plugin) {
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
            return false;
        }

        Player player = (Player) sender;

        if (label.equals("wallet")) {
            player.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "You open your wallet...");
        }

        Skyblock skyblock = Skyblock.load(player.getUniqueId());
        player.sendMessage(ChatColor.YELLOW + "You have " + skyblock.coins + " coins!");

        return true;
    }
}
