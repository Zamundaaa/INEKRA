package data.chunkLoading;

import java.util.ArrayDeque;

import data.*;
import threadingStuff.ThreadManager;
import toolBox.Meth;

public class ChunkLoader extends Thread {

	public static long delayBetweenChunkLoads = 10;

	public static final ArrayDeque<Key3D> queue = new ArrayDeque<>();
	private final ArrayDeque<Chunk> toAdd;

	public ChunkLoader(ArrayDeque<Chunk> toAdd) {
		super("ChunkLoader");
		this.toAdd = toAdd;
	}

	@Override
	public void run() {
		while (ThreadManager.running()) {
			Key3D next = queue.peek();
			if(next != null){
				if(ChunkManager.getWithChunkCoords(next) == null){
					Chunk c = new Chunk(next.getX(), next.getY(), next.getZ());
					toAdd.add(c);
				}
				queue.pop();
			}
			Meth.wartn(delayBetweenChunkLoads);
		}
	}

	public void loadChunk(int x, int y, int z) {
		queue.add(Key3D.getInstance(x, y, z));
	}

}
