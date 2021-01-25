package hr.fer.rg.draw;

import static java.lang.Math.acos;
import static java.lang.Math.toDegrees;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import hr.fer.rg.utils.Color;
import hr.fer.rg.utils.Polygon;
import hr.fer.rg.utils.Vertex;

public class Particle {

	public Vertex position;
	private Vertex orientation;
	private Vertex rotation;
	private Vertex velocity;
	private int lifeTime;
	private int life;
	private Color rgb;
	private double size;
	private Polygon polygon;

	public Particle(Vertex position, Vertex orientation, Vertex velocity, int lifeTime, Color rgb, double size) {
		this.position = position;
		this.orientation = orientation;
		this.velocity = velocity;
		this.lifeTime = lifeTime;
		this.life = lifeTime;
		this.rgb = rgb;
		this.size = size;
		this.polygon = new Polygon(size);
	}

	public Particle copy() {
		return new Particle(position.copy(), orientation.copy(), velocity.copy(), lifeTime, rgb.copy(), size);
	}
	
	public void update(Vertex eye) {
		polygon.update(eye);
		rotation = rotate(polygon.getNormal(), followEye(eye));
	}
	
	public Vertex followEye(Vertex eye) {
		return Vertex.fromVector3D(eye.toVector3D().subtract(position.toVector3D()).normalize());
	}
	
	private Vertex rotate(Vertex start, Vertex end) {
		Vector3D s = start.toVector3D();
		Vector3D e = end.toVector3D();
		Vector3D axis = s.crossProduct(e);
		double angle = s.dotProduct(e) / (s.getNorm() * e.getNorm());
		return Vertex.fromVector3D(axis, toDegrees(acos(angle)));
	}

	public Vertex getPosition() {
		return position;
	}
	
	public Vertex getRotation() {
		return rotation;
	}

	public Color getRgb() {
		return rgb;
	}
	
	public Polygon getPolygon() {
		return polygon;
	}
	
	public boolean alive() {
		return life > 0;
	}

	public void clock(double dt, Vertex eye) {
		--life;
		rgb.scaleAlpha(1.0 * life / lifeTime);
		position = Vertex.fromVector3D(position.toVector3D().subtract(dt, velocity.toVector3D()));
		update(eye);
	}
}
