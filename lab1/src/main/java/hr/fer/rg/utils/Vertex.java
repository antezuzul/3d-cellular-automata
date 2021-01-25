package hr.fer.rg.utils;

import static java.lang.Math.pow;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Vertex {
	public double x, y, z;
	public double angle;
	Vector3D normal;
	int counter;

	public Vertex(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.normal = new Vector3D(0, 0, 0);
	}

	public Vertex(double[] vertex) {
		this(vertex[0], vertex[1], vertex[2]);
	}

	@Override
	public String toString() {
		return String.format("[%f, %f, %f] a: %f", x, y, z, angle);
	}

	public static Vertex fromString(String s) {
		String[] values = s.split("\\s+");
		double x = Double.parseDouble(values[0]);
		double y = Double.parseDouble(values[1]);
		double z = Double.parseDouble(values[2]);
		return new Vertex(x, y, z);
	}

	public Vector3D getNormal() {
		if (counter == 0) {
			return normal;
		}
		Vector3D normalTmp = normal.scalarMultiply(1.0 / counter);
		if (normalTmp.getNorm() == 0) {
			return normalTmp;
		}
		return normalTmp.normalize();
	}

	public void updateNorm(Vector3D v) {
		normal = normal.add(v);
		counter++;
	}

	public double[] intensity(Vertex eye, Vertex light, double[] Ia, double[] Ii, double[] ka, double[] kd, double ks,
			int n) {
		double[] intensity = { Ia[0] * ka[0], Ia[1] * ka[1], Ia[2] * ka[2] };
		Vector3D L = new Vector3D(light.y - x, light.y - y, light.z - z).normalize();
		Vector3D V = new Vector3D(eye.x - x, eye.y - y, eye.z - z).normalize();
		Vector3D normal = this.getNormal();

		double dotProductLN = L.dotProduct(normal);
		Vector3D R = normal.scalarMultiply(2 * dotProductLN * L.getNorm()).subtract(L).normalize();

		dotProductLN = dotProductLN > 0 ? dotProductLN : 0;
		double dotProductRV = R.dotProduct(V);
		dotProductRV = dotProductRV > 0 ? dotProductRV : 0;

		intensity[0] += Ii[0] * (kd[0] * dotProductLN + ks * pow(dotProductRV, n));
		intensity[1] += Ii[1] * (kd[1] * dotProductLN + ks * pow(dotProductRV, n));
		intensity[2] += Ii[2] * (kd[2] * dotProductLN + ks * pow(dotProductRV, n));
		return intensity;
	}


	public Vector3D toVector3D() {
		return new Vector3D(x, y, z);
	}
	
	public static Vertex fromVector3D(Vector3D vector, double angle) {
		Vertex vertex = new Vertex(vector.toArray());
		vertex.angle = angle;
		return vertex;
	}
	
	public double[] toArray() {
		return new double[] {x, y, z};
	}
}
