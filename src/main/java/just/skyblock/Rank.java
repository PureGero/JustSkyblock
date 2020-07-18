package just.skyblock;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Rank {
    public static ArrayList<Rank> all = new ArrayList<>();

    public static Rank DEFAULT = new Rank("default", "SkyCrafter", ChatColor.DARK_PURPLE, 0);
    public static Rank SKYBUILDER = new Rank("skybuilder", "SkyBuilder", ChatColor.YELLOW, 2);
    public static Rank SKYAPPRENTICE = new Rank("skyapprentice", "SkyPrentice", ChatColor.GOLD, 4);
    public static Rank SKYWALKER = new Rank("skywalker", "SkyWalker", ChatColor.GREEN, 8);
    public static Rank SKYMASTER = new Rank("skymaster", "SkyMaster", ChatColor.AQUA, 16);
    public static Rank SKYWIZARD = new Rank("skywizard", "SkyWizard", ChatColor.LIGHT_PURPLE, 32);
    public static Rank SKYLORD = new Rank("skylord", "SkyLord", ChatColor.DARK_RED, 48);
    public static Rank SKYOVERLORD = new Rank("skyoverlord", "SkyOverlord", ChatColor.of("#8c0000"), 72);

    private static HashMap<UUID, PermissionAttachment> attachs = new HashMap<>();
    public String name;
    public String prefix;
    public ChatColor color;
    public int objectives;
    public Team team;

    public Rank(String name, String prefix, ChatColor color, int objectives) {
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.objectives = objectives;
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
        if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam(p.getName()) != null)
            Bukkit.getScoreboardManager().getMainScoreboard().getTeam(p.getName()).removeEntry(p.getName());
        if (r.team != null)
            r.team.addEntry(p.getName());
    }

    public String toString() {
        return color + prefix + ChatColor.RESET;
    }
}
