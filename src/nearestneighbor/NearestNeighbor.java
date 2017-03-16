package nearestneighbor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import toolkit.Matrix;
import toolkit.SupervisedLearner;

public class NearestNeighbor extends SupervisedLearner {
	private Random rand;
	final private int k = 15;

	public NearestNeighbor(Random rand) {
		this.rand = rand;
	}

	private double nonWeightedClassificationTraining(Matrix features, Matrix labels, double[] feature) {
		// If there was 10 0's and 10 1's, then this map would be
		// [ 0 => 10, 1 => 10 ]
		Map<Double, Integer> numberOfTimesOccurred = new HashMap<>();
		for (int i = 0; i < labels.rows(); i++) {
			double label = labels.row(i)[0];
			if (numberOfTimesOccurred.get(label) == null) {
				numberOfTimesOccurred.put(label, 1);
			} else {
				int count = numberOfTimesOccurred.get(label);
				numberOfTimesOccurred.put(label, count + 1);
			}
		}

		double[] results = new double[features.rows()];
		for (int i = 0; i < features.rows(); i++) {
			double[] row = features.row(i);
			double num = 0;
			for (int x = 0; x < row.length; x++) {
				num += Math.abs(row[x] - feature[x]);
			}
			results[i] = num;
		}
		double[] originalResults = new double[results.length];
		for (int i = 0; i < originalResults.length; i++) {
			originalResults[i] = results[i];
		}
		Arrays.sort(results);
		Map<Double, List<Double>> outputs = new HashMap<>();
		for (int i = 0; i < k; i++) {
			int index = indexOf(originalResults, results[i]);
			double label = labels.row(index)[0];
			if (outputs.get(label) == null) {
				List<Double> list = new ArrayList<>();
				list.add(results[i]);
				outputs.put(label, list);
			} else {
				List<Double> list = outputs.get(label);
				list.add(results[i]);
				outputs.put(label, list);
			}
		}
		double result = -1;
		int greatest = Integer.MIN_VALUE;
		for (Double key : outputs.keySet()) {
			List<Double> list = outputs.get(key);
			if (list.size() > greatest) {
				greatest = list.size();
				result = key;
			}
		}
		return result;
	}

	private int indexOf(double[] arr, double in) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == in) {
				return i;
			}
		}
		return -1;
	}

	private double weightedClassificationTraining(Matrix features, Matrix labels, double[] feature) {

		double[] results = new double[features.rows()];
		for (int i = 0; i < features.rows(); i++) {
			double[] row = features.row(i);
			double num = 0;
			for (int x = 0; x < row.length; x++) {
				num += Math.abs(row[x] - feature[x]);
			}
			results[i] = 1 / num;
		}

		Arrays.sort(results);
		Map<Double, Double> outputs = new HashMap<>();
		for (int i = 0; i < k; i++) {
			// if (outputs.get(results[i]) == null) {
			// outputs.put(results[i], results[i]);
			// } else {
			// List<Double> list = outputs.get(results[i]);
			// list.add(labels.row(i)[0]);
			// outputs.put(results[i], list);
			// }
		}
		// double result = -1;
		// int greatest = Integer.MIN_VALUE;
		// for (Double key : outputs.keySet()) {
		// List<Double> list = outputs.get(key);
		// if (list.size() > greatest) {
		// greatest = list.size();
		// result = key;
		// }
		// }
		// return result;
		return 0;
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		// TODO Auto-generated method stub
		myFeatures = new Matrix(features, 0, 0, features.rows(), features.cols());
		myLabels = new Matrix(labels, 0, 0, labels.rows(), labels.cols());
	}

	private Matrix myFeatures;
	private Matrix myLabels;

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub
		double output = this.nonWeightedClassificationTraining(myFeatures, myLabels, features);
		labels[0] = output;
	}

	@Override
	public void setTestSet(Matrix testFeatures, Matrix testLabels) throws Exception {
		// TODO Auto-generated method stub
		// i may or may not need this.

	}
}
