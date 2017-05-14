package network;

import static network.Server.DATA_LENGTH;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

import org.joml.Vector3f;

import threadingStuff.ThreadManager;
import toolBox.Meth;

public class Client {

	private static DatagramPacket r = new DatagramPacket(new byte[DATA_LENGTH], DATA_LENGTH),
			s = new DatagramPacket(new byte[DATA_LENGTH], DATA_LENGTH);
	// private static SocketAddress server;

	private DatagramSocket socket;
	private ByteBuffer bb = ByteBuffer.allocate(DATA_LENGTH);

	public Client() throws IOException {
		socket = new DatagramSocket();
		getServerInfo();
		// socket.connect(server);
	}

	public void getData() throws IOException {
		// System.out.println("--CLIENT: sent a message!");
		socket.send(s);
		// System.out.println("--CLIENT: trying to receive data...");

		socket.receive(r);
		bb.clear();
		bb.put(r.getData());
		bb.rewind();
		bb.getFloat();
		getVect(MultiplayerData.otherPos);
		getVect(MultiplayerData.otherVel);
		MultiplayerData.otherRotY = bb.getFloat();
		// System.out.println("--CLIENT: data received! " +
		// MultiplayerData.otherRotY);
	}

	private void getVect(Vector3f v) {
		v.x = bb.getFloat();
		v.y = bb.getFloat();
		v.z = bb.getFloat();
	}

	private void getServerInfo() throws IOException {
		s.getData()[0] = 1;
		s.getData()[1] = 1;
		s.getData()[2] = 1;
		s.getData()[3] = 1;

		s.setAddress(InetAddress.getByName("255.255.255.255"));
		s.setPort(Server.PORT);
		// System.out.println("--CLIENT: sending client message...");
		socket.send(s);
		// System.out.println("--CLIENT: client message sent!");
		// System.out.println("--CLIENT: receiving server message...");
		socket.receive(r);
		// System.out.println("--CLIENT: server message received!");
		// server = r.getSocketAddress();
		s.setAddress(r.getAddress());
		s.setPort(r.getPort());
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
					while (ThreadManager.running()) {
						c.getData();
						Meth.wartn(100);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		c.start();
	}

}