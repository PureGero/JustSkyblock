package just.skyblock.dragonfight;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class SkyblockEnderDragon extends EntityEnderDragon {
    private final Location center;

    public SkyblockEnderDragon(World world, Location center) {
        super(EntityTypes.ENDER_DRAGON, world);
        this.center = center;
    }

    private long lastDebugBroadcast = System.currentTimeMillis();

    // aiStep()
    @Override
    public void movementTick() {
        setDragonFightCenter(center);

        if (WorldGenEndTrophy.a == null) {
            Bukkit.broadcastMessage("WorldGenEndTrophy.a is null");
        }

        if (System.currentTimeMillis() > lastDebugBroadcast + 1000) {
            lastDebugBroadcast = System.currentTimeMillis();
            Bukkit.broadcastMessage("Current phase: " + getDragonControllerManager().a().getClass().getSimpleName());
        }

        if (getDragonControllerManager().a() != null && getDragonControllerManager().a().g() != null) {
            //Bukkit.broadcastMessage(getDragonControllerManager().a().g().toString());
        }

        if (WorldGenEndTrophy.a == null) {
            Bukkit.broadcastMessage("WorldGenEndTrophy.a is still null");
        }

        super.movementTick();

        int meCount = 0;
        for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
            if (e.getClassName().equals("just.skyblock.dragonfight.SkyblockEnderDragon") && e.getMethodName().equals("movementTick")) {
                meCount += 1;
            }
        }

        if (meCount != 1) {
            new RuntimeException("Running movementTick inside a movementTick").printStackTrace();
        }

        setDragonFightCenter(null);
        //setDragonFightCenter(new Location(center.getWorld(), 0, 0, 0));
    }

    @Override
    public int l() {
        PathPoint[] nodes;
        int[] nodeAdjacency;

        try {
            Field bR = EntityEnderDragon.class.getDeclaredField("bR");
            bR.setAccessible(true);
            nodes = (PathPoint[]) bR.get(this);

            Field bS = EntityEnderDragon.class.getDeclaredField("bS");
            bS.setAccessible(true);
            nodeAdjacency = (int[]) bS.get(this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        if (nodes[0] == null) {
            for(int i = 0; i < 24; ++i) {
                int j = 5;
                int k;
                int l;
                int i1;
                if (i < 12) {
                    k = MathHelper.d(60.0F * MathHelper.cos(2.0F * (-3.1415927F + 0.2617994F * (float)i)));
                    l = MathHelper.d(60.0F * MathHelper.sin(2.0F * (-3.1415927F + 0.2617994F * (float)i)));
                } else if (i < 20) {
                    i1 = i - 12;
                    k = MathHelper.d(40.0F * MathHelper.cos(2.0F * (-3.1415927F + 0.3926991F * (float)i1)));
                    l = MathHelper.d(40.0F * MathHelper.sin(2.0F * (-3.1415927F + 0.3926991F * (float)i1)));
                    j += 10;
                } else {
                    i1 = i - 20;
                    k = MathHelper.d(20.0F * MathHelper.cos(2.0F * (-3.1415927F + 0.7853982F * (float)i1)));
                    l = MathHelper.d(20.0F * MathHelper.sin(2.0F * (-3.1415927F + 0.7853982F * (float)i1)));
                }

                k = k + center.getBlockX();
                l = l + center.getBlockZ();

                i1 = Math.max(this.world.getSeaLevel() + 10, this.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPosition(k, 0, l)).getY() + j);
                nodes[i] = new PathPoint(k, i1, l);
            }

            nodeAdjacency[0] = 6146;
            nodeAdjacency[1] = 8197;
            nodeAdjacency[2] = 8202;
            nodeAdjacency[3] = 16404;
            nodeAdjacency[4] = 32808;
            nodeAdjacency[5] = 32848;
            nodeAdjacency[6] = 65696;
            nodeAdjacency[7] = 131392;
            nodeAdjacency[8] = 131712;
            nodeAdjacency[9] = 263424;
            nodeAdjacency[10] = 526848;
            nodeAdjacency[11] = 525313;
            nodeAdjacency[12] = 1581057;
            nodeAdjacency[13] = 3166214;
            nodeAdjacency[14] = 2138120;
            nodeAdjacency[15] = 6373424;
            nodeAdjacency[16] = 4358208;
            nodeAdjacency[17] = 12910976;
            nodeAdjacency[18] = 9044480;
            nodeAdjacency[19] = 9706496;
            nodeAdjacency[20] = 15216640;
            nodeAdjacency[21] = 13688832;
            nodeAdjacency[22] = 11763712;
            nodeAdjacency[23] = 8257536;
        }

        return this.o(this.locX(), this.locY(), this.locZ());
    }

    private Field endPodiumLocation = null;
    private void setDragonFightCenter(Location location) {
        try {
            if (endPodiumLocation == null) {
                // net.minecraft.core.BlockPos net.minecraft.world.level.levelgen.feature.EndPodiumFeature.END_PODIUM_LOCATION
                endPodiumLocation = WorldGenEndTrophy.class.getField("a");
                endPodiumLocation.setAccessible(true);

                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(endPodiumLocation, endPodiumLocation.getModifiers() & ~Modifier.FINAL);
            }

            if (location == null) {
                endPodiumLocation.set(null, null);
            } else {
                BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                Bukkit.broadcastMessage("Setting endPodiumLocation to " + blockPosition);
                endPodiumLocation.set(null, blockPosition);
                Bukkit.broadcastMessage("endPodiumLocation.get() = " + endPodiumLocation.get(null) + "; WorldGenEndTrophy.a = " + WorldGenEndTrophy.a);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
