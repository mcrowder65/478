package toolkit;
// ----------------------------------------------------------------

import java.util.Random;

import utilities.Utilities;

public abstract class SupervisedLearner {

	// Before you call this method, you need to divide your data
	// into a feature matrix and a label matrix.
	public abstract void train(Matrix features, Matrix labels) throws Exception;

	// A feature vector goes in. A label vector comes out. (Some supervised
	// learning algorithms only support one-dimensional label vectors. Some
	// support multi-dimensional label vectors.)
	public abstract void predict(double[] features, double[] labels) throws Exception;

	// The model must be trained before you call this method. If the label is
	// nominal,
	// it returns the predictive accuracy. If the label is continuous, it
	// returns
	// the root mean squared error (RMSE). If confusion is non-NULL, and the
	// output label is nominal, then confusion will hold stats for a confusion
	// matrix.
	public double measureAccuracy(Matrix features, Matrix labels, Matrix confusion) throws Exception {
		if (features.rows() != labels.rows())
			throw (new Exception("Expected the features and labels to have the same number of rows"));
		if (labels.cols() != 1)
			throw (new Exception("Sorry, this method currently only supports one-dimensional labels"));
		if (features.rows() == 0)
			throw (new Exception("Expected at least one row"));

		int labelValues = labels.valueCount(0);
		if (labelValues == 0) // If the label is continuous...
		{
			// The label is continuous, so measure root mean squared error
			double[] pred = new double[1];
			double sse = 0.0;
			for (int i = 0; i < features.rows(); i++) {
				double[] feat = features.row(i);
				double[] targ = labels.row(i);
				pred[0] = 0.0; // make sure the prediction is not biassed by a
								// previous prediction
				predict(feat, pred);
				double delta = targ[0] - pred[0];
				sse += (delta * delta);
			}
			return Math.sqrt(sse / features.rows());
		} else {
			// The label is nominal, so measure predictive accuracy
			if (confusion != null) {
				confusion.setSize(labelValues, labelValues);
				for (int i = 0; i < labelValues; i++)
					confusion.setAttrName(i, labels.attrValue(0, i));
			}
			int correctCount = 0;
			double[] prediction = new double[1];
			for (int i = 0; i < features.rows(); i++) {
				double[] feat = features.row(i);
				int targ = (int) labels.get(i, 0);
				if (targ >= labelValues)
					throw new Exception("The label is out of range");
				predict(feat, prediction);
				int pred = (int) prediction[0];
				if (confusion != null)
					confusion.set(targ, pred, confusion.get(targ, pred) + 1);
				if (pred == targ)
					correctCount++;
			}
			return (double) correctCount / features.rows();
		}
	}

	// MY STUFF

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
	protected double[] epoch(Matrix features, Matrix labels, double learningRate, double[] weights, double bias) {
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
