package just.skyblock;

public class C {
	public static void log(Object... a){
		String s = new String();
		for(Object o : a){
			if(s.length() > 0)
				s += " ";
			s += o.toString();
		}
		SkyblockPlugin.plugin.getLogger().info(s);
	}
}
