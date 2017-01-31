package perceptron;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import toolkit.Matrix;
import utilities.Utilities;

public class MultiplePerceptron extends Perceptron {
	private Random rand;

	private final static double THRESHOLD = 0;

	private final static int MAX_ITERATIONS = 5;

	private final static double LEARNING_RATE = 0.1;
	private List<Matrix> labelsArray;
	private List<double[]> weights;

	public MultiplePerceptron(Random rand) {
		this.rand = rand;
		labelsArray = new ArrayList<>();
		weights = new ArrayList<>();
	}

	private int currentIndex = -1;

	private void initializeLabels(Matrix labels) {
		for (int i = 0; i < 3; i++) {
			Matrix temp = new Matrix(labels);
		}

	}

	// TODO change enum to str to 1, 0, 0; 0, 1, 0; 0, 0, 1
	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("multiple perceptron train!");
		// should always be 3 for our purposes.
		int size = labels.m_enum_to_str.get(0).size();
		// split into sets of 50s
		int set = features.rows() / size;
		for (int i = 0; i < size; i++) {
			double[] tempWeights = new double[features.cols() + 1];
			tempWeights = this.initializeWeights(tempWeights, this.rand);

			this.weights.add(tempWeights);
			int epochs = 0;
			currentIndex = i;
			double maxAccuracy = 0;
			int iterations = 0;
			while (iterations != MAX_ITERATIONS) {
				double[] temp = epoch(features, labels, LEARNING_RATE, weights.get(this.currentIndex),
						set * this.currentIndex, set * this.currentIndex + set - 1);
				this.weights.set(this.currentIndex, temp);
				++epochs;
				// System.out.println("epoch #: " + epochs);
				double accuracy = measureAccuracy(features, labels, null);

				if (accuracy > maxAccuracy) {
					maxAccuracy = accuracy;
					iterations = 0;
				} else if (accuracy <= maxAccuracy) {
					++iterations;
				}
				features.shuffle(rand, labels);

			}

			Utilities.outputArray("final weights:", this.weights.get(this.currentIndex), true);
			System.out.println("most important weight index: "
					+ this.mostImportantWeightIndex(this.weights.get(this.currentIndex)));
			System.out.println("most important feature: "
					+ features.m_attr_name.get(this.mostImportantWeightIndex(this.weights.get(this.currentIndex))));
			System.out.println("epochs: " + epochs);
		}

	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO: if outputting 1, then output 0, 1, or 2

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
	 * @param min
	 *            int representing the first 1 in the set
	 * @param max
	 *            int representing the last 1 in the set
	 * @return double[]
	 */
	// currentIndex = 0
	// min = 0
	// max = 49

	// currentIndex = 1
	// min = 50
	// max = 99

	// currentIndex = 2
	// min = 100
	// max = 149
	private double[] epoch(Matrix features, Matrix labels, double learningRate, double[] weights, int min, int max) {
		for (int i = 0; i < features.rows(); i++) {
			// System.out.println("min: " + min + " max: " + max + "
			// this.currentBias: " + this.currentBias + " i: " + i);
			double[] pattern = features.row(i);
			double target = labels.row(i)[0];

			double net = evaluateNet(pattern, weights);
			double z = net > 0 ? 1 : 0;
			double[] changeInWeights = perceptronAlgorithm(pattern, learningRate, target, net, z);
			weights = combineArrays(weights, changeInWeights);
		}
		return weights;
	}

}
