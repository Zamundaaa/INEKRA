package toolBox;

import java.lang.Math;
import java.util.ArrayList;

import org.joml.*;

import controls.Mouse;
import data.Block;
import data.ChunkManager;
import entities.Camera;
import particles.PTM;
import particles.ParticleMaster;
import renderStuff.DisplayManager;
import renderStuff.MasterRenderer;

public class MousePicker {

	// private static final int RECURSION_COUNT = 200;
	// private static final float RAY_RANGE = 600;

	private static Vector3f currentRay = new Vector3f();

	private static Matrix4f projectionMatrix;
	private static Matrix4f viewMatrix;

	// private Terrains terrain;
	// private Vector3f currentTerrainPoint;

	public static void init() {
		projectionMatrix = MasterRenderer.getProjectionMatrix();
		viewMatrix = Meth.createViewMatrix();
		// this.terrain = terrain;
	}

	// public Block getNextBlockOnCurrentRay(float blockCoordMaxDist, float
	// blockCoordInterval) {
	// return getNextBlock(currentRay, blockCoordMaxDist, blockCoordInterval);
	// }

	// public Vector3f getNextBlockCoordOnCurrentRay(float blockCoordMaxDist,
	// float blockCoordInterval) {
	// Block b = getNextBlockOnCurrentRay(blockCoordMaxDist,
	// blockCoordInterval);
	// if (b != null) {
	// return b.getBlockPos();
	// }
	// return null;
	// }

	// public Block getNextBlock(Vector3f ray, float blockCoordMaxDist, float
	// blockCoordInterval) {
	// Vector3f point;
	// Vector3f lastPoint = null;
	// for (int i2 = 0; i2 < (int) (blockCoordMaxDist / blockCoordInterval);
	// i2++) {
	// point = getPointOnRay(ray, i2 * blockCoordInterval);
	// if (point != null) {
	// if (lastPoint != null && Vects.sameIntVects(point, lastPoint)) {
	// continue;
	// }
	// lastPoint = point;
	// Block b = ChunkManager.getBlockID(point.x, point.y, point.z);
	// if (b != null) {
	// return b;
	// }
	// }
	// }
	//
	// return null;
	// }

	public static Vector3f getCurrentRay() {
		return currentRay;
	}

	public static void update() {
		viewMatrix = Meth.createViewMatrix();
		currentRay = calculateMouseRay();
	}

	private static Vector3f calculateMouseRay() {
		float mouseX = Mouse.getX();
		float mouseY = DisplayManager.HEIGHT * 0.5f - (Mouse.getY() - DisplayManager.HEIGHT * 0.5f);
		// H*0.5 - Y + H*0.5
		// = -Y
//		float mouseY = -Mouse.getY();
		Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	private static Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = viewMatrix.invert(new Matrix4f());
		Vector4f rayWorld = invertedView.transform(eyeCoords);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalize();
		return mouseRay;
	}

