package perceptron;

import java.util.Random;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public class Perceptron extends SupervisedLearner {
	private Random rand;
	private final static int BIAS = 1;

	private final static double THRESHOLD = 0;

	private double[] myWeights;
	private final static int MAX_ITERATIONS = 5;

	public Perceptron(Random rand) {
		this.rand = rand;
	}

	/**
	 * 
	 * @param pattern
	 *            double[]
	 * @param learningRate
	 *            double
	 * @param target
	 *            double
	 * @param netValue
	 *            double
	 * @param z
	 *            double
	 * @return
	 */
	private double[] perceptronAlgorithm(final double[] pattern, final double learningRate, final double target,
			final double netValue, final double z) {
		// c(t - z)x

		// add 1 to account for bias!
		double[] output = new double[pattern.length + 1];
		for (int i = 0; i < pattern.length + 1; i++) {

			double a = learningRate * (target - z) * (i == pattern.length ? BIAS : pattern[i]);
			output[i] = Utilities.round(a);
		}

		return output;
	}

	private double evaluateNet(double[] pattern) {
		double net = 0;
		for (int i = 0; i < pattern.length + 1; i++) {
			net += (i == pattern.length ? BIAS * myWeights[i] : pattern[i] * myWeights[i]);
		}
		return Utilities.round(net);
	}

	private void epoch(Matrix features, Matrix labels) {
		final double learningRate = 0.1;
		for (int i = 0; i < features.rows(); i++) {
			final double[] pattern = features.row(i);
			final double target = labels.row(i)[0];

			double net = evaluateNet(pattern);
			double z = net > 0 ? 1 : 0;
			final double[] changeInWeights = perceptronAlgorithm(pattern, learningRate, target, net, z);
			myWeights = combineArrays(myWeights, changeInWeights);
			// this.outputStuff(pattern, net, z, changeInWeights, target);
		}
	}

	private void initializeWeights() {
		for (int i = 0; i < myWeights.length; i++) {
			myWeights[i] = Utilities.randomDouble(rand, -0.5, 0.5);
		}
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		myWeights = new double[features.cols() + 1];
		initializeWeights();
		Utilities.outputArray("weights:", myWeights, true);
		int epochs = 0;
		double maxAccuracy = 0;
		int iterations = 0;
		while (iterations != MAX_ITERATIONS) {

			epoch(features, labels);
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
		System.out.println("epochs: " + epochs);
	}

	private double[] combineArrays(double[] one, double[] two) {
		double[] newArray = new double[one.length];
		for (int i = 0; i < one.length; i++) {
			newArray[i] = one[i] + two[i];
		}
		return newArray;
	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		double net = evaluateNet(features);
		if (net > THRESHOLD) {
			labels[0] = 1;
		} else {
			labels[0] = 0;
		}
	}

	@SuppressWarnings("unused")
	private void outputStuff(double[] pattern, double net, double z, double[] changeInWeights, double target) {
		Utilities.outputArray(pattern, false);
		System.out.print(" " + BIAS);
		System.out.print(" " + target + " ");
		Utilities.outputArray(myWeights, false);
		System.out.print(" " + net);
		System.out.print(" " + z + " ");
		Utilities.outputArray(changeInWeights, false);
		System.out.println();
	}

}
