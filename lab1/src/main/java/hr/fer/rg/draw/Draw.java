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

public class Draw extends KeyAdapter implements Runnable, GLEventListener, ActionListener {

	private Model model;
	private GLCanvas glcanvas;
	private BSpline bSpline;
	
	private static final int delay = 10;

	public Draw(Model model, BSpline bSpline) {
		this.model = model;
		this.bSpline = bSpline;
	}

	@Override
	public void run() {
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		glcanvas = new GLCanvas(glcapabilities);

		glcanvas.addGLEventListener(this);
		glcanvas.addKeyListener(this);

		final JFrame jframe = new JFrame("Crtanje objekata");
		jframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		jframe.getContentPane().add(glcanvas, BorderLayout.CENTER);
		jframe.setSize(800, 600);
		jframe.setVisible(true);
		glcanvas.requestFocusInWindow();
		
		Timer timer = new Timer(delay, this);
		timer.start();
		jframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				timer.stop();
			}
		});
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl2 = drawable.getGL().getGL2();
		gl2.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl2.glDepthFunc(GL.GL_LESS);
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glEnable(GL.GL_DEPTH_TEST);
		gl2.glLoadIdentity();

		GLU glu = new GLU();
		glu.gluLookAt(model.eye.x, model.eye.y, model.eye.z, model.lookAt.x, model.lookAt.y, model.lookAt.z,
				model.viewUp.x, model.viewUp.y, model.viewUp.z);
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		glu.gluPerspective(90.0, 1, 0.1, 100);
		
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		
		bSpline.draw(gl2);
		model.drawSpline(gl2);
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
	}

	@Override
	public void init(GLAutoDrawable arg0) {
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
			model.eye.x += 0.1;
		} else if (keyCode == KeyEvent.VK_A) {
			model.eye.x -= 0.1;
		} else if (keyCode == KeyEvent.VK_W) {
			model.eye.y += 0.1;
		} else if (keyCode == KeyEvent.VK_S) {
			model.eye.y -= 0.1;
		} else if (keyCode == KeyEvent.VK_E) {
			model.eye.z += 0.1;
		} else if (keyCode == KeyEvent.VK_D) {
			model.eye.z -= 0.1;
		} else if (keyCode == KeyEvent.VK_T) {
			model.type = 1 - model.type;
		} else if (keyCode == KeyEvent.VK_M) {
			model.dcm = !model.dcm;
			model.reset();
		} else {
			return;
		}
		System.out.println(String.format("Očište: %s", model.eye));
		glcanvas.display();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		model.update();
		glcanvas.repaint();
	}

}
