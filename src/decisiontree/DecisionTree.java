package decisiontree;

import java.util.HashMap;
import java.util.Map;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public class DecisionTree extends SupervisedLearner {

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		// go for number of attributes
		// calculate biggest entropy
		double[] labelsArray = this.translateLabelsToDoubleArray(labels);
		Map<Double, Integer> mapOfOuterEntropy = calculateSplit(labelsArray);
		Utilities.outputMap(mapOfOuterEntropy);
		for (int y = 0; y < features.row(0).length; y++) {
			System.out.println(y);
		}
		for (int i = 0; i < features.rows(); i++) {
			double[] feature = features.row(i);
			Utilities.outputArray("feature:", feature, false);
			double[] label = labels.row(i);
			Utilities.outputArray(" label:", label);
		}
		System.out.println("hello");
		System.out.println("log(8): " + Math.log(8));
		System.out.println("log(8)/log(2): " + logB2(8));
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
