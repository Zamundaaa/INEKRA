package collectionsStuff;

public class ArrayListF {

	private float[] values;
	private int size;

	public ArrayListF() {
		this(10);
	}

	public ArrayListF(int startCapacity) {
		values = new float[startCapacity];
	}

	public void ensureCapacity(int min) {
		if (min > values.length) {
			grow();
		}
	}

	public void add(float f) {
		add(size, f);
	}

	public void add(int i, float f) {
		ensureCapacity(i + 1);
		values[i] = f;
		size++;
	}

	public float remove(int i) {
		if (i >= 0 && i < size) {
			float ret = values[i];
			size--;
			for (int I = i; I < size; I++) {
				values[I] = values[I + 1];
			}
			values[size] = 0;
			return ret;
		} else {
			throw new ArrayIndexOutOfBoundsException(i);
		}
	}

	public float get(int i) {
		return values[i];
	}

	public int size() {
		return size;
	}

	private void grow() {
		int nS = values.length * 2;
		float[] newValues = new float[nS];
		for (int i = 0; i < values.length; i++) {
			newValues[i] = values[i];
		}
		values = newValues;
	}

	public void clear() {
		for (int i = 0; i < size; i++) {
			values[i] = 0;
		}
		size = 0;
	}
	
	@Override
	public String toString(){
		return arrayToString(values);
	}
	
	public static String arrayToString(float[] arr) {
		StringBuilder ret = new StringBuilder();
		ret.append("[ ");
		for(int i = 0; i < arr.length-1; i++){
			ret.append(arr[i]);
			ret.append(", ");
		}
		ret.append(arr[arr.length-1]);
		ret.append(" ]");
		return ret.toString();
	}

}
