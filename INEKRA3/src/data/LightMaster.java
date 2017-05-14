package data;

import java.util.ArrayDeque;
import java.util.ArrayList;

import blockRendering.ChunkEntity;
import entities.Camera;
import entities.Light;
import toolBox.Vects;

/**
 * this class handles (pretty much all) of point lights. The sun is a bit
 * included but seen as a directional light source
 * 
 * @author Xaver
 *
 */
public class LightMaster {

	public static boolean dontMaskYet = false;

	public static final int MAX_LIGHTS = 8;
	public static final float relevantLightDistanceSquared = Chunk.SIZE * Chunk.SIZE * 4;

	private static ArrayList<Light> lights = new ArrayList<>();
	private static ArrayList<Light> relevants = new ArrayList<>();

	private static ArrayDeque<Integer> xs = new ArrayDeque<>(), ys = new ArrayDeque<>(), zs = new ArrayDeque<>(),
			values = new ArrayDeque<>(), xa = new ArrayDeque<>(), ya = new ArrayDeque<>(), za = new ArrayDeque<>(),
			va = new ArrayDeque<>(), sxs = new ArrayDeque<>(), sys = new ArrayDeque<>(), szs = new ArrayDeque<>(),
			svalues = new ArrayDeque<>();
	private static ArrayDeque<Key3D> toUpdate = new ArrayDeque<>(), toRemove = new ArrayDeque<>();
	private static ArrayDeque<Chunk> toLoadSun = new ArrayDeque<>();
	private static ArrayDeque<Integer> sux = new ArrayDeque<>(), suy = new ArrayDeque<>(), suz = new ArrayDeque<>();
	// private static ArrayDeque<ArrayList<Light>> LU = new
	// ArrayDeque<ArrayList<Light>>();

//	public static final Thread lightUpdater = new Thread("LightUpdater") {// deactivated
//																			// anyway
//		@Override
//		public void run() {
//			while (ThreadManager.running()) {
//				while (toLoadSun.size() == 0) {
//					Meth.wartn(10);
//				}
//				while (toLoadSun.size() > 0) {
//					Chunk c = toLoadSun.poll();
//					if (!c.unloaded)
//						doSunLightLoad(c);
//				}
				// while (suz.size() > 0) {
				// doSunLightUpdate(sux.poll(), suy.poll(), suz.poll());
				// }
				// while(LU.size() > 0){
				// ArrayList<Light> ls = LU.poll();
				//// dontMaskYet = true;
				// for(int i = 0; i < ls.size(); i++){
				// updateLightRemoval(ls.get(i));
				// }
				//// dontMaskYet = false;
				// for(int i = 0; i < ls.size(); i++){
				// updateLight(ls.get(i));
				// }
				// }
//			}
//		}
//	};

	public static void init() {
		// if(!lightUpdater.isAlive())
		// lightUpdater.start();
	}

	public static void update() {
		Key3D k;
		while (toUpdate.size() > 0) {
			k = toUpdate.poll();
			updateLight(k);
			k.flush();
		}
		while (toRemove.size() > 0) {
			k = toRemove.poll();
			updateLightRemoval(k);
			k.flush();
		}
		while (toLoadSun.size() > 0) {
			Chunk c = toLoadSun.poll();
			if (!c.unloaded)
				doSunLightLoad(c);
		}
		while (suz.size() > 0) {
			doSunLightUpdate(sux.poll(), suy.poll(), suz.poll());
		}

	}

	public static void addLight(Light l) {
		lights.add(l);
		toUpdate.add(Key3D.getInstance((int) Math.floor(l.getPosition().x), (int) Math.floor(l.getPosition().y),
				(int) Math.floor(l.getPosition().z)));
	}

	public static void addLightUpdate(int x, int y, int z) {
//		toUpdate.add(new Key3D(x, y, z));
		toUpdate.add(Key3D.getInstance(x, y, z));
	}

	public static void removeLight(Light l) {
		lights.remove(l);
//		toRemove.add(new Key3D((int) Math.floor(l.getPosition().x), (int) Math.floor(l.getPosition().y),
//				(int) Math.floor(l.getPosition().z)));
		toRemove.add(Key3D.getInstance((int) Math.floor(l.getPosition().x), (int) Math.floor(l.getPosition().y),
				(int) Math.floor(l.getPosition().z)));
	}

	public static void getLights(ChunkEntity entity, Light[] ls) {
		Vects.calcVect.set(entity.getX() + Chunk.SIZE * 0.5f, entity.getY() + Chunk.SIZE * 0.5f,
				entity.getZ() + Chunk.SIZE * 0.5f);
		for (int i = 0; i < lights.size(); i++) {
			if (Vects.calcVect.distanceSquared(lights.get(i).getPosition()) <= relevantLightDistanceSquared) {
				relevants.add(lights.get(i));
			}
		}
		for (int x = 1; x < MAX_LIGHTS; x++) {
			if (relevants.size() > 0) {
				int nearest = 0;
				float distSq = Camera.getPosition().distanceSquared(relevants.get(0).getPosition());
				float shortest = distSq;
				for (int i = 1; i < relevants.size(); i++) {
					distSq = Camera.getPosition().distanceSquared(relevants.get(i).getPosition());
					if (distSq < shortest) {
						nearest = i;
						shortest = distSq;
					}
				}
				ls[x] = relevants.get(nearest);
				relevants.remove(nearest);
			} else {
				ls[x] = null;
			}
		}
		relevants.clear();
	}

