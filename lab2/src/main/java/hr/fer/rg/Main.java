package hr.fer.rg;

import java.util.Scanner;

import javax.swing.SwingUtilities;

import com.jogamp.opengl.GLProfile;

import hr.fer.rg.draw.Engine;

public class Main {

	private static final String eyeString = "60 0 0";
	private static final String lookAtString = "0 0 0";
	private static final String viewUpString = "0 1 0";
	private static final String textureString = "snowflake.png";
	
	static {
		GLProfile.initSingleton();
	}

	public static void main(String[] args) {
		try (Scanner sc = new Scanner(System.in);) {
			System.out.println(String.format("Navedite ime datotekte teksture koju želite iscrtati (%s):", textureString));
			String texture = sc.nextLine();
			
			System.out.println(String.format("Upišite koordinate očišta (%s):", eyeString));
			String eye = sc.nextLine();
			
			System.out.println(String.format("Upišite koordinate gledišta (%s):", lookAtString));
			String lookAt = sc.nextLine();
			
			System.out.println(String.format("Upišite koordinate viewUp (%s):", viewUpString));
			String viewUp = sc.nextLine();
			
			Engine engine = new Engine(
				eye.equals("") ? eyeString : eye, 
				lookAt.equals("") ? lookAtString : lookAt, 
				viewUp.equals("") ? viewUpString : viewUp,
				texture.equals("") ? textureString : texture
			);
			
			SwingUtilities.invokeLater(engine);
		}
	}
	
}