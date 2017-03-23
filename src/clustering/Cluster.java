package clustering;

import java.util.ArrayList;
import java.util.List;

import utilities.Utilities;

public class Cluster {
	private Point centroid;

	@Override
	public String toString() {
		return "Cluster [centroid=" + centroid + ", instances=" + instances + ", distances=" + distances + "]";
	}

	private List<Point> instances;
	private List<Double> distances;

	public List<Double> getDistances() {
		return distances;
	}

	public void setDistances(List<Double> distances) {
		this.distances = distances;
	}

	public Point getCentroid() {
		if (centroid == null) {
			if (instances == null) {
				System.err.println("instances has not been initialized yet!");
			}
			centroid = new Point();
			double[] vals = new double[instances.size()];

			for (int i = 0; i < instances.size(); i++) {
				Point dimension = instances.get(i);
				vals[i] += dimension.getDimension(i);
			}
			for (int i = 0; i < vals.length; i++) {
				vals[i] /= vals.length;
				centroid.addDimension(vals[i]);
			}
		}
		return centroid;
	}

	public Cluster(Point centroid, List<Point> points) {
		super();
		this.centroid = centroid;
		this.instances = points;
	}

	public Cluster() {
	}

	public Cluster(double[] row) {
		centroid = new Point(Utilities.arrayToList(row));
	}

	public List<Point> getDimensions() {
		return instances;
	}

	public Point getDimension(int x) {
		if (instances == null || x > instances.size()) {
			System.err.println("Points is not initialized or this index is about of bounds");
			return null;
		}
		return instances.get(x);
	}

	public void addInstance(Point instance) {
		if (instances == null) {
			instances = new ArrayList<>();
		}
		this.instances.add(instance);
	}
}