	// private static void updateLight(Light l) {
	// updateLight((int) Math.floor(l.getPosition().x), (int)
	// Math.floor(l.getPosition().y),
	// (int) Math.floor(l.getPosition().z));
	// }

	private static void updateLight(Key3D k) {
		updateLight(k.getX(), k.getY(), k.getZ());
	}

	private static void updateLight(int x, int y, int z) {// CAN AND SHOULD BE
															// OPTIMIZED!!!
		xs.clear();
		ys.clear();
		zs.clear();
		values.clear();
		Chunk c = ChunkManager.getWithBlockCoords(x, y, z);
		if (c == null) {
			return;
		}
		xs.add(x);
		ys.add(y);
		zs.add(z);
		values.add(c.getTorchLight(x, y, z));
		boolean b;
		while (xs.size() > 0) {
			x = xs.poll();
			y = ys.poll();
			z = zs.poll();
			int val = values.poll();
			b = (x + 1) % Chunk.SIZE == 0;
			if (b) {
				c = ChunkManager.getWithBlockCoords(x + 1, y, z);
			} else {
				c = ChunkManager.getWithBlockCoords(x, y, z);
			}
			if (c != null && Block.isTransparent(c.get(x + 1, y, z)) && c.getTorchLight(x + 1, y, z) <= val - 2) {
				c.setTorchLight(x + 1, y, z, Math.max(0, val - Block.lightReduction(c.get(x + 1, y, z))));
				xs.add(x + 1);
				ys.add(y);
				zs.add(z);
				values.add(Math.max(0, val - Block.lightReduction(c.get(x + 1, y, z))));
			}
			if (b) {
				c = ChunkManager.getWithBlockCoords(x - 1, y, z);
			} else {
				b = x % Chunk.SIZE == 0;
				if (b)
					c = ChunkManager.getWithBlockCoords(x - 1, y, z);
			}
			if (c != null && Block.isTransparent(c.get(x - 1, y, z)) && c.getTorchLight(x - 1, y, z) <= val - 2) {
				c.setTorchLight(x - 1, y, z, Math.max(0, val - Block.lightReduction(c.get(x - 1, y, z))));
				xs.add(x - 1);
				ys.add(y);
				zs.add(z);
				values.add(Math.max(0, val - Block.lightReduction(c.get(x - 1, y, z))));
			}
			if (b) {
				c = ChunkManager.getWithBlockCoords(x, y + 1, z);
			} else {
				b = (y + 1) % Chunk.SIZE == 0;
				if (b)
					c = ChunkManager.getWithBlockCoords(x, y + 1, z);
			}
			if (c != null && Block.isTransparent(c.get(x, y + 1, z)) && c.getTorchLight(x, y + 1, z) <= val - 2) {
				c.setTorchLight(x, y + 1, z, Math.max(0, val - Block.lightReduction(c.get(x, y + 1, z))));
				xs.add(x);
				ys.add(y + 1);
				zs.add(z);
				values.add(Math.max(0, val - Block.lightReduction(c.get(x, y + 1, z))));
			}
			if (b) {
				c = ChunkManager.getWithBlockCoords(x, y - 1, z);
			} else {
				b = y % Chunk.SIZE == 0;
				if (b)
					c = ChunkManager.getWithBlockCoords(x, y - 1, z);
			}
			if (c != null && Block.isTransparent(c.get(x, y - 1, z)) && c.getTorchLight(x, y - 1, z) <= val - 2) {
				c.setTorchLight(x, y - 1, z, Math.max(0, val - Block.lightReduction(c.get(x, y - 1, z))));
				xs.add(x);
				ys.add(y - 1);
				zs.add(z);
				values.add(Math.max(0, val - Block.lightReduction(c.get(x, y - 1, z))));
			}
			if (b) {
				c = ChunkManager.getWithBlockCoords(x, y, z + 1);
			} else {
				b = (z + 1) % Chunk.SIZE == 0;
				if (b)
					c = ChunkManager.getWithBlockCoords(x, y, z + 1);
			}
			if (c != null && Block.isTransparent(c.get(x, y, z + 1)) && c.getTorchLight(x, y, z + 1) <= val - 2) {
				c.setTorchLight(x, y, z + 1, Math.max(0, val - Block.lightReduction(c.get(x, y, z + 1))));
				xs.add(x);
				ys.add(y);
				zs.add(z + 1);
				values.add(Math.max(0, val - Block.lightReduction(c.get(x, y, z + 1))));
			}
			if (b) {
				c = ChunkManager.getWithBlockCoords(x, y, z - 1);
			} else {
				b = z % Chunk.SIZE == 0;
				if (b)
					c = ChunkManager.getWithBlockCoords(x, y, z - 1);
			}
			if (c != null && Block.isTransparent(c.get(x, y, z - 1)) && c.getTorchLight(x, y, z - 1) <= val - 2) {
				c.setTorchLight(x, y, z - 1, Math.max(0, val - Block.lightReduction(c.get(x, y, z - 1))));
				xs.add(x);
				ys.add(y);
				zs.add(z - 1);
				values.add(Math.max(0, val - Block.lightReduction(c.get(x, y, z - 1))));
			}
			// removal not necessary!
		}
	}

