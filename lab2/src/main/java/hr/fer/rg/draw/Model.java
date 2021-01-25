package hr.fer.rg.draw;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import hr.fer.rg.utils.Polygon;
import hr.fer.rg.utils.Vertex;

public class Model {

	private List<Vertex> vertexes = new ArrayList<>();
	private Set<Polygon> polygons = new HashSet<>();
	private Set<Polygon> polygonsToDraw;
	public double xmin, xmax, ymin, ymax, zmin, zmax;
	Vertex eye, lookAt, viewUp;
	int type = 1;

	private static Vertex light = new Vertex(-3, -3, 2);
	private static double[] Ii = { 0.4, 0.4, 0.5 };
	private static double[] Ia = { 0.3, 0.3, 0.6 };
	private static double[] ka = { 0.6, 0.6, 0.6 };
	private static double[] kd = { 0.5, 0.5, 0.5 };
	static double ks = 0.5;
	private static int n = 30;

	private BSpline bSpline;
	boolean dcm = true;
	private Vertex currentOrientation = new Vertex(0, 0, 1);
	private double interval = 0.01;
	private double t = 0;
	private int index = 0;

	private double centerX, centerY, centerZ;

	public Model(Path filePath, String eye, String lookAt, String viewUp, BSpline bSpline) {
		xmax = ymax = zmax = Double.MIN_VALUE;
		xmin = ymin = zmin = Double.MAX_VALUE;
		this.eye = Vertex.fromString(eye);
		this.lookAt = Vertex.fromString(lookAt);
		this.viewUp = Vertex.fromString(viewUp);
		populate(filePath);
		this.polygonsToDraw = this.polygons;
		this.bSpline = bSpline;
	}

	private void populate(Path filePath) {

		try (BufferedReader br = Files.newBufferedReader(filePath)) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("v")) {
					Vertex newVertex = Vertex.fromString(line.substring(1).trim());
					vertexes.add(newVertex);
					checkIfMaxOrMin(newVertex);
				} else if (line.startsWith("f")) {
//					polygons.add(Polygon.fromString(line));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Došlo je do pogreške...");
		}

		this.centerX = (xmax + xmin) / 2;
		this.centerY = (ymax + ymin) / 2;
		this.centerZ = (zmax + zmin) / 2;

		double M = Math.max(xmax - xmin, ymax - ymin);
		M = Math.max(M, zmax - zmin);

		for (Vertex vertex : vertexes) {
			vertex.x = 2 / M * (vertex.x - centerX);
			vertex.y = 2 / M * (vertex.y - centerY);
			vertex.z = 2 / M * (vertex.z - centerZ);
		}
		calculatePolygons();
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

	private void calculatePolygons() {
//		for (Polygon polygon : polygons) {
//			polygon.calculateCoef(vertexes);
//		}
	}

	public void draw(GL2 gl2) {
		for (Polygon polygon : polygonsToDraw) {
//			if (!polygon.isVisible(eye)) {
//				continue;
//			}
			if (type == 0) {
				triangles(polygon, gl2);
			} else {
				gouraud(polygon, gl2);
			}
		}
	}

	private void gouraud(Polygon polygon, GL2 gl2) {
		gl2.glBegin(GL.GL_TRIANGLES);
		for (Vertex vertex : polygon) {
			double[] intensity = vertex.intensity(eye, light, Ia, Ii, ka, kd, ks, n);
//			double[] intensity = { 0.27, 0.5, 0.71 };
			gl2.glColor3d(intensity[0], intensity[1], intensity[2]);
			gl2.glVertex3d(vertex.x, vertex.y, vertex.z);
		}
		gl2.glEnd();
	}

	private void triangles(Polygon polygon, GL2 gl2) {
		gl2.glBegin(GL.GL_LINE_LOOP);
		for (Vertex vertex : polygon) {
			gl2.glVertex3d(vertex.x, vertex.y, vertex.z);
		}
		gl2.glEnd();
	}

	public void drawSpline(GL2 gl2) {
		Vertex translation = bSpline.translate(t, index);
	
		gl2.glPushMatrix();
		gl2.glTranslated(translation.x, translation.y, translation.z);
		gl2.glScaled(5, 5, 5);
		
		Vertex orientation = followEye(translation);
		
		Vertex rotation = BSpline.rotate(currentOrientation, orientation);
		gl2.glRotated(rotation.angle, rotation.x, rotation.y, rotation.z);
		polygonsToDraw = polygons;

		this.draw(gl2);
		gl2.glPopMatrix();
	}

	private Vertex followEye(Vertex translation) {
		return Vertex.fromVector3D(eye.toVector3D().subtract(translation.toVector3D()), 0);
	}

	public void reset() {
		t = 0.0;
		index = 0;
	}
	
	public void update() {
		if (t + interval > 1.0) {
			t = 0.0;
			index++;
		} else {
			t += interval;
		}
		if (index >= bSpline.vertexes.size() - 3) {
			index = 0;
			t = 0.0;
		}
	}

}
