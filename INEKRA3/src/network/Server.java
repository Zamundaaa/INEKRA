package network;

import java.io.IOException;
import java.net.*;
import java.util.*;

import org.joml.Vector3f;

import collectionsStuff.ArrayListI;
import collectionsStuff.SmartByteBuffer;
import data.Chunk;
import data.ChunkManager;
import entities.Player;
import threadingStuff.ThreadManager;
import toolBox.Meth;

public class Server {
	
	public static final int maxBSNsAtOnce = 100;
	
	public static final int CODE = 562465348;
	public static final byte CHUNKSENDFLAG = 100, STANDARDFLAG = 99, LOGOUTFLAG = 88, LOGINFLAG = 87;

	public static final int PORT = 7777;
	public static final int MAX_DATA_LENGTH = 20000;
	public static byte[] data = new byte[MAX_DATA_LENGTH];

	private static DatagramSocket socket;

	private static DatagramPacket r = new DatagramPacket(new byte[MAX_DATA_LENGTH], MAX_DATA_LENGTH),
			s = new DatagramPacket(new byte[MAX_DATA_LENGTH], MAX_DATA_LENGTH);

	private static ArrayList<InetAddress> clients = new ArrayList<InetAddress>();

	private static Thread server;

	private static final SmartByteBuffer rBuff = new SmartByteBuffer(r.getData());

	private static final SmartByteBuffer buffer = new SmartByteBuffer();
	
//	private static int chunksSent, chunkRequests;
//	private static long lastShoutout;
	
