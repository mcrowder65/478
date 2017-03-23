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

}
