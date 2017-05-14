package network;

import org.joml.Vector3f;

public class SensorData {

	// private static final int DATA_LENGTH = 20*4;

	// private static final int D = 40;
	// private static float[] bx = new float[D];
	// private static float[] by = new float[D];
	// private static float[] bz = new float[D];
	// private static float[] prox = new float[D];
	public static final Vector3f orientation = new Vector3f(), dorientation = new Vector3f();

	public static float proximity;

	// private static Thread t;

	public static void init() {
		// t = new Thread("SensorDataReceiver"){
		// @Override
		// public void run(){
		// try{
		//
		// DatagramSocket serverSocket = new DatagramSocket(7777);
		//
		//// System.out.println("Port: " + serverSocket.getPort() + " IP: " +
		// serverSocket.getLocalAddress());
		//
		// byte[] data = new byte[DATA_LENGTH];
		// ByteBuffer buff = ByteBuffer.wrap(data);
		// FloatBuffer floatbuff = buff.asFloatBuffer();
		//
		// while(ThreadManager.running()){
		// DatagramPacket pack = new DatagramPacket(data, data.length);
		// serverSocket.receive(pack);
		//
		// for(int i = bx.length-1; i > 0; i--){
		// bx[i] = bx[i-1];
		// by[i] = by[i-1];
		// bz[i] = bz[i-1];
		//// prox[i] = prox[i-1];
		// }
		// bx[0] = floatbuff.get(9)*Meth.angToRad;
		// by[0] = floatbuff.get(10)*Meth.angToRad;
		// bz[0] = floatbuff.get(11)*Meth.angToRad;
		//// prox[0] = floatbuff.get(13);
		// float dx = 0, dy = 0, dz = 0;//, dprox = 0
		// for(int i = 0; i < bx.length; i++){
		// dx += bx[i];
		// dy += by[i];
		// dz += bz[i];
		//// dprox += prox[i];
		// }
		// dx /= bx.length;
		// dy /= by.length;
		// dz /= bz.length;
		//// dprox /= prox.length;
		//
		// proximity = floatbuff.get(15);
		//
		// orientation.sub(dx, dy, dz, dorientation);
		// dorientation.negate();
		//
		// orientation.set(dx, dy, dz);
		//
		// }
		// serverSocket.close();
		// }catch(Exception e){
		// e.printStackTrace();
		// }
		// }
		// };
		// t.start();
		proximity = Integer.MAX_VALUE;
	}

}
