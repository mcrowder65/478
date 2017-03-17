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
	private Matrix myFeatures;
	private Matrix myLabels;
	final private int k = 9;

	public NearestNeighbor(Random rand) {
		this.rand = rand;
	}

	private double mostKeys(Map<Double, List<Double>> outputs) {
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
	private double nonWeightedClassificationTraining(Matrix features, Matrix labels, double[] feature) {

		double[] manhattanDistances = this.calculateManhattanDistances(features, feature);
		double[] originalResults = this.copyArray(manhattanDistances);
		Arrays.sort(manhattanDistances);
		Map<Double, List<Double>> outputs = this.calculateOutputs(originalResults, manhattanDistances, labels);

		return mostKeys(outputs);
	}

	private double weighted(Map<Double, List<Double>> outputs) {
		double result = -1;
		double greatest = Double.MIN_VALUE;
		for (Double key : outputs.keySet()) {
			List<Double> list = outputs.get(key);
			double num = 0;
			for (int i = 0; i < list.size(); i++) {

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
	private double weightedClassificationTraining(Matrix features, Matrix labels, double[] feature) {

		double[] manhattanDistances = this.calculateManhattanDistances(features, feature);
		double[] originalResults = this.copyArray(manhattanDistances);
		Arrays.sort(manhattanDistances);
		Map<Double, List<Double>> outputs = this.calculateOutputs(originalResults, manhattanDistances, labels);
		return weighted(outputs);

	}

	@SuppressWarnings("unused")
	private double nonWeightedRegressionTraining(Matrix features, Matrix labels, double[] feature) {
		double[] manhattanDistances = this.calculateManhattanDistances(features, feature);
		double[] originalResults = this.copyArray(manhattanDistances);
		Arrays.sort(manhattanDistances);

		Map<Double, List<Double>> outputs = this.calculateOutputs(originalResults, manhattanDistances, labels);
		double result = 0;

		for (double key : outputs.keySet()) {
			result += key;
		}

		return result / (double) k;
	}

	private double weightedRegressionTraining(Matrix features, Matrix labels, double[] feature) {
		double[] manhattanDistances = this.calculateManhattanDistances(features, feature);
		double[] originalResults = this.copyArray(manhattanDistances);
		Arrays.sort(manhattanDistances);

		Map<Double, List<Double>> outputs = this.calculateOutputs(originalResults, manhattanDistances, labels);
		return weighted(outputs);
	}

	private double continuousAndClassification(Matrix features, Matrix labels, double[] feature) {
		double[] heomDistances = this.calculateHEOM(features, feature);
		double[] originalResults = this.copyArray(heomDistances);
		Arrays.sort(heomDistances);
		Map<Double, List<Double>> outputs = this.calculateOutputs(originalResults, heomDistances, labels);
		return weighted(outputs);
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		double removePercent = 1;
		int featureRows = (int) (features.rows() * removePercent);
		int featureCols = (int) (features.cols() * removePercent);
		myFeatures = new Matrix(features, 0, 0, featureRows, featureCols);

		int labelRows = (int) (labels.rows() * removePercent);
		myLabels = new Matrix(labels, 0, 0, labelRows, labels.cols());
	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		double output = this.weightedClassificationTraining(myFeatures, myLabels, features);

		labels[0] = output;
	}

	@Override
	public void setTestSet(Matrix testFeatures, Matrix testLabels) throws Exception {
		// i may or may not need this.
	}

	private double[] copyArray(double[] arr) {
		double[] retArray = new double[arr.length];
		for (int i = 0; i < retArray.length; i++) {
			retArray[i] = arr[i];
		}
		return retArray;
	}

	private double[] calculateEuclideanDistnces(Matrix features, double[] feature) {
		double[] results = new double[features.rows()];
		for (int i = 0; i < features.rows(); i++) {
			double[] row = features.row(i);
			double num = 0;
			for (int x = 0; x < row.length; x++) {
				num += Math.sqrt(Math.pow(feature[x] - row[x], 2));
			}
			results[i] = num;
		}
		return results;
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

	private double[] calculateHEOM(Matrix features, double[] feature) {
		double[] results = new double[features.rows()];
		for (int i = 0; i < features.rows(); i++) {
			double[] row = features.row(i);
			double num = 0;
			for (int x = 0; x < row.length; x++) {
				if (row[x] == Double.MAX_VALUE) {
					// unknown
					num += 1;
				} else if (features.m_enum_to_str.get(x).size() == 0) {
					// categorical/nominal
					num += (feature[x] == row[x]) ? 0 : 1;
				} else {
					// continuous
					num += Math.pow(Math.abs(feature[x] - row[x]), 2);
				}
			}
			results[i] = Math.sqrt(num);
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
