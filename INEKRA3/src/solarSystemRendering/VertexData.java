package solarSystemRendering;

import collectionsStuff.ArrayListF;
import collectionsStuff.ArrayListI;
import models.RawModel;
import renderStuff.Loader;

public class VertexData {
	
	private ArrayListF x, y, z;
	private ArrayListI indices;
	
	public VertexData() {
		x = new ArrayListF();
		y = new ArrayListF();
		z = new ArrayListF();
		indices = new ArrayListI();
	}
	
	public void addVertexWithIndices(float x, float y, float z){
		for(int i = 0; i < this.x.size(); i++){
			if(this.x.get(i) == x)
				if(this.y.get(i) == y)
					if(this.z.get(i) == z){
						indices.add(i);
						return;
					}
		}
		indices.add(this.x.size());
		this.x.add(x);
		this.y.add(y);
		this.z.add(z);
	}
	
	
//	public static void main(String[] args){
//		Vector3f point = new Vector3f(1, 1, 1).normalize();
//		System.out.println(point);
//		point.set(1, -1, -1).normalize();
//		System.out.println(point);
//		point.set(-1, 1, -1).normalize();
//		System.out.println(point);
//		point.set(-1, -1, 1).normalize();
//		System.out.println(point);
//	}
	
	public void updateVAO(RawModel raw){
		float[] positions = new float[x.size()*3];
		for(int i = 0; i < x.size(); i++){
			positions[i*3] = x.get(i);
			positions[i*3+1] = y.get(i);
			positions[i*3+2] = z.get(i);
		}
//		System.out.println(ArrayListF.arrayToString(positions));
		int[] inds = indices.capToArray();
		Loader.updateVAO(raw, positions, inds);
	}

	public String vertexCount() {
		return "vertices: " + x.size() + " indices: " + indices.size();
	}
	
	@Override
	public String toString(){
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < x.size(); i++){
			b.append(" x ");
			b.append(x.get(i));
			b.append(" y ");
			b.append(y.get(i));
			b.append(" z ");
			b.append(z.get(i));
		}
		b.append(indices);
		return b.toString();
	}
	
	
}
