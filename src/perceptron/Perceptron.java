package perceptron;

import java.util.Random;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public class Perceptron extends SupervisedLearner {
	private Random rand;
	private final static double BIAS = 1;
	private final static double THRESHOLD = 0;

	private double[] myWeights;
	private final static int MAX_ITERATIONS = 5;

	private final static double LEARNING_RATE = 0.1;

	public Perceptron(Random rand) {
		this.rand = rand;
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {

		this.myWeights = new double[features.cols() + 1];
		this.myWeights = this.initializeWeights(this.myWeights, this.rand);
		Utilities.outputArray("weights:", this.myWeights, true);
		// el salvador aid
		// education spending
		int epochs = 0;
		double maxAccuracy = 0;
		int iterations = 0;
		while (iterations != MAX_ITERATIONS) {
			this.myWeights = this.epoch(features, labels, LEARNING_RATE, this.myWeights, BIAS);
			++epochs;
			double accuracy = this.measureAccuracy(features, labels, null);
			if (accuracy > maxAccuracy) {
				maxAccuracy = accuracy;
				iterations = 0;
			} else if (accuracy <= maxAccuracy) {
				++iterations;
			}
			features.shuffle(rand, labels);

		}

		Utilities.outputArray("final weights:", this.myWeights, true);
		System.out.println("most important weight index: " + this.mostImportantWeightIndex(this.myWeights));
		System.out.println(
				"most important feature: " + features.m_attr_name.get(this.mostImportantWeightIndex(this.myWeights)));
		System.out.println("epochs: " + epochs);
	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		double net = this.evaluateNet(features, this.myWeights, BIAS);
		if (net > THRESHOLD) {
			labels[0] = 1;
		} else {
			labels[0] = 0;
		}
	}

}
