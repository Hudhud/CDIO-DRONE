package de.yadrone.apps.paperchase;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

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
	private ArrayList<QRCode> qrCodes;
	private DroneCommander commander;
	private BufferedImage qrImage;
	
	
	public QRCodeScanner(DroneCommander commander){
		this.commander = commander;
	}

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
		setQrImage(qr.getQrImage());
		
		ListIterator<QRCode> iterator = qrCodes.listIterator();
		qrCodes.size();
		int i = 0;
		while(iterator.hasNext()){
			
			//Her kan i få fat i QR koderne
			QRCode qrCode = iterator.next();
			
			
			//setQrImage(qrCode.getQRimage());
			double distanceAC = qrCode.getDistanceAC();
			double distanceBD = qrCode.getDistanceBD();
			double distanceAB = qrCode.getDistanceAB();
			Point[] corners = qrCode.getCorners();
			
			double centerX = distanceAB/2 + qrCode.getCorners()[0].x;
			double centerY = distanceAC/2+ qrCode.getCorners()[0].y;
			
			int margin = 5;
			double difference;
			if(distanceAC > distanceBD){
				difference = distanceAC - distanceBD;
			} else {
				difference = distanceBD - distanceAC;
			}

			if(difference < margin){
				int marginSpin = margin*5;
				//CENTERED
				System.out.println("QR CENTERED");
				if(centerY < image.getHeight()/2-marginSpin) {
					System.out.println("UP");
				} else if(centerY > image.getHeight()/2+marginSpin) {
					System.out.println("DOWN");
				}
				if(centerX < image.getWidth()/2-marginSpin) {
					commander.SpinLeftQR();
				} else if(centerX > image.getWidth()/2+marginSpin) {
					commander.SpinRightQR();
				}
			} 
			
			else if(distanceAC+margin > distanceBD){
				//QR LEFT
				commander.MoveRightQR();
			}
			else if(distanceBD+margin > distanceAC){
				//QR RIGHT
				commander.MoveLeftQR();
			}
			
			

		}

	}

	private void findQRCodes(BufferedImage image){
		pixel = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		Mat frame = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		frame.put(0, 0, pixel);		

		try {
			qrCodes = qr.findQRCodes(frame);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readMultiple(BinaryBitmap bitmap){
		QRCodeMultiReader multiReader = new QRCodeMultiReader();

		double theta = Double.NaN;
		try
		{
			//			System.out.println("STARTING TO READ QR");
			multiScanResult = multiReader.decodeMultiple(bitmap);
			double[] thetas = new double[multiScanResult.length];

			for(int i = 0; i < multiScanResult.length; i++){

				ResultPoint[] points = multiScanResult[i].getResultPoints();
				ResultPoint a = points[1]; // top-left
				ResultPoint b = points[2]; // top-right
				ResultPoint c = points[3];//bottom left

				System.out.println("TOP LEFT X = " + a.getX() + " TOP LEFT Y = " + a.getY());
				//			System.out.println("TOP RIGHT X = " + b.getX() + " TOP RIGHT Y = " + b.getY());
				System.out.println("BOTTOM LEFT X = " + c.getX() + " BOTTOM LEFT Y = " + c.getY());
				// Find the degree of the rotation (needed e.g. for auto control)
				System.out.println("DISTANCE BETWEEN TOP LEFT AND TOP RIGHT = " + ResultPoint.distance(a, c));
				float pixel = ResultPoint.distance(a, c);
				double distanceToObject = 4.45 * 150 * 360 / (pixel * 3.17);
				System.out.println("DISTANCE IN MM = " + distanceToObject);
				double z = Math.abs(a.getX() - c.getX());
				double x = Math.abs(a.getY() - c.getY());
				thetas[i] = Math.atan(x / z); // degree in rad (+- PI/2)

				thetas[i] = thetas[i] * (180 / Math.PI); // convert to degree

				if ((b.getX() < a.getX()) && (b.getY() > a.getY()))
				{ // code turned more than 90ï¿½ clockwise
					thetas[i] = 180 - thetas[i];
				}
				else if ((b.getX() < a.getX()) && (b.getY() < a.getY()))
				{ // code turned more than 180ï¿½ clockwise
					thetas[i] = 180 + thetas[i];
				}
				else if ((b.getX() > a.getX()) && (b.getY() < a.getY()))
				{ // code turned more than 270 clockwise
					thetas[i] = 360 - thetas[i];
				}
			}
		}
		catch (ReaderException e) 
		{
			// no code found.
			multiScanResult = null;
		}

		// inform all listener
		for (int i=0; i < listener.size(); i++)
		{
		//	listener.get(i).onTags(multiScanResult, (float)theta);
		}

		if(multiScanResult.length >= 3) {
		//	positioning.calculatePosition(multiScanResult);;
		} else if(multiScanResult.length >= 2) {
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
			{ // code turned more than 90ï¿½ clockwise
				theta = 180 - theta;
			}
			else if ((b.getX() < a.getX()) && (b.getY() < a.getY()))
			{ // code turned more than 180ï¿½ clockwise
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

	public BufferedImage getQrImage() {
		return qrImage;
	}

	public void setQrImage(BufferedImage qrImage) {
		this.qrImage = qrImage;
	}
	
	
}
