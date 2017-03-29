package utilities;

public class DistanceMetric {
	public static double calculate(double one, double two) {
		// euclidean
		return Math.pow(one - two, 2);
	}
}