	private static Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = projectionMatrix.invert(new Matrix4f());
		Vector4f eyeCoords = invertedProjection.transform(clipCoords);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private static Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2.0f * mouseX) / DisplayManager.getWidth() - 1f;
		float y = (2.0f * mouseY) / DisplayManager.getHeight() - 1f;
		return new Vector2f(x, y);
	}

	// **********************************************************

	public static Vector3f getPoint(float distance, Vector3f dest) {
		return Camera.getPosition().add(currentRay.x * distance, currentRay.y * distance, currentRay.z * distance,
				dest);
	}

	public static Vector3f getPoint(float distance) {
		return getPoint(distance, new Vector3f());
	}

	public static Vector3f getPointOnRay(Vector3f start, Vector3f ray, float distance, Vector3f dest) {
		return start.add(ray.x * distance, ray.y * distance, ray.z * distance, dest);
	}

	public static Vector3f getPointOnRay(Vector3f start, Vector3f ray, float distance) {
		return start.add(ray.x * distance, ray.y * distance, ray.z * distance, new Vector3f());
	}

	public static Vector3f getPointOnRay(Vector3f ray, float distance) {
		return Camera.getPosition().add(ray.x * distance, ray.y * distance, ray.z * distance, new Vector3f());
	}

	/**
	 * @param value
	 * @param coord
	 *            'x' 'y' or 'z'
	 * @return The position of the currentRay at the coord value
	 */
	public static Vector3f getPointOnCoord(float value, char coord) {
		Vector3f ray = new Vector3f(currentRay);
		Vector3f start = new Vector3f(Camera.getPosition());
		if (coord == 'x') {
			float mult = value / ray.x;
			Vector3f scaledRay = new Vector3f(ray.x * mult, ray.y * mult, ray.z * mult);
			return start.add(scaledRay, new Vector3f());
		} else if (coord == 'y') {
			float mult = value / ray.y;
			Vector3f scaledRay = new Vector3f(ray.x * mult, ray.y * mult, ray.z * mult);
			return start.add(scaledRay, new Vector3f());
		} else if (coord == 'z') {
			float mult = value / ray.z;
			Vector3f scaledRay = new Vector3f(ray.x * mult, ray.y * mult, ray.z * mult);
			return start.add(scaledRay, new Vector3f());
		}
		return null;
	}

	public static Vector3f calcVect = new Vector3f();

	public static Vector3f getLastEmptyBlockCoord(float range) {
		// -----------------------TRY 1:-----------------------
		Vector3f start = new Vector3f(Camera.getPosition());
		Vector3f end = getPointOnRay(start, currentRay, range);

		int stepX = Meth.vorzeichen(end.x - start.x);
		int stepY = Meth.vorzeichen(end.y - start.y);
		int stepZ = Meth.vorzeichen(end.z - start.z);

		float X = start.x;
		float Y = start.y;
		float Z = start.z;

		float tDeltaX, tDeltaY, tDeltaZ;
		float tMaxX, tMaxY, tMaxZ;

		if (stepX != 0) {
			tDeltaX = Math.min(stepX / (end.x - start.x), 1000000000);
		} else {
			tDeltaX = Float.POSITIVE_INFINITY;
		}
		if (stepX > 0) {
			tMaxX = tDeltaX * frac1(start.x);
		} else {
			tMaxX = tDeltaX * frac0(start.x);
		}

		if (stepY != 0) {
			tDeltaY = Math.min(stepY / (end.y - start.y), Float.POSITIVE_INFINITY);
		} else {
			tDeltaY = Float.POSITIVE_INFINITY;
		}
		if (stepY > 0) {
			tMaxY = tDeltaY * frac1(start.y);
		} else {
			tMaxY = tDeltaY * frac0(start.y);
		}

		if (stepZ != 0) {
			tDeltaZ = Math.min(stepZ / (end.z - start.z), 1000000000);
		} else {
			tDeltaZ = Float.POSITIVE_INFINITY;
		}
		if (stepZ > 0) {
			tMaxZ = tDeltaZ * frac1(start.z);
		} else {
			tMaxZ = tDeltaZ * frac0(start.z);
		}

		Vector3f lastStep = calcVect;
		float t = 0;
		short b;
		do {
			if (tMaxX < tMaxY) {
				if (tMaxX < tMaxZ) {
					X += stepX;
					tMaxX += tDeltaX;
					t += tDeltaX;
					lastStep.x = -stepX;
					lastStep.y = 0;
					lastStep.z = 0;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					t += tDeltaZ;
					lastStep.x = 0;
					lastStep.y = 0;
					lastStep.z = -stepZ;
				}
			} else {
				if (tMaxY < tMaxZ) {
					Y += stepY;
					tMaxY += tDeltaY;
					t += tDeltaY;
					lastStep.x = 0;
					lastStep.y = -stepY;
					lastStep.z = 0;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					t += tDeltaZ;
					lastStep.x = 0;
					lastStep.y = 0;
					lastStep.z = -stepZ;
				}
			}
			b = ChunkManager.getBlockID(X, Y, Z);
			if (b != Block.AIR) {
				return new Vector3f((float) Math.floor(X + lastStep.x), (float) Math.floor(Y + lastStep.y),
						(float) Math.floor(Z + lastStep.z));
				// return new Vector3f(X, Y, Z).add(lastStep);
			}
		} while (Math.abs(t) < range);
		return null;
	}

	/**
	 * @param range
	 * @return
	 * @comment sets calcVect to the last step taken !! uses the calcVect in
	 *          this class!! save your data of that Vector if you need it
	 */
	public static Vector3f getLastEmptyBlockCoordWithOrientation(float range) {
		// -----------------------TRY 1:-----------------------
		Vector3f start = new Vector3f(Camera.getPosition());
		Vector3f end = getPointOnRay(start, currentRay, range);

		int stepX = Meth.vorzeichen(end.x - start.x);
		int stepY = Meth.vorzeichen(end.y - start.y);
		int stepZ = Meth.vorzeichen(end.z - start.z);

		float X = start.x;
		float Y = start.y;
		float Z = start.z;

		float tDeltaX, tDeltaY, tDeltaZ;
		float tMaxX, tMaxY, tMaxZ;

		if (stepX != 0) {
			tDeltaX = Math.min(stepX / (end.x - start.x), 1000000000);
		} else {
			tDeltaX = Float.POSITIVE_INFINITY;
		}
		if (stepX > 0) {
			tMaxX = tDeltaX * frac1(start.x);
		} else {
			tMaxX = tDeltaX * frac0(start.x);
		}

		if (stepY != 0) {
			tDeltaY = Math.min(stepY / (end.y - start.y), Float.POSITIVE_INFINITY);
		} else {
			tDeltaY = Float.POSITIVE_INFINITY;
		}
		if (stepY > 0) {
			tMaxY = tDeltaY * frac1(start.y);
		} else {
			tMaxY = tDeltaY * frac0(start.y);
		}

		if (stepZ != 0) {
			tDeltaZ = Math.min(stepZ / (end.z - start.z), 1000000000);
		} else {
			tDeltaZ = Float.POSITIVE_INFINITY;
		}
		if (stepZ > 0) {
			tMaxZ = tDeltaZ * frac1(start.z);
		} else {
			tMaxZ = tDeltaZ * frac0(start.z);
		}

		Vector3f lastStep = calcVect;
		float t = 0;
		short b;
		do {
			if (tMaxX < tMaxY) {
				if (tMaxX < tMaxZ) {
					X += stepX;
					tMaxX += tDeltaX;
					t += tDeltaX;
					lastStep.x = -stepX;
					lastStep.y = 0;
					lastStep.z = 0;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					t += tDeltaZ;
					lastStep.x = 0;
					lastStep.y = 0;
					lastStep.z = -stepZ;
				}
			} else {
				if (tMaxY < tMaxZ) {
					Y += stepY;
					tMaxY += tDeltaY;
					t += tDeltaY;
					lastStep.x = 0;
					lastStep.y = -stepY;
					lastStep.z = 0;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					t += tDeltaZ;
					lastStep.x = 0;
					lastStep.y = 0;
					lastStep.z = -stepZ;
				}
			}
			b = ChunkManager.getBlockID(X, Y, Z);
			if (b != 0) {
				return new Vector3f(X, Y, Z).add(lastStep.x, lastStep.y, lastStep.z);
			}
		} while (Math.abs(t) < range);
		return null;
	}

	public static ArrayList<Vector3f> getBlocks(float range) {
		ArrayList<Vector3f> onRay = new ArrayList<Vector3f>();
		Vector3f end = getPoint(range);
		Vector3f start = new Vector3f(Camera.getPosition());

		int X = (int) (start.x - Meth.vorzeichen(start.x));
		int Y = (int) (start.y - Meth.vorzeichen(start.y));
		int Z = (int) (start.z - Meth.vorzeichen(start.z));

		int eX = (int) (end.x + Meth.vorzeichen(end.x));
		int eY = (int) (end.y + Meth.vorzeichen(end.y));
		int eZ = (int) (end.z + Meth.vorzeichen(end.z));

		int stepX = Meth.vorzeichen(eX - X);
		int stepY = Meth.vorzeichen(eY - Y);
		int stepZ = Meth.vorzeichen(eZ - Z);

		for (; Math.abs(X) <= Math.abs(eX); X += stepX) {
			for (; Math.abs(Y) <= Math.abs(eY); Y += stepY) {
				for (; Math.abs(Z) <= Math.abs(eZ); Z += stepZ) {
					if (isOnCurrentRay(X, Y, Z)) {
						onRay.add(new Vector3f(X, Y, Z));
					}
				}
			}
		}
		if (onRay.size() > 0) {
			ArrayList<Vector3f> sorted = new ArrayList<Vector3f>();
			Vector3f nearest = onRay.get(0);
			while (onRay.size() > 0) {
				for (int i = 1; i < onRay.size(); i++) {
					if (Vects.nearer(onRay.get(i), nearest, start)) {
						nearest = onRay.get(i);
					}
				}
				sorted.add(nearest);
				onRay.remove(nearest);
			}
			return sorted;
		}
		return null;
	}

	public static boolean isOnCurrentRay(int x, int y, int z) {
		Vector3f ray = new Vector3f(currentRay);
		float mult = x / ray.x;
		ray.x *= mult;
		ray.y *= mult;
		ray.z *= mult;
		if ((int) ray.y == y) {
			if ((int) ray.z == z) {
				return true;
			}
		}
		return false;
	}

	public static Vector3f getNextFilledBlockCoord(float range) {
		return getNextFilledBlockCoord(Camera.getPosition(), currentRay, range, true);
	}

	public static Vector3f getNextFilledBlockCoord(float range, boolean ignoreWater) {
		return getNextFilledBlockCoord(Camera.getPosition(), currentRay, range, ignoreWater);
	}

	public static boolean DEBUG = false;

	/**
	 * STORES ITS RESULT IN {@link toolBox.Vects#calcVect}!!!
	 * @param pos
	 * @param ray
	 * @param range
	 * @param ignoreWater
	 * @return
	 */
	public static Vector3f getNextFilledBlockCoord(Vector3f pos, Vector3f ray, float range, boolean ignoreWater) {
		// Vector3f start = new Vector3f(pos);
		Vector3f start = pos;
		Vector3f end = getPointOnRay(start, ray, range);

		int stepX = Meth.vorzeichen(end.x - start.x);
		int stepY = Meth.vorzeichen(end.y - start.y);
		int stepZ = Meth.vorzeichen(end.z - start.z);

		float X = start.x;
		float Y = start.y;
		float Z = start.z;

		float tDeltaX, tDeltaY, tDeltaZ;
		float tMaxX, tMaxY, tMaxZ;

		if (stepX != 0) {
			tDeltaX = Math.min(stepX / (end.x - start.x), 1000000000);
		} else {
			tDeltaX = Float.POSITIVE_INFINITY;
		}
		if (stepX > 0) {
			tMaxX = tDeltaX * frac1(start.x);
		} else {
			tMaxX = tDeltaX * frac0(start.x);
		}

		if (stepY != 0) {
			tDeltaY = Math.min(stepY / (end.y - start.y), Float.POSITIVE_INFINITY);
		} else {
			tDeltaY = Float.POSITIVE_INFINITY;
		}
		if (stepY > 0) {
			tMaxY = tDeltaY * frac1(start.y);
		} else {
			tMaxY = tDeltaY * frac0(start.y);
		}

		if (stepZ != 0) {
			tDeltaZ = Math.min(stepZ / (end.z - start.z), 1000000000);
		} else {
			tDeltaZ = Float.POSITIVE_INFINITY;
		}
		if (stepZ > 0) {
			tMaxZ = tDeltaZ * frac1(start.z);
		} else {
			tMaxZ = tDeltaZ * frac0(start.z);
		}

		// float t = 0;
		Vector3f result = Vects.calcVect;
		short b;
		do {
			if (tMaxX < tMaxY) {
				if (tMaxX < tMaxZ) {
					X += stepX;
					tMaxX += tDeltaX;
					// t += tDeltaX;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					// t += tDeltaZ;
				}
			} else {
				if (tMaxY < tMaxZ) {
					Y += stepY;
					tMaxY += tDeltaY;
					// t += tDeltaY;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					// t += tDeltaZ;
				}
			}
			if (DEBUG)
				ParticleMaster.addNewParticle(PTM.snowflake, new Vector3f(X, Y, Z), Vects.NULL, 0, 10, 0, 0.2f);
			b = ChunkManager.getBlockID(X, Y, Z);
			// if (b != null && (!ignoreWater || b.id() != Block.WATER)) {
			// return new Vector3f(b.getBlockPos());
			// }
			if (b != 0 && (!ignoreWater || !Block.isWater(b))) {
				return new Vector3f((float) Math.floor(X), (float) Math.floor(Y), (float) Math.floor(Z));
			}
			// } while (Math.abs(t) < range);
			result.set(X, Y, Z).sub(start);
		} while (result.lengthSquared() <= range * range);

		return null;
	}
	
	public static Vector3f getNextFilledBlockCoord(float range, boolean ignoreWater, boolean ignoreEverythingYouCanGoThrough){
		return getNextFilledBlockCoord(Camera.getPosition(), currentRay, range, ignoreWater, ignoreEverythingYouCanGoThrough);
	}
	
	public static Vector3f getNextFilledBlockCoord(Vector3f pos, Vector3f ray, float range, boolean ignoreWater, boolean ignoreEverythingYouCanGoThrough) {
		// Vector3f start = new Vector3f(pos);
		Vector3f start = pos;
		Vector3f end = getPointOnRay(start, ray, range);

		int stepX = Meth.vorzeichen(end.x - start.x);
		int stepY = Meth.vorzeichen(end.y - start.y);
		int stepZ = Meth.vorzeichen(end.z - start.z);

		float X = start.x;
		float Y = start.y;
		float Z = start.z;

		float tDeltaX, tDeltaY, tDeltaZ;
		float tMaxX, tMaxY, tMaxZ;

		if (stepX != 0) {
			tDeltaX = Math.min(stepX / (end.x - start.x), 1000000000);
		} else {
			tDeltaX = Float.POSITIVE_INFINITY;
		}
		if (stepX > 0) {
			tMaxX = tDeltaX * frac1(start.x);
		} else {
			tMaxX = tDeltaX * frac0(start.x);
		}

		if (stepY != 0) {
			tDeltaY = Math.min(stepY / (end.y - start.y), Float.POSITIVE_INFINITY);
		} else {
			tDeltaY = Float.POSITIVE_INFINITY;
		}
		if (stepY > 0) {
			tMaxY = tDeltaY * frac1(start.y);
		} else {
			tMaxY = tDeltaY * frac0(start.y);
		}

		if (stepZ != 0) {
			tDeltaZ = Math.min(stepZ / (end.z - start.z), 1000000000);
		} else {
			tDeltaZ = Float.POSITIVE_INFINITY;
		}
		if (stepZ > 0) {
			tMaxZ = tDeltaZ * frac1(start.z);
		} else {
			tMaxZ = tDeltaZ * frac0(start.z);
		}

		// float t = 0;
		Vector3f result = Vects.calcVect;
		short b;
		do {
			if (tMaxX < tMaxY) {
				if (tMaxX < tMaxZ) {
					X += stepX;
					tMaxX += tDeltaX;
					// t += tDeltaX;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					// t += tDeltaZ;
				}
			} else {
				if (tMaxY < tMaxZ) {
					Y += stepY;
					tMaxY += tDeltaY;
					// t += tDeltaY;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					// t += tDeltaZ;
				}
			}
			if (DEBUG)
				ParticleMaster.addNewParticle(PTM.snowflake, new Vector3f(X, Y, Z), Vects.NULL, 0, 10, 0, 0.2f);
			b = ChunkManager.getBlockID(X, Y, Z);
			// if (b != null && (!ignoreWater || b.id() != Block.WATER)) {
			// return new Vector3f(b.getBlockPos());
			// }
			if (b != 0 && (!ignoreWater || !Block.isWater(b)) && (!ignoreEverythingYouCanGoThrough || !Block.passable(b))) {
				return new Vector3f((float) Math.floor(X), (float) Math.floor(Y), (float) Math.floor(Z));
			}
			// } while (Math.abs(t) < range);
			result.set(X, Y, Z).sub(start);
		} while (result.lengthSquared() <= range * range);

		return null;
	}

	/**
	 * @param startPos
	 * @param ray
	 * @param range
	 * @return !!! ignores everything where {@link Block.durchGehbar(b)} is true
	 *         !!!
	 */
	public static Vector3f getNextFilledBlockCoord(Vector3f startPos, Vector3f ray, float range) {
		Vector3f start = new Vector3f(startPos);
		Vector3f end = getPointOnRay(start, ray, range);

		int stepX = Meth.vorzeichen(end.x - start.x);
		int stepY = Meth.vorzeichen(end.y - start.y);
		int stepZ = Meth.vorzeichen(end.z - start.z);

		float X = start.x;
		float Y = start.y;
		float Z = start.z;

		float tDeltaX, tDeltaY, tDeltaZ;
		float tMaxX, tMaxY, tMaxZ;

		if (stepX != 0) {
			tDeltaX = Math.min(stepX / (end.x - start.x), 1000000000);
		} else {
			tDeltaX = Float.POSITIVE_INFINITY;
		}
		if (stepX > 0) {
			tMaxX = tDeltaX * frac1(start.x);
		} else {
			tMaxX = tDeltaX * frac0(start.x);
		}

		if (stepY != 0) {
			tDeltaY = Math.min(stepY / (end.y - start.y), Float.POSITIVE_INFINITY);
		} else {
			tDeltaY = Float.POSITIVE_INFINITY;
		}
		if (stepY > 0) {
			tMaxY = tDeltaY * frac1(start.y);
		} else {
			tMaxY = tDeltaY * frac0(start.y);
		}

		if (stepZ != 0) {
			tDeltaZ = Math.min(stepZ / (end.z - start.z), 1000000000);
		} else {
			tDeltaZ = Float.POSITIVE_INFINITY;
		}
		if (stepZ > 0) {
			tMaxZ = tDeltaZ * frac1(start.z);
		} else {
			if (tDeltaZ != Float.POSITIVE_INFINITY && tDeltaZ != Float.NEGATIVE_INFINITY) {
				tMaxZ = tDeltaZ * frac0(start.z);
			} else {
				tMaxZ = tDeltaZ;
			}
		}

		if (end.x != start.x || end.y != start.y || end.z != start.z) {
			Vector3f result = Vects.calcVect;
			short b;
			do {
				if (tMaxX < tMaxY) {
					if (tMaxX < tMaxZ) {
						X += stepX;
						tMaxX += tDeltaX;
					} else {
						Z += stepZ;
						tMaxZ += tDeltaZ;
					}
				} else {
					if (tMaxY < tMaxZ) {
						Y += stepY;
						tMaxY += tDeltaY;
					} else {
						Z += stepZ;
						tMaxZ += tDeltaZ;
					}
				}
				b = ChunkManager.getBlockID(X, Y, Z);

				if (b != 0 && !Block.passable(b)) {
					return new Vector3f((float) Math.floor(X), (float) Math.floor(Y), (float) Math.floor(Z));
				}

				result.set(X, Y, Z).sub(start);

			} while (result.lengthSquared() <= range * range);
		}
		return null;
	}

	public static Vector3f getNextFilledBlockCoord(Vector3f ray, float range, boolean ignoreWater) {
		return getNextFilledBlockCoord(Camera.getPosition(), currentRay, range, ignoreWater);
	}

	public static Vector3f getNextFilledBlockCoord(Vector3f ray, float range) {
		return getNextFilledBlockCoord(Camera.getPosition(), currentRay, range, true);
	}

	/**
	 * @param range
	 *            selfexplaining; in blockcoords
	 * @return ArrayList of blockcoords the ray hits; in order
	 */
	public static ArrayList<Short> getBlocksOnCurrentRay(float range) {
		ArrayList<Short> blocks = new ArrayList<Short>();

		Vector3f start = new Vector3f(Camera.getPosition());
		Vector3f end = getPointOnRay(start, currentRay, range);

		int stepX = Meth.vorzeichen(end.x - start.x);
		int stepY = Meth.vorzeichen(end.y - start.y);
		int stepZ = Meth.vorzeichen(end.z - start.z);

		int X = (int) start.x;
		int Y = (int) start.y;
		int Z = (int) start.z;

		float tDeltaX, tDeltaY, tDeltaZ;
		float tMaxX, tMaxY, tMaxZ;

		if (stepX != 0) {
			tDeltaX = Math.min(stepX / (end.x - start.x), 1000000000);
		} else {
			tDeltaX = 1000000000;
		}
		if (stepX > 0) {
			tMaxX = tDeltaX * frac1(start.x);
		} else {
			tMaxX = tDeltaX * frac0(start.x);
		}

		if (stepY != 0) {
			tDeltaY = Math.min(stepY / (end.y - start.y), 1000000000);
		} else {
			tDeltaY = 1000000000;
		}
		if (stepY > 0) {
			tMaxY = tDeltaY * frac1(start.y);
		} else {
			tMaxY = tDeltaY * frac0(start.y);
		}

		if (stepZ != 0) {
			tDeltaZ = Math.min(stepZ / (end.z - start.z), 1000000000);
		} else {
			tDeltaZ = 1000000000;
		}
		if (stepZ > 0) {
			tMaxZ = tDeltaZ * frac1(start.z);
		} else {
			tMaxZ = tDeltaZ * frac0(start.z);
		}

		// float t = 0;
		Vector3f result = Vects.calcVect;
		short b;
		do {
			if (tMaxX < tMaxY) {
				if (tMaxX < tMaxZ) {
					X += stepX;
					tMaxX += tDeltaX;
					// t += tDeltaX;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					// t += tDeltaZ;
				}
			} else {
				if (tMaxY < tMaxZ) {
					Y += stepY;
					tMaxY += tDeltaY;
					// t += tDeltaY;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					// t += tDeltaZ;
				}
			}
			b = ChunkManager.getBlockID(X, Y, Z);
			blocks.add(b);
			// } while (Math.abs(t) < range);
			result.set(X, Y, Z).sub(start);
		} while (result.lengthSquared() <= range * range);

		return blocks;
	}

	public static float getNextBlockDist(Vector3f pos, Vector3f ray, float range) {
		Vector3f start = new Vector3f(pos);
		Vector3f end = getPointOnRay(start, ray, range);
		// start.y -= 0.5f;
		// end.y -= 0.5f;

		int stepX = Meth.vorzeichen(end.x - start.x);
		int stepY = Meth.vorzeichen(end.y - start.y);
		int stepZ = Meth.vorzeichen(end.z - start.z);

		float X = start.x;
		float Y = start.y;
		float Z = start.z;

		float tDeltaX, tDeltaY, tDeltaZ;
		float tMaxX, tMaxY, tMaxZ;

		if (stepX != 0) {
			tDeltaX = Math.min(stepX / (end.x - start.x), 1000000000);
		} else {
			tDeltaX = Float.POSITIVE_INFINITY;
		}
		if (stepX > 0) {
			tMaxX = tDeltaX * frac1(start.x);
		} else {
			tMaxX = tDeltaX * frac0(start.x);
		}

		if (stepY != 0) {
			tDeltaY = Math.min(stepY / (end.y - start.y), Float.POSITIVE_INFINITY);
		} else {
			tDeltaY = Float.POSITIVE_INFINITY;
		}
		if (stepY > 0) {
			tMaxY = tDeltaY * frac1(start.y);
		} else {
			tMaxY = tDeltaY * frac0(start.y);
		}

		if (stepZ != 0) {
			tDeltaZ = Math.min(stepZ / (end.z - start.z), 1000000000);
		} else {
			tDeltaZ = Float.POSITIVE_INFINITY;
		}
		if (stepZ > 0) {
			tMaxZ = tDeltaZ * frac1(start.z);
		} else {
			tMaxZ = tDeltaZ * frac0(start.z);
		}

		// float t = 0;
		Vector3f result = Vects.calcVect;
		short b;
		do {
			if (tMaxX < tMaxY) {
				if (tMaxX < tMaxZ) {
					X += stepX;
					tMaxX += tDeltaX;
					// t += tDeltaX;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					// t += tDeltaZ;
				}
			} else {
				if (tMaxY < tMaxZ) {
					Y += stepY;
					tMaxY += tDeltaY;
					// t += tDeltaY;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					// t += tDeltaZ;
				}
			}
			b = ChunkManager.getBlockID(X, Y, Z);
			result.set(X, Y, Z).sub(start);
			if (b != 0) {
				return result.length();
			}
			// } while (Math.abs(t) < range);
		} while (result.lengthSquared() <= range * range);

		return Float.POSITIVE_INFINITY;
	}

	/**
	 * @param range
	 *            selfexplaining; in blockcoords
	 * @return ArrayList of blockcoords the ray hits; in order
	 */
	public static ArrayList<Vector3f> getBlockCoordsOnCurrentRay(float range) {
		ArrayList<Vector3f> blocks = new ArrayList<Vector3f>();

		Vector3f start = new Vector3f(Camera.getPosition());
		Vector3f end = getPointOnRay(currentRay, range);

		int stepX = Meth.vorzeichen(end.x - start.x);
		int stepY = Meth.vorzeichen(end.y - start.y);
		int stepZ = Meth.vorzeichen(end.z - start.z);

		float X = start.x;
		float Y = start.y;
		float Z = start.z;

		float tDeltaX, tDeltaY, tDeltaZ;
		float tMaxX, tMaxY, tMaxZ;

		if (stepX != 0) {
			tDeltaX = Math.min(stepX / (end.x - start.x), 1000000000);
		} else {
			tDeltaX = 1000000000;
		}
		if (stepX > 0) {
			tMaxX = tDeltaX * frac1(start.x);
		} else {
			tMaxX = tDeltaX * frac0(start.x);
		}

		if (stepY != 0) {
			tDeltaY = Math.min(stepY / (end.y - start.y), 1000000000);
		} else {
			tDeltaY = 1000000000;
		}
		if (stepY > 0) {
			tMaxY = tDeltaY * frac1(start.y);
		} else {
			tMaxY = tDeltaY * frac0(start.y);
		}

		if (stepZ != 0) {
			tDeltaZ = Math.min(stepZ / (end.z - start.z), 1000000000);
		} else {
			tDeltaZ = 1000000000;
		}
		if (stepZ > 0) {
			tMaxZ = tDeltaZ * frac1(start.z);
		} else {
			tMaxZ = tDeltaZ * frac0(start.z);
		}
		// ParticleMaster.addNewParticle(
		// Projectil.particles, new Vector3f(start), new Vector3f(), 0, 5, 0,
		// 1);
		ParticleMaster.addNewParticle(PTM.cosmic, new Vector3f(end), new Vector3f(), 0, 5, 0, 1);

		// float t = 0;
		Vector3f result = Vects.calcVect;
		// Block b;
		do {
			if (tMaxX < tMaxY) {
				if (tMaxX < tMaxZ) {
					X += stepX;
					tMaxX += tDeltaX;
					// t += tDeltaX;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					// t += tDeltaZ;
				}
			} else {
				if (tMaxY < tMaxZ) {
					Y += stepY;
					tMaxY += tDeltaY;
					// t += tDeltaY;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					// t += tDeltaZ;
				}
			}
			// b = ChunkManager.getBlockb((int)X, (int)Y, (int)Z);
			blocks.add(new Vector3f(X, Y, Z));
			// }while((new Vector3f(X - start.x, Y - start.y, Z -
			// start.z)).lengthSquared() < range * range);
			result.set(X, Y, Z).sub(start);
			// } while (Math.abs(t) < range);
		} while (result.lengthSquared() < range * range);
		ParticleMaster.addNewParticle(PTM.projectile, new Vector3f(blocks.get(blocks.size() - 1)), new Vector3f(), 0, 5,
				0, 1);
		return blocks;
	}

	private static float frac0(float x) {
		if (x != Float.POSITIVE_INFINITY && x != Float.NEGATIVE_INFINITY && x != 0) {
			return (x - (float) Math.floor(x));
		} else {
			return x;
		}
	}

	private static float frac1(float x) {
		if (x != Float.POSITIVE_INFINITY && x != Float.NEGATIVE_INFINITY && x != 0) {
			return (1 - x + (float) Math.floor(x));
		} else {
			return x;
		}
	}

}