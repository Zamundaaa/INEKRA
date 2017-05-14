package cubyWater;

import java.util.ArrayList;

import renderStuff.MasterRenderer;
import toolBox.Meth;
import toolBox.Tools;

public class WaterManager {

	public static final int AVERAGE_ALL = 0, AVERAGE_VISIBLE = 1, AVERAGE_ALL_ROUND_UP = 2,
			AVERAGE_VISIBLE_ROUND_UP = 3;

	public static int reflectionHeightMode = (int) Tools.loadLongPreference("reflectionHeightMode",
			AVERAGE_VISIBLE_ROUND_UP);

	private volatile static ArrayList<Water> watersToRender = new ArrayList<Water>();

	public static ArrayList<Water> getWater() {
		return watersToRender;
	}

	public static void add(Water w) {
		if (!watersToRender.contains(w)) {
			watersToRender.add(w);
		}
	}

	public static void remove(Water w) {
		watersToRender.remove(w);
	}

	public static void cleanUp() {
		watersToRender.clear();
		// WaterUpdater.cleanUp();
	}

	public static void updateSome(int constupdates) {
		if (watersToRender.size() < constupdates) {
			for (int i = 0; i < watersToRender.size(); i++) {
				watersToRender.get(i).update();
			}
		} else {
			for (int i = 0; i < constupdates; i++) {
				watersToRender.get(Meth.randomInt(0, watersToRender.size() - 1)).update();
			}
		}
	}

	public static float getAverageAbsHeight() {
		float ret = 0;
		int checked = 0;
		if (reflectionHeightMode == AVERAGE_VISIBLE || reflectionHeightMode == AVERAGE_VISIBLE_ROUND_UP) {
			for (int i = 0; i < watersToRender.size(); i++) {
				Water w = watersToRender.get(i);
				float wy = w.getSavedPos().y + w.height();
				if (MasterRenderer.FI.testPoint(w.getSavedPos().x, w.getSavedPos().y + w.height(), w.getSavedPos().z)) {
					ret += wy;
					checked++;
				}
			}
		} else {
			for (int i = 0; i < watersToRender.size(); i++) {
				Water w = watersToRender.get(i);
				float wy = w.getSavedPos().y + w.height();
				ret += wy;
				checked++;
			}
		}
		if (checked != 0) {
			ret /= checked;
		} else {
			ret = Meth.waterHeight + 1;
		}
		if (reflectionHeightMode == AVERAGE_ALL_ROUND_UP || reflectionHeightMode == AVERAGE_VISIBLE_ROUND_UP) {
			ret = (float) (Math.floor(ret) + 1);
		}
		return ret;
	}

	public static void incrementRefHMode() {
		reflectionHeightMode++;
		if (reflectionHeightMode > AVERAGE_VISIBLE_ROUND_UP) {
			reflectionHeightMode = AVERAGE_ALL;
		}
	}

	public static String refHMode() {
		switch (reflectionHeightMode) {
		case AVERAGE_ALL:
			return "average all";
		case AVERAGE_ALL_ROUND_UP:
			return "average all and round up";
		case AVERAGE_VISIBLE:
			return "average visible";
		case AVERAGE_VISIBLE_ROUND_UP:
			return "average visible and round up";
		default:
			return "HÄ?";
		}
	}

}
