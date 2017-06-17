package dataAdvanced;

import data.ChunkManager;
import mainInterface.CM;

public class Builder {

	public static void build(int x, int y, int z, Script script) {
		int px = 0, py = 0, pz = 0;
		for (int i = 0; i < script.getNOC(); i++) {
			switch (script.command.get(i)) {
			case Script.SETIFEMPTY:
				if (ChunkManager.getBlockID(px + x + script.x.get(i), py + y + script.y.get(i),
						pz + z + script.z.get(i)) != 0) {
					break;
				}
			case Script.SET:
				CM.setBlock(px + x + script.x.get(i), py + y + script.y.get(i), pz + z + script.z.get(i),
						script.ids.get(i));
				break;
			case Script.FILL:
				// for (int X = script.x.get(i); X <= script.x.get(i + 1); X++)
				// {
				// for (int Y = script.y.get(i); Y <= script.y.get(i + 1); Y++)
				// {
				// for (int Z = script.z.get(i); Z <= script.z.get(i + 1); Z++)
				// {
				// CM.setBlockIDCG(px + x + X, py + y + Y, pz + z + Z,
				// script.ids.get(i));
				// }
				// }
				// }
				SimpleConstructs.fill(script.x.get(i), script.y.get(i), script.z.get(i), script.x.get(i + 1),
						script.y.get(i + 1), script.z.get(i + 1), script.ids.get(i));
				i++;
				break;
			case Script.FILLIFEMPTY:
				// for (int X = script.x.get(i); X <= script.x.get(i + 1); X++)
				// {
				// for (int Y = script.y.get(i); Y <= script.y.get(i + 1); Y++)
				// {
				// for (int Z = script.z.get(i); Z <= script.z.get(i + 1); Z++)
				// {
				// if (ChunkManager.getBlockID(px + x + X, py + y + Y, pz + z +
				// Z) == 0) {
				// CM.setBlockIDCG(px + x + X, py + y + Y, pz + z + Z,
				// script.ids.get(i));
				// }
				// }
				// }
				// }
				SimpleConstructs.fill(script.x.get(i), script.y.get(i), script.z.get(i), script.x.get(i + 1),
						script.y.get(i + 1), script.z.get(i + 1), script.ids.get(i), false);
				i++;
				break;
			case Script.SETTRANSLATION:
				px = script.x.get(i);
				py = script.y.get(i);
				pz = script.z.get(i);
				break;
			}
		}
	}

	public static void build(float x, float y, float z, Script s) {
		build((int) x, (int) y, (int) z, s);
	}

}
