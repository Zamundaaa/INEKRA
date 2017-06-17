package data;

import mainInterface.CM;
import threadingStuff.ThreadManager;
import toolBox.Meth;

public class BlockStuffUpdater {
	
	public static Thread worker;
	public static final long wantedFrameTime = 1000/60;
	
	public static void init(){
		if(!CM.isServer)return;
		worker = new Thread("BlockStuffUpdater"){
			@Override
			public void run(){
				long time;
				while(ThreadManager.running()){
					time = System.currentTimeMillis();
					ChunkManager.doBlockUpdates();
					long t = wantedFrameTime - (System.currentTimeMillis()-time);
					if(t > 0){
						Meth.wartn(t);
					}
				}
			}
		};
		worker.start();
	}
	
	public static void update(){
		if(!CM.isServer && !CM.singlePlayer)return;
		if(worker == null || !worker.isAlive()){
			init();
		}
	}

}
