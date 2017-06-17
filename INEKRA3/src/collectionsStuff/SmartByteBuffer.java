package collectionsStuff;

public class SmartByteBuffer extends ArrayListB {

	private int pos = 0;

	public SmartByteBuffer(int size) {
		super(size);
	}

	public SmartByteBuffer() {
		super();
	}

	public SmartByteBuffer(byte[] data) {
		super(data);
	}

	/**
	 * reads a byte at the current position and increments the position
	 */
	public byte read() {
		if(pos < size)
			return values[pos++];
		else
			throw new IndexOutOfBoundsException("pos (" + pos + ") is bigger than size (" + size + ") !");
	}
	
	public void addChar(char c){
		add((byte)((c >> 8) & 0x00FF));
		add((byte)(c & 0x00FF));
	}
	
	/**
	 * @return the char composed of the first 8 bits of the byte at position i and the last 8 bits of th byte at position i+1 
	 */
	public char getChar(int i){
		return (char) (((values[i++] & 0xFF) << 8) | (values[i] & 0xFF));
	}
	
	/**
	 * reads a char at the position and increments the position by 2
	 */
	public char readChar(){
		int p = pos;
		if(pos+1 < size){
			return (char) (((values[pos++] & 0xFF) << 8) | (values[pos++] & 0xFF));
		}else{
			pos = p;
			throw new IndexOutOfBoundsException("Failed to read a char! Index: " + pos + " size: " + size);
		}
	}
	
	public void addShort(short s){
		add((byte)((s >> 8) & 0xFF));
		add((byte)(s & 0xFF));
	}
	
	/**
	 * @return the short composed of the first 8 bits of the byte at position i and the last 8 bits of th byte at position i+1 
	 */
	public short getShort(int i){
		return (short) (((values[i++] & 0xFF) << 8) | (values[i] & 0xFF));
	}
	
	public static int NEW = 1, OLD = 0;
	
	private int shortByteOrder = NEW;
	
	public void setToOldShortByteOrder(){
		shortByteOrder = OLD;
	}
	
	/**
	 * reads a short at the position and increments the position by 2
	 */
	public short readShort(){
		int p = pos;
		if(pos+1 < size){
			if(shortByteOrder == OLD)
				return (short) ((values[pos++] & 0xFF) | ((values[pos++] & 0xFF) << 8));
			else
				return (short) (((values[pos++] & 0xFF) << 8) | (values[pos++] & 0xFF));
		}else{
			pos = p;
			throw new IndexOutOfBoundsException("Failed to read a short! Index: " + pos + " size: " + size);
		}
	}
	
	public void addFloat(float f){
		addInt(Float.floatToRawIntBits(f));
	}
	
	public float getFloat(int i){
		return Float.intBitsToFloat(getInt(i));
	}
	
	public float readFloat(){
		return Float.intBitsToFloat(readInt());
	}
	
	public void addDouble(double d){
		addLong(Double.doubleToRawLongBits(d));
	}
	
	public double getDouble(int i){
		return Double.longBitsToDouble(getLong(i));
	}
	
	public double readDouble(){
		return Double.longBitsToDouble(readLong());
	}
	
	public void addInt(int i){
		add((byte)((i >> 24) & 0xFF));
		add((byte)((i >> 16) & 0xFF));
		add((byte)((i >> 8) & 0xFF));
		add((byte)(i & 0xFF));
	}
	
	public int getInt(int i){
		return (((values[i++] & 0xFF) << 24) | ((values[i++] & 0xFF) << 16) | ((values[i++] & 0xFF) << 8) | (values[i] & 0xFF));
	}
	
	public int readInt(){
		int p = pos;
		if(pos+3 < size){
			return (((values[pos++] & 0xFF) << 24) | ((values[pos++] & 0xFF) << 16) | ((values[pos++] & 0xFF) << 8) | (values[pos++] & 0xFF));
		}else{
			pos = p;
			throw new IndexOutOfBoundsException("Failed to read an int! Index: " + pos + " size: " + size);
		}
	}
	
	public void addLong(long l){
		add((byte)((l >> 56) & 0x00FF));
		add((byte)((l >> 48) & 0x00FF));
		add((byte)((l >> 40) & 0x00FF));
		add((byte)((l >> 32) & 0x00FF));
		
		add((byte)((l >> 24) & 0x00FF));
		add((byte)((l >> 16) & 0x00FF));
		add((byte)((l >> 8) & 0x00FF));
		add((byte)(l & 0x00FF));
	}
	
	public long getLong(int i){
		return ((values[i++] & 0xFF) << 56) | ((values[i++] & 0xFF) << 48) | ((values[i++] & 0xFF) << 40) | ((values[i++] & 0xFF) << 32) | ((values[i++] & 0xFF) << 24) | ((values[i++] & 0xFF) << 16) | ((values[i++] & 0xFF) << 8) | (values[i] & 0xFF);
	}
	
	public long readLong(){
		int p = pos;
		if(pos+7 < size){
			return ((long)(values[pos++] & 0xFF) << 56) | ((long)(values[pos++] & 0xFF) << 48) | ((long)(values[pos++] & 0xFF) << 40) | ((long)(values[pos++] & 0xFF) << 32) | ((long)(values[pos++] & 0xFF) << 24) | ((long)(values[pos++] & 0xFF) << 16) | ((long)(values[pos++] & 0xFF) << 8) | ((long)values[pos++] & 0xFF);
		}else{
			pos = p;
			throw new IndexOutOfBoundsException("Failed to read a long! Index: " + pos + " size: " + size);
		}
	}
	
	/**
	 * only Strings with a length less than a short are permitted (for now)
	 */
	public void addString(String s){
		addShort((short)s.length());
		for(int i = 0; i < s.length(); i++){
			addChar(s.charAt(i));
		}
	}
	
	/**
	 * reads a short, this is the length of the returned string. 
	 * This string is then read by a consecutive array of readChar() and returned
	 */
	public String readString(){
		short l = readShort();
		StringBuilder ret = new StringBuilder(l);
		for(int i = 0; i < l; i++){
			ret.append(readChar());
		}
		return ret.toString();
	}
	
	public int position(){
		return pos;
	}
	
	public void resetPos(){
		pos = 0;
	}

	public int remaining() {
		return size - pos-1;
	}

	public void setPosition(int pos) {
		this.pos = pos;
	}
	
	@Override
	public void clear(){
		super.clear();
		resetPos();
	}

	/**
	 * sets size to s, or if s is bigger than this Buffers internal array's length, to the internal array's length
	 */
	public void setSize(int s) {
		this.size = s;
		if(this.size > values.length){
			this.size = values.length;
		}
	}
	
//	public static void main(String[] args){
//		SmartByteBuffer s = new SmartByteBuffer();
//		byte b = -128;
//		for(short i = 0; i < 1000; i++){
//			if(i % 2 == 0){
//				s.addShort(i);
//			}else{
//				s.add(b);
//			}
//		}
//		for(int i = 0; i < 1000; i++){
//			if(i % 2 == 0){
//				System.out.println(s.readShort());
//			}else{
//				System.out.println(s.read());
//			}
//		}
//	}

}
