package perceptron;

import java.util.Random;

import toolkit.Matrix;
import utilities.Utilities;

public class SinglePerceptron extends Perceptron {
	private Random rand;
	private final static double BIAS = 1;
	private final static double THRESHOLD = 0;

	private double[] myWeights;
	private final static int MAX_ITERATIONS = 5;

	private final static double LEARNING_RATE = 0.1;

	public SinglePerceptron(Random rand) {
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
			myWeights = epoch(features, labels, LEARNING_RATE, myWeights, BIAS);
			++epochs;
			double accuracy = measureAccuracy(features, labels, null);

			if (accuracy > maxAccuracy) {
				maxAccuracy = accuracy;
				iterations = 0;
			} else if (accuracy <= maxAccuracy) {
				++iterations;
			}
			features.shuffle(rand, labels);

		}

		Utilities.outputArray("final weights:", myWeights, true);
		System.out.println("most important weight index: " + this.mostImportantWeightIndex(myWeights));
		System.out.println(
				"most important feature: " + features.m_attr_name.get(this.mostImportantWeightIndex(myWeights)));
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

	/**
	 * 
	 * @param features
	 *            Matrix
	 * @param labels
	 *            Matrix
	 * @param learningRate
	 *            double
	 * @param weights
	 *            double[]
	 * @param bias
	 *            double
	 * @return double[]
	 */
	private double[] epoch(Matrix features, Matrix labels, double learningRate, double[] weights, double bias) {
		for (int i = 0; i < features.rows(); i++) {
			final double[] pattern = features.row(i);
			final double target = labels.row(i)[0];

			final double net = evaluateNet(pattern, weights, bias);
			final double z = net > 0 ? 1 : 0;
			final double[] changeInWeights = perceptronAlgorithm(pattern, learningRate, target, net, z, bias);
			weights = combineArrays(weights, changeInWeights);
		}
		return weights;
	}

}
