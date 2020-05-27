package just.skyblock.commands;

import just.skyblock.objectives.Objectives;
import just.skyblock.Skyblock;
import just.skyblock.SkyblockPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkyblockCommand implements CommandExecutor, TabCompleter {

    private final SkyblockPlugin plugin;

    public SkyblockCommand(SkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String l, String[] a) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
            return false;
        }

        final Player p = (Player) sender;
        if (a.length == 0) {
            Skyblock i = Skyblock.load(p.getUniqueId());
            i.spawn(p);
            return true;
        } else if (a.length == 1) {
            if (a[0].equalsIgnoreCase("help") || a[0].equalsIgnoreCase("?")) {
                help(p, l);
            } else if (a[0].equalsIgnoreCase("reset")) {
                reset(p, l);
            }/*else if(a[0].equalsIgnoreCase("old")){
                    p.sendMessage(ChatColor.RED + "WARNING: This skyblock will be deleted soon!");
                    p.sendMessage(ChatColor.RED + "Please remove all the stuff you want to keep from this skyblock.");
                    Skyblock.load(p.getUniqueId()).spawnOld(p);
                }*/ else {
                OfflinePlayer o = plugin.getServer().getOfflinePlayer(a[0]);
                if (o.getFirstPlayed() > 0) {
                    final Skyblock i = Skyblock.load(o.getUniqueId());
                    if (i.allowed.contains(p.getUniqueId())) {
                        i.spawn(p);
                        Objectives.visitAnotherPlot(Skyblock.load(p.getUniqueId()));
                    } else {
                        p.sendMessage(ChatColor.RED + o.getName() + " has not added you to their skyblock!");
                        Skyblock.safeDispose(o.getUniqueId());
                    }
                } else {
                    help(p, l);
                }
            }
        } else if (a.length == 2) {
            if (a[0].equalsIgnoreCase("add") || a[0].equalsIgnoreCase("trust")) {
                add(p, a[1]);
            } else if (a[0].equalsIgnoreCase("remove") || a[0].equalsIgnoreCase("untrust")) {
                remove(p, a[1]);
            } else if (a[0].equalsIgnoreCase("op") && p.hasPermission("skyblock.admin")) {
                op(p, a[1]);
            } else {
                help(p, l);
            }
        } else if (a.length == 3) {
            if (sender.hasPermission("skyblock.admin") && a[0].equalsIgnoreCase("giveloot")) {
                giveLoot(p, a[1], a[2]);
            } else {
                help(p, l);
            }
        } else {
            help(p, l);
        }
        return false;
    }

    private void giveLoot(Player p, String who, String countStr) {
        int count = Integer.parseInt(countStr);
        OfflinePlayer whoPlayer = plugin.getServer().getOfflinePlayer(who);
        if (whoPlayer.getFirstPlayed() > 0) {
            Skyblock skyblock = Skyblock.load(whoPlayer.getUniqueId());
            skyblock.crates += count;
            Skyblock.safeDispose(whoPlayer.getUniqueId());
            p.sendMessage(ChatColor.GREEN + "Given " + whoPlayer.getName() + " " + count + " loot boxes");
            Player givenPlayer = plugin.getServer().getPlayer(whoPlayer.getUniqueId());
            if (givenPlayer != null)
                givenPlayer.sendMessage(ChatColor.YELLOW + "You have been given " + count + " loot boxes to open!");
        } else {
            p.sendMessage(ChatColor.RED + whoPlayer.getName() + " has never played on this server!");
        }
    }

    private void reset(Player player, String label) {
        Skyblock skyblock = Skyblock.load(player.getUniqueId());
        if (skyblock.resetCount >= 3 && skyblock.lastReset > System.currentTimeMillis() - 60 * 60 * 1000L && player.getGameMode() == GameMode.SURVIVAL) {
            player.sendMessage(ChatColor.RED + "You can only reset your skyblock once every hour.");

        } else if (skyblock.lastResetRequest > System.currentTimeMillis() - 30 * 1000L) {
            ArrayList<Player> ps = skyblock.getPlayers();
            skyblock.reset();
            for (Player r : ps) {
                skyblock.spawn(r);
                if (r != player)
                    r.sendMessage(ChatColor.YELLOW + "The owner has reset this skyblock.");
            }
            // skyblock.spawn(p);
            player.sendMessage(ChatColor.GREEN + "Your skyblock has been reset.");
            // p.sendMessage(ChatColor.GREEN + "Forgot something? Go back to your skyblock with "
            //        + ChatColor.DARK_GREEN + "/" + l + " old");

        } else {
            player.sendMessage(ChatColor.YELLOW + "Are you sure you want to reset your skyblock?");
            player.sendMessage(ChatColor.YELLOW + "If so, do /" + label + " reset again!");
            skyblock.lastResetRequest = System.currentTimeMillis();
        }
    }

    private void add(Player player, String who) {
        OfflinePlayer whoPlayer = plugin.getServer().getOfflinePlayer(who);
        if (whoPlayer.getFirstPlayed() > 0) {
            Skyblock skyblock = Skyblock.load(player.getUniqueId());
            if (skyblock.allowed.contains(whoPlayer.getUniqueId())) {
                player.sendMessage(ChatColor.RED + whoPlayer.getName() + " has already been added to your skyblock!");
            } else {
                Objectives.addToPlot(skyblock);
                skyblock.allowed.add(whoPlayer.getUniqueId());
                player.sendMessage(ChatColor.GREEN + whoPlayer.getName() + " has been added to your skyblock!");
            }
        } else {
            player.sendMessage(ChatColor.RED + whoPlayer.getName() + " has never played on this server!");
        }
    }

    private void remove(Player player, String who) {
        OfflinePlayer whoPlayer = plugin.getServer().getOfflinePlayer(who);
        if (whoPlayer.getFirstPlayed() > 0) {
            Skyblock skyblock = Skyblock.load(player.getUniqueId());
            if (skyblock.allowed.contains(whoPlayer.getUniqueId())) {
                skyblock.allowed.remove(whoPlayer.getUniqueId());
                Player removedPlayer = plugin.getServer().getPlayer(whoPlayer.getUniqueId());
                if (removedPlayer != null && skyblock.inIsland(removedPlayer.getLocation())) {
                    removedPlayer.sendMessage(ChatColor.RED + "You have been removed from this skyblock.");
                    removedPlayer.teleport(plugin.lobby.getSpawnLocation().add(0.5, 0.5, 0.5));
                }
                player.sendMessage(ChatColor.GREEN + whoPlayer.getName() + " has been removed from your skyblock!");
            } else {
                player.sendMessage(ChatColor.RED + whoPlayer.getName() + " has been not been added to your skyblock!");
            }
        } else {
            player.sendMessage(ChatColor.RED + whoPlayer.getName() + " has never played on this server!");
        }
    }

    private void op(Player player, String who) {
        OfflinePlayer whoPlayer = plugin.getServer().getOfflinePlayer(who);
        if (whoPlayer.getFirstPlayed() > 0) {
            Skyblock skyblock = Skyblock.load(whoPlayer.getUniqueId());
            skyblock.spawn(player);
            Skyblock.safeDispose(whoPlayer.getUniqueId());
        } else {
            player.sendMessage(ChatColor.RED + whoPlayer.getName() + " has never played on this server!");
        }
    }

    public void help(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.DARK_GREEN + " --- " + ChatColor.GREEN + "Help: SkyblockPlugin" + ChatColor.DARK_GREEN + " --- ");
        sender.sendMessage(ChatColor.GREEN + "/" + label + ChatColor.DARK_GREEN + ": Go to your skyblock");
        sender.sendMessage(ChatColor.GREEN + "/" + label + " <player>" + ChatColor.DARK_GREEN + ": Go to player's skyblock");
        sender.sendMessage(ChatColor.GREEN + "/" + label + " add <player>" + ChatColor.DARK_GREEN + ": Add a player to your skyblock");
        sender.sendMessage(ChatColor.GREEN + "/" + label + " remove <player>" + ChatColor.DARK_GREEN + ": Remove a player from your skyblock");
        sender.sendMessage(ChatColor.GREEN + "/" + label + " reset" + ChatColor.DARK_GREEN + ": Reset your skyblock");
        sender.sendMessage(ChatColor.GREEN + "/spawn" + ChatColor.DARK_GREEN + ": Return to spawn");

        if (sender.hasPermission("skyblock.admin")) {
            sender.sendMessage(ChatColor.GREEN + "/" + label + " op <player>" + ChatColor.DARK_GREEN + ": Go to a player's skyblock even if you aren't added");
            sender.sendMessage(ChatColor.GREEN + "/" + label + " giveloot <player> <amount>" + ChatColor.DARK_GREEN + ": Give a player amount of loot boxes");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> ret = new ArrayList<>();
        if (args.length == 1) {
            for (String s : new String[]{
                    "add", "remove", "reset"
            })
                if (s.startsWith(args[0].toLowerCase()))
                    ret.add(s);
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    Skyblock i = Skyblock.load(p.getUniqueId());
                    if (i.allowed.contains(((Player) sender).getUniqueId()))
                        ret.add(p.getName());
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("trust")) {
                Skyblock i = Skyblock.load(((Player) sender).getUniqueId());
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())
                            && !i.allowed.contains(p.getUniqueId()))
                        ret.add(p.getName());
                }
            } else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("untrust")) {
                for (UUID uuid : Skyblock.load(((Player) sender).getUniqueId()).allowed) {
                    OfflinePlayer p = plugin.getServer().getOfflinePlayer(uuid);
                    if (p.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                        ret.add(p.getName());
                }
            }
        }
        return ret;
    }
}
