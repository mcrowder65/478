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
			double num = weight * (i == startingPoint + input.length ? BIAS : input[i - startingPoint]);
			net += num;
		}
		return net;
	}

	/**
	 * 
	 * @param output
	 * @param startingPoint
	 *            int - generally this is the numOutputNodes, because we don't
	 *            want to add the output of the other nodes to the net
	 * @param weightIndex
	 * @return
	 */
	private double calculateLastNet(double[] output, int startingPoint, int weightIndex) {
		double net = 0;

		for (int i = startingPoint; i < output.length + 1; i++) {
			// increment weightIndex everytime.
			// do i - startingPoint so that it increments from 0 to
			// output.length
			double weight = myWeights[weightIndex + (i - startingPoint)];
			double num = weight * (i == output.length ? BIAS : output[i]);
			net += num;
		}

		return net;
	}

	private double calculateOutput(double net) {
		double output = 1 / (1 + Math.exp(-net));
		return output;
	}

	/**
	 * (t1 - o1) o1 (1 - o1)
	 * 
	 * @param target
	 *            double 1 or 0
	 * @param output
	 * @return
	 */
	private double calculateExteriorDelta(double target, double output) {
		double result = (target - output) * output * (1 - output);

		return result;
	}

	/**
	 * output * (1 - output) * sigma (upstreamDelta * w)
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
			int startingPoint = 0;
			for (int i = numOutputNodes; i < netArray.length; i++) {
				// if it's 0, then go ahead and calculate it to be 27 for the
				// first time. think, the number of outputNodes is 3, and the
				// number of hidden nodes is 8. add 1 for bias 3 * 9 = 27 -> so
				// now the first weights from input to hidden nodes starts at
				// index 27. only init when it's set at 0.

				startingPoint = startingPoint == 0 ? (numOutputNodes * (numHiddenNodes + 1)) : startingPoint;
				netArray[i] = calculateNet(inputs, startingPoint);

				// now, add the input length (4) + 1 for bias. so 5..... there
				// would be 5 weights connected plus the starting point to get
				// the new index!
				startingPoint = inputs.length + 1 + startingPoint;
			}

			double[] outputArray = new double[numHiddenNodes + numOutputNodes];
			for (int i = numOutputNodes; i < outputArray.length; i++) {
				outputArray[i] = calculateOutput(netArray[i]);
			}
			for (int i = 0; i < numOutputNodes; i++) {
				// calculate weight index based on the number of hidden nodes.
				// think about it. there are 3 outputs... so there are 9 weights
				// that connect to each output, so (numHiddenNodes + 1) * i will
				// get you the right starting index.

				int weightIndex = (numHiddenNodes + 1) * i;
				netArray[i] = calculateLastNet(outputArray, numOutputNodes, weightIndex);
			}

			for (int i = 0; i < numOutputNodes; i++) {
				double net = netArray[i];
				outputArray[i] = calculateOutput(net);
				outputNodes[i] = outputArray[i];
			}
			double[] deltaArray = new double[numHiddenNodes + numOutputNodes];
			for (int i = 0; i < numOutputNodes; i++) {
				// make target 1 if the current label is the one i'm looking
				// for?

				double target = i == labels.row(x)[0] ? 1 : 0;

				deltaArray[i] = calculateExteriorDelta(target, outputArray[i]);
			}

			for (int i = numOutputNodes; i < deltaArray.length; i++) {

				double[] tempDeltaArray = new double[numOutputNodes];
				double[] tempWeightArray = new double[numOutputNodes];

				for (int k = 0; k < tempDeltaArray.length; k++) {
					// only use the first three delta
					tempDeltaArray[k] = deltaArray[k];
					// index is found by taking the number of hidden nodes + 1
					// for bias * k so this is how it works...
					// the first hidden node to the first output is index 0...
					// then all the other hidden nodes plus bias are indices
					// 1-8. so then next time (numHiddenNodes + 1) * k will be 9
					// when k is 1. Add (i - numOutputNodes) to increment so
					// that the next delta weights will be correct.

					int index = ((numHiddenNodes + 1) * k) + (i - numOutputNodes);
					tempWeightArray[k] = myWeights[index];
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
		int weightLength = (features.row(0).length + 1) * (numHiddenNodes) + (numOutputNodes * (numHiddenNodes + 1));
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
