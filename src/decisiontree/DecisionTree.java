package decisiontree;

import java.util.HashMap;
import java.util.Map;

import toolkit.Matrix;
import toolkit.SupervisedLearner;
import utilities.Utilities;

public class DecisionTree extends SupervisedLearner {
	private DTNode decisionTree;
	private Matrix myFeatures;

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

	private void myTrain(Matrix features, Matrix labels, DTNode node) {
		// go for number of attributes
		// calculate biggest entropy
		double[] labelsArray = this.translateLabelsToDoubleArray(labels);
		Map<Double, Integer> mapOfOuterEntropy = calculateSplit(labelsArray);
		int mapOfOuterEntropyValueLength = this.calculateValueLength(mapOfOuterEntropy);
		double outerEntropy = this.calculateEntropy(mapOfOuterEntropy);
		double[] infoGains = new double[features.cols()];
		for (int x = 0; x < features.cols(); x++) {
			// Go through each column in features
			// TODO abstract this
			double[] column = features.col(x);
			// System.out.println("splitting on: " +
			// features.m_attr_name.get(x));
			infoGains[x] = outerEntropy;
			Map<Double, Integer> map = calculateSplit(column);
			// The outer map data structure splits it up like so:
			// {0.0=8, 1.0=8, 2.0=8}
			double[] individualEntropies = new double[map.keySet().size()];
			double[] fractions = new double[map.keySet().size()];
			int iter = 0;
			// System.out.println(outerEntropy + " ");
			for (double key : map.keySet()) {
				// find splits in the individual outputs
				// Now we go ahead and split the things even further.
				// if key == 0, you compare the key to the output. So we could
				// end up with {0.0=2, 1.0=2, 2.0=4 }

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
				// do the 3/4 * log(3/4) / log 2 ... etc. etc.
				individualEntropies[iter] = this.calculateEntropy(compareToOutput);
				double valueLength = this.calculateValueLength(compareToOutput);
				// System.out.println(" -" + (int) valueLength + "/" +
				// mapOfOuterEntropyValueLength + " * "
				// + individualEntropies[iter]);

				fractions[iter] = valueLength / mapOfOuterEntropyValueLength;
				infoGains[x] -= (fractions[iter] * individualEntropies[iter]);

				iter++;
			}
			// System.out.println(" = " + infoGains[x]);
			// System.out.println();
			// System.out.println();

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
			// System.out.println("done splitting");
			node.setLeafNode(true);

			node.setValue(labels.m_enum_to_str.get(0).get((int) labels.row(0)[0]));
			return;
		}
		// System.out.println("best info gain: " +
		// infoGains[bestInfoGainIndex]);
		// System.out.println("splitting on: " +
		// features.m_attr_name.get(bestInfoGainIndex));
		node.setValue(features.m_attr_name.get(bestInfoGainIndex));
		int attrNameIndex = features.m_attr_name.indexOf(node.getValue());
		Map<String, Integer> nodes = features.m_str_to_enum.get(attrNameIndex);
		for (String key : nodes.keySet()) {
			node.setNode(key, new DTNode());
		}

		// get new features and labels
		double[] bestInfoGainColumn = features.col(bestInfoGainIndex);
		Map<Double, Integer> bestInfoGainMap = this.calculateSplit(bestInfoGainColumn);
		// Utilities.outputMap(bestInfoGainMap);
		for (double key : bestInfoGainMap.keySet()) {
			String featureName = features.m_enum_to_str.get(attrNameIndex).get((int) key);
			Matrix newFeatures = new Matrix(features, 0, 0, features.rows(), features.cols());
			Matrix newLabels = new Matrix(labels, 0, 0, labels.rows(), labels.cols());
			for (int i = features.rows() - 1; i > -1; i--) {
				if (features.get(i, bestInfoGainIndex) != key) {

					try {
						newFeatures.removeRow(i);
						newLabels.removeRow(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// newFeatures.print();
			// newLabels.print();
			this.myTrain(newFeatures, newLabels, node.getNode(featureName));
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
		// System.out.println("train");
		decisionTree = new DTNode();
		myFeatures = new Matrix(features, 0, 0, features.rows(), features.cols());
		myTrain(features, labels, decisionTree);

		// this.setFeatures(features);
		// System.out.println(decisionTree);
	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		System.out.println("predict");
		Utilities.outputArray(features, false);
		System.out.println(" " + labels[0]);
		for (int i = 0; i < features.length; i++) {
			String attribute = myFeatures.m_attr_name.get(i);

			System.out.println("attribute: " + attribute);
			String feature = myFeatures.m_enum_to_str.get(i).get((int) features[i]);
			System.out.println("feature: " + feature);
			System.out.println();
			// String attribute = myFeatures.m_str_to_enum;
		}
		// TODO figure out testing ... do i put some features through and
		// compare my label?
	}

	@Override
	public void setTestSet(Matrix testFeatures, Matrix testLabels) throws Exception {
		// TODO do i need to do this one?
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
