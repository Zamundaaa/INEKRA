package menuThings;

public class Rectangle {

	public float x, y, w, h;

	public Rectangle() {

	}

	public Rectangle(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public boolean contains(float x, float y) {
		return x >= this.x && x <= this.x + this.w && y >= this.y && y <= this.y + this.h;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return w;
	}

	public float getHeight() {
		return h;
	}

	public void setBounds(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public boolean isNull() {
		return x == 0 && y == 0 && w == 0 && h == 0;
	}
	
	public boolean noSize(){
		return w == 0 && h == 0;
	}
	
	@Override
	public String toString(){
		return "X: " + x + " Y; " + y + " W: " + w + " H: " + h;
	}

}
