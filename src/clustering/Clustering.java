package clustering;

import java.util.ArrayList;
import java.util.List;

import toolkit.Matrix;

public class Clustering {
	private final int k = 5;

	public void clusterTrain(Matrix features) {
		int firstAttrIndex = 0;

		// don't ever include the id!
		if (features.m_attr_name.get(0).equals("'id'")) {
			firstAttrIndex = 1;
		}

		List<Cluster> clusters = new ArrayList<>();
		boolean imAwesome = true;
		while (imAwesome) {
			for (int clusterIterator = 0; clusterIterator < k; clusterIterator++) {
				double[] rowArr = features.row(clusterIterator);
				Cluster cluster = clusterIterator <= clusters.size() ? new Cluster(rowArr)
						: clusters.get(clusterIterator);

				List<Double> distances = new ArrayList<>();
				for (int row = 0; row < features.rows(); row++) {
					double[] feature = features.row(row);

					double distance = 0;
					for (int dimensionIndex = firstAttrIndex; dimensionIndex < feature.length; dimensionIndex++) {
						double val = feature[dimensionIndex];

						double centroidVal = cluster.getCentroid().getDimension(dimensionIndex);
						double answer = Double.MIN_VALUE;
						if (val == Double.MAX_VALUE || centroidVal == Double.MAX_VALUE) {
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

						distance += answer;

					}
					distances.add(Math.sqrt(distance));
				}
				cluster.setDistances(distances);
				clusters.add(cluster);
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
				System.out.println(x + "=" + clusterIndex);
				clust.addInstance(new Point(features.row(x)));
			}
			double totalSSE = 0;
			for (int i = 0; i < clusters.size(); i++) {
				totalSSE += clusters.get(i).getSSE(features);
			}
			System.out.println("SSE: " + totalSSE);

			for (int i = 0; i < clusters.size(); i++) {
				clusters.get(i).calculateNewCentroid(features);
			}
		}

		// TODO keep going!
		// TODO calculate SSE
	}

}
