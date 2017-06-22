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
//		long lastLoadedChunk = Meth.systemTime();
//		float loadingSpeed = 0;
		Key3D last = null;
		while (ThreadManager.running()) {
			if(queue.size() > 0){
				Key3D next = queue.peek();
				if(next != null){
					if(!next.equals(last) && ChunkManager.getWithChunkCoords(next) == null){
						Chunk c = new Chunk(next.getX(), next.getY(), next.getZ());
						toAdd.add(c);
//					loadingSpeed = (Meth.systemTime()-lastLoadedChunk)*0.001f;
//					lastLoadedChunk = Meth.systemTime();
//					System.out.println(loadingSpeed);
//					System.out.println("loaded Chunk at blockPos " + c.realX() + ", " + c.realY() + ", " + c.realZ());
					}
					last = queue.pop();
					Meth.wartn(delayBetweenChunkLoads);
				}else{
					queue.pop();
				}
			}else{
				Meth.wartn(delayBetweenChunkLoads);
			}
		}
//		System.out.println("ChunkLoader stopped!");
	}

	public void loadChunk(int x, int y, int z) {
		queue.add(Key3D.getInstance(x, y, z));
	}

}
