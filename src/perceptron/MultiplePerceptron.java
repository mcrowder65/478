package perceptron;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import toolkit.Matrix;
import utilities.Utilities;

public class MultiplePerceptron extends Perceptron {
	private Random rand;
	private final static int THRESHOLD = 0;
	private final static int MAX_ITERATIONS = 10;

	private final static double LEARNING_RATE = 0.1;
	private List<Matrix> labelsArray;
	private List<double[]> weights;

	public MultiplePerceptron(Random rand) {
		this.rand = rand;
		labelsArray = new ArrayList<>();
		weights = new ArrayList<>();
	}

	private int currentIndex = -1;

	private void initializeLabels(Matrix labels, int size) {
		for (int i = 0; i < size; i++) {
			Matrix temp = new Matrix(labels, 0, 0, labels.rows(), labels.cols());
			TreeMap<String, Integer> map = temp.m_str_to_enum.get(0);
			Map<String, Integer> newMap = new TreeMap<>();
			for (String key : map.keySet()) {
				newMap.put(key, i == map.get(key) ? 1 : 0);
			}
			List<TreeMap<String, Integer>> newList = new ArrayList<>();
			newList.add((TreeMap<String, Integer>) newMap);
			temp.m_str_to_enum = (ArrayList<TreeMap<String, Integer>>) newList;
			this.labelsArray.add(temp);

		}
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		// should always be 3 for our purposes.
		int size = labels.m_enum_to_str.get(0).size();
		initializeLabels(labels, size);
		for (int i = 0; i < size; i++) {
			double[] tempWeights = new double[features.cols() + 1];
			tempWeights = this.initializeWeights(tempWeights, this.rand);

			this.weights.add(tempWeights);
			currentIndex = i;
			Utilities.outputArray("initial weights:", this.weights.get(currentIndex), true);
			int epochs = 0;

			double maxAccuracy = 0;
			int iterations = 0;
			while (iterations != MAX_ITERATIONS) {
				// Utilities.outputArrayList(this.labelsArray.get(this.currentIndex).m_str_to_enum);
				double[] temp = epoch(features, labelsArray.get(this.currentIndex), LEARNING_RATE,
						this.weights.get(this.currentIndex));
				this.weights.set(this.currentIndex, temp);
				Utilities.outputArray("weights:", this.weights.get(currentIndex), true);
				++epochs;
				double accuracy = measureAccuracy(features, this.labelsArray.get(this.currentIndex), null);

				if (accuracy > maxAccuracy) {
					maxAccuracy = accuracy;
					iterations = 0;
				} else if (accuracy <= maxAccuracy) {
					++iterations;
				}

			}

			// Utilities.outputArray("final weights:",
			// this.weights.get(this.currentIndex), true);
			// System.out.println("most important weight index: "
			// +
			// this.mostImportantWeightIndex(this.weights.get(this.currentIndex)));
			// System.out.println("most important feature: "
			// +
			// features.m_attr_name.get(this.mostImportantWeightIndex(this.weights.get(this.currentIndex))));
			System.out.println("epochs: " + epochs);
		}

	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO: if outputting 1, then output 0, 1, or 2
		double net = this.evaluateNet(features, this.weights.get(this.currentIndex));
		// System.out.println(net);
		if (this.currentIndex > 0) {
			System.out.println("breakpoint");
		}
		if (net > THRESHOLD) {
			labels[0] = this.currentIndex;
		} else {
			labels[0] = 0;
		}
		// Utilities.outputArray(labels, true);
		// System.out.println("labels[0]: " + labels[0]);
		// labels[0] = this.currentIndex;
	}

}
