package just.skyblock;

public class C {
    public static void log(Object... array) {
        StringBuilder builder = new StringBuilder();

        for (Object object : array) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(object);
        }

        SkyblockPlugin.plugin.getLogger().info(builder.toString());
    }
}
