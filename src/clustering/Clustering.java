package clustering;

import java.util.ArrayList;
import java.util.List;

import toolkit.Matrix;
import utilities.Utilities;

public class Clustering {
	private final int k = 5;

	public void clusterTrain(Matrix features) {
		Matrix originalFeatures = new Matrix(features, 0, 0, features.rows(), features.cols());
		int firstAttrIndex = 0;

		// don't ever include the id!
		if (features.m_attr_name.get(0).equals("'id'")) {
			firstAttrIndex = 1;
			System.out.println("****************************************");
			System.out.println("SKIPPING FIRST COLUMN BECAUSE OF ID!!!!!");
			System.out.println("****************************************");
		}

		List<Cluster> clusters = new ArrayList<>();
		boolean imAwesome = true;
		double previousSSE = Double.MAX_VALUE;
		int iterations = 1;
		while (imAwesome) {
			if (!originalFeatures.equals(features)) {
				System.err.println("features changed....");
				return;
			}
			System.out.println("***************");
			System.out.println("Iteration " + iterations);
			System.out.println("***************");
			for (int clusterIterator = 0; clusterIterator < k; clusterIterator++) {
				double[] rowArr = features.row(clusterIterator);
				Cluster cluster = clusters.size() != k ? new Cluster(rowArr) : clusters.get(clusterIterator);

				if (clusters.size() == k) {

					cluster.prepareForNextIteration();
				}
				List<Double> distances = new ArrayList<>();
				for (int row = 0; row < features.rows(); row++) {
					double[] feature = features.row(row);

					double distance = 0;
					for (int dimensionIndex = firstAttrIndex; dimensionIndex < feature.length; dimensionIndex++) {
						double val = feature[dimensionIndex];

						double centroidVal = cluster.getCentroid().getDimension(dimensionIndex);
						double answer = Double.MIN_VALUE;
						if (Double.isNaN(val) || val == Double.MAX_VALUE || Double.isNaN(centroidVal)
								|| centroidVal == Double.MAX_VALUE) {
							// unknown
							answer = 1;
						} else if (features.m_enum_to_str.get(dimensionIndex).size() == 0) {
							// real / continuous
							answer = Math.pow(val - centroidVal, 2);

						} else {
							// nominal / categorical
							if (val == centroidVal) {
								answer = 0;
							} else {
								answer = 1;
							}
						}
						if (answer == Double.MIN_VALUE) {
							System.err.println("something is going wrong with calculating distances");
							return;
						}
						distance += answer;

					}
					double num = Utilities.round(Math.sqrt(distance), 1000);
					distances.add(num);
				}
				cluster.setDistances(distances);
				if (clusters.size() != k) {
					clusters.add(cluster);
				}

			}

			for (int x = 0; x < features.rows(); x++) {
				Cluster clust = null;
				int clusterIndex = -1;
				double min = Double.MAX_VALUE;
				for (int c = 0; c < clusters.size(); c++) {
					Cluster cluster = clusters.get(c);

					List<Double> distances = cluster.getDistances();
					// go vertically not horizontally
					if (distances.get(x) < min) {
						min = distances.get(x);
						clust = cluster;
						clusterIndex = c;
					}

				}
				// System.out.println(x + "=" + clusterIndex);

				clust.addInstance(new Point(features.row(x)));
			}
			double totalSSE = 0;
			System.out.println("# of clusters: " + clusters.size());
			for (int i = 0; i < clusters.size(); i++) {
				totalSSE += Utilities.round(clusters.get(i).getSSE(features), 1000);
				clusters.get(i).outputCentroid(features);
			}
			if (previousSSE == totalSSE) {
				imAwesome = false;
			}
			previousSSE = totalSSE;
			System.out.println("Total SSE: " + Utilities.round(totalSSE, 1000));

			for (int i = 0; i < clusters.size(); i++) {
				clusters.get(i).calculateNewCentroid(features);
			}
			iterations++;
		}
	}

}
