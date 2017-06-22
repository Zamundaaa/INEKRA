package network;

import static network.Server.MAX_DATA_LENGTH;

import java.io.IOException;
import java.net.*;
import java.util.ArrayDeque;

import org.joml.Vector3f;

import collectionsStuff.SmartByteBuffer;
import data.ChunkManager;
import data.Key3D;
import data.chunkLoading.QueueKeeper;
import entities.Player;
import threadingStuff.ThreadManager;
import toolBox.Meth;

public class Client {

	private static DatagramPacket r = new DatagramPacket(new byte[MAX_DATA_LENGTH], MAX_DATA_LENGTH),
			s = new DatagramPacket(new byte[MAX_DATA_LENGTH], MAX_DATA_LENGTH);
	private static SmartByteBuffer rBuff = new SmartByteBuffer(r.getData());
	private static SmartByteBuffer buff = new SmartByteBuffer();
	
	private DatagramSocket socket;

	public Client() throws IOException {
		socket = new DatagramSocket();
		socket.setSoTimeout(500);
		getServerInfo();
		// socket.connect(server);
	}
	
	private static final int playerID = 0;
	
	public void sendLogOut(){
		buff.clear();
		buff.add(Server.LOGOUTFLAG);
		addCode();
		buff.addInt(playerID);
		s.setData(buff.getArray(), 0, buff.size());
		tryToSendTillReceived();
	}
	
//	private long lastShoutout;
//	private int chunkRequestsSent, chunksReceived;

	public void sendRequest() throws IOException {
		buff.clear();
		buff.add(Server.zero);
		addCode();
		buff.addInt(playerID);
		
		addPlayerData();
		
		
		Key3D k = QueueKeeper.next();// should give the next chunk to load. 
		// May and should sometimes jump back to unloaded ones it needs to request again
//		Key3D k = new Key3D(0, 0, 0);// for testing? Or just put the system in place faster!
		if(k != null){
			buff.add(Server.one);
			buff.addInt(k.getX());
			buff.addInt(k.getY());
			buff.addInt(k.getZ());
//			chunkRequestsSent++;
		}else{
			buff.add(Server.zero);
		}
		buff.addInt(notifiesCleared);
//		System.out.println("cleared " + notifiesCleared + " BSNs!");
		
		int c = bsn.size();
		if(c > Server.maxBSNsAtOnce){
			c = Server.maxBSNsAtOnce;
		}
		buff.addInt(c);
		for(int i = 0; i < c; i++){
			bsn.pop().putData(buff);
		}
		
//		System.out.println("Sent " + c + " BSNs!");
		
		s.setData(buff.getArray(), 0, buff.size());
		
//		System.out.println("buffsize " + buff.size() + " packagesize: " + s.getLength() + " packageOffset: " + s.getOffset());
		
		rBuff.clear();
		tryToSendTillReceived();
		
		rBuff.setSize(r.getLength());
//		rBuff.readFloat();
		
		byte flag = rBuff.read();
		
		boolean gettinChunk = k != null;
		
//		if(flag == Server.CHUNKSENDFLAG){
//			System.out.println("CHUNK!");
//		}
		
//		else if(flag == Server.STANDARDFLAG){
//			System.out.println("standard Expecting a chunk? " + gettinChunk);
//		}
//		else{
//			System.out.println("?!? " + flag);
//		}
		
		if(flag == Server.CHUNKSENDFLAG){
			int cx = rBuff.readInt();
			int cy = rBuff.readInt();
			int cz = rBuff.readInt();
			ChunkManager.loadCompressedChunk(rBuff, cx, cy, cz);
//			System.out.println(cx + ", " + cy + ", " + cz + " received!");
//			chunksReceived++;
			boolean received = false;
			for(int i = 0; i < 10 && !received; i++){
				try{
					socket.receive(r);
					rBuff.setPosition(0);
					rBuff.setSize(r.getLength());
					
					received = true;
				}catch(Exception e){};
			}
			if(!received){
				return;
			}
			gettinChunk = false;
			rBuff.read();
//			System.out.println("received a chunk!");
		}else if(gettinChunk){
			gettinChunk = rBuff.read() == 1;
//			if(gettinChunk){
//				System.out.println("A CHUNK!!!!!");
//			}else{
//				System.out.println("I'm waitin...");
//			}
		}else{
			rBuff.read();
		}
		
		int notifies = rBuff.readInt();
		
		BlockSetNotify.applyDatas(notifies, rBuff);
		
		notifiesCleared = notifies;
		
		int players = rBuff.readInt();
		for(int i = 0; i < players; i++){
			// TODO go through players list and so on...
			getVect(MultiplayerData.otherPos);
			getVect(MultiplayerData.otherVel);
			MultiplayerData.otherRotY = rBuff.readFloat();
		}
		
		if(gettinChunk){
			boolean received = false;
			for(int i = 0; i < 10 && !received; i++){
				try{
					socket.receive(r);
					rBuff.setPosition(0);
					rBuff.setSize(r.getLength());
					received = true;
//					System.out.println("received a chunk!");
				}catch(Exception e){};
			}
			if(received){
				if(rBuff.read() == Server.CHUNKSENDFLAG){
					int cx = rBuff.readInt();
					int cy = rBuff.readInt();
					int cz = rBuff.readInt();
					ChunkManager.loadCompressedChunk(rBuff, cx, cy, cz);
//					System.out.println(cx + ", " + cy + ", " + cz + " received!");
//					chunksReceived++;
				}
			}
		}
		
//		if(Meth.systemTime() > lastShoutout+3000){
//			System.out.println("ChunkRequestsSent: " + chunkRequestsSent + " chunksReceived: " + chunksReceived);
//			lastShoutout = Meth.systemTime();
//		}
		
	}
	
