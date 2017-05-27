package data;

import threadingStuff.ThreadManager;
import toolBox.Meth;

public class BlockStuffUpdater {
	
	private static Thread worker;
	private static final long wantedFrameTime = 1000/60;
	
	public static void init(){
		worker = new Thread("BlockStuffUpdater"){
			@Override
			public void run(){
				long time;
				while(ThreadManager.running()){
					time = Meth.systemTime();
					ChunkManager.doBlockUpdates();
					long t = wantedFrameTime - (Meth.systemTime()-time);
					if(t > 0){
						Meth.wartn(t);
					}
				}
			}
		};
		worker.start();
	}

}
