package just.skyblock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;

/**
 * An interface to Minecraft's UserCache object
 */
public class UsernameCache {

    private static final String LOOKUP_ADDRESS = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    public static UUID getUUID(String username) {
        Player player = Bukkit.getPlayerExact(username);
        if (player != null) {
            return player.getUniqueId();
        }

        try {
            Object userCache = getUserCache();
            Object gameProfile = userCache.getClass().getMethod("getProfile", String.class).invoke(userCache, username);
            if (gameProfile != null) {
                return (UUID) gameProfile.getClass().getMethod("getId").invoke(gameProfile);
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUsername(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return player.getName();
        }

        try {
            Object userCache = getUserCache();
            Object gameProfile = userCache.getClass().getMethod("getProfile", UUID.class).invoke(userCache, uuid);
            if (gameProfile != null) {
                return (String) gameProfile.getClass().getMethod("getName").invoke(gameProfile);
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void addCacheEntry(UUID uuid, String username) {
        try {
            GameProfile gameProfile = new GameProfile(uuid, username);
            Object userCache = getUserCache();

            userCache.getClass().getMethod("a", gameProfile.getClass()).invoke(userCache, gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void lookup(Collection<UUID> uuids) {

        // Remove already cached uuids
        uuids.removeIf(uuid -> getUsername(uuid) != null);

        if (!uuids.isEmpty()) {
            Bukkit.getScheduler().runTaskAsynchronously(SkyblockPlugin.plugin, new Lookup(uuids));
        }
    }

    private static Object getUserCache() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Server server = Bukkit.getServer();

        Field console = server.getClass().getDeclaredField("console");
        console.setAccessible(true);

        Object dedicatedServer = console.get(server);
        return dedicatedServer.getClass().getMethod("getUserCache").invoke(dedicatedServer);
    }

    private static class Lookup implements Runnable {

        private final Collection<UUID> uuids;

        public Lookup(Collection<UUID> uuids) {
            this.uuids = uuids;
        }

        @Override
        public void run() {
            for (UUID uuid : uuids) {
                SkyblockPlugin.plugin.getLogger().info("Looking up username for " + uuid);
                lookup(uuid);
            }
        }

        private void lookup(UUID uuid) {
            try {
                URL url = new URL(String.format(LOOKUP_ADDRESS, uuid.toString().replaceAll("-","")));

                try (InputStreamReader reader = new InputStreamReader(url.openStream())) {

                    JsonObject json = (JsonObject) new JsonParser().parse(reader);

                    if (json.has("name")) {
                        addCacheEntry(uuid, json.get("name").getAsString());
                    } else {
                        throw new IOException("'name' field not found in lookup for uuid " + uuid);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

}
