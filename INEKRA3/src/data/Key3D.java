package data;

import java.util.Stack;

/**
 * a simple class representing a point in space using the integer coordinates x,
 * y and z. Could be called a integer vector / Vector3i. Has a optimized
 * hashCode() and equals() Method for easy and fast use in maps
 * 
 * @author xaver
 *
 */
public class Key3D {

	private static final Stack<Key3D> stack = new Stack<>();

	public static Key3D getInstance(int x, int y, int z) {
		if (stack.isEmpty()) {
			return new Key3D(x, y, z);
		} else {
			return stack.pop().set(x, y, z);
		}
	}

	public static Key3D getInstance(Key3D k) {
		return getInstance(k.x, k.y, k.z);
	}
	
	public Key3D add(int x, int y, int z){
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	private int x, y, z;

	public Key3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Key3D() {
		
	}

	public Key3D set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	@Override
	public boolean equals(Object otherone) {
		if (otherone instanceof Key3D) {
			Key3D o = (Key3D) otherone;
			return x == o.x && y == o.y && z == o.z;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (42 * x + 1646 * y) * z;
	}

	@Override
	public String toString() {
		return "X: " + x + " Y: " + y + " Z: " + z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	/**
	 * releases this Key3D to the internal stack!
	 */
	public void flush() {
		stack.add(this);
	}

}
