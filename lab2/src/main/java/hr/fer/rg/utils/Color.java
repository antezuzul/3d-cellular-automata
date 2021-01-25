package hr.fer.rg.utils;

public class Color {
	double r, g, b;
	double alpha;
	double currentAlpha;
	
	public Color(double r, double g, double b, double alpha) {
		super();
		this.r = r;
		this.g = g;
		this.b = b;
		this.alpha = alpha;
		this.currentAlpha = alpha;
	}
	
	public Color copy() {
		return new Color(r, g, b, alpha);
	}

	public double getR() {
		return r;
	}

	public double getG() {
		return g;
	}

	public double getB() {
		return b;
	}

	public double getAlpha() {
		return currentAlpha;
	}
	
	public void scaleAlpha(double s) {
		currentAlpha = alpha * s;
	}
	
}
