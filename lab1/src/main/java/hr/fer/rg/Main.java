package hr.fer.rg;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import com.jogamp.opengl.GLProfile;

import hr.fer.rg.draw.BSpline;
import hr.fer.rg.draw.Draw;
import hr.fer.rg.draw.Model;

public class Main {

	private static final String eyeString = "0 -1 -3";
	private static final String lookAtString = "0 0 0";
	private static final String viewUpString = "0 1 0";
	private static final String objectString = "plane";
	private static final String bSplineString = "spiral";
	
	static {
		GLProfile.initSingleton();
	}

	public static void main(String[] args) {
		Model model;
		BSpline bspline;

		try (Scanner sc = new Scanner(System.in);) {
			System.out.println(String.format("Upišite koordinate očišta (%s):", eyeString));
			String eye = sc.nextLine();
			
			System.out.println(String.format("Upišite koordinate gledišta (%s):", lookAtString));
			String lookAt = sc.nextLine();
			
			System.out.println(String.format("Upišite koordinate viewUp (%s):", viewUpString));
			String viewUp = sc.nextLine();
		
			bspline = new BSpline(getFilePath(sc, "krivulje", "bsplines", bSplineString));
			
			model = new Model(
				getFilePath(sc, "objekta", "objects", objectString), 
				eye.equals("") ? eyeString : eye, 
				lookAt.equals("") ? lookAtString : lookAt, 
				viewUp.equals("") ? viewUpString : viewUp,
				bspline
			);
		
		}

		SwingUtilities.invokeLater(new Draw(model, bspline));
	}
	
	private static Path getFilePath (Scanner sc, String fileType, String dirName, String defaultName) {
		Path filePath;
		while (true) {
			System.out.println(String.format("Upišite naziv datoteke %s koju želite iscrtati (%s):", fileType, defaultName));
			String input = sc.nextLine();
			filePath = Paths.get(String.format("./src/main/resources/%s/%s.obj", dirName, input.equals("") ? defaultName : input));
			if (!Files.exists(filePath)) {
				System.out.println("Datoteka s danim nazivom ne postoji!");
				continue;
			}
			break;
		}
		return filePath;
	}
}