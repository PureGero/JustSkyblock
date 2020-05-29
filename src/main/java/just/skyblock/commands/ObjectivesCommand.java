package just.skyblock.commands;

import just.skyblock.Objective;
import just.skyblock.Skyblock;
import just.skyblock.SkyblockPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ObjectivesCommand implements CommandExecutor {

    public ObjectivesCommand(SkyblockPlugin plugin) {
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
            return false;
        }

        Player player = (Player) sender;
        Skyblock skyblock = Skyblock.load(player.getUniqueId());

        int page = 0;

        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (Exception e) {
                page = -1;
            }
        }

        player.sendMessage(ChatColor.DARK_GREEN + " --- --- " + ChatColor.GREEN + "Your Objectives" + ChatColor.DARK_GREEN + " --- --- ");

        if (page == 0) {
            player.sendMessage(ChatColor.DARK_GREEN + "Hover to see objective info!");
            Objective.sendProgress(player, skyblock);
            player.sendMessage(ChatColor.GREEN + "View all with " + ChatColor.BOLD + "/objectives 1");

        } else if (page < 0 || page > 1 + Objective.values().length) {
            player.sendMessage(ChatColor.RED + "Invalid page");

        } else {
            player.sendMessage(ChatColor.DARK_GREEN + "View the next page with /objectives " + (page + 1));

            for (int j = page * 10 - 10; j < page * 10 && j < Objective.values().length; j++) {
                Objective objective = Objective.getById(j);

                player.spigot().sendMessage(
                        new ComponentBuilder("[\u2713] " + objective.getName())
                        .color(objective.has(skyblock) ? ChatColor.GREEN : ChatColor.RED)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(objective.getDescription()).create()))
                        .create());
            }
        }

        return true;
    }
}
