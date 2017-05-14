package data;

public class CM {

	// if (px != cx || py != cy || pz != cz || listUpdater == null ||
	// !listUpdater.isAlive()) {
	// cx = px;
	// cy = py;
	// cz = pz;
	// if (listUpdater == null || !listUpdater.isAlive()) {
	// listUpdater = new Thread("listUpdater") {
	// @Override
	// public void run() {
	// // System.out.println("ListUpdater started");
	// while (ThreadManager.running()) {
	// while (!rebuildList && ThreadManager.running()) {
	// Meth.wartn(10);
	// }
	// if (!ThreadManager.running()) {
	// return;
	// }
	// ArrayDeque<Key3D> xq = Queues.help1;
	// xq.add(new Key3D(0, 0, 0));
	// toLoad.clear();
	// rebuildList = false;
	// while (xq.size() > 0 && !rebuildList) {
	// Key3D k = xq.poll();
	// int rx = cx + k.getX();
	// int ry = cy + k.getY();
	// int rz = cz + k.getZ();
	// toLoad.add(new Key3D(rx, ry, rz));
	// placeholder_ListUpdater.set(rx + 1, ry, rz);
	// if ((k.getX() + 1) * (k.getX() + 1) + (k.getY()) * (k.getY())
	// + (k.getZ()) * (k.getZ()) <= genRadSq
	// && !toLoad.contains(placeholder_ListUpdater)) {
	// placeholder_ListUpdater.set(k.getX() + 1, k.getY(), k.getZ());
	// if (!xq.contains(placeholder_ListUpdater))
	// xq.add(new Key3D(k.getX() + 1, k.getY(), k.getZ()));
	// }
	// placeholder_ListUpdater.set(rx - 1, ry, rz);
	// if ((k.getX() - 1) * (k.getX() - 1) + (k.getY()) * (k.getY())
	// + (k.getZ()) * (k.getZ()) <= genRadSq
	// && !toLoad.contains(placeholder_ListUpdater)) {
	// placeholder_ListUpdater.set(k.getX() - 1, k.getY(), k.getZ());
	// if (!xq.contains(placeholder_ListUpdater))
	// xq.add(new Key3D(k.getX() - 1, k.getY(), k.getZ()));
	// }
	// placeholder_ListUpdater.set(rx, ry + 1, rz);
	// if ((k.getX()) * (k.getX()) + (k.getY() + 1) * (k.getY() + 1)
	// + (k.getZ()) * (k.getZ()) <= genRadSq && (k.getY() * k.getY()) <=
	// genDistYSq
	// && !toLoad.contains(placeholder_ListUpdater)) {
	// placeholder_ListUpdater.set(k.getX(), k.getY() + 1, k.getZ());
	// if (!xq.contains(placeholder_ListUpdater))
	// xq.add(new Key3D(k.getX(), k.getY() + 1, k.getZ()));
	// }
	// placeholder_ListUpdater.set(rx, ry - 1, rz);
	// if ((k.getX()) * (k.getX()) + (k.getY() - 1) * (k.getY() - 1)
	// + (k.getZ()) * (k.getZ()) <= genRadSq && (k.getY() * k.getY()) <=
	// genDistYSq
	// && !toLoad.contains(placeholder_ListUpdater)) {
	// placeholder_ListUpdater.set(k.getX(), k.getY() - 1, k.getZ());
	// if (!xq.contains(placeholder_ListUpdater))
	// xq.add(new Key3D(k.getX(), k.getY() - 1, k.getZ()));
	// }
	// placeholder_ListUpdater.set(rx, ry, rz + 1);
	// if ((k.getX()) * (k.getX()) + (k.getY()) * (k.getY())
	// + (k.getZ() + 1) * (k.getZ() + 1) <= genRadSq
	// && !toLoad.contains(placeholder_ListUpdater)) {
	// placeholder_ListUpdater.set(k.getX(), k.getY(), k.getZ() + 1);
	// if (!xq.contains(placeholder_ListUpdater))
	// xq.add(new Key3D(k.getX(), k.getY(), k.getZ() + 1));
	// }
	// placeholder_ListUpdater.set(rx, ry, rz - 1);
	// if ((k.getX()) * (k.getX()) + (k.getY()) * (k.getY())
	// + (k.getZ() - 1) * (k.getZ() - 1) <= genRadSq
	// && !toLoad.contains(placeholder_ListUpdater)) {
	// placeholder_ListUpdater.set(k.getX(), k.getY(), k.getZ() - 1);
	// if (!xq.contains(placeholder_ListUpdater))
	// xq.add(new Key3D(k.getX(), k.getY(), k.getZ() - 1));
	// }
	// }
	// }
	// }
	// };
	// listUpdater.start();
	// }
	// rebuildList = true;
	// }

}
