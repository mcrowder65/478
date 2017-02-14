package perceptron;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public abstract class Perceptron extends SupervisedLearner {
	public abstract void train(Matrix features, Matrix labels) throws Exception;

	public abstract void predict(double[] features, double[] labels) throws Exception;

	static final protected int BIAS = 1;

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
	 * @return double[]
	 */
	protected double[] perceptronAlgorithm(final double[] pattern, final double learningRate, final double target,
			final double netValue, final double z) {
		// c(t - z)x

		// add 1 to account for bias!
		double[] output = new double[pattern.length + 1];
		for (int i = 0; i < pattern.length + 1; i++) {

			double a = learningRate * (target - z) * (i == pattern.length ? BIAS : pattern[i]);
			output[i] = Utilities.round(a, 100.0);
		}

		return output;
	}

	/**
	 * 
	 * @param weights
	 *            double[]
	 * @return int
	 */
	protected int mostImportantWeightIndex(double[] weights) {
		double max = Double.MIN_VALUE;
		int chosenIndex = -1;
		for (int i = 0; i < weights.length - 1; i++) {
			double weight = Math.abs(weights[i]);
			if (weight > max) {
				max = weight;
				chosenIndex = i;
			}
		}
		return chosenIndex;
	}

	/**
	 * 
	 * @param pattern
	 *            double[]
	 * @param weights
	 *            double[]
	 * @return double
	 */
	protected double evaluateNet(double[] pattern, double[] weights) {
		double net = 0;
		for (int i = 0; i < pattern.length + 1; i++) {
			net += (i == pattern.length ? BIAS * weights[i] : pattern[i] * weights[i]);
		}
		return Utilities.round(net, 100.0);
	}

	/**
	 * 
	 * @param one
	 *            double[]
	 * @param two
	 *            double[]
	 * @return double[]
	 */
	protected double[] combineArrays(double[] one, double[] two) {
		double[] newArray = new double[one.length];
		for (int i = 0; i < one.length; i++) {
			newArray[i] = one[i] + two[i];
		}
		return newArray;
	}

	/**
	 * 
	 * @param pattern
	 *            double[]
	 * @param net
	 *            double
	 * @param z
	 *            double
	 * @param changeInWeights
	 *            double[]
	 * @param target
	 *            double
	 * @param weights
	 *            double[]
	 */
	protected void outputStuff(double[] pattern, double net, double z, double[] changeInWeights, double target,
			double[] weights) {
		Utilities.outputArray(pattern, false);
		System.out.print(" " + BIAS);
		System.out.print(" " + target + " ");
		Utilities.outputArray(weights, false);
		System.out.print(" " + net);
		System.out.print(" " + z + " ");
		Utilities.outputArray(changeInWeights, false);
		System.out.println();
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
	 * @return double[]
	 */
	protected double[] epoch(Matrix features, Matrix labels, double learningRate, double[] weights) {
		for (int i = 0; i < features.rows(); i++) {
			double[] pattern = features.row(i);
			double target = labels.row(i)[0];

			double net = evaluateNet(pattern, weights);
			double z = net > 0 ? 1 : 0;
			double[] changeInWeights = perceptronAlgorithm(pattern, learningRate, target, net, z);
			weights = combineArrays(weights, changeInWeights);
		}
		return weights;
	}

}
