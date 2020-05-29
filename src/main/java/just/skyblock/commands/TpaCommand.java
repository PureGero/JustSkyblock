package just.skyblock.commands;

import just.skyblock.SkyblockPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TpaCommand implements CommandExecutor {

    public TpaCommand(SkyblockPlugin plugin) {
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(org.bukkit.ChatColor.YELLOW + "/tpa is currently not avaliable.");
        sender.sendMessage(ChatColor.YELLOW + "Add someone to your skyblock with /s add <player>");

        return true;
    }
}