	private static void updateLightRemoval(Key3D k) {
		updateLightRemoval(k.getX(), k.getY(), k.getZ());
	}

	private static void updateLightRemoval(int x, int y, int z) {// CAN AND
																	// SHOULD BE
																	// OPTIMIZED!!!

		Chunk c = ChunkManager.getWithBlockCoords(x, y, z);
		if (c != null) {
			xs.clear();
			ys.clear();
			zs.clear();
			values.clear();

			xa.clear();
			ya.clear();
			za.clear();
			va.clear();

			c.setTorchLight(x, y, z, 0);
			xs.add(x);
			ys.add(y);
			zs.add(z);
			values.add((int) Chunk.LIGHTR - 1);

			while (!xs.isEmpty()) {
				x = xs.poll();// here?!?
				y = ys.poll();
				z = zs.poll();
				int val = values.poll();
				c = ChunkManager.getWithBlockCoords(x + 1, y, z);
				if (c != null) {
					int ka = c.getTorchLight(x + 1, y, z);
					if (ka != 0 && ka < val) {
						c.setTorchLight(x + 1, y, z, 0);
						xs.add(x + 1);
						ys.add(y);
						zs.add(z);
						values.add(Math.max(0, val - Block.lightReduction(c.get(x + 1, y, z))));
					} else if (ka >= val) {
						xa.add(x + 1);
						ya.add(y);
						za.add(z);
						va.add(ka);
					}
				}
				c = ChunkManager.getWithBlockCoords(x - 1, y, z);
				if (c != null) {
					int ka = c.getTorchLight(x - 1, y, z);
					if (ka != 0 && ka < val) {
						c.setTorchLight(x - 1, y, z, 0);
						xs.add(x - 1);
						ys.add(y);
						zs.add(z);
						values.add(Math.max(0, val - Block.lightReduction(c.get(x - 1, y, z))));
					} else if (ka >= val) {
						xa.add(x - 1);
						ya.add(y);
						za.add(z);
						va.add(ka);
					}
				}
				c = ChunkManager.getWithBlockCoords(x, y + 1, z);
				if (c != null) {
					int ka = c.getTorchLight(x, y + 1, z);
					if (ka != 0 && ka < val) {
						c.setTorchLight(x, y + 1, z, 0);
						xs.add(x);
						ys.add(y + 1);
						zs.add(z);
						values.add(Math.max(0, val - Block.lightReduction(c.get(x, y + 1, z))));
					} else if (ka >= val) {
						xa.add(x);
						ya.add(y + 1);
						za.add(z);
						va.add(ka);
					}
				}
				c = ChunkManager.getWithBlockCoords(x, y - 1, z);
				if (c != null) {
					int ka = c.getTorchLight(x, y - 1, z);
					if (ka != 0 && ka < val) {
						c.setTorchLight(x, y - 1, z, 0);
						xs.add(x);
						ys.add(y - 1);
						zs.add(z);
						values.add(Math.max(0, val - Block.lightReduction(c.get(x, y - 1, z))));
					} else if (ka >= val) {
						xa.add(x);
						ya.add(y - 1);
						za.add(z);
						va.add(ka);
					}
				}
				c = ChunkManager.getWithBlockCoords(x, y, z + 1);
				if (c != null) {
					int ka = c.getTorchLight(x, y, z + 1);
					if (ka != 0 && ka < val) {
						c.setTorchLight(x, y, z + 1, 0);
						xs.add(x);
						ys.add(y);
						zs.add(z + 1);
						values.add(Math.max(0, val - Block.lightReduction(c.get(x, y, z + 1))));
					} else if (ka >= val) {
						xa.add(x);
						ya.add(y);
						za.add(z + 1);
						va.add(ka);
					}
				}
				c = ChunkManager.getWithBlockCoords(x, y, z - 1);
				if (c != null) {
					int ka = c.getTorchLight(x, y, z - 1);
					if (ka != 0 && ka < val) {
						c.setTorchLight(x, y, z - 1, 0);
						xs.add(x);
						ys.add(y);
						zs.add(z - 1);
						values.add(Math.max(0, val - Block.lightReduction(c.get(x, y, z - 1))));
					} else if (ka >= val) {
						xa.add(x);
						ya.add(y);
						za.add(z - 1);
						va.add(ka);
					}
				}
			}

			while (!xa.isEmpty()) {
				x = xa.poll();
				y = ya.poll();
				z = za.poll();
				int val = va.poll();
				c = ChunkManager.getWithBlockCoords(x + 1, y, z);
				if (c != null && Block.isTransparent(c.get(x + 1, y, z)) && c.getTorchLight(x + 1, y, z) <= val - 2) {
					c.setTorchLight(x + 1, y, z, Math.max(0, val - Block.lightReduction(c.get(x + 1, y, z))));
					xa.add(x + 1);
					ya.add(y);
					za.add(z);
					va.add(Math.max(0, val - Block.lightReduction(c.get(x + 1, y, z))));
				}
				c = ChunkManager.getWithBlockCoords(x - 1, y, z);
				if (c != null && Block.isTransparent(c.get(x - 1, y, z)) && c.getTorchLight(x - 1, y, z) <= val - 2) {
					c.setTorchLight(x - 1, y, z, Math.max(0, val - Block.lightReduction(c.get(x - 1, y, z))));
					xa.add(x - 1);
					ya.add(y);
					za.add(z);
					va.add(Math.max(0, val - Block.lightReduction(c.get(x - 1, y, z))));
				}
				c = ChunkManager.getWithBlockCoords(x, y + 1, z);
				if (c != null && Block.isTransparent(c.get(x, y + 1, z)) && c.getTorchLight(x, y + 1, z) <= val - 2) {
					c.setTorchLight(x, y + 1, z, Math.max(0, val - Block.lightReduction(c.get(x, y + 1, z))));
					xa.add(x);
					ya.add(y + 1);
					za.add(z);
					va.add(Math.max(0, val - Block.lightReduction(c.get(x, y + 1, z))));
				}
				c = ChunkManager.getWithBlockCoords(x, y - 1, z);
				if (c != null && Block.isTransparent(c.get(x, y - 1, z)) && c.getTorchLight(x, y - 1, z) <= val - 2) {
					c.setTorchLight(x, y - 1, z, Math.max(0, val - Block.lightReduction(c.get(x, y - 1, z))));
					xa.add(x);
					ya.add(y - 1);
					za.add(z);
					va.add(Math.max(0, val - Block.lightReduction(c.get(x, y - 1, z))));
				}
				c = ChunkManager.getWithBlockCoords(x, y, z + 1);
				if (c != null && Block.isTransparent(c.get(x, y, z + 1)) && c.getTorchLight(x, y, z + 1) <= val - 2) {
					c.setTorchLight(x, y, z + 1, Math.max(0, val - Block.lightReduction(c.get(x, y, z + 1))));
					xa.add(x);
					ya.add(y);
					za.add(z + 1);
					va.add(Math.max(0, val - Block.lightReduction(c.get(x, y, z + 1))));
				}
				c = ChunkManager.getWithBlockCoords(x, y, z - 1);
				if (c != null && Block.isTransparent(c.get(x, y, z - 1)) && c.getTorchLight(x, y, z - 1) <= val - 2) {
					c.setTorchLight(x, y, z - 1, Math.max(0, val - Block.lightReduction(c.get(x, y, z - 1))));
					xa.add(x);
					ya.add(y);
					za.add(z - 1);
					va.add(Math.max(0, val - Block.lightReduction(c.get(x, y, z - 1))));
				}
				// removal not necessary anymore
			}
		}
	}

