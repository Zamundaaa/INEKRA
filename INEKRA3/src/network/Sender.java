package network;

import java.io.IOException;
import java.net.*;

public class Sender {

	private static DatagramSocket socket;
	private static DatagramPacket p;

	public static void main(String[] args) {
		try {
			System.out.println("creating Socket...");
			socket = new DatagramSocket(Server.PORT);
			System.out.println("sending packet!");
			p = new DatagramPacket(new byte[] { 0, 0, 0, 0, 0 }, 5);
			p.setAddress(InetAddress.getByName("localhost"));
			socket.send(p);
			System.out.println("Sent!");
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
