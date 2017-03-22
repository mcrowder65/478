package clustering;

import toolkit.Matrix;

public class Clustering {
	private final int k = 2;

	public void clusterTrain(Matrix features) {
		int firstFeature = 0;

		// don't ever include the id!
		if (features.m_attr_name.get(0).equals("'id'")) {
			firstFeature = 1;
		}
		for (int r = 0; r < features.rows(); r++) {
			double[] feature = features.row(r);
			for (int x = firstFeature; x < feature.length; x++) {
				double val = feature[x];
			}
		}
	}

}
