package perceptron;

import java.util.Random;

import toolkit.Matrix;
import utilities.Utilities;

public class SinglePerceptron extends Perceptron {
	private Random rand;
	private final static double THRESHOLD = 0;

	private double[] myWeights;
	private final static int MAX_ITERATIONS = 1000;

	private final static double LEARNING_RATE = 0.1;

	public SinglePerceptron(Random rand) {
		this.rand = rand;
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {

		this.myWeights = new double[features.cols() + 1];
		this.myWeights = this.initializeWeights(this.myWeights, this.rand);
		Utilities.outputArray("weights:", this.myWeights, true);
		// el salvador aid
		// education spending
		int epochs = 0;
		double maxAccuracy = 0;
		int iterations = 0;
		while (iterations != MAX_ITERATIONS) {
			myWeights = epoch(features, labels, LEARNING_RATE, myWeights);
			++epochs;

			double accuracy = Utilities.round(measureAccuracy(features, labels, null));
			// System.out.println("accuracy: " + accuracy + " maxAccuracy: " +
			// maxAccuracy);
			System.out.print(epochs + ", ");
			System.out.print(accuracy + "\n");
			if (accuracy > maxAccuracy) {
				maxAccuracy = accuracy;
				iterations = 0;
			} else if (accuracy <= maxAccuracy) {
				++iterations;
			}
			// TODO only do for voting
			// features.shuffle(rand, labels);

		}
		System.out.println();
		Utilities.outputArray("final weights:", this.myWeights, true);
		System.out.println("accuracy: " + maxAccuracy);
		System.out.println("epochs: " + epochs);
	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		double net = this.evaluateNet(features, this.myWeights);
		if (net > THRESHOLD) {
			labels[0] = 1;
		} else {
			labels[0] = 0;
		}
	}

}
