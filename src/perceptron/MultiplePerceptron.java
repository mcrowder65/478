package perceptron;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import toolkit.Matrix;

public class MultiplePerceptron extends Perceptron {
	private Random rand;
	private final static int BIAS = 1;

	private final static double THRESHOLD = 0;

	private final static int MAX_ITERATIONS = 5;

	private final static double LEARNING_RATE = 0.1;
	private List<Matrix> matrices;
	private List<double[]> weights;

	public MultiplePerceptron(Random rand) {
		this.rand = rand;
		matrices = new ArrayList<>();
		weights = new ArrayList<>();
	}

	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("multiple perceptron train!");
		// should always be 3 for our purposes.
		int size = labels.m_enum_to_str.get(0).size();

		for (int i = 0; i < size; i++) {
			System.out.println(i);
		}

	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("multiple perceptron predict!");

	}

}
