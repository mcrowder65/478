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
	@SuppressWarnings("unused")
	private Random rand;
	final private int k = 15;

	public NearestNeighbor(Random rand) {
		this.rand = rand;
	}

	@SuppressWarnings("unused")
	private double nonWeightedClassificationTraining(Matrix features, Matrix labels, double[] feature) {

		double[] manhattanDistances = this.calculateManhattanDistances(features, feature);
		double[] originalResults = this.copyArray(manhattanDistances);
		Arrays.sort(manhattanDistances);
		Map<Double, List<Double>> outputs = this.calculateOutputs(originalResults, manhattanDistances, labels);

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

	@SuppressWarnings("unused")
	private double weightedClassificationTraining(Matrix features, Matrix labels, double[] feature) {

		double[] manhattanDistances = this.calculateManhattanDistances(features, feature);
		double[] originalResults = this.copyArray(manhattanDistances);
		Arrays.sort(manhattanDistances);
		Map<Double, List<Double>> outputs = this.calculateOutputs(originalResults, manhattanDistances, labels);
		double result = -1;
		double greatest = Double.MIN_VALUE;
		for (Double key : outputs.keySet()) {
			List<Double> list = outputs.get(key);
			double num = 0;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) == 0) {
					double temp = 1 / list.get(i);
					// maybe do small distance or just return label
					System.out.println(temp);
				}
				num += (1 / Math.pow(list.get(i), 2));
			}
			if (num > greatest) {
				greatest = num;
				result = key;
			}
		}
		return result;

	}

	@SuppressWarnings("unused")
	private double nonWeightedRegressionTraining(Matrix features, Matrix labels, double[] feature) {
		double[] manhattanDistances = this.calculateManhattanDistances(features, feature);
		double[] originalResults = this.copyArray(manhattanDistances);
		return 0.0;
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		myFeatures = new Matrix(features, 0, 0, features.rows(), features.cols());
		myLabels = new Matrix(labels, 0, 0, labels.rows(), labels.cols());
	}

	private Matrix myFeatures;
	private Matrix myLabels;

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		double output = this.weightedClassificationTraining(myFeatures, myLabels, features);
		labels[0] = output;
	}

	@Override
	public void setTestSet(Matrix testFeatures, Matrix testLabels) throws Exception {
		// i may or may not need this.

	}

	private double[] copyArray(double[] manhattanDistances) {
		double[] retArray = new double[manhattanDistances.length];
		for (int i = 0; i < retArray.length; i++) {
			retArray[i] = manhattanDistances[i];
		}
		return retArray;
	}

	private double[] calculateManhattanDistances(Matrix features, double[] feature) {
		double[] results = new double[features.rows()];
		for (int i = 0; i < features.rows(); i++) {
			double[] row = features.row(i);
			double num = 0;
			for (int x = 0; x < row.length; x++) {
				num += Math.abs(row[x] - feature[x]);
			}
			results[i] = num;
		}
		return results;
	}

	private Map<Double, List<Double>> calculateOutputs(double[] originalResults, double[] manhattanDistances,
			Matrix labels) {
		Map<Double, List<Double>> outputs = new HashMap<>();
		for (int i = 0; i < k; i++) {
			int index = indexOf(originalResults, manhattanDistances[i]);
			double label = labels.row(index)[0];
			if (outputs.get(label) == null) {
				List<Double> list = new ArrayList<>();
				list.add(manhattanDistances[i]);
				outputs.put(label, list);
			} else {
				List<Double> list = outputs.get(label);
				list.add(manhattanDistances[i]);
				outputs.put(label, list);
			}
		}
		return outputs;
	}

	private int indexOf(double[] arr, double in) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == in) {
				return i;
			}
		}
		return -1;
	}

}
