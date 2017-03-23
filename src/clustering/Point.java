package clustering;

import java.util.ArrayList;
import java.util.List;

import toolkit.Matrix;
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

	public double calculateDistance(Point that, Matrix features) {
		if (that.dimensions.size() != dimensions.size()) {
			System.err.println("Why are the dimensions lengths different?");
		}
		double result = 0;
		for (int i = 0; i < dimensions.size(); i++) {
			double thisDimension = getDimension(i);
			double thatDimension = that.getDimension(i);
			// TODO ask here..
			double answer = Double.MIN_VALUE;
			if (thisDimension == Double.MAX_VALUE || thatDimension == Double.MAX_VALUE) {
				// unknown
				answer = 1;
			} else if (features.m_enum_to_str.get(i).size() == 0) {
				// real / continuous
				answer = Math.pow(thisDimension - thatDimension, 2);

			} else {
				// nominal / categorical
				if (thisDimension == thatDimension) {
					answer = 0;
				} else {
					answer = 1;
				}
			}
			if (answer == Double.MIN_VALUE) {
				System.err.println("something is going wrong while calculating distances");
			}
			result += answer;
		}

		return Math.sqrt(result);
	}

}
