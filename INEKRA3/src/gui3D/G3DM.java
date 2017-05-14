package gui3D;

import java.util.ArrayList;

public class G3DM {

	private static ArrayList<GuiTex> texes = new ArrayList<GuiTex>();

	public static void add(GuiTex g) {
		if (!texes.contains(g)) {
			texes.add(g);
		}
	}

	public static void remove(GuiTex g) {
		texes.remove(g);
	}

	public static ArrayList<GuiTex> getTexes() {
		return texes;
	}

}
