package de.yadrone.apps.paperchase;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Positioning {

	private HashMap<String, Double[]> values;
	
	public Positioning() {
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
	
	public int[] calculatePosition(String[] qrNames, double[] distances) {
		ArrayList<Double[]> qrCoordinates = new ArrayList<>();
		for(int i = 0; i < distances.length; i++)
			distances[i]=distances[i]/10;
		int[] position = new int[2];
		for(String i: qrNames) {
			qrCoordinates.add(values.get(i));
		}
		Double[] p1 = qrCoordinates.get(0);
		Double[] p2 = qrCoordinates.get(1);
		
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
		
		System.out.println(pfinal1[0] + ", " + pfinal1[1]);
		System.out.println(pfinal2[0] + ", " + pfinal2[1]);
		
		if(pfinal1[0] >= 0 && pfinal1[0] <= 1000 && pfinal1[1] >= 0 && pfinal1[1] <= 1100) {
			position[0] = (int)pfinal1[0];
			position[1] = (int)pfinal1[1];
		} else {
			position[0] = (int)pfinal2[0];
			position[1] = (int)pfinal2[1];
		}
		System.out.println(position[0] + ", " + position[1]);
		return position;
	}
}
