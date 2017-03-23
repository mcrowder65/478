package clustering;

import java.util.ArrayList;
import java.util.List;

import utilities.Utilities;

public class Point {
	@Override
	public String toString() {
		return "Point [dimensions=" + dimensions + "]";
	}

	private List<Double> dimensions;

	public Point(double[] arr) {
		this.dimensions = Utilities.arrayToList(arr);
	}

	public Point(List<Double> list) {
		this.dimensions = list;
	}

	public Point() {
	}

	public List<Double> getDimensions() {
		if (dimensions == null) {
			dimensions = new ArrayList<>();
		}
		return dimensions;
	}

	public double getDimension(int i) {
		return getDimensions().get(i);
	}

	public void addDimension(double d) {
		getDimensions().add(d);
	}

	public double calculateDistance(Point two) {
		if (two.dimensions.size() != dimensions.size()) {
			System.err.println("Why are the dimensions lengths different?");
		}
		double result = 0;
		for (int i = 0; i < dimensions.size(); i++) {
			double thisDimension = getDimension(i) == Double.MAX_VALUE ? 1 : getDimension(i);
			double thatDimension = two.getDimension(i) == Double.MAX_VALUE ? 1 : two.getDimension(i);
			// TODO ask here..
			result += Math.pow(thisDimension - thatDimension, 2);
		}

		return Math.sqrt(result);
	}

}
