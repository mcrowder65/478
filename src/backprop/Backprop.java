package backprop;

import java.util.Random;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public class Backprop extends SupervisedLearner {
	private Random rand;
	private double[] myWeights;
	private double[] changeInWeights;
	final private static double MOMENTUM = 0.9;
	final private static double LEARNING_RATE = 0.175;
	final private static int BIAS = 1;
	final private static int MAX_ITERATIONS = 10;

	public Backprop(Random rand) {
		this.rand = rand;
	}

	private double calculateNet(double[] input, int startingPoint) {
		// Sigma WiXi
		// + 1 to account for bias.
		double net = 0;
		for (int i = startingPoint; i < startingPoint + input.length + 1; i++) {
			double weight = myWeights[i];
			double num = weight * (i == startingPoint ? BIAS : input[i - startingPoint - 1]);
			net += num;
		}
		return net;
	}

	private double calculateLastNet(double[] input) {
		double net = 0;
		for (int i = 0; i < input.length; i++) {
			double weight = myWeights[i];
			double num = weight * (i == 0 ? BIAS : input[i]);
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
		double result = (target - output) * output * (1 - output);
		// TODO should momentum be here?

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
		double result = output * (1 - output) * upstreamDelta * w;
		return result;
	}

	private double calculateDeltaW(double output, double delta, double momentum) {
		double result = LEARNING_RATE * delta * output;
		result += momentum;
		return result;
	}

	private void calculateNewWeights(double[] changeInWeights) {
		if (changeInWeights.length != myWeights.length) {
			throw new Error("Why aren't changeInWeights and myWeights equal length?");
		}
		for (int i = 0; i < changeInWeights.length; i++) {
			myWeights[i] = myWeights[i] + changeInWeights[i];
		}
	}

	private void epoch(Matrix features, Matrix labels) {
		final int numHiddenNodes = 3;// features.row(0).length * 2;
		// TODO fix this
		// int weightLength = (features.row(0).length + 1) * (numHiddenNodes +
		// 1);

		for (int x = 0; x < features.rows(); x++) {
			final double[] inputs = features.row(x);
			final double target = labels.row(x)[0];

			double[] netArray = new double[numHiddenNodes + 1];
			for (int i = 1; i < netArray.length; i++) {
				netArray[i] = calculateNet(inputs, i * (inputs.length + 1) + 1);
			}
			double[] outputArray = new double[numHiddenNodes + 1];
			for (int i = 1; i < outputArray.length; i++) {
				outputArray[i] = calculateOutput(netArray[i]);
			}
			netArray[0] = calculateLastNet(outputArray);

			outputArray[0] = calculateOutput(netArray[0]);
			double[] deltaArray = new double[numHiddenNodes + 1];
			deltaArray[0] = calculateExteriorDelta(target, outputArray[0]);

			for (int i = 1; i < deltaArray.length; i++) {
				deltaArray[i] = calculateHiddenNodeDelta(outputArray[i], deltaArray[0], myWeights[i]);
			}
			// num hidden nodes to output node
			for (int i = 0; i < numHiddenNodes + 1; i++) {
				double output = i == 0 ? BIAS : outputArray[i];
				changeInWeights[i] = calculateDeltaW(output, deltaArray[0], MOMENTUM * changeInWeights[i]);
			}
			int inputCounter = -1;
			int deltaCounter = 1;
			for (int i = numHiddenNodes + 1; i < changeInWeights.length; i++) {
				if (inputCounter < inputs.length) {
					inputCounter++;
				} else if (inputCounter == inputs.length) {
					inputCounter = 0;
					deltaCounter++;
				}
				double output = inputCounter == 0 ? BIAS : inputs[inputCounter - 1];
				double weight = deltaArray[deltaCounter];
				changeInWeights[i] = calculateDeltaW(output, weight, MOMENTUM * changeInWeights[i]);
			}
			calculateNewWeights(changeInWeights);
			Utilities.outputArray(myWeights);
		}
		System.out.println("epoch complete");

	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {

		int epochs = 0;
		double maxAccuracy = 0;
		int iterations = 0;
		// final int numHiddenNodes = features.row(0).length * 2;
		// int weightLength = (features.row(0).length + 1) * (numHiddenNodes +
		// 1);
		// this.myWeights = new double[weightLength];
		// this.myWeights = Utilities.initializeWeights(this.myWeights,
		// this.rand, -0.05, 0.05);
		// FIXME temp
		// w_0=0.02, w_1=-0.01, w_2=0.03, w_3=0.02, w_4=-0.01, w_5=-0.03,
		// w_6=0.03, w_7=0.01, w_8=0.04, w_9=-0.02, w_10=-0.02, w_11=0.03,
		// w_12=0.02

		this.myWeights = new double[] { 0.02, -0.01, 0.03, 0.02, -0.01, -0.03, 0.03, 0.01, 0.04, -0.02, -0.02, 0.03,
				0.02 };
		changeInWeights = new double[myWeights.length];
		for (int i = 0; i < myWeights.length; i++) {
			changeInWeights[i] = 0;
		}
		// while (iterations != MAX_ITERATIONS) {
		for (int i = 0; i < 3; i++)
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

		// }
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
