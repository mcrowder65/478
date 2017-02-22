package backprop;

import java.util.Random;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public class Backprop extends SupervisedLearner {
	private Random rand;
	private double[] myWeights;
	private double[] changeInWeights;
	final private static double MOMENTUM = 0;
	final private static double LEARNING_RATE = 0.1;
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

	private void epoch(Matrix features, Matrix labels, int numHiddenNodes, int numOutputNodes) {

		for (int x = 0; x < features.rows(); x++) {
			final double[] inputs = features.row(x);
			final double target = labels.row(x)[0];

			double[] netArray = new double[numHiddenNodes + 1];
			for (int i = numOutputNodes; i < netArray.length; i++) {
				netArray[i] = calculateNet(inputs, i * (inputs.length + 1) + 1);
			}
			double[] outputArray = new double[numHiddenNodes + 1];
			for (int i = numOutputNodes; i < outputArray.length; i++) {
				outputArray[i] = calculateOutput(netArray[i]);
			}
			for (int i = 0; i < numOutputNodes; i++) {
				netArray[i] = calculateLastNet(outputArray);
			}

			for (int i = 0; i < numOutputNodes; i++) {
				outputArray[i] = calculateOutput(netArray[i]);
			}

			double[] deltaArray = new double[numHiddenNodes + 1];
			for (int i = 0; i < numOutputNodes; i++) {
				deltaArray[i] = calculateExteriorDelta(target, outputArray[i]);
			}

			for (int i = numOutputNodes; i < deltaArray.length; i++) {
				// TODO how do i decide upstream node?
				double delta = calculateHiddenNodeDelta(outputArray[i], deltaArray[0], myWeights[i]);
				deltaArray[i] = delta;
			}
			// num hidden nodes to output node
			for (int i = 0; i < numHiddenNodes + 1; i++) {
				double output = i < numOutputNodes ? BIAS : outputArray[i];
				// TODO deltaIndex does not work.
				int deltaIndex = numHiddenNodes / numOutputNodes;
				System.out.println(deltaIndex);
				changeInWeights[i] = calculateDeltaW(output, deltaArray[deltaIndex], MOMENTUM * changeInWeights[i]);
			}
			int inputCounter = -1;
			int deltaCounter = numOutputNodes;
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
		}

	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {

		int epochs = 0;
		double maxAccuracy = 0;
		int iterations = 0;
		final int numHiddenNodes = features.row(0).length * 2;
		final int numOutputNodes = labels.m_enum_to_str.get(0).size();
		int weightLength = (features.row(0).length + 1) * (numHiddenNodes + 1) * numOutputNodes;
		this.myWeights = new double[weightLength];
		this.myWeights = Utilities.initializeWeights(this.myWeights, this.rand, -0.05, 0.05);

		changeInWeights = new double[weightLength];
		if (myWeights.length != changeInWeights.length) {

			System.err.println("why aren't changeInWeights length and myWeights length the same");
			System.out.println(
					"changeInWeights.length: " + changeInWeights.length + " myWeights.length: " + myWeights.length);
			return;
		}

		for (int i = 0; i < myWeights.length; i++) {
			changeInWeights[i] = 0;
		}
		Matrix features2 = new Matrix(features, 0, 0, features.rows(), features.cols());
		Matrix labels2 = new Matrix(features, 0, features.cols() - 1, features.rows(), 1);
		// Matrix validationSet = new Matrix(features);
		// Utilities.outputArrayList(features2.m_data);
		while (iterations != MAX_ITERATIONS) {
			epoch(features2, labels2, numHiddenNodes, numOutputNodes);
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
		System.out.println();
		System.out.println("weight length: " + myWeights.length);
		Utilities.outputArray("final weights:", this.myWeights, true);
		System.out.println("accuracy: " + maxAccuracy);
		System.out.println("epochs: " + epochs);
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

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub
		// 3 different output nodes
		if (labels[0] != 0) {
			// System.out.println("label not 0!: " + labels[0]);
		}
		double net = this.evaluateNet(features, this.myWeights);
		// System.out.println(net);
		labels[0] = 2;

	}

}
