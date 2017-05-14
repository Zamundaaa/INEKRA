package data;

import static data.ChunkManager.genDistYSq;
import static data.ChunkManager.genRadSq;

import java.util.ArrayDeque;
import java.util.ArrayList;

import entities.Camera;
import threadingStuff.ThreadManager;
import toolBox.Meth;
import toolBox.Queues;

public class ChunkLoader extends Thread {

	public static long delayBetweenChunkLoads = 20;

	public static final ArrayDeque<Key3D> queue = new ArrayDeque<>();
	private static ArrayList<Key3D> toLoad = new ArrayList<Key3D>();
	private final ArrayDeque<Chunk> toAdd;
	// private final ArrayDeque<Key3D> added;
	private int pointer = 0;

	public ChunkLoader(ArrayDeque<Chunk> toAdd) {
		super("ChunkLoader");
		queue.clear();
		this.toAdd = toAdd;
		// added = new ArrayDeque<>();
		buildQueue();
	}

	@Override
	public void run() {
		while (ThreadManager.running()) {
			pointer = 0;
			int X = ChunkManager.toChunkCoord(Camera.getPosition().x);
			int Y = ChunkManager.toChunkCoord(Camera.getPosition().y);
			int Z = ChunkManager.toChunkCoord(Camera.getPosition().z);
			while (ThreadManager.running() && pointer < toLoad.size()
					&& ChunkManager.toChunkCoord(Camera.getPosition().x) == X
					&& ChunkManager.toChunkCoord(Camera.getPosition().y) == Y
					&& ChunkManager.toChunkCoord(Camera.getPosition().z) == Z) {
				Key3D k = toLoad.get(pointer++);
				Key3D k2 = new Key3D(k.getX() + X, k.getY() + Y, k.getZ() + Z);
				if (ChunkManager.getWithChunkCoords(k2) == null// &&
																// !added.contains(k2)
				) {
					Chunk c = new Chunk(k2.getX(), k2.getY(), k2.getZ(), false);
					toAdd.add(c);
					Meth.wartn(delayBetweenChunkLoads);
				}
			}
			while (ThreadManager.running() && ChunkManager.toChunkCoord(Camera.getPosition().x) == X
					&& ChunkManager.toChunkCoord(Camera.getPosition().y) == Y
					&& ChunkManager.toChunkCoord(Camera.getPosition().z) == Z) {
				Meth.wartn(delayBetweenChunkLoads);
			}
		}
	}

	private final Key3D somePlaceholder = new Key3D(0, 0, 0);

	private void buildQueue() {
		ArrayDeque<Key3D> xq = Queues.help1;
		xq.add(new Key3D(0, 0, 0));
		toLoad.clear();
		while (xq.size() > 0) {
			Key3D k = xq.poll();
			int rx = k.getX();
			int ry = k.getY();
			int rz = k.getZ();
			toLoad.add(new Key3D(rx, ry, rz));
			somePlaceholder.set(rx + 1, ry, rz);
			if ((k.getX() + 1) * (k.getX() + 1) + (k.getY()) * (k.getY()) + (k.getZ()) * (k.getZ()) <= genRadSq
					&& !toLoad.contains(somePlaceholder)) {
				somePlaceholder.set(k.getX() + 1, k.getY(), k.getZ());
				if (!xq.contains(somePlaceholder))
					xq.add(new Key3D(k.getX() + 1, k.getY(), k.getZ()));
			}
			somePlaceholder.set(rx - 1, ry, rz);
			if ((k.getX() - 1) * (k.getX() - 1) + (k.getY()) * (k.getY()) + (k.getZ()) * (k.getZ()) <= genRadSq
					&& !toLoad.contains(somePlaceholder)) {
				somePlaceholder.set(k.getX() - 1, k.getY(), k.getZ());
				if (!xq.contains(somePlaceholder))
					xq.add(new Key3D(k.getX() - 1, k.getY(), k.getZ()));
			}
			somePlaceholder.set(rx, ry + 1, rz);
			if ((k.getX()) * (k.getX()) + (k.getY() + 1) * (k.getY() + 1) + (k.getZ()) * (k.getZ()) <= genRadSq
					&& (k.getY() * k.getY()) <= genDistYSq && !toLoad.contains(somePlaceholder)) {
				somePlaceholder.set(k.getX(), k.getY() + 1, k.getZ());
				if (!xq.contains(somePlaceholder))
					xq.add(new Key3D(k.getX(), k.getY() + 1, k.getZ()));
			}
			somePlaceholder.set(rx, ry - 1, rz);
			if ((k.getX()) * (k.getX()) + (k.getY() - 1) * (k.getY() - 1) + (k.getZ()) * (k.getZ()) <= genRadSq
					&& (k.getY() * k.getY()) <= genDistYSq && !toLoad.contains(somePlaceholder)) {
				somePlaceholder.set(k.getX(), k.getY() - 1, k.getZ());
				if (!xq.contains(somePlaceholder))
					xq.add(new Key3D(k.getX(), k.getY() - 1, k.getZ()));
			}
			somePlaceholder.set(rx, ry, rz + 1);
			if ((k.getX()) * (k.getX()) + (k.getY()) * (k.getY()) + (k.getZ() + 1) * (k.getZ() + 1) <= genRadSq
					&& !toLoad.contains(somePlaceholder)) {
				somePlaceholder.set(k.getX(), k.getY(), k.getZ() + 1);
				if (!xq.contains(somePlaceholder))
					xq.add(new Key3D(k.getX(), k.getY(), k.getZ() + 1));
			}
			somePlaceholder.set(rx, ry, rz - 1);
			if ((k.getX()) * (k.getX()) + (k.getY()) * (k.getY()) + (k.getZ() - 1) * (k.getZ() - 1) <= genRadSq
					&& !toLoad.contains(somePlaceholder)) {
				somePlaceholder.set(k.getX(), k.getY(), k.getZ() - 1);
				if (!xq.contains(somePlaceholder))
					xq.add(new Key3D(k.getX(), k.getY(), k.getZ() - 1));
			}
		}
	}

}
