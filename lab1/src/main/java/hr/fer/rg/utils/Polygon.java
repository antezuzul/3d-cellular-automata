package hr.fer.rg.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Polygon implements Iterable<Vertex> {
	int a, b, c;
	double A, B, C, D;
	Vertex v1, v2, v3;
	Vertex center;
	Vector3D normal;

	public Polygon(int a, int b, int c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public void calculateCoef(List<Vertex> vertexes) {
		this.v1 = vertexes.get(a);
		this.v2 = vertexes.get(b);
		this.v3 = vertexes.get(c);

		this.A = (v2.y - v1.y) * (v3.z - v1.z) - (v2.z - v1.z) * (v3.y - v1.y);
		this.B = -(v2.x - v1.x) * (v3.z - v1.z) + (v2.z - v1.z) * (v3.x - v1.x);
		this.C = (v2.x - v1.x) * (v3.y - v1.y) - (v2.y - v1.y) * (v3.x - v1.x);
		this.D = -v1.x * A - v1.y * B - v1.z * C;

		this.center = new Vertex((v1.x + v2.x + v3.x) / 3, (v1.y + v2.y + v3.y) / 3, (v1.z + v2.z + v3.z) / 3);
		this.normal = new Vector3D(A, B, C);
		this.v1.updateNorm(this.normal);
		this.v2.updateNorm(this.normal);
		this.v3.updateNorm(this.normal);
	}

	@Override
	public String toString() {
		return String.format("coef: [%f, %f, %f, %f]\nv1: %s\nv2: %s\nv3: %s", A, B, C, D, v1, v2, v3);
	}

	public static Polygon fromString(String s) {
		String[] values = s.split("\\s+");
		return new Polygon(Integer.parseInt(values[1]) - 1, Integer.parseInt(values[2]) - 1,
				Integer.parseInt(values[3]) - 1);
	}

	public boolean isVisible(Vertex eye) {
		return eye.x * A + eye.y * B + eye.z * C + D > 0;
	}

	@Override
	public Iterator<Vertex> iterator() {
		return Arrays.asList(v1, v2, v3).iterator();
	}

	public Polygon transform(List<Vertex> vertexes) {
		Polygon newPolygon = new Polygon(a, b, c);
		newPolygon.calculateCoef(vertexes);
		return newPolygon;
	}
}
