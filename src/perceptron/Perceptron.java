package perceptron;

import java.util.Random;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public abstract class Perceptron extends SupervisedLearner {
	public abstract void train(Matrix features, Matrix labels) throws Exception;

	public abstract void predict(double[] features, double[] labels) throws Exception;

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
	 * @param bias
	 *            double
	 * @return double[]
	 */
	protected double[] perceptronAlgorithm(final double[] pattern, final double learningRate, final double target,
			final double netValue, final double z, final double bias) {
		// c(t - z)x

		// add 1 to account for bias!
		double[] output = new double[pattern.length + 1];
		for (int i = 0; i < pattern.length + 1; i++) {

			double a = learningRate * (target - z) * (i == pattern.length ? bias : pattern[i]);
			output[i] = Utilities.round(a);
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
	 * @param bias
	 *            double
	 * @return double
	 */
	protected double evaluateNet(double[] pattern, double[] weights, double bias) {
		double net = 0;
		for (int i = 0; i < pattern.length + 1; i++) {
			net += (i == pattern.length ? bias * weights[i] : pattern[i] * weights[i]);
		}
		return Utilities.round(net);
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
	 * @param bias
	 *            double
	 * @param weights
	 *            double[]
	 */
	protected void outputStuff(double[] pattern, double net, double z, double[] changeInWeights, double target,
			double bias, double[] weights) {
		Utilities.outputArray(pattern, false);
		System.out.print(" " + bias);
		System.out.print(" " + target + " ");
		Utilities.outputArray(weights, false);
		System.out.print(" " + net);
		System.out.print(" " + z + " ");
		Utilities.outputArray(changeInWeights, false);
		System.out.println();
	}

	/**
	 * 
	 * @param weights
	 *            double[]
	 * @param rand
	 *            Random
	 * @return double[]
	 */
	protected double[] initializeWeights(double[] weights, Random rand) {
		for (int i = 0; i < weights.length; i++) {
			weights[i] = Utilities.randomDouble(rand, -0.05, 0.05);
		}
		return weights;
	}

}
