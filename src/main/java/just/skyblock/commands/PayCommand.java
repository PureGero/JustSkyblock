package just.skyblock.commands;

import just.skyblock.Objectives;
import just.skyblock.Skyblock;
import just.skyblock.SkyblockPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final SkyblockPlugin plugin;

    public PayCommand(SkyblockPlugin plugin) {
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

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player> <amount>");
            return false;
        }

        OfflinePlayer receiving = Bukkit.getOfflinePlayer(args[0]);

        if (receiving.getFirstPlayed() == 0) {
            player.sendMessage(ChatColor.RED + args[0] + " has never played on this server!");
            return false;
        }

        if (receiving.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You cannot pay yourself!");
            return false;
        }

        Skyblock receivingSkyblock = Skyblock.load(receiving.getUniqueId());
        int amount = Integer.parseInt(args[1]);

        if (amount > skyblock.coins) {
            player.sendMessage(ChatColor.RED + "You do not have enough coins!");
            return false;
        }

        receivingSkyblock.coins += amount;
        skyblock.coins -= amount;

        if (amount >= 10000) {
            Objectives.pay10000(skyblock);
        }

        player.sendMessage(ChatColor.GREEN + "Paid " + receiving.getName() + " " + amount + " coins!");
        player.sendMessage(ChatColor.YELLOW + "You have " + skyblock.coins + " coins!");

        if (receiving.isOnline()) {
            Player receivingPlayer = Bukkit.getPlayer(receiving.getUniqueId());
            receivingPlayer.playSound(receivingPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            receivingPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " coins from " + player.getName());
        }
        return true;
    }
}
