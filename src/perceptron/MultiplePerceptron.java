package perceptron;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import toolkit.Matrix;

public class MultiplePerceptron extends Perceptron {
	private Random rand;

	private final static double THRESHOLD = 0;

	private final static int MAX_ITERATIONS = 5;

	private final static double LEARNING_RATE = 0.1;
	private List<Matrix> featuresArray;
	private List<Matrix> labelsArray;
	private List<double[]> weights;

	public MultiplePerceptron(Random rand) {
		this.rand = rand;
		featuresArray = new ArrayList<>();
		labelsArray = new ArrayList<>();
		weights = new ArrayList<>();
	}

	private int getBias(int i, int set, int size) {
		// if i == 0
		// 000 - 049 -> 1
		// 050 - 099 -> 0
		// 100 - 149 -> 0
		int iSet = i * set;
		switch (iSet) {
		case 0: {
			return 1;
		}
		case 50: {
			return 0;
		}
		case 100: {
			return 0;
		}
		default:
			return -1;
		}

	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("multiple perceptron train!");
		// should always be 3 for our purposes.
		int size = labels.m_enum_to_str.get(0).size();
		// split into sets of 50s
		int set = features.rows() / size;
		for (int i = 0; i < size; i++) {

		}

	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("multiple perceptron predict!");

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
	 * @param activeIndex
	 *            int indicating which index should set the bias to 1
	 * @return double[]
	 */
	private double[] epoch(Matrix features, Matrix labels, double learningRate, double[] weights, double bias,
			int activeBias) {
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

}