	/**
	 * @param x,y,z
	 *            coords of changed Block!
	 */
	public static void checkForLightUpdates(int x, int y, int z) {// , boolean
																	// newBlockTransparent
		if (!Block.isTransparent(ChunkManager.getBlockID(x, y, z))) {
			// updateLightRemoval(x, y, z);
			toRemove.add(new Key3D(x, y, z));
		} else {
			int v1 = ChunkManager.getTorchLight(x + 1, y, z);
			int v2 = ChunkManager.getTorchLight(x - 1, y, z);
			int v3 = ChunkManager.getTorchLight(x, y + 1, z);
			int v4 = ChunkManager.getTorchLight(x, y - 1, z);
			int v5 = ChunkManager.getTorchLight(x, y, z + 1);
			int v6 = ChunkManager.getTorchLight(x, y, z - 1);
			if (v1 >= v2 && v2 >= v3 && v1 >= v4 && v1 >= v5 && v2 >= v6) {
				// updateLight(x+1, y, z);
				toUpdate.add(new Key3D(x + 1, y, z));
			} else if (v2 >= v3 && v2 >= v4 && v2 >= v5 && v2 >= v6) {
				// updateLight(x-1, y, z);
				toUpdate.add(new Key3D(x - 1, y, z));
			} else if (v3 >= v4 && v3 >= v5 && v3 >= v6) {
				// updateLight(x, y+1, z);
				toUpdate.add(new Key3D(x, y + 1, z));
			} else if (v4 >= v5 && v4 >= v6) {
				// updateLight(x, y-1, z);
				toUpdate.add(new Key3D(x, y - 1, z));
			} else if (v5 >= v6) {
				// updateLight(x, y, z+1);
				toUpdate.add(new Key3D(x, y, z + 1));
			} else {
				// updateLight(x, y, z-1);
				toUpdate.add(new Key3D(x, y, z - 1));
			}
		}
	}

	public static void loadSunLight(Chunk c) {
		toLoadSun.add(c);
	}

