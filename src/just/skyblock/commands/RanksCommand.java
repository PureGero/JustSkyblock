package just.skyblock.commands;

import just.skyblock.SkyblockPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RanksCommand implements CommandExecutor {

    private final SkyblockPlugin plugin;

    public RanksCommand(SkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
            return false;
        }

        sender.sendMessage(ChatColor.GOLD + "The ranks are as following:");
        sender.sendMessage(ChatColor.DARK_PURPLE + "[SkyCrafter] " + ChatColor.WHITE + "The beginner's rank");
        sender.sendMessage(ChatColor.YELLOW + "[SkyBuilder] " + ChatColor.GRAY + "2 objectives");
        sender.sendMessage(ChatColor.GOLD + "[SkyPrentice] " + ChatColor.WHITE + "4 objectives");
        sender.sendMessage(ChatColor.GREEN + "[SkyWalker] " + ChatColor.GRAY + "8 objectives");
        sender.sendMessage(ChatColor.AQUA + "[SkyMaster] " + ChatColor.WHITE + "16 objectives");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "[SkyLord] " + ChatColor.GRAY + "32 objectives");
        sender.sendMessage(ChatColor.DARK_RED + "[SkyOverlord] " + ChatColor.WHITE + "64 objectives");

        return true;
    }
}
