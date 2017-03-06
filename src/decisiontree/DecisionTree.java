package decisiontree;

import java.util.HashMap;
import java.util.Map;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public class DecisionTree extends SupervisedLearner {

	private double calculateOuterEntropy(Map<Double, Integer> map) {
		int totalSize = 0;
		for (double key : map.keySet()) {
			totalSize += map.get(key);
		}
		double returnValue = 0;
		for (double key : map.keySet()) {
			double value = map.get(key);
			double temp = -1 * (value / totalSize) * logB2(value / totalSize);
			returnValue += temp;
		}
		return returnValue;
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		// go for number of attributes
		// calculate biggest entropy
		double[] labelsArray = this.translateLabelsToDoubleArray(labels);
		Map<Double, Integer> mapOfOuterEntropy = calculateSplit(labelsArray);
		Utilities.outputMap(mapOfOuterEntropy);
		double outerEntropy = this.calculateOuterEntropy(mapOfOuterEntropy);
		System.out.println("outerEntropy: " + outerEntropy);
	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTestSet(Matrix testFeatures, Matrix testLabels) throws Exception {
		// TODO Auto-generated method stub

	}

	private Map<Double, Integer> calculateSplit(double[] column) {
		Map<Double, Integer> map = new HashMap<>();
		for (int i = 0; i < column.length; i++) {
			double label = column[i];
			if (map.get(label) == null) {
				map.put(label, 1);
			} else {
				int newCount = map.get(label) + 1;
				map.put(label, newCount);
			}
		}
		return map;
	}

	private double[] translateLabelsToDoubleArray(Matrix labels) {
		double[] labelsArr = new double[labels.rows()];
		for (int i = 0; i < labels.rows(); i++) {
			labelsArr[i] = labels.row(i)[0];
		}
		return labelsArr;
	}

	private double logB2(double x) {
		return log(x) / log(2);
	}

	private double log(double x) {
		return Math.log(x);
	}
}
