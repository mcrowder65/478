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
	private double[] outputNodes;

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

	private double calculateLastNet(double[] output, int startingPoint, int weightIndex) {
		// TODO this is probably wrong :(

		double net = 0;
		for (int i = startingPoint; i < output.length; i++) {
			double weight = myWeights[weightIndex + i];
			// TODO output
			System.out.println(weightIndex + i);
			double num = weight * (i == startingPoint ? BIAS : output[i - startingPoint]);
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
	private double calculateHiddenNodeDelta(double output, double[] upstreamDeltas, double[] weights) {
		// output ( 1 - output) * upstreamDelta * w
		double upstreamStuff = 0;
		for (int i = 0; i < upstreamDeltas.length; i++) {
			upstreamStuff += (upstreamDeltas[i] * weights[i]);
		}
		double result = output * (1 - output) * upstreamStuff;
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
			double[] netArray = new double[numHiddenNodes + numOutputNodes];
			for (int i = numOutputNodes; i < netArray.length; i++) {
				netArray[i] = calculateNet(inputs, i * (inputs.length + 1) + 1);
			}
			double[] outputArray = new double[numHiddenNodes + numOutputNodes];
			for (int i = numOutputNodes; i < outputArray.length; i++) {
				outputArray[i] = calculateOutput(netArray[i]);
			}
			for (int i = 0; i < numOutputNodes; i++) {
				// TODO pick the right weights!
				int weightIndex = 0;
				netArray[i] = calculateLastNet(outputArray, numOutputNodes, weightIndex);
			}

			for (int i = 0; i < numOutputNodes; i++) {
				double net = netArray[i];
				outputArray[i] = calculateOutput(net);
				outputNodes[i] = outputArray[i];
			}
			double[] deltaArray = new double[numHiddenNodes + 1];
			for (int i = 0; i < numOutputNodes; i++) {
				double target = i == labels.row(x)[0] ? 1 : 0;

				deltaArray[i] = calculateExteriorDelta(target, outputArray[i]);
			}

			for (int i = numOutputNodes; i < deltaArray.length; i++) {

				double[] tempDeltaArray = new double[numOutputNodes];
				double[] tempWeightArray = new double[numOutputNodes];

				// TODO tempWeightArray may be incorrect...
				for (int k = 0; k < tempDeltaArray.length; k++) {
					tempDeltaArray[k] = deltaArray[k];
					int index = i * numOutputNodes + k;
					tempWeightArray[k] = myWeights[i * numOutputNodes + k];
				}
				double delta = calculateHiddenNodeDelta(outputArray[i], tempDeltaArray, tempWeightArray);
				deltaArray[i] = delta;
			}
			// num hidden nodes to output node
			for (int i = 0; i < numHiddenNodes + 1; i++) {
				double output = i < numOutputNodes ? BIAS : outputArray[i];
				int deltaIndex = i / numOutputNodes;
				changeInWeights[i] = calculateDeltaW(output, deltaArray[deltaIndex], MOMENTUM * changeInWeights[i]);
			}
			int inputCounter = -1;
			int deltaCounter = numOutputNodes;
			for (int i = numHiddenNodes + 1; i < changeInWeights.length; i++) {
				if (inputCounter < inputs.length) {
					inputCounter++;
				} else if (inputCounter == inputs.length) {
					inputCounter = 0;
					deltaCounter = deltaCounter >= deltaArray.length - 1 ? numOutputNodes : deltaCounter + 1;
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
		outputNodes = new double[numOutputNodes];
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
		Matrix labels2 = new Matrix(labels, 0, labels.cols() - 1, labels.rows(), 1);
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
			features2.shuffle(rand, labels);

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

	/**
	 * returns index of biggest output node
	 * 
	 * @return
	 */
	private int biggestOutputNode() {
		double biggest = 0;
		int winningIndex = 0;
		for (int i = 0; i < outputNodes.length; i++) {
			if (outputNodes[i] > biggest) {
				biggest = outputNodes[i];
				winningIndex = i;
			}
		}
		return winningIndex;
	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// 3 different output nodes
		labels[0] = biggestOutputNode();

	}

}
