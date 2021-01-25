package hr.fer.rg.draw;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import hr.fer.rg.utils.Vertex;

public class Engine extends KeyAdapter implements Runnable, GLEventListener, ActionListener {

	private final JFrame jframe = new JFrame("Crtanje objekata");
	private ParticleSystem particleSystem;
	private String textureFileName;
	private GLCanvas glcanvas;
	Vertex eye, lookAt, viewUp;
	boolean rotation = true;
	Timer timer;
	
	
	private static final int delay = 10;

	public Engine(String eye, String lookAt, String viewUp, String textureFileName) {
		this.eye = Vertex.fromString(eye);
		this.lookAt = Vertex.fromString(lookAt);
		this.viewUp = Vertex.fromString(viewUp);
		this.textureFileName = textureFileName;
	}

	@Override
	public void run() {
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		glcanvas = new GLCanvas(glcapabilities);
		
		glcanvas.addGLEventListener(this);
		glcanvas.addKeyListener(this);

		jframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		jframe.getContentPane().add(glcanvas, BorderLayout.CENTER);
		jframe.setSize(800, 600);
		jframe.setVisible(true);
		glcanvas.requestFocusInWindow();
		
		timer = new Timer(delay, this);
		jframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				timer.stop();
			}
		});
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl2 = drawable.getGL().getGL2();
		gl2.glClearColor(0.2f, 0.3f, 0.4f, 0.2f);
		gl2.glDepthFunc(GL.GL_LESS);
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl2.glEnable(GL.GL_DEPTH_TEST);
		
		GLU glu = GLU.createGLU(gl2);
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		glu.gluPerspective(90.0, 1, 0.1, 100);
		
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
		glu.gluLookAt(eye.x, eye.y, eye.z, lookAt.x, lookAt.y, lookAt.z, viewUp.x, viewUp.y, viewUp.z);
		
		particleSystem.draw(gl2, rotation);
		

	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		particleSystem = new ParticleSystem(textureFileName, eye, viewUp, delay);
		particleSystem.updateView();
		timer.start();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl2 = drawable.getGL().getGL2();
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();

		gl2.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl2.glPointSize(1.0f);
		gl2.glColor3f(0.0f, 0.0f, 0.0f);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_Q) {
			eye.x += 0.1;
		} else if (keyCode == KeyEvent.VK_A) {
			eye.x -= 0.1;
		} else if (keyCode == KeyEvent.VK_W) {
			eye.y += 0.1;
		} else if (keyCode == KeyEvent.VK_S) {
			eye.y -= 0.1;
		} else if (keyCode == KeyEvent.VK_E) {
			eye.z += 0.1;
		} else if (keyCode == KeyEvent.VK_D) {
			eye.z -= 0.1;
		} else if (keyCode == KeyEvent.VK_R) {
			rotation = !rotation;
		} else if (keyCode == KeyEvent.VK_F) {
			particleSystem.updateFrequency();
		} else if (keyCode == KeyEvent.VK_G) {
			particleSystem.updateFrequency();
		} else {
			return;
		}
		System.out.println(String.format("Očište: %s", eye));
		particleSystem.updateView();
		glcanvas.display();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (particleSystem.dead()) {
			jframe.dispose();
			timer.stop();
			return;
		}
		particleSystem.updateClock();
		glcanvas.repaint();
		
	}

}
