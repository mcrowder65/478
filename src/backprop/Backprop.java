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

	public Backprop(Random rand) {
		this.rand = rand;
	}

	private double calculateNet(double[] input, int iteration) {
		// Sigma WiXi
		// + 1 to account for bias.
		double net = 0;
		for (int i = 0; i < input.length + 1; i++) {
			net += (this.myWeights[i + iteration] * (i < input.length ? input[i] : BIAS));
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

	// TODO figure out parameters
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

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		// TODO calculate weight size based on hidden nodes
		// FIXME temp
		double[] inputs = new double[2];
		inputs[0] = 0;
		inputs[1] = 0;
		// TODO how do we get target?
		final double target = 1;
		// TODO this is supposed to be twice the inputs
		int numHiddenNodes = 2;

		int weightLength = (inputs.length + 1) * (numHiddenNodes + 1);
		this.myWeights = new double[weightLength];
		this.myWeights = Utilities.initializeWeights(this.myWeights, this.rand, 1, 1);
		double[] changeInWeights = new double[weightLength];
		double[] netArray = new double[numHiddenNodes + 1];
		for (int i = 1; i < netArray.length; i++) {
			netArray[i] = calculateNet(inputs, i);
		}
		double[] outputArray = new double[numHiddenNodes + 1];
		for (int i = 1; i < outputArray.length; i++) {
			outputArray[i] = calculateOutput(netArray[i]);
		}
		netArray[0] = calculateNet(outputArray, 0);

		outputArray[0] = calculateOutput(netArray[0]);
		double[] deltaArray = new double[numHiddenNodes + 1];
		// // TODO calculate delta array
		deltaArray[0] = calculateExteriorDelta(target, outputArray[0]);

		for (int i = 1; i < deltaArray.length; i++) {
			deltaArray[i] = calculateHiddenNodeDelta(outputArray[i], deltaArray[0], myWeights[i]);
		}
		int iterations = 3;
		// num hidden nodes to output node
		for (int i = 0; i < numHiddenNodes + 1; i++) {
			double output = i < numHiddenNodes ? outputArray[i] : BIAS;
			changeInWeights[i] = calculateDeltaW(output, deltaArray[0]);
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
			double output = inputCounter == inputs.length ? BIAS : inputs[inputCounter];
			double weight = deltaArray[deltaCounter];
			changeInWeights[i] = calculateDeltaW(output, weight);
		}
		Utilities.outputArray(changeInWeights, true);

	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub

	}

}