	public static void init() {
		try {
			socket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		server = new Thread("NetworkServer") {
			@Override
			public void run() {
				try {
					ArrayList<Player> nearPlayers = new ArrayList<Player>();
					socket.setSoTimeout(10000);
					while (ThreadManager.running()) {
						boolean received = false;
						while (ThreadManager.running() && !received) {
							try {
								socket.receive(r);
								received = true;
							} catch (Exception e) {
							}
							;
						}
						
						if(!ThreadManager.running())
							break;

						buffer.clear();
						rBuff.resetPos();
						rBuff.setSize(r.getLength());
						
//						System.out.println(r.getLength());
						byte request = -1;
						if(rBuff.size() > 0)
							request = rBuff.read();
						
						if (request == LOGINFLAG && !clients.contains(r.getAddress())) {
							// System.out.println("SERVER: received message...
							// sending reply");
							clients.add(r.getAddress());
							s.setAddress(r.getAddress());
							s.setPort(r.getPort());
							int loginID = rBuff.readInt();
							buffer.clear();
							if (Player.playerIDs.get(loginID) == null) {
								buffer.add(one);
								System.out.println("Player with ID " + loginID + " just logged in!");
								new Player(new Vector3f(), 0, 0, 0, 0.15f, loginID);
							} else {
								buffer.add(zero);
							}
							// buffer.addInt(DataManager.addPlayer(loginID));
							s.setData(buffer.getArray(), 0, buffer.size());
							socket.send(s);
//							System.out.println("SERVER: message sent back! " + r.getAddress());
						} else if (rBuff.readInt() == CODE) {
							if (request == 0) {
								// buffer.addInt(0);
								int playerID = rBuff.readInt();
								Player p = Player.playerIDs.get(playerID);
//								System.out.println(playerID + " sent a package! data: ");
								updatePlayerData(p, rBuff);
								boolean sendChunk = rBuff.read() == 1;
								int cx = 0;
								int cy = 0;
								int cz = 0;
								
								buffer.add(STANDARDFLAG);
								
								if(sendChunk){
//									chunkRequests++;
									cx = rBuff.readInt();
									cy = rBuff.readInt();
									cz = rBuff.readInt();
//									if(checkForChunk(cx, cy, cz) == null){
//										buffer.add(zero);
//										sendChunk = false;
////										System.out.println(ChunkManager.getWithChunkCoords(cx, cy, cz) == null);
//									}else{
										buffer.add(one);
//									}
								}
								
								ArrayList<BlockSetNotify> notifies = ns.get(playerID);
								int bsnscleared = rBuff.readInt();
								if(notifies != null){
									for(int i = 0; i < bsnscleared; i++){
										release(notifies.remove(0));
									}
									int ns = notifies.size();
									if(ns > maxBSNsAtOnce){
										ns = maxBSNsAtOnce;
									}
									buffer.addInt(ns);
									for(int i = 0; i < ns; i++){
										notifies.get(i).putData(buffer);
									}
//									if(ns > 0){
//										System.out.println("notifies!");
//									}
								}else{
									buffer.addInt(0);
								}
								
								int bsns = rBuff.readInt();
								BlockSetNotify.applyDatas(bsns, rBuff);
								
//								}else{
//									buffer.addInt(0);
//								}
								
								
								
								nearPlayers.clear();
								for (int i = 0; i < Player.players.size(); i++) {
									Player p2 = Player.players.get(i);
									if (p2.getPosition().distanceSquared(p.getPosition()) < 65) {
										nearPlayers.add(p2);
									}
								}
								buffer.addInt(nearPlayers.size());
								for (int i = 0; i < nearPlayers.size(); i++) {
									p = nearPlayers.get(i);
									addVec(p.getPosition());
									addVec(p.getVelocity());
									buffer.addFloat(p.getRotY());
								}
								
								s.setData(buffer.getArray(), 0, buffer.size());
								s.setAddress(r.getAddress());
								s.setPort(r.getPort());
								socket.send(s);
								
								if(sendChunk){
									Chunk c = checkForChunk(cx, cy, cz);
									while(c == null){
										c = checkForChunk(cx, cy, cz);
										Meth.wartn(20);
									}
									buffer.clear();
									buffer.add(CHUNKSENDFLAG);
									putChunkData(c, cx, cy, cz);
									s.setData(buffer.getArray(), 0, buffer.size());
									socket.send(s);
//									System.out.println(cx + ", " + cy + ", " + cz + " sent!");
//									chunksSent++;
//									System.out.println("Sent chunk!");
								}
								
							} else if (request == 1) {
								int x = rBuff.readInt();
								int y = rBuff.readInt();
								int z = rBuff.readInt();
								Chunk c = checkForChunk(x, y, z);
								if(c == null){
									buffer.add(zero);
								}else{
									buffer.add(one);
									putChunkData(c, x, y, z);
								}
								s.setData(buffer.getArray(), 0, buffer.size());
								s.setPort(r.getPort());
								s.setAddress(r.getAddress());
								socket.send(s);
							} else if (request == LOGOUTFLAG){
								int playerID = rBuff.readInt();
								Player.remove(playerID);
								
								System.out.println("Player with ID " + playerID + " just logged out!");
								buffer.add(one);
								s.setData(buffer.getArray(), 0, buffer.size());
								s.setPort(r.getPort());
								s.setAddress(r.getAddress());
								socket.send(s);
							}
//							System.out.println("Request was: " + request);
						}
						Meth.wartn(5);
//						if(Meth.systemTime() > lastShoutout + 3000){
//							System.out.println("Requests got: " + chunkRequests + " Chunks sent: " + chunksSent
//									+ " Chunks in queue: " + ChunkLoader.queue.size());
//							lastShoutout = Meth.systemTime();
//						}
					}
					socket.close();
					System.out.println("Server stopped!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		server.start();
	}
	
	private static Chunk checkForChunk(int X, int Y, int Z){
		Chunk c = ChunkManager.getWithChunkCoords(X, Y, Z);
		if (c == null) {
			ChunkManager.markChunkForLoading(X, Y, Z);
			return null;
		}else{
			return c;
		}
	}

	private static void putChunkData(Chunk c, int X, int Y, int Z) {
		buffer.addInt(X);
		buffer.addInt(Y);
		buffer.addInt(Z);
		short count = 0;
		short ID = c.getIC(0, 0, 0);
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
//					buffer.addShort(c.getIC(x, y, z));
					if(c.getIC(x, y, z) == ID){
						count++;
					}else{
						buffer.addShort(count);
						buffer.addShort(ID);
						count = 1;
						ID = c.getIC(x, y, z);
					}
				}
			}
		}
		buffer.addShort(count);
		buffer.addShort(ID);
//		for (int x = 0; x < Chunk.SIZE; x++) {
//			for (int y = 0; y < Chunk.SIZE; y++) {
//				for (int z = 0; z < Chunk.SIZE; z++) {
//					buffer.addShort(c.getLightValueIC(x, y, z));
//				}
//			}
//		}
	}

	private static void updatePlayerData(Player p, SmartByteBuffer rbuff) {
		p.setPosition(rbuff.readFloat(), rbuff.readFloat(), rbuff.readFloat());
		p.getVelocity().set(rbuff.readFloat(), rbuff.readFloat(), rbuff.readFloat());
		p.setRotY(rbuff.readFloat());
	}

	public static final byte zero = 0, one = 1;

	private static void addVec(Vector3f v) {
		buffer.addFloat(v.x);
		buffer.addFloat(v.y);
		buffer.addFloat(v.z);
	}

	public static void notityBlockSet(int x, int y, int z, short ID) {
		ArrayListI players = getNearEnoughs(x, y, z, 200);
		for (int i = 0; i < players.size(); i++) {
			ArrayList<BlockSetNotify> list = ns.get(players.get(i));
			if (list == null) {
				list = new ArrayList<>();
				ns.put(players.get(i), list);
			}
			list.add(get(x, y, z, ID));
		}
	}

	private static ArrayListI getNearEnoughs(float x, float y, float z, float dist) {
		ArrayListI ret = lists.get(Thread.currentThread());
		if (ret == null) {
			ret = new ArrayListI();
			lists.put(Thread.currentThread(), ret);
		}
		ret.clear();
		for (int i = 0; i < Player.players.size(); i++) {
			Player p = Player.players.get(i);
			if (p.getPosition().distanceSquared(x, y, z) < dist * dist) {
				ret.add(p.playerID());
			}
		}
		return ret;
	}

	private static final Map<Thread, ArrayListI> lists = new HashMap<Thread, ArrayListI>();

	private static final Map<Integer, ArrayList<BlockSetNotify>> ns = new HashMap<Integer, ArrayList<BlockSetNotify>>();

	private static final ArrayDeque<BlockSetNotify> bsns = new ArrayDeque<BlockSetNotify>();

	private static void release(BlockSetNotify bsn) {
		synchronized (bsns) {
			bsns.add(bsn);
		}
	}

	private static BlockSetNotify get(int x, int y, int z, short ID) {
		if (bsns.isEmpty()) {
			return new BlockSetNotify(x, y, z, ID);
		} else {
			synchronized (bsns) {
				return bsns.pop().set(x, y, z, ID);
			}
		}
	}

}