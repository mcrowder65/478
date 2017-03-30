package clustering;

import java.util.ArrayList;
import java.util.List;

import toolkit.Matrix;
import utilities.Utilities;

public class Cluster {
	private Point centroid;

	private List<Point> instances;
	private List<Double> distances;
	private List<Double> silhouettes;

	public List<Double> getSilhouettes(List<Cluster> clusters, Matrix features) {
		if (instances == null) {
			System.err.println("You're trying to find the silhouettes with null instances?");
			return null;
		}

		silhouettes = new ArrayList<>();
		List<Double> a = new ArrayList<>();
		for (int i = 0; i < instances.size(); i++) {
			double temp = 0;
			for (int x = 0; x < instances.size(); x++) {
				if (i != x) {
					temp += instances.get(i).calculateDistance(instances.get(x), features);
				}
			}
			// - 1 because we don't want to include itself in the average
			temp /= (instances.size() - 1);
			a.add(temp);
		}
		List<Double> b = new ArrayList<>();
		// b(i)
		for (int i = 0; i < instances.size(); i++) {
			List<Double> arr = new ArrayList<>();
			for (int x = 0; x < clusters.size(); x++) {

				if (!this.equals(clusters.get(x))) {
					arr.add(instances.get(i).calculateDissimilarity(clusters.get(x), features));
				}

			}
			double smallest = Double.MAX_VALUE;
			for (int x = 0; x < arr.size(); x++) {
				if (arr.get(x) < smallest) {
					smallest = arr.get(x);
				}
			}
			b.add(smallest);

		}
		for (int i = 0; i < instances.size(); i++) {
			if (a.get(i) < b.get(i)) {
				silhouettes.add((double) 1 - a.get(i) / b.get(i));
			} else if (a.get(i) == b.get(i)) {
				silhouettes.add((double) 0);
			} else if (a.get(i) > b.get(i)) {
				silhouettes.add(b.get(i) / a.get(i) - (double) 1);
			}
		}
		return silhouettes;
	}

	public void setSilhouettes(List<Double> silhouettes) {
		this.silhouettes = silhouettes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((centroid == null) ? 0 : centroid.hashCode());
		result = prime * result + ((instances == null) ? 0 : instances.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Cluster [centroid=" + centroid + ", instances=" + instances + ", distances=" + distances + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cluster other = (Cluster) obj;
		if (centroid == null) {
			if (other.centroid != null)
				return false;
		} else if (!centroid.equals(other.centroid))
			return false;
		if (instances == null) {
			if (other.instances != null)
				return false;
		} else if (!instances.equals(other.instances))
			return false;
		if (instances.size() != other.instances.size()) {
			return false;
		}
		for (int i = 0; i < instances.size(); i++) {
			if (!instances.get(i).equals(other.instances.get(i))) {
				return false;
			}
		}
		return true;
	}

	public List<Double> getDistances() {
		return distances;
	}

	public void setDistances(List<Double> temp) {
		if (temp != null) {
			this.distances = new ArrayList<>();
			for (int i = 0; i < temp.size(); i++) {
				this.distances.add(temp.get(i));
			}
		}

	}

	public Point getCentroid() {

		return centroid;
	}

	public Cluster(Point centroid, List<Point> points) {
		super();
		this.centroid = new Point(centroid);
		instances = new ArrayList<>();
		for (int i = 0; i < points.size(); i++) {
			instances.add(new Point(points.get(i)));
		}
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
			System.err.println("Points is not initialized or this index is out of bounds");
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
	}

	public void outputCentroid(Matrix features) {
		List<Double> dimensions = centroid.getDimensions();
		double sse = Utilities.round(getSSE(features), 1000);
		String strSSE = String.valueOf(sse);
		System.out.print("SSE: " + strSSE + " ");
		System.out.print("# of instances: " + instances.size() + " ");
		// for (int i = 0; i < dimensions.size(); i++) {
		// String prepend = i != 0 ? ", " : "";
		// System.out.print(prepend);
		// if (Double.isNaN(dimensions.get(i))) {
		// System.out.print("?");
		// } else if (features.m_enum_to_str.get(i).size() > 0) {
		// int index = (int) dimensions.get(i).doubleValue();
		// System.out.print(features.m_enum_to_str.get(i).get(index));
		//
		// } else {
		// if (Double.isNaN(dimensions.get(i))) {
		// System.out.print("?");
		// } else {
		// String valRounded = String.valueOf(Utilities.round(dimensions.get(i),
		// 1000));
		//
		// System.out.print(Utilities.round(dimensions.get(i), 1000));
		// if (valRounded.substring(valRounded.indexOf(".") + 1).length() != 3)
		// {
		// for (int z = valRounded.substring(valRounded.indexOf(".") +
		// 1).length(); z < 3; z++) {
		// System.out.print("0");
		// }
		// }
		// }
		//
		// }
		// }
		System.out.println();
	}
}
