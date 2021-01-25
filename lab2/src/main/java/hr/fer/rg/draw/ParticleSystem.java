package hr.fer.rg.draw;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

import hr.fer.rg.utils.Color;
import hr.fer.rg.utils.Polygon;
import hr.fer.rg.utils.Vertex;

public class ParticleSystem {

	private List<Particle> particles;
	private Texture texture;
	private Vertex eye, viewUp;
	
	private double min_size = 0.5, max_size = 0.9;
	private int nParticles = 2000;
	private double min_speed = 0.3, max_speed = 0.6;
	private int min_ttl = 600, max_ttl = 750;
	private int min_x = -40, max_x = 40, min_y = 80, max_y = 100, min_z = -70, max_z = 70;
	private double frequency = 1, min_frequency = 0, max_frequency = 2, threshold = 0.01;
	private boolean incFreq = true;
	private double dt = 0.5;

	public ParticleSystem(String textureFileName, Vertex eye, Vertex viewUp, int delay) {
		particles = new ArrayList<>();
		URL textureURL = getClass().getClassLoader().getResource("textures/" + textureFileName);
		try {
			texture = TextureIO.newTexture(textureURL, false, null);
		} catch (GLException | IOException e) {
			e.printStackTrace();
		}
		this.eye = eye;
		this.viewUp = viewUp;
//		initDemo();
		init();
	}

	public void initDemo() {
		Color c = new Color(1, 1, 1, 0.5);
		double size = 0.1;
		eye.x = 1; eye.y = 0; eye.z = 0;
		Vertex v = new Vertex(0, 0, 0); Vertex o = new Vertex(0, 0, 0);
		int lifetime = 100000;
		particles.add(new Particle(new Vertex(0, -0.2, 0.3), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, -0.2, -0.3), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0, 0.5), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0, -0.5), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0, 0.2), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0, -0.2), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0.2, 0.1), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0.2, -0.1), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0.2, 0.4), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0.2, -0.4), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0.4, 0.1), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0.4, -0.1), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0.4, 0.3), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0.4, -0.3), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0.4, 0.5), o, v, lifetime, c, size));
		particles.add(new Particle(new Vertex(0, 0.4, -0.5), o, v, lifetime, c, size));
	}
	
	private void init() {
		for (int i = 0; i < nParticles; i++) {
			particles.add(createParticle());
		}
	}
	
	private Particle createParticle() {
		Random r = new Random(); 
		return new Particle(
			new Vertex(randomDoubleInRange(min_x, max_x, r), randomDoubleInRange(min_y, max_y, r), randomDoubleInRange(min_z, max_z, r)), 
			new Vertex(1, 1, 1), 
			Vertex.fromVector3D(viewUp.toVector3D().add(new Vector3D(0, 0 , -1)).scalarMultiply(randomDoubleInRange(min_speed, max_speed, r))), 
			randomIntInRange(min_ttl, max_ttl, r),
			new Color(1, 1, 1, 1), 
			randomDoubleInRange(min_size, max_size, r)
		);
	}
	
	private double randomDoubleInRange(double l, double h, Random r) {
		return r.nextDouble() * (h - l) + l;
	}
	
	private int randomIntInRange(int l, int h, Random r) {
		return r.nextInt() % (h - l + 1) + l;
	}

	public void updateView() {
		particles.forEach(p -> p.update(eye));
	}
	
	public void updateClock() {
		particles.forEach(p -> p.clock(dt, eye));
	}

	public void draw(GL2 gl2, boolean rotation) {
		gl2.glEnable(GL.GL_BLEND);
		gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl2.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		
		texture.enable(gl2);
		texture.bind(gl2);
		
		List<Particle> newParticles = new ArrayList<>();
		
		for (Particle particle : particles) {
			if (!particle.alive()) continue;
			newParticles.add(particle);
			
			gl2.glPushMatrix();
			
			Vertex p = particle.getPosition();
			gl2.glTranslated(p.x, p.y, p.z);
			Vertex r = particle.getRotation();
			if (rotation) gl2.glRotated(r.angle, r.x, r.y, r.z);
			
			TextureCoords texcoords = texture.getImageTexCoords();
			Polygon polygon = particle.getPolygon();
			
			Color c = particle.getRgb();
			gl2.glColor4d(c.getR(), c.getG(), c.getB(), c.getAlpha());
			
			gl2.glBegin(GL2.GL_QUADS);
			gl2.glVertex3d(polygon.v1.x, polygon.v1.y, polygon.v1.z);
			gl2.glTexCoord2f(texcoords.right(), texcoords.top());
			gl2.glVertex3d(polygon.v2.x, polygon.v2.y, polygon.v2.z);
			gl2.glTexCoord2f(texcoords.left(), texcoords.top());
			gl2.glVertex3d(polygon.v3.x, polygon.v3.y, polygon.v3.z);
			gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
			gl2.glVertex3d(polygon.v4.x, polygon.v4.y, polygon.v4.z);
			gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
			gl2.glEnd();
			
			gl2.glPopMatrix();
		}

		updateParticles(newParticles);
		
		texture.disable(gl2);
		gl2.glDisable(GL.GL_BLEND);
	}

	private void updateParticles(List<Particle> newParticles) {
		int diff = particles.size() - newParticles.size();
		if (diff != 0) updateFrequency();
		int numberOfNewParticles = (int) (diff * frequency);
		for (int i = 0; i < numberOfNewParticles; i++) {
			newParticles.add(createParticle());
		}
		particles = newParticles;
//		System.out.println("Broj čestica u sustavu: " + particles.size());
	}

	public boolean dead() {
		return particles.size() == 0;
	}
	
	public void test(double x, double y, double z) {
		for (Particle particle : particles) {
			particle.position.x += x;
			particle.position.y += y;
			particle.position.z += z;
			particle.update(eye);
		}
	}
	
	public void increaseFrequency() {
		frequency = Math.min(max_frequency, frequency + threshold);
//		System.out.println("Frekvencija " + frequency);
	}
	
	public void decreaseFrequency() {
		frequency = Math.max(min_frequency, frequency - threshold);
//		System.out.println("Frekvencija " + frequency);
	}
	
	public void updateFrequency() {
		if (incFreq) {
			increaseFrequency();
			if (frequency >= max_frequency) {
				incFreq = !incFreq;
			}
		} else if (!incFreq) {
			decreaseFrequency();
			if (frequency <= min_frequency) {
				incFreq = !incFreq;
			}
		}
	}
	
}
