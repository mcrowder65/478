package clustering;

import java.util.ArrayList;
import java.util.List;

public class Point {
	private List<Double> dimensions;

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
