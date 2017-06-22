package entities.graphicsParts;

public class RawMods {

	public static final short sapling = -1;// "Sapling"
	public static final short torch = -2;// "torch"
	public static final short grass = -3;// "grass"
	
	
	public static final short person = 1;
	public static final short cube = 2;//"cube"
	public static final short gun90 = 3;// gun-90
	public static final short pick90 = 4;// pick-90
	public static final short meteo = 5;// meteo

	public static String[] getPositiveFiles() {
		return new String[]{"person", "cube", "gun-90", "pick-90", "meteo"};
	}
}
