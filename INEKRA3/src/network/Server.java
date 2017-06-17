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

	public static final int CODE = 562465348;

	public static final int PORT = 7777;
	public static final int DATA_LENGTH = 8 * 4;
	public static byte[] data = new byte[DATA_LENGTH];

	private static DatagramSocket socket;

	private static DatagramPacket r = new DatagramPacket(new byte[DATA_LENGTH], DATA_LENGTH),
			s = new DatagramPacket(new byte[DATA_LENGTH], DATA_LENGTH);

	private static ArrayList<InetAddress> clients = new ArrayList<InetAddress>();

	private static Thread server;

	private static final SmartByteBuffer rBuff = new SmartByteBuffer(r.getData());

	private static final SmartByteBuffer buffer = new SmartByteBuffer();

	public static void init() {
		try {
			socket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		server = new Thread() {
			@Override
			public void run() {
				try {
					ArrayList<Player> nearPlayers = new ArrayList<Player>();
					socket.setSoTimeout(10000);
					while (ThreadManager.running()) {
						while (ThreadManager.running()) {
							try {
								socket.receive(r);
							} catch (Exception e) {
							}
							;
						}

						buffer.clear();
						rBuff.resetPos();
						rBuff.setSize(r.getLength());

						if (!clients.contains(r.getAddress())) {
							// System.out.println("SERVER: received message...
							// sending reply");
							clients.add(r.getAddress());
							s.setAddress(r.getAddress());
							s.setPort(r.getPort());
							int loginID = rBuff.readInt();
							buffer.clear();
							if (Player.playerIDs.get(loginID) == null) {
								buffer.add(one);
							} else {
								buffer.add(zero);
							}
							// buffer.addInt(DataManager.addPlayer(loginID));
							s.setData(buffer.getArray(), 0, buffer.size());
							socket.send(s);
							System.out.println("SERVER: message sent back! " + r.getAddress());
						} else if (rBuff.readInt() == CODE) {
							byte request = rBuff.read();
							if (request == 0) {
								// buffer.addInt(0);
								int playerID = rBuff.readInt();
								Player p = Player.playerIDs.get(playerID);
								updatePlayerData(p, rBuff);

								putChunkData();

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
							} else if (request == 1) {
								putChunkData();
								s.setData(buffer.getArray(), 0, buffer.size());
								s.setPort(r.getPort());
								s.setAddress(r.getAddress());
								socket.send(s);
							}
							System.out.println("Request was: " + request);
						}
						Meth.wartn(5);
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

	private static boolean putChunkData() {
		int X = rBuff.readInt();
		int Y = rBuff.readInt();
		int Z = rBuff.readInt();
		Chunk c = ChunkManager.getWithChunkCoords(X, Y, Z);
		if (c == null) {
			ChunkManager.markChunkForLoading(X, Y, Z);
			buffer.add(zero);
			return false;
		}
		buffer.add(one);
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					buffer.addShort(c.getIC(x, y, z));
				}
			}
		}
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					buffer.addShort(c.getLightValueIC(x, y, z));
				}
			}
		}
		return true;
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