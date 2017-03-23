package clustering;

import java.util.ArrayList;
import java.util.List;

import toolkit.Matrix;

public class Clustering {
	private final int k = 5;

	private double[] replaceUnknownWithNum(double[] arr, double num) {
		// TODO this may need to take into account nominal
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == Double.MAX_VALUE) {
				arr[i] = num;
			}
		}
		return arr;
	}

	public void clusterTrain(Matrix features) {
		int firstAttrIndex = 0;

		// don't ever include the id!
		if (features.m_attr_name.get(0).equals("'id'")) {
			firstAttrIndex = 1;
		}
		List<Cluster> clusters = new ArrayList<>();
		for (int amountOfClusters = 0; amountOfClusters < k; amountOfClusters++) {
			double[] rowArr = features.row(amountOfClusters);
			Cluster cluster = new Cluster(rowArr);
			List<Double> distances = new ArrayList<>();
			for (int row = 0; row < features.rows(); row++) {
				double[] feature = features.row(row);

				double distance = 0;
				for (int dimensionIndex = firstAttrIndex; dimensionIndex < feature.length; dimensionIndex++) {
					double val = feature[dimensionIndex];
					double centroidVal = cluster.getCentroid().getDimension(dimensionIndex);
					double answer = 0;
					if (features.m_enum_to_str.size() == 0) {
						// real
						if (val == Double.MAX_VALUE || centroidVal == Double.MAX_VALUE) {
							answer = 1;
						} else {
							// TODO idk if i need this here?
							answer = Math.pow(val - centroidVal, 2);
						}
					} else {
						// nominal
						if (val == Double.MAX_VALUE || centroidVal == Double.MAX_VALUE) {
							answer = 1;
						} else if (val == centroidVal) {
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

		for (int x = 0; x < features.cols(); x++) {
			Cluster clust = null;
			int minIndex = -1;
			int clusterIndex = -1;
			double min = Double.MAX_VALUE;
			for (int c = 0; c < clusters.size(); c++) {
				min = Double.MAX_VALUE;
				minIndex = -1;
				clusterIndex = -1;
				Cluster cluster = clusters.get(c);
				List<Double> distances = cluster.getDistances();
				// go vertically not horizontally
				for (int i = 0; i < distances.size(); i++) {
					if (distances.get(i) < min) {
						min = distances.get(i);
						minIndex = i;
						clust = cluster;
						clusterIndex = c;
					}
				}

			}
			clust.addInstance(new Point(features.row(minIndex)));
			System.out.println("hello");
		}

	}

}
