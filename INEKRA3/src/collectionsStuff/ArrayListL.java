package collectionsStuff;

public class ArrayListL extends PrimitiveList{

	private long[] values;

	public ArrayListL() {
		this(STARTCAPACITY);
	}

	public ArrayListL(int startCapacity) {
		values = new long[startCapacity];
	}

	public void ensureCapacity(int min) {
		if (min > values.length) {
			grow();
		}
	}

	public void add(long l) {
		ensureCapacity(size+1);
		values[size] = l;
		size++;
	}

	public void set(int i, long l) {
		values[i] = l;
	}

	public long remove(int i) {
		if (i >= 0 && i < size) {
			long ret = values[i];
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

	public long get(int i) {
		return values[i];
	}

	public int size() {
		return size;
	}

	/**
	 * 
	 */
	private void grow() {
		int nS = values.length * 2;
		long[] newValues = new long[nS];
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
	
	/**
	 * just returns {@link collectionsStuff.ArrayListL#arrToString(values)}
	 */
	@Override
	public String toString(){
		StringBuilder b = new StringBuilder();
		b.append("[ ");
		for(int i = 0; i < size-1; i++){
			b.append(values[i]);
			b.append(", ");
		}
		b.append(values[size-1]);
		b.append(" ]");
		return b.toString();
	}

	public static String arrToString(long[] ls) {
		StringBuilder b = new StringBuilder();
		b.append("[ ");
		for(int i = 0; i < ls.length-1; i++){
			b.append(ls[i]);
			b.append(", ");
		}
		b.append(ls[ls.length-1]);
		b.append(" ]");
		return b.toString();
	}

	public long averageValue() {
		return sum()/size;
	}

	public long sum() {
		long ret = 0;
		for(int i = 0; i < size; i++)
			ret += values[i];
		return ret;
	}
	
//	public static void main(String[] args){
//		ArrayListL list = new ArrayListL();
//		for(long l = 0; l < 20; l++){
//			list.add(l);
//		}
//		list.remove(10);
//		System.out.println(list);
//	}

}
