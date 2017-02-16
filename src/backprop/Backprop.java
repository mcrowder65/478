package backprop;

import java.util.Random;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public class Backprop extends SupervisedLearner {
	private Random rand;
	private double[] myWeights;
	final private static int LEARNING_RATE = 1;
	final private static int BIAS = 1;
	final private static int MAX_ITERATIONS = 10;

	public Backprop(Random rand) {
		this.rand = rand;
	}

	private double calculateNet(double[] input) {
		// Sigma WiXi
		// + 1 to account for bias.
		double net = 0;
		for (int i = 0; i < input.length + 1; i++) {
			double num = myWeights[i] * (i < input.length ? input[i] : BIAS);
			net += num;
		}
		return net;
	}

	private double calculateOutput(double net) {
		double output = 1 / (1 + Math.exp(-net));
		return output;
	}

	private double calculateExteriorDelta(double target, double output) {
		// (t1 - o1) o1 (1 - o1)
		target = Utilities.round(target, 1000);
		output = Utilities.round(output, 1000);
		double result = (target - output) * output * (1 - output);
		result = Utilities.round(result, 100000);
		return result;
	}

	/**
	 * 
	 * @param output
	 *            double
	 * @param upstreamDelta
	 *            double
	 * @param w
	 *            double
	 * @return double
	 */
	private double calculateHiddenNodeDelta(double output, double upstreamDelta, double w) {
		// output ( 1 - output) * upstreamDelta * w
		output = Utilities.round(output, 1000);
		double result = output * (1 - output) * upstreamDelta * w;
		result = Utilities.round(result, 100000);
		return result;
	}

	private double calculateDeltaW(double output, double delta) {
		// delta = Utilities.round(delta, 1000000);
		output = Utilities.round(output, 1000);
		double result = LEARNING_RATE * delta * output;
		result = Utilities.round(result, 100000);
		return result;
	}

	private void calculateNewWeights(double[] changeInWeights) {
		if (changeInWeights.length != myWeights.length) {
			throw new Error("Why aren't changeInWeights and myWeights equal length?");
		}
		for (int i = 0; i < changeInWeights.length; i++) {
			myWeights[i] += changeInWeights[i];
		}
	}

	private void epoch(Matrix features, Matrix labels) {
		final int numHiddenNodes = features.row(0).length * 2;
		int weightLength = (features.row(0).length + 1) * (numHiddenNodes + 1);
		for (int x = 0; x < features.rows(); x++) {
			final double[] inputs = features.row(x);
			final double target = labels.row(x)[0];

			double[] changeInWeights = new double[weightLength];
			double[] netArray = new double[numHiddenNodes + 1];
			for (int i = 1; i < netArray.length; i++) {
				netArray[i] = calculateNet(inputs);
			}
			double[] outputArray = new double[numHiddenNodes + 1];
			for (int i = 1; i < outputArray.length; i++) {
				outputArray[i] = calculateOutput(netArray[i]);
			}
			netArray[0] = calculateNet(outputArray);

			outputArray[0] = calculateOutput(netArray[0]);
			double[] deltaArray = new double[numHiddenNodes + 1];
			deltaArray[0] = calculateExteriorDelta(target, outputArray[0]);

			for (int i = 1; i < deltaArray.length; i++) {
				deltaArray[i] = calculateHiddenNodeDelta(outputArray[i], deltaArray[0], myWeights[i]);
			}
			// num hidden nodes to output node
			for (int i = 0; i < numHiddenNodes + 1; i++) {
				double output = i < numHiddenNodes ? outputArray[i] : BIAS;
				changeInWeights[i] = calculateDeltaW(output, deltaArray[0]);
			}
			int inputCounter = -1;
			int deltaCounter = 1;
			// TODO this is a little different cuz of the amount of hidden
			// nodes.
			for (int i = numHiddenNodes + 1; i < changeInWeights.length; i++) {

				if (inputCounter < inputs.length) {
					inputCounter++;
				} else if (inputCounter == inputs.length) {
					inputCounter = 0;
					deltaCounter++;
					// System.out.println("*****************");
				}
				double output = inputCounter == inputs.length ? BIAS : inputs[inputCounter];
				double weight = deltaArray[deltaCounter];
				// System.out.println("output: " + output);// + " weight: "
				// +weight);
				changeInWeights[i] = calculateDeltaW(output, weight);
			}
			calculateNewWeights(changeInWeights);
		}

	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {

		int epochs = 0;
		double maxAccuracy = 0;
		int iterations = 0;
		final int numHiddenNodes = features.row(0).length * 2;
		int weightLength = (features.row(0).length + 1) * (numHiddenNodes + 1);
		this.myWeights = new double[weightLength];
		this.myWeights = Utilities.initializeWeights(this.myWeights, this.rand, -0.05, 0.05);
		while (iterations != MAX_ITERATIONS) {
			epoch(features, labels);
			++epochs;

			double accuracy = measureAccuracy(features, labels, null);
			// System.out.println("accuracy: " + accuracy + " maxAccuracy: " +
			// maxAccuracy);
			// System.out.print(epochs + ", ");
			// System.out.print(accuracy + "\n");
			if (accuracy > maxAccuracy) {
				maxAccuracy = accuracy;
				iterations = 0;
			} else if (accuracy <= maxAccuracy) {
				++iterations;
			}
			// features.shuffle(rand, labels);

		}
		System.out.println();
		Utilities.outputArray("final weights:", this.myWeights, true);
		System.out.println("accuracy: " + maxAccuracy);
		System.out.println("epochs: " + epochs);
	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub

	}

}
