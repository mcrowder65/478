package utilities;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

public class Utilities {
	static public void outputArray(double[] arr, boolean outputNewLine) {
		System.out.print("[ ");
		for (int i = 0; i < arr.length; i++) {
			System.out.print(i != arr.length - 1 ? Utilities.round(arr[i]) + ", " : Utilities.round(arr[i]));
		}
		System.out.print(" ]");
		if (outputNewLine) {
			System.out.println();
		}

	}

	@SuppressWarnings("rawtypes")
	static public void outputArrayList(ArrayList list) throws Exception {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof TreeMap) {
				outputMap((TreeMap) list.get(i));
			} else if (list.get(i) instanceof double[]) {
				outputArray((double[]) list.get(i), true);
			} else {
				throw new Exception("This type is not supported");
			}

		}
	}

	@SuppressWarnings("rawtypes")
	static private void outputMap(TreeMap map) {
		for (Object i : map.keySet()) {
			System.out.println(i + ": " + map.get(i));
		}
	}

	static public void outputArray(String[] arr, boolean outputNewLine) {
		System.out.print("[ ");
		for (int i = 0; i < arr.length; i++) {
			System.out.print(i != arr.length - 1 ? arr[i] + ", " : arr[i]);
		}
		System.out.print(" ]");
		if (outputNewLine) {
			System.out.println();
		}
	}

	static public void outputArray(String prepend, double[] arr, boolean outputNewLine) {
		System.out.print(prepend + " ");
		outputArray(arr, outputNewLine);
	}

	static public void outputArray(String prepend, String[] arr, boolean outputNewLine) {
		System.out.print(prepend + " ");
		outputArray(arr, outputNewLine);
	}

	static public double randomDouble(Random rand, double start, double end) {
		double random = rand.nextDouble();
		return start + (random * (end - start));
	}

	static public double round(double dub) {
		return Math.round(dub * 100.0) / 100.0;
	}
}
