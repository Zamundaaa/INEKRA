package dataAdvanced;

import java.util.ArrayList;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import collectionsStuff.SmartByteBuffer;
import controls.Keyboard;
import data.*;
import line.Quad;
import mainInterface.Intraface;
import particles.PTM;
import particles.ParticleMaster;
import renderStuff.DisplayManager;
import toolBox.*;

public class Mark extends SpecialBlock {

	private static final int ACTIVATE = GLFW.GLFW_KEY_ENTER;
	private static final ArrayList<Mark> currentMarks = new ArrayList<Mark>();
	private Quad q;

	public Mark(int x, int y, int z) {
		super(x, y, z);
		// TODO
		if (currentMarks.size() == 3) {
			Mark m = currentMarks.get(0);
			Intraface.deleteBlock(m.x, m.y, m.z);
		}
		currentMarks.add(this);
		q = new Quad(x, y, z, x + 1, y + 1, z + 1);
	}

	@Override
	public void update() {
		float vel = 0.5f;
		float life = 1;
		float distanceFromBorders = 0.2f;
		if (Meth.doChance(5 * DisplayManager.getFrameTimeSeconds())) {
			ParticleMaster.addNewParticle(PTM.fireworks,
					Vects.randomVector3f(x + distanceFromBorders, x + 1 - distanceFromBorders, y + distanceFromBorders,
							y + 1 - distanceFromBorders, z + distanceFromBorders, z + 1 - distanceFromBorders),
					Vects.randomVector3f(-vel, vel, -vel, vel, -vel, vel), 0.1f, life, Meth.randomFloat(0, 360), 0.1f);
		}
		if (Meth.doChance((Keyboard.isKeyDown(ACTIVATE) ? 10 : 3) * DisplayManager.getFrameTimeSeconds())) {
			int i = currentMarks.indexOf(this);
			if (currentMarks.size() > 1) {
				int c = Meth.randomInt(0, currentMarks.size() - 1);
				while (c == i) {
					c = Meth.randomInt(0, currentMarks.size() - 1);
				}
				Mark m = currentMarks.get(c);
				Vector3f v = new Vector3f(m.x - x, m.y - y, m.z - z);
				float r = Meth.randomFloat(0.5f, 1.5f);
				v.mul(r);
				ParticleMaster.addNewParticle(PTM.fireworks, new Vector3f(x + .5f, y + .5f, z + .5f), v, 0, life / r,
						Meth.randomFloat(0, 360), 0.1f);
			}
		}
		q.setColors();
	}

	@Override
	public void cleanUp() {
		currentMarks.remove(this);
		q.hide();
	}

	@Override
	public void initAfterGen() {

	}

	public static void saveToFile(String fileInScreenshotFolder) {
		// TODO
		// File f = new File(Tools.scriptFolder + fileInScreenshotFolder);
		// if(!f.exists()){
		// try {
		// f.createNewFile();
		int sx = currentMarks.get(0).x;
		int sy = currentMarks.get(0).y;
		int sz = currentMarks.get(0).z;
		int zx = sx, zy = sy, zz = sz;
		for (int i = 1; i < currentMarks.size(); i++) {
			Mark m = currentMarks.get(i);
			if (m.x < sx) {
				sx = m.x;
			} else if (m.x > zx) {
				zx = m.x;
			}
			if (m.y < sy) {
				sy = m.y;
			} else if (m.y > zy) {
				zy = m.y;
			}
			if (m.z < sz) {
				sz = m.z;
			} else if (m.z > zz) {
				zz = m.z;
			}
		}
		StringBuilder data = new StringBuilder();
		data.append(Script.fill);
		data.append(Script.cct);
		data.append(zx - sx);
		data.append(Script.coordt);
		data.append(zy - sy);
		data.append(Script.coordt);
		data.append(zz - sz);
		data.append(Script.coordt);
		data.append(0);
		data.append(Script.coordt);
		data.append(0);
		data.append(Script.coordt);
		data.append(0);
		data.append(Script.coordt);
		data.append(Block.AIR);
		data.append(Script.trenner);
		for (int x = sx; x <= zx; x++) {
			for (int y = sy; y <= zy; y++) {
				for (int z = sz; z <= zz; z++) {
					short ID = ChunkManager.getBlockID(x, y, z);
					if (ID != Block.AIR) {
						data.append(Script.set);
						data.append(Script.cct);
						data.append(x - sx);
						data.append(Script.coordt);
						data.append(y - sy);
						data.append(Script.coordt);
						data.append(z - sz);
						data.append(Script.coordt);
						data.append(ID);
						data.append(Script.trenner);
					}
				}
			}
		}
		Tools.writeToFile(Tools.scriptFolderInINEKRA + fileInScreenshotFolder, data.toString());
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
	}

	@Override
	public int metaDataLength() {
		return 0;
	}

	@Override
	public void applyMetaData(SmartByteBuffer data) {
		
	}

	@Override
	public void addMetaData(SmartByteBuffer data) {
		
	}

}
