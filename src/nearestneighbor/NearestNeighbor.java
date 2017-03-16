package nearestneighbor;

import java.util.Random;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public class NearestNeighbor extends SupervisedLearner {
	private Random rand;

	public NearestNeighbor(Random rand) {
		this.rand = rand;
	}

	private void myTrain(Matrix features, Matrix labels) {
		for (int i = 0; i < features.rows(); i++) {
			Utilities.outputArray(features.row(i));
		}
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		// TODO Auto-generated method stub
		this.myTrain(features, labels);
	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTestSet(Matrix testFeatures, Matrix testLabels) throws Exception {
		// TODO Auto-generated method stub
		// i may or may not need this.

	}
}
