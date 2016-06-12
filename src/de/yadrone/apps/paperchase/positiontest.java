package de.yadrone.apps.paperchase;

public class positiontest {
	public static void main(String[] args) {
		Positioning posTest = new Positioning();
		String[] str = {"W00.00", "W00.02"};
		double[] dd = {2000, 2000};
		posTest.calculatePosition(str, dd);
	}
}
