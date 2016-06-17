package de.yadrone.apps.paperchase;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.google.zxing.Result;

public class Positioning {

	private static HashMap<String, Double[]> values;
	private static int[] position = new int[2];
	private static final int GRID_WIDTH = 10;
	private static final int GRID_HEIGHT = 11;
	private static int[] positionInGrid = new int[2];
	private static final int WIDTH = 640;
	private static final int HEIGHT = 360;
	private static final double DIAGONAL_SIZE = Math.hypot(WIDTH, HEIGHT);
	private static final int CAMERA_ANGLE = 92;

	public Positioning() {
		if(values == null) {
			values = new HashMap<>(); 

			String filepath = "src/de/yadrone/apps/paperchase/WallCoordinates.csv";
			File file = new File(filepath);
			try {
				Scanner in = new Scanner(file);
				in.nextLine();
				while(in.hasNextLine()) {
					String[] str = in.nextLine().split(";");
					Double[] s0 = {Double.parseDouble(str[1]), Double.parseDouble(str[2])};
					values.put(str[0], s0);
				}
				in.close();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int[] calculatePosition(ArrayList<QRCode> codes) {
		String[] codeNames = new String[codes.size()];
		double[] distances = new double[codes.size()];
		for(int i = 0; i < codeNames.length; i++) {
			codeNames[i] = codes.get(i).getCode();
			distances[i] = codes.get(i).getDistance();
		}
		return calculatePosition(codeNames, distances);
	}

	public int[] calculatePosition(String[] qrNames, double[] distances) {
		ArrayList<Double[]> qrCoordinates = new ArrayList<>();
		for(int i = 0; i < distances.length; i++)
			distances[i]=distances[i]/10;
		int[] position = new int[2];
		for(String i: qrNames) {
			qrCoordinates.add(values.get(i));
		}
		Double[] interm1 = qrCoordinates.get(0);
		Double[] interm2 = qrCoordinates.get(1);
		
		double[] p1 = new double[interm1.length];
		double[] p2 = new double[interm2.length];
		
		for(int i = 0; i < interm1.length; i++) {
			p1[i] = interm1[i];
			p2[i] = interm2[i];
		}

		double[] coords = calculateCircleIntersection(p1, p2, distances);
		
//		double d = Math.hypot(p1[0]-p2[0], p1[1]-p2[1]);
//		double a = (Math.pow(distances[0], 2)
//				- Math.pow(distances[1], 2) + Math.pow(d, 2))/(2*d); 
//		double h = Math.sqrt(Math.pow(distances[0], 2)-Math.pow(a, 2));
//
//		double[] px = {p1[0] + a*(p2[0]-p1[0])/d, p1[1] + a*(p2[1]-p1[1])/d}; 
//		double[] pfinal1 = new double[2];
//		double[] pfinal2 = new double[2];
//		pfinal1[0] = px[0]+h*(p2[1]-p1[1])/d;
//		pfinal2[0] = px[0]-h*(p2[1]-p1[1])/d;
//		pfinal1[1] = px[1]-h*(p2[0]-p1[0])/d;
//		pfinal2[1] = px[1]+h*(p2[0]-p1[0])/d;
//
//		System.out.println(pfinal1[0] + ", " + pfinal1[1]);
//		System.out.println(pfinal2[0] + ", " + pfinal2[1]);
//
//		if(pfinal1[0] >= 0 && pfinal1[0] <= 1000 && pfinal1[1] >= 0 && pfinal1[1] <= 1100) {
//			position[0] = (int)pfinal1[0];
//			position[1] = (int)pfinal1[1];
//		} else {
//			position[0] = (int)pfinal2[0];
//			position[1] = (int)pfinal2[1];
//		}
		
		for(int i = 0; i < coords.length; i++) {
			position[i] = (int)coords[i];
		}
		
		positionInGrid[position[1]/100] = position[2]/100;
		
		System.out.println("POSITION (X,Y): " +position[0] + ", " + position[1]);
		return position;
	}

	

	public void calculatePosition(Result[] scanResults) {
		double[] angles = new double[scanResults.length-1];
		double[] distanceBetweenQRs = new double[angles.length]; 
		double[] angleBetweenQRs = new double[angles.length];
		for(int i = 0; i < scanResults.length-1; i++) {
			double distanceBetweenCodes =
					Math.hypot(scanResults[i].getResultPoints()[0].getX()
					-scanResults[i+1].getResultPoints()[0].getX(),
					scanResults[i].getResultPoints()[0].getY()
					-scanResults[i+1].getResultPoints()[0].getY());
			angles[i] = distanceBetweenCodes*CAMERA_ANGLE/DIAGONAL_SIZE;
			Double[] p1 = values.get(scanResults[i].getText());
			Double[] p2 = values.get(scanResults[i+1].getText());
			distanceBetweenQRs[i] = Math.hypot(p2[0]-p1[0], p2[1]-p1[1]);
		}
		System.out.println(angles[0]);
		
		double[][] centers = new double[angles.length][2];
		
		for(int i = 0; i < angles.length; i++) {
			double qrAngles = (180-angles[i])/2;
			double lengthToCenter = distanceBetweenQRs[i]
					*Math.sin(qrAngles)/Math.sin(angles[i]);
			
		}
	}

	private double[] calculateCircleIntersection(double[] p1, double[] p2, double[] distances) {
		double d = Math.hypot(p1[0]-p2[0], p1[1]-p2[1]);
		double a = (Math.pow(distances[0], 2)
				- Math.pow(distances[1], 2) + Math.pow(d, 2))/(2*d); 
		double h = Math.sqrt(Math.pow(distances[0], 2)-Math.pow(a, 2));

		double[] px = {p1[0] + a*(p2[0]-p1[0])/d, p1[1] + a*(p2[1]-p1[1])/d}; 
		double[] pfinal1 = new double[2];
		double[] pfinal2 = new double[2];
		pfinal1[0] = px[0]+h*(p2[1]-p1[1])/d;
		pfinal2[0] = px[0]-h*(p2[1]-p1[1])/d;
		pfinal1[1] = px[1]-h*(p2[0]-p1[0])/d;
		pfinal2[1] = px[1]+h*(p2[0]-p1[0])/d;

		double[] coords = new double[2];
		
		if(pfinal1[0] >= 0 && pfinal1[0] <= 1000 && pfinal1[1] >= 0 && pfinal1[1] <= 1100) {
			coords[0] = pfinal1[0];
			coords[1] = pfinal1[1];
		} else {
			coords[0] = pfinal2[0];
			coords[1] = pfinal2[1];
		}
		return coords;
	}
	
	public int[] getPosition() {
		return position;
	}
}
