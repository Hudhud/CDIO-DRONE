package de.yadrone.apps.paperchase;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.google.zxing.qrcode.QRCodeReader;

import de.yadrone.base.video.ImageListener;

public class QRCodeScanner implements ImageListener
{
	private ArrayList<TagListener> listener = new ArrayList<TagListener>();
	
	private Result scanResult;
	private Result[] multiScanResult;
	
	private long imageCount = 0;
	private QRCodeScan qr = new QRCodeScan();
	byte[] pixel = new byte[16];
	
	public void imageUpdated(BufferedImage image)
	{
		if ((++imageCount % 2) == 0){
			return;
			}
		
		// try to detect QR code
//		LuminanceSource source = new BufferedImageLuminanceSource(image);
//		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		//readMultiple(bitmap);
		findQRCodes(image);
	}
	
	private void findQRCodes(BufferedImage image){
		pixel = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		Mat frame = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		frame.put(0, 0, pixel);		

		try {
			qr.findQRCodes(frame);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void readMultiple(BinaryBitmap bitmap){
		QRCodeMultiReader multiReader = new QRCodeMultiReader();

		//double theta = Double.NaN;
		try
		{
			//System.out.println("STARTING TO READ QR");
			multiScanResult = multiReader.decodeMultiple(bitmap);
			double[] thetas = new double[multiScanResult.length];
					
			for(int i = 0; i < multiScanResult.length; i++){
			
			ResultPoint[] points = multiScanResult[i].getResultPoints();
			ResultPoint a = points[1]; // top-left
			ResultPoint b = points[2]; // top-right
			ResultPoint c = points[0]; //bottom-left
			
			System.out.println("TOP LEFT X = " + a.getX() + " TOP LEFT Y = " + a.getY());
			System.out.println("TOP RIGHT X = " + b.getX() + " TOP RIGHT Y = " + b.getY());
			System.out.println("BOTTOM LEFT X = " + c.getX() + " BOTTOM LEFT Y = " + c.getY());
			
			//System.out.println("BOTTOM LEFT X = " + c.getX() + " BOTTOM LEFT Y = " + c.getY());
			// Find the degree of the rotation (needed e.g. for auto control)
			System.out.println("DISTANCE BETWEEN TOP LEFT AND TOP RIGHT = " + ResultPoint.distance(a, b));
			float distanceAB = ResultPoint.distance(a, b);
			float distanceAC = ResultPoint.distance(a, c);
			float distanceBC = ResultPoint.distance(b, c);
			double distanceToObject = 4.45 * 150 * 720 / (distanceAC * 3.17);
//			double distanceToA = 4.45 * 150 * 360 / (distanceAC * 3.17);
//			double distanceToB = 4.45 * 150 * 360 / (distanceBC * 3.17);
			System.out.println("DISTANCE IN MM = " + distanceToObject);
			System.out.println("DISTANCE AB PIXEL" + distanceAB);
			System.out.println("DISTANCE AC PIXEL = " + distanceAC);
			System.out.println("DISTANCE BC PIXEL = " + distanceBC);
			
			int margin = 5;
			float difference;
			if(distanceAB > distanceAC){
				difference = distanceAB - distanceAC;
				System.out.println();
			} else {
				difference = distanceAC - distanceAB;
			}
			
			if(difference <= margin){
				//centered
			}
			
			//if(difference)
			
			if(distanceAB+margin >= distanceAC && distanceAC+margin >= distanceAB){
				//centered
				
				System.out.println("QR CENTERED");
			}
			
			if(distanceAC+margin > distanceAB){
				//left
				System.out.println("QR LEFT");
			}
			
			if(distanceAC+margin < distanceAB){
				//right
				System.out.println("QR RIGHT");
			}
			
			double z = Math.abs(a.getX() - b.getX());
			double x = Math.abs(a.getY() - b.getY());
			thetas[i] = Math.atan(x / z); // degree in rad (+- PI/2)

			thetas[i] = thetas[i] * (180 / Math.PI); // convert to degree

			if ((b.getX() < a.getX()) && (b.getY() > a.getY()))
			{ // code turned more than 90° clockwise
				thetas[i] = 180 - thetas[i];
			}
			else if ((b.getX() < a.getX()) && (b.getY() < a.getY()))
			{ // code turned more than 180° clockwise
				thetas[i] = 180 + thetas[i];
			}
			else if ((b.getX() > a.getX()) && (b.getY() < a.getY()))
			{ // code turned more than 270 clockwise
				thetas[i] = 360 - thetas[i];
			}
			}
			
			// inform all listener
			for (int i=0; i < listener.size(); i++)
			{
				listener.get(i).onTags(multiScanResult, thetas);
			}
			
		}
		catch (ReaderException e) 
		{
			// no code found.
			multiScanResult = null;
		}
			
		
	}
	
	private void readSingle(BinaryBitmap bitmap){
		QRCodeReader reader = new QRCodeReader();
		
		double theta = Double.NaN;
		try
		{
			scanResult = reader.decode(bitmap);
			
			ResultPoint[] points = scanResult.getResultPoints();
			ResultPoint a = points[1]; // top-left
			ResultPoint b = points[2]; // top-right
			
			// Find the degree of the rotation (needed e.g. for auto control)

			double z = Math.abs(a.getX() - b.getX());
			double x = Math.abs(a.getY() - b.getY());
			theta = Math.atan(x / z); // degree in rad (+- PI/2)

			theta = theta * (180 / Math.PI); // convert to degree

			if ((b.getX() < a.getX()) && (b.getY() > a.getY()))
			{ // code turned more than 90° clockwise
				theta = 180 - theta;
			}
			else if ((b.getX() < a.getX()) && (b.getY() < a.getY()))
			{ // code turned more than 180° clockwise
				theta = 180 + theta;
			}
			else if ((b.getX() > a.getX()) && (b.getY() < a.getY()))
			{ // code turned more than 270 clockwise
				theta = 360 - theta;
			}
		} 
		catch (ReaderException e) 
		{
			// no code found.
			scanResult = null;
		}
		
		// inform all listener
		for (int i=0; i < listener.size(); i++)
		{
			listener.get(i).onTag(scanResult, (float)theta);
		}
	}
	
	
		
	public void addListener(TagListener listener)
	{
		this.listener.add(listener);
	}
	
	public void removeListener(TagListener listener)
	{
		this.listener.remove(listener);
	}
}
