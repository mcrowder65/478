package backprop;

import java.util.Random;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public class Backprop extends SupervisedLearner {
	private Random rand;
	private double[] myWeights;

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

	// TODO figure out parameters
	private double calculateOutput(double net) {
		double output = 1 / (1 + Math.exp(-net));
		return output;
	}

	// TODO figure out parameters
	private double calculateDelta() {
		return -1;
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		// TODO calculate weight size based on hidden nodes
		// FIXME temp
		double[] inputs = new double[2];
		inputs[0] = 0;
		inputs[1] = 0;

		// TODO this is supposed to be twice the inputs
		int numHiddenNodes = 2;

		int weightLength = (inputs.length + 1) * (numHiddenNodes + 1);
		this.myWeights = new double[weightLength];
		this.myWeights = Utilities.initializeWeights(this.myWeights, this.rand, 1, 1);
		double[] netArray = new double[numHiddenNodes + 1];
		int iteration = 0;
		for (int i = 0; i < netArray.length - 1; i++) {
			netArray[i] = calculateNet(inputs, iteration++);
		}
		double[] outputArray = new double[numHiddenNodes + 1];
		for (int i = 0; i < outputArray.length - 1; i++) {
			outputArray[i] = calculateOutput(netArray[i]);
		}
		netArray[iteration] = calculateNet(outputArray, iteration);
		outputArray[iteration] = calculateOutput(netArray[iteration]);
		// TODO calculate delta array
		int x = 0;
	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub

	}

}
