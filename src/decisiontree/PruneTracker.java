package decisiontree;

public class PruneTracker {
	private DTNode node;
	private double accuracy;

	public PruneTracker(DTNode node, double accuracy) {
		this.node = new DTNode(node);
		this.accuracy = accuracy;
	}

	public DTNode getNode() {
		return node;
	}

	public void setNode(DTNode node) {
		this.node = node;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	@Override
	public String toString() {
		return "PruneTracker [node=" + node + ", accuracy=" + accuracy + "]";
	}

}
