package collectionsStuff;

public class ArrayListI extends PrimitiveList{
	
	private int[] values;

	public ArrayListI() {
		this(STARTCAPACITY);
	}

	public ArrayListI(int startCapacity) {
		values = new int[startCapacity];
	}

	public void ensureCapacity(int min) {
		if (min > values.length) {
			grow();
		}
	}

	public void add(int f) {
		ensureCapacity(size + 1);
		values[size] = f;
		size++;
	}

	public void set(int i, int f) {
		values[i] = f;
	}

	public int remove(int i) {
		if (i >= 0 && i < size) {
			int ret = values[i];
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

	public int get(int i) {
		return values[i];
	}

	public int size() {
		return size;
	}

	private void grow() {
		int nS = values.length * 2;
		int[] newValues = new int[nS];
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

	public int[] capToArray() {
		int[] ret = new int[size];
		for(int i = 0; i < ret.length; i++)
			ret[i] = values[i];
		return ret;
	}
	
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

	public static String arrToString(int[] is) {
		StringBuilder b = new StringBuilder();
		b.append("[ ");
		for(int i = 0; i < is.length-1; i++){
			b.append(is[i]);
			b.append(", ");
		}
		b.append(is[is.length-1]);
		b.append(" ]");
		return b.toString();
	}
	
	public void addAll(ArrayListI a){
		for(int i = 0; i < a.size; i++)
			add(a.get(i));
	}

	public void addAll(ArrayListI a, int add) {
		for(int i = 0; i < a.size; i++)
			add(a.get(i)+add);
	}

}
