package utilities;

public class DistanceMetric {
	public static double calculate(double one, double two) {
		// euclidean
		return euclideanDistance(one, two);
	}

	/**
	 * This does not do the sqrt!!!!!!
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public static double euclideanDistance(double one, double two) {
		return Math.pow(one - two, 2);
	}
}
