package perceptron;

import java.util.Random;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public class Perceptron extends SupervisedLearner {
	private Random rand;
	// private double[] netValues = { 0, 0.11, 0.12, 0.11, 0.08, -0.08, 0.00,
	// -0.17 };
	private final static int BIAS = 1;

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

	private double calculateNet(double[] pattern) {
		double net = 0;
		for (int i = 0; i < pattern.length; i++) {
			net += pattern[i] * Utilities.randomDouble(rand, -1, 1);
		}
		return Utilities.round(net);
	}

	private double[] epoch(Matrix features, Matrix labels, double[] weights) {

		for (int i = 0; i < features.rows(); i++) {
			final double[] pattern = features.row(i);
			final double target = labels.row(i)[0];
			Utilities.outputArray(pattern, false);
			System.out.print("      " + BIAS);
			System.out.print("       " + target + "      ");
			Utilities.outputArray(weights, false);
			double net = calculateNet(pattern);
			double z = net >= 0 ? 1 : 0;
			final double[] changeInWeights = perceptronAlgorithm(pattern, .1, target, net, z);
			weights = combineArrays(weights, changeInWeights);

			System.out.print("       " + net);
			System.out.print("         " + z + "                 ");
			Utilities.outputArray(changeInWeights, false);
			System.out.println();
		}
		return weights;
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		double[] weights = new double[features.cols() + 1];
		System.out.println(
				"   Pattern       Bias    Target         Weight Vector         Net       Output                  Change in Weight");
		int epochs = 0;
		while (true) {
			weights = epoch(features, labels, weights);
			++epochs;
			if (stopTraining(weights)) {
				break;
			}
		}
	}

	private boolean stopTraining(double[] weights) {
		return true;
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
		// TODO Auto-generated method stub
		Utilities.outputArray("features", features, true);
	}

}
