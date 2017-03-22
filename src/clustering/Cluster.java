package clustering;

import java.util.List;

public class Cluster {
	private Point centroid;
	private List<Point> instances;

	public Point getCentroid() {
		if (centroid == null) {
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

	public void setPoints(List<Point> points) {
		this.instances = points;
	}
}
