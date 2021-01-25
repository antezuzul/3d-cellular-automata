package hr.fer.rg.draw;

import static java.lang.Math.acos;
import static java.lang.Math.toDegrees;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import hr.fer.rg.utils.Vertex;

public class BSpline {

	protected List<Vertex> vertexes = new ArrayList<>();
	public double xmin, xmax, ymin, ymax, zmin, zmax;
	private double interval = 0.25;
	

	public BSpline(Path filePath) {
		populate(filePath);
	}

	private void populate(Path filePath) {
		try (BufferedReader br = Files.newBufferedReader(filePath)) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("v")) {
					Vertex newVertex = Vertex.fromString(line.substring(1).trim());
					vertexes.add(newVertex);
					checkIfMaxOrMin(newVertex);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Došlo je do pogreške...");
		}
	}

	private void checkIfMaxOrMin(Vertex v) {
		double x = v.x, y = v.y, z = v.z;
		xmin = Math.min(xmin, x);
		xmax = Math.max(xmax, x);
		ymin = Math.min(ymin, y);
		ymax = Math.max(ymax, y);
		zmin = Math.min(zmin, z);
		zmax = Math.max(zmax, z);
	}

	public void draw(GL2 gl2) {
		gl2.glBegin(GL.GL_LINE_STRIP);
		for (int index = 0; index < vertexes.size() - 3; index++) {
			for (double t = 0; t <= 1; t += this.interval) {
				Vertex translation = translate(t, index);
				System.out.println(translation + " -> " + t);
				gl2.glVertex3d(translation.x, translation.y, translation.z);
			}
		}
		gl2.glEnd();
	}

	protected Vertex translate(double t, int index) {
		double [][] time = {
				{ t * t * t, t * t, t, 1 }
		};
		RealMatrix timeMatrix = new Array2DRowRealMatrix(time);
		
		double[][] parameters = {
			{ -1,  3, -3, 1 },
			{  3, -6,  3, 0 }, 
			{ -3,  0,  3, 0 },
			{  1,  4,  1, 0 }
		};
		RealMatrix parametersMatrix = new Array2DRowRealMatrix(parameters);
		parametersMatrix = parametersMatrix.scalarMultiply(1.0 / 6.0);
		
		double[][] points = getPoints(index);
		RealMatrix pointsMatrix = new Array2DRowRealMatrix(points);
		
		RealMatrix p = timeMatrix.multiply(parametersMatrix).multiply(pointsMatrix);
		return new Vertex(p.getRow(0));
	}

	protected Vertex orient(double t, int index) {
		double [][] time = {
				{ t * t, t, 1 }
		};
		RealMatrix timeMatrix = new Array2DRowRealMatrix(time);
		double[][] parameters = {
			{ -1,  3, -3, 1 },
			{  2, -4,  2, 0 }, 
			{ -1,  0,  1, 0 },
		};
		RealMatrix parametersMatrix = new Array2DRowRealMatrix(parameters);
		parametersMatrix = parametersMatrix.scalarMultiply(1.0 / 2.0);
		
		double[][] points = getPoints(index);
		RealMatrix pointsMatrix = new Array2DRowRealMatrix(points);
		
		RealMatrix p = timeMatrix.multiply(parametersMatrix).multiply(pointsMatrix);
		return new Vertex(p.getRow(0));
	}
		
	protected static Vertex rotate(Vertex start, Vertex end) {
		Vector3D s = start.toVector3D();
		Vector3D e = end.toVector3D();
		
		Vector3D axis = s.crossProduct(e);
		double angle = s.dotProduct(e) / (s.getNorm() * e.getNorm());
		
		return Vertex.fromVector3D(axis, toDegrees(acos(angle)));
	}

	private Vertex secondDer(double t, int index) {
		double [][] time = {
				{ t, 1 }
		};
		RealMatrix timeMatrix = new Array2DRowRealMatrix(time);
		
		double[][] parameters = {
			{ -1,  3, -3, 1 },
			{  1, -2,  1, 0 } 
		};
		RealMatrix parametersMatrix = new Array2DRowRealMatrix(parameters);
		
		double[][] points = getPoints(index);
		RealMatrix pointsMatrix = new Array2DRowRealMatrix(points);
		
		RealMatrix p = timeMatrix.multiply(parametersMatrix).multiply(pointsMatrix);
		return new Vertex(p.getRow(0));
	}
	
	private double[][] getPoints(int index) {
		Vertex r1 = vertexes.get(index);
		Vertex r2 = vertexes.get(index + 1);
		Vertex r3 = vertexes.get(index + 2);
		Vertex r4 = vertexes.get(index + 3);
		
		double[][] points = {
			{ r1.x, r1.y, r1.z },
			{ r2.x, r2.y, r2.z }, 
			{ r3.x, r3.y, r3.z },
			{ r4.x, r4.y, r4.z }
		};
		return points;
	}

	
	public RealMatrix getDCM(double t, int index, Vertex orientation) {
		Vector3D w = orientation.toVector3D().normalize();
		Vector3D u = w.crossProduct(secondDer(t, index).toVector3D()).normalize();
		Vector3D v = w.crossProduct(u).normalize();
		
		double[][] dcm = {
			{ v.getX(), u.getX(), w.getX() },
			{ v.getY(), u.getY(), w.getY() },
			{ v.getZ(), u.getZ(), w.getZ() }
		};
		return new LUDecomposition(new Array2DRowRealMatrix(dcm)).getSolver().getInverse();
	}
	
}
