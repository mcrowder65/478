package decisiontree;

import java.util.HashMap;
import java.util.Map;

import toolkit.Matrix;
import toolkit.SupervisedLearner;

public class DecisionTree extends SupervisedLearner {

	private double calculateEntropy(Map<Double, Integer> map) {
		int totalSize = this.calculateValueLength(map);
		double returnValue = 0;
		for (double key : map.keySet()) {
			double value = map.get(key);
			double temp = -1 * (value / totalSize) * logB2(value / totalSize);
			returnValue += temp;
		}
		return returnValue;
	}

	private void myTrain(Matrix features, Matrix labels) {
		// go for number of attributes
		// calculate biggest entropy
		double[] labelsArray = this.translateLabelsToDoubleArray(labels);
		Map<Double, Integer> mapOfOuterEntropy = calculateSplit(labelsArray);
		int mapOfOuterEntropyValueLength = this.calculateValueLength(mapOfOuterEntropy);
		// Utilities.outputMap(mapOfOuterEntropy);
		double outerEntropy = this.calculateEntropy(mapOfOuterEntropy);
		double[] infoGains = new double[features.cols()];
		for (int x = 0; x < features.cols(); x++) {
			double[] column = features.col(x);
			infoGains[x] = outerEntropy;
			Map<Double, Integer> map = calculateSplit(column);
			// Utilities.outputMap(map);
			double[] individualEntropies = new double[map.keySet().size()];
			double[] fractions = new double[map.keySet().size()];
			int iter = 0;
			for (double key : map.keySet()) {
				Map<Double, Integer> compareToOutput = new HashMap<>();
				for (int i = 0; i < column.length; i++) {

					if (column[i] == key) {
						double label = labels.row(i)[0];
						if (compareToOutput.get(label) == null) {
							compareToOutput.put(label, 1);
						} else {
							int newCount = compareToOutput.get(label) + 1;
							compareToOutput.put(label, newCount);
						}
					}
				}
				// Utilities.outputMap(compareToOutput);
				individualEntropies[iter] = this.calculateEntropy(compareToOutput);
				double valueLength = this.calculateValueLength(compareToOutput);
				fractions[iter] = valueLength / mapOfOuterEntropyValueLength;
				infoGains[x] -= (fractions[iter] * individualEntropies[iter]);
				iter++;
			}
		}
		int bestInfoGainIndex = -1;
		double MAX_INFO_GAIN = 0;
		for (int i = 0; i < infoGains.length; i++) {
			if (infoGains[i] > MAX_INFO_GAIN) {
				MAX_INFO_GAIN = infoGains[i];
				bestInfoGainIndex = i;
			}
		}
		if (bestInfoGainIndex == -1) {
			System.out.println("done splitting");
			return;
		}
		System.out.println("best info gain: " + infoGains[bestInfoGainIndex]);
		System.out.println("splitting on: " + features.m_attr_name.get(bestInfoGainIndex));
		double[] bestInfoGainColumn = features.col(bestInfoGainIndex);
		Map<Double, Integer> bestInfoGainMap = this.calculateSplit(bestInfoGainColumn);
		// Utilities.outputMap(bestInfoGainMap);
		for (double key : bestInfoGainMap.keySet()) {
			int value = bestInfoGainMap.get(key);
			Matrix newFeatures = new Matrix(features, 0, 0, features.rows(), features.cols());
			Matrix newLabels = new Matrix(labels, 0, 0, labels.rows(), labels.cols());
			for (int i = features.rows() - 1; i > -1; i--) {
				if (features.get(i, bestInfoGainIndex) != key) {

					try {
						// TODO figure out what 4 is
						// newFeatures.add(features, i, bestInfoGainIndex, 1);
						newFeatures.removeRow(i);
						newLabels.removeRow(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			newFeatures.print();
			newLabels.print();
			this.myTrain(newFeatures, newLabels);
		}
	}

	private int calculateValueLength(Map<Double, Integer> map) {
		int returnValue = 0;
		for (double key : map.keySet()) {
			returnValue += map.get(key);
		}
		return returnValue;
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		myTrain(features, labels);
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
