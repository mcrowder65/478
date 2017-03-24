package clustering;

import java.util.ArrayList;
import java.util.List;

import toolkit.Matrix;
import utilities.Utilities;

public class Cluster {
	private Point centroid;

	private List<Point> instances;
	private List<Double> distances;

	@Override
	public String toString() {
		return "Cluster [centroid=" + centroid + ", instances=" + instances + ", distances=" + distances + "]";
	}

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

	public double getSSE(Matrix features) {
		double sse = 0;
		for (int i = 0; i < instances.size(); i++) {
			sse += Math.pow(instances.get(i).calculateDistance(centroid, features), 2);
		}
		return sse;
	}

	public void prepareForNextIteration() {
		if (centroid == null) {
			System.err.println("why is your centroid null while preparing for the next iteration?");
		}
		for (int i = 0; i < centroid.getDimensions().size(); i++) {
			double roundedDimension = Utilities.round(centroid.getDimension(i), 1000);
			centroid.setDimension(i, roundedDimension);
		}
		this.instances = null;
		this.distances = null;

	}

	public void calculateNewCentroid(Matrix features) {
		double[] arr = new double[features.cols()];
		if (instances.get(0).getDimensions().size() != arr.length) {
			System.err.println("dimensions size and features cols should be equal");
		}
		for (int i = 0; i < features.cols(); i++) {
			if (features.m_enum_to_str.get(i).size() == 0) {
				// real/continuous
				int ignoreLength = 0;
				for (int x = 0; x < instances.size(); x++) {
					double num = instances.get(x).getDimension(i);

					// System.out.println(num);
					if (num == Double.MAX_VALUE) {
						num = 0;
						++ignoreLength;
					}
					arr[i] += num;
				}
				arr[i] /= (instances.size() - ignoreLength);
				arr[i] = Utilities.round(arr[i], 1000);
			} else {
				// nominal/categorical
				int[] occurences = new int[features.m_enum_to_str.get(i).size()];
				for (int x = 0; x < instances.size(); x++) {
					double num = instances.get(x).getDimension(i);

					if (num != Double.MAX_VALUE) {
						++occurences[(int) num];
					}

				}
				int most = Integer.MIN_VALUE;
				int desiredIndex = -1;
				// TODO you're supposed to pick the first one if there is a
				// tie....... this may cause issues
				for (int x = 0; x < occurences.length; x++) {
					if (occurences[x] > most) {
						most = occurences[x];
						desiredIndex = x;
					}
				}
				if (most == Integer.MIN_VALUE || desiredIndex == -1) {
					System.err.println("something went wrong while looking through occurences");
				}
				arr[i] = desiredIndex;
			}

		}
		this.centroid = new Point(arr);
		this.outputCentroid(features);
	}

	public void outputCentroid(Matrix features) {
		List<Double> dimensions = centroid.getDimensions();
		for (int i = 0; i < dimensions.size(); i++) {
			String prepend = i != 0 ? ", " : "";
			System.out.print(prepend);
			if (features.m_enum_to_str.get(i).size() > 0) {
				int index = (int) dimensions.get(i).doubleValue();
				System.out.print(features.m_enum_to_str.get(i).get(index));

			} else {
				if (Double.isNaN(dimensions.get(i))) {
					System.out.print("?");
				} else {
					System.out.print(Utilities.round(dimensions.get(i), 1000));
				}

			}
		}
		System.out.println();
	}
}
