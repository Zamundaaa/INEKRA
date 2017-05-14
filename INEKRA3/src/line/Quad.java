package line;

import org.joml.Vector3f;

import toolBox.FontColorManager;

public class Quad {

	private Line[] lines;
	private float x, y, z, x2, y2, z2;

	public Quad(float x, float y, float z, float x2, float y2, float z2) {
		lines = new Line[12];
		for (int i = 0; i < 12; i++) {
			lines[i] = new Line();
		}
		set(x, y, z, x2, y2, z2);
	}

	private boolean hidden = false;

	public void hide() {
		if (!hidden) {
			for (int i = 0; i < 12; i++) {
				lines[i].hide();
			}
		}
	}

	public void show() {
		if (hidden) {
			for (int i = 0; i < 12; i++) {
				lines[i].show();
			}
		}
	}

	public void set1(float x, float y, float z) {
		set(x, y, z, x2, y2, z2);
	}

	public void set2(float x, float y, float z) {
		set(this.x, this.y, this.z, x, y, z);
	}

	public void set(float x, float y, float z, float x2, float y2, float z2) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		if (!blocky) {
			lines[0].set(x, y, z, x2, y, z);
			lines[1].set(x, y, z, x, y2, z);
			lines[2].set(x, y2, z, x2, y2, z);
			lines[3].set(x2, y, z, x2, y2, z);

			lines[4].set(x, y, z2, x2, y, z2);
			lines[5].set(x, y, z2, x, y2, z2);
			lines[6].set(x, y2, z2, x2, y2, z2);
			lines[7].set(x2, y, z2, x2, y2, z2);

			lines[8].set(x, y, z, x, y, z2);
			lines[9].set(x, y2, z, x, y2, z2);
			lines[10].set(x2, y, z, x2, y, z2);
			lines[11].set(x2, y2, z, x2, y2, z2);
		} else {
			float dx, dy, dz, dx2, dy2, dz2;
			if (x < x2) {
				dx = x;
				dx2 = x2 + 1;
			} else {
				dx = x2;
				dx2 = x + 1;
			}
			if (y < y2) {
				dy = y;
				dy2 = y2 + 1;
			} else {
				dy = y2;
				dy2 = y + 1;
			}
			if (z < z2) {
				dz = z;
				dz2 = z2 + 1;
			} else {
				dz = z2;
				dz2 = z + 1;
			}

			lines[0].set(dx, dy, dz, dx2, dy, dz);
			lines[1].set(dx, dy, dz, dx, dy2, dz);
			lines[2].set(dx, dy2, dz, dx2, dy2, dz);
			lines[3].set(dx2, dy, dz, dx2, dy2, dz);

			lines[4].set(dx, dy, dz2, dx2, dy, dz2);
			lines[5].set(dx, dy, dz2, dx, dy2, dz2);
			lines[6].set(dx, dy2, dz2, dx2, dy2, dz2);
			lines[7].set(dx2, dy, dz2, dx2, dy2, dz2);

			lines[8].set(dx, dy, dz, dx, dy, dz2);
			lines[9].set(dx, dy2, dz, dx, dy2, dz2);
			lines[10].set(dx2, dy, dz, dx2, dy, dz2);
			lines[11].set(dx2, dy2, dz, dx2, dy2, dz2);
		}

		setColors();

	}

	public void setColors() {
		for (int i = 0; i < 12; i++) {
			lines[i].setColor(1 - FontColorManager.CV, FontColorManager.CV, 1 - FontColorManager.CV);
		}
	}

	public void set1(Vector3f v) {
		set1(v.x, v.y, v.z);
	}

	public void set2(Vector3f v) {
		set2(v.x, v.y, v.z);
	}

	public float x1() {
		return x;
	}

	public float y1() {
		return y;
	}

	public float z1() {
		return z;
	}

	public float x2() {
		return x2;
	}

	public float y2() {
		return y2;
	}

	public float z2() {
		return z2;
	}

	private boolean blocky = false;

	public void setBlocky() {
		blocky = true;
	}

	public void cleanUp() {
		for(int i = 0; i < lines.length; i++)
			lines[i].cleanUp();
	}

}
