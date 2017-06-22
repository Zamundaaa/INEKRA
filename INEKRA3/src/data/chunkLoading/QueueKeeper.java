package data.chunkLoading;

import static data.ChunkManager.genDistYSq;
import static data.ChunkManager.genRadSq;

import java.util.ArrayDeque;
import java.util.ArrayList;

import data.*;
import entities.Camera;
import toolBox.Queues;

public class QueueKeeper {
	
	private static ArrayList<Key3D> toLoad = new ArrayList<>();
	private static int pointer = 0;
	
	public static Key3D next(){
		Chunk c = ChunkManager.getWithChunkCoords(ChunkManager.toChunkCoord(Camera.getPosition().x), 
				ChunkManager.toChunkCoord(Camera.getPosition().y), ChunkManager.toChunkCoord(Camera.getPosition().z));
		pointer = 0;
		while(c != null && pointer < toLoad.size()-1){
			pointer++;
			Key3D k = toLoad.get(pointer);
			c = ChunkManager.getWithChunkCoords(k.getX()+ChunkManager.toChunkCoord(Camera.getPosition().x), 
					k.getY()+ChunkManager.toChunkCoord(Camera.getPosition().y), 
					k.getZ()+ChunkManager.toChunkCoord(Camera.getPosition().z));
		}
		if(pointer >= toLoad.size())
			return null;
		else
			return Key3D.getInstance(toLoad.get(pointer)).add(ChunkManager.toChunkCoord(Camera.getPosition().x), 
					ChunkManager.toChunkCoord(Camera.getPosition().y), ChunkManager.toChunkCoord(Camera.getPosition().z));
	}
	
	private static final Key3D somePlaceholder = new Key3D(0, 0, 0);
	
	static{
		buildQueue();
	}

	private static void buildQueue() {
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
