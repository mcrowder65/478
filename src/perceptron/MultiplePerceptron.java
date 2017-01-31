package perceptron;

import java.util.Random;

import toolkit.Matrix;

public class MultiplePerceptron extends Perceptron {
	private Random rand;
	private final static int BIAS = 1;

	private final static double THRESHOLD = 0;

	private double[] myWeights;
	private final static int MAX_ITERATIONS = 5;

	private final static double LEARNING_RATE = 0.1;

	public MultiplePerceptron(Random rand) {
		this.rand = rand;
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("multiple perceptron train!");

	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("multiple perceptron predict!");

	}

}
