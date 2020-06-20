package just.skyblock.listeners;

import just.skyblock.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.UUID;

public class UsernameCacheListener implements org.bukkit.event.Listener {

    public UsernameCacheListener(SkyblockPlugin plugin) {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Skyblock skyblock = Skyblock.load(e.getPlayer());

        HashSet<UUID> uuidsReferenced = new HashSet<>();

        uuidsReferenced.addAll(skyblock.allowed);
        uuidsReferenced.addAll(skyblock.allowedMe);

        // Ensure these uuids have their usernames cached
        UsernameCache.lookup(uuidsReferenced);
    }
}
