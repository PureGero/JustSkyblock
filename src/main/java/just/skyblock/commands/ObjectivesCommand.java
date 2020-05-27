package just.skyblock.commands;

import just.skyblock.objectives.Objectives;
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

    private final SkyblockPlugin plugin;

    public ObjectivesCommand(SkyblockPlugin plugin) {
        this.plugin = plugin;
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
            Objectives.sendProgress(player, skyblock);
            player.sendMessage(ChatColor.GREEN + "View all with " + ChatColor.BOLD + "/objectives 1");

        } else if (page < 0 || page > 1 + Objectives.length() / 10) {
            player.sendMessage(ChatColor.RED + "Invalid page");

        } else {
            player.sendMessage(ChatColor.DARK_GREEN + "View the next page with /objectives " + (page + 1));

            for (int j = page * 10 - 10; j < page * 10 && j < Objectives.length(); j++) {
                if (Objectives.has(skyblock, j)) {
                    player.spigot().sendMessage(
                            new ComponentBuilder("[\u2713] " + Objectives.getName(j))
                            .color(ChatColor.GREEN)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(Objectives.getDesc(j)).create()))
                            .create());
                } else {
                    player.spigot().sendMessage(
                            new ComponentBuilder("[\u2715] " + Objectives.getName(j))
                            .color(ChatColor.RED)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(Objectives.getDesc(j)).create()))
                            .create());
                }
            }
        }

        return true;
    }
}
