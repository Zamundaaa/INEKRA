package network;

import java.net.*;

class UDPServer {

	public static void main(String args[]) throws Exception {
		DatagramSocket serverSocket = new DatagramSocket(9876);
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		// do{
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		System.out.println("waiting for receive!");
		serverSocket.receive(receivePacket);
		String sentence = new String(receivePacket.getData());
		System.out.println("RECEIVED: " + sentence);
		InetAddress IPAddress = receivePacket.getAddress();
		int port = receivePacket.getPort();
		String capitalizedSentence = sentence.toUpperCase();
		sendData = capitalizedSentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		System.out.println("Sending: '" + capitalizedSentence + "'");
		serverSocket.send(sendPacket);
		// }while(System.in.read() == -1);
		serverSocket.close();
	}
}