	public static void doSunLightLoad(Chunk c) {
		sxs.clear();
		sys.clear();
		szs.clear();
		svalues.clear();
		Chunk TOP = ChunkManager.getWithChunkCoords(c.cx(), c.cy() + 1, c.cz());
		if (TOP != null) {
			for (int x = 0; x < Chunk.SIZE; x++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					if (Block.isTransparent(c.getIC(x, Chunk.SIZE - 1, z))) {
						int tsl = TOP.getSunLightIC(x, 0, z);
						if (tsl > 0) {
							sxs.add(c.realX() + x);
							sys.add(c.realY() + Chunk.SIZE - 1);
							szs.add(c.realZ() + z);
							svalues.add(Chunk.MAXL);
							c.setSunLight(c.realX() + x, c.realY() + Chunk.SIZE - 1, c.realZ() + z,
									tsl == Chunk.MAXL ? Chunk.MAXL : tsl - 1);
						}
					}
				}
			}
		} else {
			for (int x = 0; x < Chunk.SIZE; x++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					if (Generator.getG().generateHeight(c.realX() + x, c.realZ() + z) >= c.realY() + Chunk.SIZE) {
						return;
					}
				}
			}
			for (int x = 0; x < Chunk.SIZE; x++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					if (Block.isTransparent(c.getIC(x, Chunk.SIZE - 1, z))) {
						sxs.add(c.realX() + x);
						sys.add(c.realY() + Chunk.SIZE - 1);
						szs.add(c.realZ() + z);
						svalues.add(Chunk.MAXL);
						c.setSunLight(c.realX() + x, c.realY() + Chunk.SIZE - 1, c.realZ() + z, Chunk.MAXL);
					}
				}
			}
		}
		int x, y, z, v;
		while (!sxs.isEmpty()) {
			x = sxs.poll();
			y = sys.poll();
			z = szs.poll();
			v = svalues.poll();
			// if(v < Chunk.MAXL){
			c = ChunkManager.getWithBlockCoords(x + 1, y, z);
			if (c != null && Block.isTransparent(c.get(x + 1, y, z)) && c.getSunLight(x + 1, y, z) <= v - 2) {
				c.setSunLight(x + 1, y, z, Math.max(0, v - Block.lightReduction(c.get(x + 1, y, z))));
				sxs.add(x + 1);
				sys.add(y);
				szs.add(z);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x + 1, y, z))));
			}
			c = ChunkManager.getWithBlockCoords(x - 1, y, z);
			if (c != null && Block.isTransparent(c.get(x - 1, y, z)) && c.getSunLight(x - 1, y, z) <= v - 2) {
				c.setSunLight(x - 1, y, z, Math.max(0, v - Block.lightReduction(c.get(x - 1, y, z))));
				sxs.add(x - 1);
				sys.add(y);
				szs.add(z);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x - 1, y, z))));
			}
			c = ChunkManager.getWithBlockCoords(x, y + 1, z);
			if (c != null && Block.isTransparent(c.get(x, y + 1, z)) && c.getSunLight(x, y + 1, z) <= v - 2) {
				c.setSunLight(x, y + 1, z, Math.max(0, v - Block.lightReduction(c.get(x, y + 1, z))));
				sxs.add(x);
				sys.add(y + 1);
				szs.add(z);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x, y + 1, z))));
			}
			c = ChunkManager.getWithBlockCoords(x, y - 1, z);
			if (c != null) {
				if ((v < Chunk.MAXL || c.get(x, y - 1, z) != Block.AIR)) {
					if (Block.isTransparent(c.get(x, y - 1, z)) && c.getSunLight(x, y - 1, z) <= v - 2) {
						c.setSunLight(x, y - 1, z, Math.max(0, v - Block.lightReduction(c.get(x, y - 1, z))));
						sxs.add(x);
						sys.add(y - 1);
						szs.add(z);
						svalues.add(Math.max(0, v - Block.lightReduction(c.get(x, y - 1, z))));
					}
				} else {
					if (c.getSunLight(x, y - 1, z) < v) {
						c.setSunLight(x, y - 1, z, v);
						sxs.add(x);
						sys.add(y - 1);
						szs.add(z);
						svalues.add(v);
					}
				}
			}

			c = ChunkManager.getWithBlockCoords(x, y, z + 1);
			if (c != null && Block.isTransparent(c.get(x, y, z + 1)) && c.getSunLight(x, y, z + 1) <= v - 2) {
				c.setSunLight(x, y, z + 1, Math.max(0, v - Block.lightReduction(c.get(x, y, z + 1))));
				sxs.add(x);
				sys.add(y);
				szs.add(z + 1);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x, y, z + 1))));
			}
			c = ChunkManager.getWithBlockCoords(x, y, z - 1);
			if (c != null && Block.isTransparent(c.get(x, y, z - 1)) && c.getSunLight(x, y, z - 1) <= v - 2) {
				c.setSunLight(x, y, z - 1, Math.max(0, v - Block.lightReduction(c.get(x, y, z - 1))));
				sxs.add(x);
				sys.add(y);
				szs.add(z - 1);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x, y, z - 1))));
			}
			// }else{
			// c = ChunkManager.getWithBlockCoords(x + 1, y, z);
			// if (c != null && Block.isTransparent(c.get(x + 1, y, z))
			// && c.getSunLight(x + 1, y, z) <= v - 2) {
			// c.setSunLight(x + 1, y, z, v);
			// sxs.add(x + 1);
			// sys.add(y);
			// szs.add(z);
			// svalues.add(v);
			// }
			// c = ChunkManager.getWithBlockCoords(x - 1, y, z);
			// if (c != null && Block.isTransparent(c.get(x - 1, y, z))
			// && c.getSunLight(x - 1, y, z) <= v - 2) {
			// c.setSunLight(x - 1, y, z, v);
			// sxs.add(x - 1);
			// sys.add(y);
			// szs.add(z);
			// svalues.add(v);
			// }
			// c = ChunkManager.getWithBlockCoords(x, y + 1, z);
			// if (c != null && Block.isTransparent(c.get(x, y + 1, z))
			// && c.getSunLight(x, y + 1, z) <= v - 2) {
			// c.setSunLight(x, y + 1, z, v);
			// sxs.add(x);
			// sys.add(y + 1);
			// szs.add(z);
			// svalues.add(v);
			// }
			// c = ChunkManager.getWithBlockCoords(x, y - 1, z);
			// if (c != null && Block.isTransparent(c.get(x, y - 1, z))
			// && c.getSunLight(x, y - 1, z) <= v - 2) {
			// c.setSunLight(x, y - 1, z, v);
			// sxs.add(x);
			// sys.add(y - 1);
			// szs.add(z);
			// svalues.add(v);
			// }
			// c = ChunkManager.getWithBlockCoords(x, y, z + 1);
			// if (c != null && Block.isTransparent(c.get(x, y, z + 1))
			// && c.getSunLight(x, y, z + 1) <= v - 2) {
			// c.setSunLight(x, y, z + 1, v);
			// sxs.add(x);
			// sys.add(y);
			// szs.add(z + 1);
			// svalues.add(v);
			// }
			// c = ChunkManager.getWithBlockCoords(x, y, z - 1);
			// if (c != null && Block.isTransparent(c.get(x, y, z - 1))
			// && c.getSunLight(x, y, z - 1) <= v - 2) {
			// c.setSunLight(x, y, z - 1, v);
			// sxs.add(x);
			// sys.add(y);
			// szs.add(z - 1);
			// svalues.add(v);
			// }
			// }
		}
	}

	/**
	 * @param x,y,z
	 *            coords of changed block.
	 */
	public static void updateSunLight(int x, int y, int z) {
		sux.add(x);
		suy.add(y);
		suz.add(z);
	}

	private static void doSunLightUpdate(int x, int y, int z) {
		Chunk c = ChunkManager.getWithBlockCoords(x, y, z);
		if (c != null) {
			sxs.clear();
			sys.clear();
			szs.clear();
			svalues.clear();
			if (Block.isTransparent(c.get(x, y, z))) {
				doSunLightAdd(c, x, y, z);
//				System.out.println("sunLightAdd at " + x + " " + y + " " + z);
			} else if (c.getSunLight(x, y, z) != 0) {
				doSunLightRemoval(c, x, y, z);
//				System.out.println("sunLightRem at " + x + " " + y + " " + z);
			}
		}
	}

	private static void doSunLightRemoval(Chunk c, int x, int y, int z) {
		xs.clear();
		ys.clear();
		zs.clear();
		values.clear();

		sxs.clear();
		sys.clear();
		szs.clear();
		svalues.clear();

		int i = c.getSunLight(x, y, z);
		c.setSunLight(x, y, z, 0);
		xs.add(x);
		ys.add(y);
		zs.add(z);
		values.add(i);

		while (!xs.isEmpty()) {
			x = xs.poll();// here?!?
			y = ys.poll();
			z = zs.poll();
			int svaluesl = values.poll();
			c = ChunkManager.getWithBlockCoords(x + 1, y, z);
			if (c != null) {
				int ka = c.getSunLight(x + 1, y, z);
				if (ka != 0 && ka <= svaluesl) {
					c.setSunLight(x + 1, y, z, 0);
					xs.add(x + 1);
					ys.add(y);
					zs.add(z);
					values.add(Math.max(0, svaluesl - Block.lightReduction(c.get(x + 1, y, z))));
				} else if (ka > svaluesl) {
					sxs.add(x + 1);
					sys.add(y);
					szs.add(z);
					svalues.add(ka);
				}
			}
			c = ChunkManager.getWithBlockCoords(x - 1, y, z);
			if (c != null) {
				int ka = c.getSunLight(x - 1, y, z);
				if (ka != 0 && ka <= svaluesl) {
					c.setSunLight(x - 1, y, z, 0);
					xs.add(x - 1);
					ys.add(y);
					zs.add(z);
					values.add(Math.max(0, svaluesl - Block.lightReduction(c.get(x - 1, y, z))));
				} else if (ka > svaluesl) {
					sxs.add(x - 1);
					sys.add(y);
					szs.add(z);
					svalues.add(ka);
				}
			}
			c = ChunkManager.getWithBlockCoords(x, y + 1, z);
			if (c != null) {
				int ka = c.getSunLight(x, y + 1, z);
				if (ka != 0 && ka <= svaluesl) {
					c.setSunLight(x, y + 1, z, 0);
					xs.add(x);
					ys.add(y + 1);
					zs.add(z);
					values.add(Math.max(0, svaluesl - Block.lightReduction(c.get(x, y + 1, z))));
				} else if (ka > svaluesl) {
					sxs.add(x);
					sys.add(y + 1);
					szs.add(z);
					svalues.add(ka);
				}
			}
			c = ChunkManager.getWithBlockCoords(x, y - 1, z);
			if (c != null) {
				// int ka = c.getSunLight(x, y - 1, z);
				c.setSunLight(x, y - 1, z, 0);
				if (Block.isTransparent(c.get(x, y - 1, z))) {
					xs.add(x);
					ys.add(y - 1);
					zs.add(z);
					values.add(svaluesl == Chunk.MAXL ? Chunk.MAXL : svaluesl - 1);
				}
			}
			c = ChunkManager.getWithBlockCoords(x, y, z + 1);
			if (c != null) {
				int ka = c.getSunLight(x, y, z + 1);
				if (ka != 0 && ka <= svaluesl) {
					c.setSunLight(x, y, z + 1, 0);
					xs.add(x);
					ys.add(y);
					zs.add(z + 1);
					values.add(Math.max(0, svaluesl - Block.lightReduction(c.get(x, y, z + 1))));
				} else if (ka > svaluesl) {
					sxs.add(x);
					sys.add(y);
					szs.add(z + 1);
					svalues.add(ka);
				}
			}
			c = ChunkManager.getWithBlockCoords(x, y, z - 1);
			if (c != null) {
				int ka = c.getSunLight(x, y, z - 1);
				if (ka != 0 && ka <= svaluesl) {
					c.setSunLight(x, y, z - 1, 0);
					xs.add(x);
					ys.add(y);
					zs.add(z - 1);
					values.add(Math.max(0, svaluesl - Block.lightReduction(c.get(x, y, z - 1))));
				} else if (ka > svaluesl) {
					sxs.add(x);
					sys.add(y);
					szs.add(z - 1);
					svalues.add(ka);
				}
			}
		}
		int v;
		while (sxs.size() > 0) {
			x = sxs.poll();
			y = sys.poll();
			z = szs.poll();
			v = svalues.poll();
			c = ChunkManager.getWithBlockCoords(x + 1, y, z);
			if (c != null && Block.isTransparent(c.get(x + 1, y, z)) && c.getSunLight(x + 1, y, z) <= v - 2) {
				c.setSunLight(x + 1, y, z, Math.max(0, v - Block.lightReduction(c.get(x + 1, y, z))));
				sxs.add(x + 1);
				sys.add(y);
				szs.add(z);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x + 1, y, z))));
			}
			c = ChunkManager.getWithBlockCoords(x - 1, y, z);
			if (c != null && Block.isTransparent(c.get(x - 1, y, z)) && c.getSunLight(x - 1, y, z) <= v - 2) {
				c.setSunLight(x - 1, y, z, Math.max(0, v - Block.lightReduction(c.get(x - 1, y, z))));
				sxs.add(x - 1);
				sys.add(y);
				szs.add(z);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x - 1, y, z))));
			}
			c = ChunkManager.getWithBlockCoords(x, y + 1, z);
			if (c != null && Block.isTransparent(c.get(x, y + 1, z)) && c.getSunLight(x, y + 1, z) <= v - 2) {
				c.setSunLight(x, y + 1, z, Math.max(0, v - Block.lightReduction(c.get(x, y + 1, z))));
				sxs.add(x);
				sys.add(y + 1);
				szs.add(z);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x, y + 1, z))));
			}
			c = ChunkManager.getWithBlockCoords(x, y - 1, z);
			if (c != null) {
				if ((v < Chunk.MAXL || c.get(x, y - 1, z) != Block.AIR)) {
					if (Block.isTransparent(c.get(x, y - 1, z)) && c.getSunLight(x, y - 1, z) <= v - 2) {
						c.setSunLight(x, y - 1, z, Math.max(0, v - Block.lightReduction(c.get(x, y - 1, z))));
						sxs.add(x);
						sys.add(y - 1);
						szs.add(z);
						svalues.add(Math.max(0, v - Block.lightReduction(c.get(x, y - 1, z))));
					}
				} else {
					if (c.getSunLight(x, y - 1, z) < v) {
						c.setSunLight(x, y - 1, z, v);
						sxs.add(x);
						sys.add(y - 1);
						szs.add(z);
						svalues.add(v);
					}
				}
			}
			c = ChunkManager.getWithBlockCoords(x, y, z + 1);
			if (c != null && Block.isTransparent(c.get(x, y, z + 1)) && c.getSunLight(x, y, z + 1) <= v - 2) {
				c.setSunLight(x, y, z + 1, Math.max(0, v - Block.lightReduction(c.get(x, y, z + 1))));
				sxs.add(x);
				sys.add(y);
				szs.add(z + 1);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x, y, z + 1))));
			}
			c = ChunkManager.getWithBlockCoords(x, y, z - 1);
			if (c != null && Block.isTransparent(c.get(x, y, z - 1)) && c.getSunLight(x, y, z - 1) <= v - 2) {
				c.setSunLight(x, y, z - 1, Math.max(0, v - Block.lightReduction(c.get(x, y, z - 1))));
				sxs.add(x);
				sys.add(y);
				szs.add(z - 1);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x, y, z - 1))));
			}
		}
	}

	private static void doSunLightAdd(Chunk c, int x, int y, int z) {
		if ((y + 1) % 16 == 0) {
			c = ChunkManager.getWithChunkCoords(x, y + 1, z);
			if (c == null) {
				return;
			}
		}
		int v = c.getSunLight(x, y + 1, z);// determine which of the 6 sides
		// is highest? would be better!
		if (v == Chunk.MAXL) {
			sxs.add(x);
			sys.add(y + 1);
			szs.add(z);
		} else {
			c = ChunkManager.getWithBlockCoords(x, y - 1, z);
			int v1 = 0;
			if (c != null)
				v1 = c.getSunLight(x, y - 1, z);
			c = ChunkManager.getWithBlockCoords(x + 1, y, z);
			int v2 = 0;
			if (c != null)
				v2 = c.getSunLight(x + 1, y, z);
			c = ChunkManager.getWithBlockCoords(x - 1, y, z);
			int v3 = 0;
			if (c != null)
				v3 = c.getSunLight(x - 1, y, z);
			c = ChunkManager.getWithBlockCoords(x, y, z + 1);
			int v4 = 0;
			if (c != null)
				v4 = c.getSunLight(x, y, z + 1);
			c = ChunkManager.getWithBlockCoords(x, y, z - 1);
			int v5 = 0;
			if (c != null)
				v5 = c.getSunLight(x, y, z - 1);
			if (v >= v1 && v >= v2 && v >= v3 && v >= v4 && v >= v5) {
				sxs.add(x);
				sys.add(y + 1);
				szs.add(z);
			} else if (v1 >= v2 && v1 >= v3 && v1 >= v4 && v1 >= v5) {
				v = v1;
				sxs.add(x);
				sys.add(y - 1);
				szs.add(z);
			} else if (v2 >= v3 && v2 >= v4 && v2 >= v5) {
				v = v2;
				sxs.add(x + 1);
				sys.add(y);
				szs.add(z);
			} else if (v3 >= v4 && v3 >= v5) {
				v = v3;
				sxs.add(x - 1);
				sys.add(y);
				szs.add(z);
			} else if (v4 >= v5) {
				v = v4;
				sxs.add(x);
				sys.add(y);
				szs.add(z + 1);
			} else {
				v = v5;
				sxs.add(x);
				sys.add(y);
				szs.add(z - 1);
			}
		}
//		System.out.println("sunlightUpdate starting from " + sxs.peek() + " " + sys.peek() + " " + szs.peek());
		svalues.add(v);
		while (sxs.size() > 0) {
			x = sxs.poll();
			y = sys.poll();
			z = szs.poll();
			v = svalues.poll();
			c = ChunkManager.getWithBlockCoords(x + 1, y, z);
			if (c != null && Block.isTransparent(c.get(x + 1, y, z)) && c.getSunLight(x + 1, y, z) <= v - 2) {
				c.setSunLight(x + 1, y, z, Math.max(0, v - Block.lightReduction(c.get(x + 1, y, z))));
				sxs.add(x + 1);
				sys.add(y);
				szs.add(z);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x + 1, y, z))));
			}
			c = ChunkManager.getWithBlockCoords(x - 1, y, z);
			if (c != null && Block.isTransparent(c.get(x - 1, y, z)) && c.getSunLight(x - 1, y, z) <= v - 2) {
				c.setSunLight(x - 1, y, z, Math.max(0, v - Block.lightReduction(c.get(x - 1, y, z))));
				sxs.add(x - 1);
				sys.add(y);
				szs.add(z);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x - 1, y, z))));
			}
			c = ChunkManager.getWithBlockCoords(x, y + 1, z);
			if (c != null && Block.isTransparent(c.get(x, y + 1, z)) && c.getSunLight(x, y + 1, z) <= v - 2) {
				c.setSunLight(x, y + 1, z, Math.max(0, v - Block.lightReduction(c.get(x, y + 1, z))));
				sxs.add(x);
				sys.add(y + 1);
				szs.add(z);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x, y + 1, z))));
			}
			c = ChunkManager.getWithBlockCoords(x, y - 1, z);
			if (c != null) {
				if ((v < Chunk.MAXL || Block.downWardSunLightReduction(c.get(x, y - 1, z)) > 0)) {
					if (Block.isTransparent(c.get(x, y - 1, z)) && c.getSunLight(x, y - 1, z) <= v - 2) {
						c.setSunLight(x, y - 1, z, Math.max(0, v - Block.downWardSunLightReduction(c.get(x, y - 1, z))));
						sxs.add(x);
						sys.add(y - 1);
						szs.add(z);
						svalues.add(Math.max(0, v - Block.downWardSunLightReduction(c.get(x, y - 1, z))));
					}
				} else {
					if (c.getSunLight(x, y - 1, z) < v) {
						c.setSunLight(x, y - 1, z, v);
						sxs.add(x);
						sys.add(y - 1);
						szs.add(z);
						svalues.add(v);
					}
				}
			}
			c = ChunkManager.getWithBlockCoords(x, y, z + 1);
			if (c != null && Block.isTransparent(c.get(x, y, z + 1)) && c.getSunLight(x, y, z + 1) <= v - 2) {
				c.setSunLight(x, y, z + 1, Math.max(0, v - Block.lightReduction(c.get(x, y, z + 1))));
				sxs.add(x);
				sys.add(y);
				szs.add(z + 1);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x, y, z + 1))));
			}
			c = ChunkManager.getWithBlockCoords(x, y, z - 1);
			if (c != null && Block.isTransparent(c.get(x, y, z - 1)) && c.getSunLight(x, y, z - 1) <= v - 2) {
				c.setSunLight(x, y, z - 1, Math.max(0, v - Block.lightReduction(c.get(x, y, z - 1))));
				sxs.add(x);
				sys.add(y);
				szs.add(z - 1);
				svalues.add(Math.max(0, v - Block.lightReduction(c.get(x, y, z - 1))));
			}
		}
	}

}
