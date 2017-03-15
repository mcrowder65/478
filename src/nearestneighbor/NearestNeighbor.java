package nearestneighbor;

import java.util.Random;

import toolkit.Matrix;
import toolkit.SupervisedLearner;

public class NearestNeighbor extends SupervisedLearner {
	private Random rand;

	public NearestNeighbor(Random rand) {
		this.rand = rand;
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTestSet(Matrix testFeatures, Matrix testLabels) throws Exception {
		// TODO Auto-generated method stub

	}
}
