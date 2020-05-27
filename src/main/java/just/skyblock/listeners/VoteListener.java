package just.skyblock.listeners;

import just.skyblock.Crate;
import just.skyblock.Skyblock;
import just.skyblock.SkyblockPlugin;
import just.skyblock.objectives.Objectives;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import puregero.network.VoteEvent;

public class VoteListener implements Listener {
    private SkyblockPlugin plugin;

    public VoteListener(SkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVote(VoteEvent e) {
        e.getPlayer().sendMessage(ChatColor.GREEN + "Thank you for voting for us at " + e.getWebsite() + ".");
        e.getPlayer().sendMessage(ChatColor.GREEN + "You have earnt a lootbox as a reward!");

        Skyblock skyblock = Skyblock.load(e.getPlayer().getUniqueId());
        skyblock.crates += 1;
        skyblock.votes += 1;
        Objectives.vote(skyblock);

        Bukkit.getScheduler().runTask(plugin, () -> {
            Crate.newCrate(skyblock); // Run in sync
        });
    }
}
