package collectionsStuff;

public class ArrayListB {
	
	protected byte[] values;
	protected int size;

	public ArrayListB() {
		this(10);
	}

	public ArrayListB(int startCapacity) {
		values = new byte[startCapacity];
	}

	public ArrayListB(byte[] data) {
		values = data;
		size = values.length;
	}

	public void ensureCapacity(int min) {
		if (min > values.length) {
			grow();
		}
	}

	public void add(byte b) {
		add(size, b);
	}

	public void add(int i, byte b) {
		ensureCapacity(i + 1);
		values[i] = b;
		size++;
	}

	public byte remove(int i) {
		if (i >= 0 && i < size) {
			byte ret = values[i];
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

	public byte get(int i) {
		return values[i];
	}

	public int size() {
		return size;
	}

	private void grow() {
		int nS = values.length * 2;
		byte[] newValues = new byte[nS];
		for (int i = 0; i < values.length; i++) {
			newValues[i] = values[i];
		}
		values = newValues;
	}

	public void clear() {
		// unnecessary!
//		for (int i = 0; i < size; i++) {
//			values[i] = 0;
//		}
		size = 0;
	}
	
	public byte[] capToArray(){
		byte[] newValues = new byte[size];
		for (int i = 0; i < size; i++) {
			newValues[i] = values[i];
		}
		return newValues;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for (int i = 0; i < size; i++) {
			sb.append(values[i]);
			if (i != size - 1)
				sb.append(", ");
		}
		sb.append(" ]");
		return sb.toString();
	}
	
	public void quickSortLowToHigh(){
		rekQuickSortLowToHigh(0, size-1);
	}
	
	public void rekQuickSortLowToHigh(int start, int end) {

		if (start >= end) {
			return;
		}
//		System.out.println("Beginning to start from start: " + start + " to end: " + end);
//		System.out.println(this);

		int i = start;
		int k = end - 1;
		byte pivot = values[end];

		do {
			while (values[i] <= pivot && i < end) {
				i++;
			}

			while (values[k] >= pivot && k > start) {
				k--;
			}

			if (i < k) {
				byte temp = values[i];
				values[i] = values[k];
				values[k] = temp;
			}

			if (values[i] > pivot) {
				byte temp = values[i];
				values[i] = values[end];
				values[end] = temp;
			}

			rekQuickSortLowToHigh(start, i - 1);
			rekQuickSortLowToHigh(i + 1, end);

		} while (i < k);
	}
	
}
