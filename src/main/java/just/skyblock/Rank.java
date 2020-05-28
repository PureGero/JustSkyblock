package just.skyblock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Rank {
    public static ArrayList<Rank> all = new ArrayList<>();

    public static Rank DEFAULT = new Rank("default", "SkyCrafter", ChatColor.DARK_PURPLE);
    public static Rank SKYBUILDER = new Rank("skybuilder", "SkyBuilder", ChatColor.YELLOW);
    public static Rank SKYAPPRENTICE = new Rank("skyapprentice", "SkyPrentice", ChatColor.GOLD);
    public static Rank SKYWALKER = new Rank("skywalker", "SkyWalker", ChatColor.GREEN);
    public static Rank SKYMASTER = new Rank("skymaster", "SkyMaster", ChatColor.AQUA);
    public static Rank SKYLORD = new Rank("skylord", "SkyLord", ChatColor.LIGHT_PURPLE);
    public static Rank SKYOVERLORD = new Rank("skyoverlord", "SkyOverlord", ChatColor.DARK_RED);
    public static Rank MODERATOR = new Rank("moderator", "Mod", ChatColor.AQUA);
    public static Rank ADMIN = new Rank("admin", "Admin", ChatColor.RED);

    public static String[] modPerms = new String[]{
            "skyblock.admin", "pure.moderator"
    };

    public static Rank[] ordered = new Rank[]{
            DEFAULT, SKYBUILDER, SKYAPPRENTICE, SKYWALKER, SKYMASTER, SKYLORD, SKYOVERLORD
    };

    // 2, 4, 8, 16, 32, 64
    private static HashMap<UUID, PermissionAttachment> attachs = new HashMap<>();
    public String name;
    public String prefix;
    public ChatColor color;
    public Team team;

    public Rank(String name, String prefix, ChatColor color) {
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        all.add(this);
        team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(name);
        if (team == null)
            team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(name);
        if (prefix != null)
            team.setPrefix(color + prefix + " ");
        else
            team.setPrefix(color + "");
    }

    public static Rank getRank(String s) {
        Rank r = DEFAULT;
        for (Rank a : all)
            if (a.name.equalsIgnoreCase(s))
                r = a;
        return r;
    }

    public static void giveRank(Player p, Rank r) {
        if (r == null) r = DEFAULT;
        if (r.color == null) p.setPlayerListName(p.getName());
        else {
            if (p.getName().length() > 14)
                p.setPlayerListName(r.color + p.getName().substring(0, 14));
            else
                p.setPlayerListName(r.color + p.getName());
        }
        PermissionAttachment m = attachs.get(p.getUniqueId());
        if (m == null) {
            m = p.addAttachment(SkyblockPlugin.plugin);
            attachs.put(p.getUniqueId(), m);
        } else {
            for (String s : m.getPermissions().keySet())
                m.unsetPermission(s);
        }
        if (Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(p) != null)
            Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(p).removePlayer(p);
        if (r.team != null)
            r.team.addPlayer(p);
        if (r == MODERATOR || r == ADMIN) {
            for (String s : modPerms)
                m.setPermission(s, true);
        }
    }

    public String toString() {
        return color + prefix + ChatColor.RESET;
    }
}
