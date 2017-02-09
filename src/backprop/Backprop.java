package backprop;

import java.util.Random;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public class Backprop extends SupervisedLearner {
	private Random rand;
	private double[] myWeights;

	public Backprop(Random rand) {
		this.rand = rand;
	}

	// TODO figure out parameters
	private double calculateNet(double[] feature) {
		return -1;
	}

	// TODO figure out parameters
	private double calculateOutput() {
		return -1;
	}

	// TODO figure out parameters
	private double calculateDelta() {
		return -1;
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		// TODO calculate weight size based on hidden nodes
		// FIXME temp
		this.myWeights = new double[6];

		// Utilities.outputArrayList(features.m_data);
		this.myWeights = Utilities.initializeWeights(this.myWeights, this.rand, 0, 0);
		Utilities.outputArray(this.myWeights, true);

	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub

	}

}
