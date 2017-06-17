package collectionsStuff;

public class ArrayListBool extends PrimitiveList{

	private boolean[] values;

	public ArrayListBool() {
		this(STARTCAPACITY);
	}

	public ArrayListBool(int startCapacity) {
		values = new boolean[startCapacity];
	}

	public void ensureCapacity(int min) {
		if (min > values.length) {
			grow();
		}
	}

	public void add(boolean f) {
		ensureCapacity(size+1);
		values[size] = f;
		size++;
	}

	public void set(int i, boolean f) {
		values[i] = f;
	}

	public boolean remove(int i) {
		if (i >= 0 && i < size) {
			boolean ret = values[i];
			size--;
			for (int I = i; I < size; I++) {
				values[I] = values[I + 1];
			}
			return ret;
		} else {
			throw new ArrayIndexOutOfBoundsException(i);
		}
	}

	public boolean get(int i) {
		return values[i];
	}

	public int size() {
		return size;
	}

	private void grow() {
		int nS = values.length * 2;
		boolean[] newValues = new boolean[nS];
		for (int i = 0; i < values.length; i++) {
			newValues[i] = values[i];
		}
		values = newValues;
	}

	public void clear() {
		size = 0;
	}
	
	@Override
	public String toString(){
		return arrayToString(values);
	}
	
	public static String arrayToString(boolean[] arr) {
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

	public boolean[] capToArray() {
		boolean[] ret = new boolean[size];
		for(int i = 0; i < size; i++)
			ret[i] = values[i];
		return ret;
	}

	public void addAll(ArrayListBool a) {
		for(int i = 0; i < a.size; i++)
			add(a.get(i));
	}

}
