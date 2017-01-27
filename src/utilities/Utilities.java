package utilities;

public class Utilities {
	static public void outputArray(double[] arr) {
		System.out.print("[ ");
		for (int i = 0; i < arr.length; i++) {
			System.out.print(i != arr.length - 1 ? arr[i] + ", " : arr[i]);
		}
		System.out.print(" ]");
		System.out.println();
	}

	static public void outputArray(String[] arr) {
		System.out.print("[ ");
		for (int i = 0; i < arr.length; i++) {
			System.out.print(i != arr.length - 1 ? arr[i] + ", " : arr[i]);
		}
		System.out.print(" ]");
		System.out.println();
	}

	static public void outputArray(String prepend, double[] arr) {
		System.out.print(prepend + " ");
		outputArray(arr);
	}

	static public void outputArray(String prepend, String[] arr) {
		System.out.print(prepend + " ");
		outputArray(arr);
	}
}
