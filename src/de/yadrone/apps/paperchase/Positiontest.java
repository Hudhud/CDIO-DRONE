package de.yadrone.apps.paperchase;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

public class Positiontest {
	public static void main(String[] args) {
		Positioning posTest = new Positioning();
		String[] str = {"W00.00", "W00.02"};
		double[] dd = {2000, 2000};
		ResultPoint rp1 = new ResultPoint(200, 200);
		ResultPoint rp2 = new ResultPoint(500, 200);
		ResultPoint rp3 = new ResultPoint(300, 200);
		ResultPoint[] rpa1 = {rp1, rp1};
		ResultPoint[] rpa2 = {rp2, rp2};
		ResultPoint[] rpa3 = {rp3, rp3};
		Result r1 = new Result("W00.00", null, rpa1, BarcodeFormat.QR_CODE);
		Result r2 = new Result("W00.01", null, rpa2, BarcodeFormat.QR_CODE);
		Result r3 = new Result("W00.02", null, rpa3, BarcodeFormat.QR_CODE);
		Result[] ra = {r1, r2, r3};
		posTest.calculatePosition(ra);
	}
}
