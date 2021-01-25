package hr.fer.rg.utils;

import java.util.Arrays;
import java.util.Iterator;

public class Polygon implements Iterable<Vertex> {

	public Vertex v1, v2, v3, v4;
	private Vertex normal;
	private double size;

	public Polygon(double size) {
		this.size = size;
	}

	public void update(Vertex eye) {
//		v1 = new Vertex(center.x + size, center.y + size, center.z + size);
//		v2 = new Vertex(center.x - size, center.y - size, center.z + size);
//		v3 = new Vertex(center.x - size, center.y - size, center.z - size);
//		v4 = new Vertex(center.x + size, center.y + size, center.z - size);
		v1 = new Vertex(size, size, size);
		v2 = new Vertex(-size, -size, size);
		v3 = new Vertex(-size, -size, -size);
		v4 = new Vertex(size, size, -size);
//		v1 = new Vertex(0, center.y + size, center.z + size);
//		v2 = new Vertex(0, center.y + size, center.z - size);
//		v3 = new Vertex(0, center.y - size, center.z - size);
//		v4 = new Vertex(0, center.y - size, center.z + size);
		normal = calcNormal(eye);
	}

	private Vertex calcNormal(Vertex eye) {
		double A = (v2.y - v1.y) * (v3.z - v1.z) - (v2.z - v1.z) * (v3.y - v1.y);
		double B = -(v2.x - v1.x) * (v3.z - v1.z) + (v2.z - v1.z) * (v3.x - v1.x);
		double C = (v2.x - v1.x) * (v3.y - v1.y) - (v2.y - v1.y) * (v3.x - v1.x);
		double D = -v1.x * A - v1.y * B - v1.z * C;
		
		Vertex normal = new Vertex(A, B, C);
		if (!isVisible(A, B, C, D, eye)) {
			normal = Vertex.fromVector3D(normal.toVector3D().scalarMultiply(-1));
		}
		return Vertex.fromVector3D(normal.toVector3D().normalize());
	}
	
	private boolean isVisible(double A, double B, double C, double D, Vertex eye) {
		return eye.x * A + eye.y * B + eye.z * C + D > 0;
	}
	
	public void setSize(double size) {
		this.size = size;
	}
	
	public Vertex getNormal() {
		return normal;
	}

	@Override
	public String toString() {
		return String.format("v1: %s\tv2: %s\tv3: %s\tv4: %s", v1, v2, v3, v4);
	}

	@Override
	public Iterator<Vertex> iterator() {
		return Arrays.asList(v1, v2, v3, v4).iterator();
	}
}
