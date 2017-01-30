package utilities;

import java.util.Random;

import org.junit.Test;

public class UtilitiesTest {

	@Test
	public void testRandomDouble() {
		Random rand = new Random();
		double start = -0.5;
		double end = 0.5;
		for (int i = 0; i < 10000000; i++) {
			double randomNumber = Utilities.randomDouble(rand, start, end);
			assert (randomNumber >= start && randomNumber <= end);
		}
	}

}
