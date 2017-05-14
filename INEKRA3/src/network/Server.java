package network;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.joml.Vector3f;

import entities.Player;
import gameStuff.WorldObjects;
import threadingStuff.ThreadManager;
import toolBox.Meth;

public class Server {

	public static final int PORT = 7777;
	public static final int DATA_LENGTH = 8 * 4;
	public static byte[] data = new byte[DATA_LENGTH];

	private static DatagramSocket socket;

	private static DatagramPacket r = new DatagramPacket(new byte[DATA_LENGTH], DATA_LENGTH),
			s = new DatagramPacket(new byte[DATA_LENGTH], DATA_LENGTH);

	private static ArrayList<InetAddress> clients = new ArrayList<InetAddress>();

	private static Thread server;

	private static final ByteBuffer b = ByteBuffer.allocate(DATA_LENGTH);

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
					while (ThreadManager.running()) {
						socket.receive(r);

						if (!clients.contains(r.getAddress())) {
							// System.out.println("SERVER: received message...
							// sending reply");
							clients.add(r.getAddress());
							s.setAddress(r.getAddress());
							s.setPort(r.getPort());
							socket.send(s);
							System.out.println("SERVER: message sent back! " + r.getAddress());
						} else if (r.getData()[0] == r.getData()[1] && r.getData()[1] == r.getData()[2]
								&& r.getData()[2] == r.getData()[3] && r.getData()[3] == 1) {
							// System.out.println("SERVER: received message from
							// Client");
							// if(b.position() == 0){
							// System.out.println("SERVER: sending Data back!");
							// }
							b.rewind();
							b.putInt(0);
							Player p = WorldObjects.player;
							putVec(p.getPosition());
							putVec(p.getVelocity());
							b.putFloat(p.getRotY());
							// for (int i = 0; i < 5; i++) {
							// b.putFloat(i);
							// }
							s.setData(b.array());
							socket.send(s);
						}
						Meth.wartn(5);
					}
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		};
		server.start();
	}

	private static void putVec(Vector3f v) {
		b.putFloat(v.x);
		b.putFloat(v.y);
		b.putFloat(v.z);
	}

	// public static void main(String[] args) throws IOException {
	// init();
	// Client.init();
	// }

}