	private int notifiesCleared = 0;
	
	public static final long MAXTIMEOUT = 5000;
	
	private boolean tryToSendTillReceived(){
		boolean received = false;
		long millis = System.currentTimeMillis();
		while(!received && (System.currentTimeMillis() - millis) < MAXTIMEOUT){
			try{
				socket.send(s);
				socket.receive(r);
				received = true;
			}catch(Exception e){};
		}
		return received;
	}
	
	private void addPlayerData(){
		Player p = Player.players.get(0);
		buff.addFloat(p.getPosition().x);
		buff.addFloat(p.getPosition().y);
		buff.addFloat(p.getPosition().z);
		buff.addFloat(p.getVelocity().x);
		buff.addFloat(p.getVelocity().y);
		buff.addFloat(p.getVelocity().z);
		buff.addFloat(p.getRotY());
	}
	
//	public void requestChunk(int cx, int cy, int cz) throws IOException{
//		buff.clear();
//		buff.add(Server.one);
//		addCode();
//		buff.addInt(cx);
//		buff.addInt(cy);
//		buff.addInt(cz);
//		
//		s.setData(buff.getArray(), 0, buff.size());
//		rBuff.clear();
//		
//		tryToSendTillReceived();
//		
//		rBuff.setSize(r.getLength());
////		rBuff.readFloat();
//		byte msg = rBuff.read();
//		if(msg == 0){
//			// TODO mark Chunk as null? Or wait for Generation?
//		}else{
//			ChunkManager.loadChunk(rBuff, cx, cy, cz);
////			short[][][] data = new short[Chunk.SIZE][Chunk.SIZE][Chunk.SIZE];
////			for(int x = 0; x < Chunk.SIZE; x++){
////				for(int y = 0; y < Chunk.SIZE; y++){
////					for(int z = 0; z < Chunk.SIZE; z++){
////						short s = rBuff.readShort();
////						if(s != 0)
////							System.out.println("values[" + x + "][" + y + "][" + z + "] = " + s);
////						data[x][y][z] = rBuff.readShort();
////					}
////				}
////			}
//		}
//	}
	
	private void addCode(){
		buff.addInt(Server.CODE);
	}

	private void getVect(Vector3f v) {
		v.x = rBuff.readFloat();
		v.y = rBuff.readFloat();
		v.z = rBuff.readFloat();
	}

	private void getServerInfo() throws IOException {
		buff.clear();
		buff.add(Server.LOGINFLAG);
		buff.addInt(playerID);
		s.setData(buff.getArray(), 0, buff.size());
		s.setAddress(InetAddress.getByName("255.255.255.255"));
		s.setPort(Server.PORT);
		// System.out.println("--CLIENT: sending client message...");
		
//		socket.send(s);
//		socket.receive(r);
		
		tryToSendTillReceived();
		
		// System.out.println("--CLIENT: server message received!");
		// server = r.getSocketAddress();
		s.setAddress(r.getAddress());
		s.setPort(r.getPort());
		
		// here the message if you're already on that server should be read!!!
		
	}

	private static Thread c;

	public static void init() {
		c = new Thread("Client") {
			@Override
			public void run() {
				try {
					// System.out.println("--CLIENT: creating client...");
					Client c = new Client();
					// System.out.println("--CLIENT: Client successfully
					// created!");
//					while (ThreadManager.running()) {
//						c.sendRequest();
//						Meth.wartn(100);
//					}
					Meth.wartn(1000);
					System.out.println("-------------------------------------------------------------------------------");
//					c.requestChunk(0, 0, 0);
					while(ThreadManager.running()){
						c.sendRequest();
					}
					c.sendLogOut();
					System.out.println("Client stopped!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		c.start();
	}

	public static void addBlockSetNotify(int x, int y, int z, short ID) {
		bsn.add(new BlockSetNotify(x, y, z, ID));
	}
	
	private static ArrayDeque<BlockSetNotify> bsn = new ArrayDeque<>();

}