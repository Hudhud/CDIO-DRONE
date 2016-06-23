package cdioProjekt.Gruppe14;

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

import cdioProjekt.Gruppe14.Commander.command;
import de.yadrone.base.video.ImageListener;

public class QRCodeScanner implements ImageListener
{
	private ArrayList<TagListener> listener = new ArrayList<TagListener>();

	private QRCodeScan qr = new QRCodeScan();
	byte[] pixel = new byte[16];
	private ArrayList<QRCode> qrCodes;
	private Commander commander;
	private BufferedImage qrImage;
	private StateController state; 
	private ArrayList<String> foundQR = new ArrayList<>();
	private CircleDetection circle;
	private boolean enabled = true;
	private DroneAI ai;
	private int timer = 0;

	public QRCodeScanner(Commander commander, StateController state, CircleDetection circle, DroneAI ai){
		this.commander = commander;
		this.state = state;
		this.circle = circle;
		circle.setScanner(this);
		this.ai = ai;

	}

	public void imageUpdated(BufferedImage image)
	{
		if(enabled){
			if(timer >= 10){
				commander.Search();
				timer = 0;
			}
			if(state.isReady()){
				findQRCodes(image);
				setQrImage(qr.getQrImage());

				ListIterator<QRCode> iterator = qrCodes.listIterator();
				while(iterator.hasNext()){

					//Her kan i få fat i QR koderne
					QRCode qrCode = iterator.next();

					if(isCircle(qrCode.getCode()) && !foundQR.contains(qrCode.getCode())){
						timer=0;

						double distanceAC = qrCode.getDistanceAC();
						double distanceBD = qrCode.getDistanceBD();
						double distanceAB = qrCode.getDistanceAB();
						Point[] corners = qrCode.getCorners();

						double distance = qrCode.getDistance();
						double centerX = distanceAB/2 + qrCode.getCorners()[0].x;
						double centerY = distanceAC/2+ qrCode.getCorners()[0].y;

						int margin = 1;
						int marginSpin = 5;
						double difference;
						if(distanceAC > distanceBD){
							difference = distanceAC - distanceBD;
						} else {
							difference = distanceBD - distanceAC;
						}

						System.out.println("difference = " + difference);

						int centrum = image.getWidth()/2;


						if(difference > margin && (centerX > centrum + marginSpin)){
							commander.newCommand(command.MoveRightQR);
						} else if(difference > margin && (centerX < centrum-marginSpin)){
							commander.newCommand(command.MoveLeftQR);
						} else if(centerX < centrum-marginSpin){
							commander.newCommand(command.SpinLeftQR);
						} else if(centerX > centrum+marginSpin){
							commander.newCommand(command.SpinRightQR);
						} else if(difference <= margin &&(centerX >= centrum-marginSpin && centerX <= centrum+marginSpin)){
							System.out.println("QR CENTERED");
							if(distance>2300){
								System.out.println("QR CENTERED BUT TOO FAR");
								commander.newCommand(command.CircleForward);
							} else if(distance < 1700)
								commander.newCommand(command.BackFromQR);
							else{
								commander.newCommand(command.UpToCircle);
								foundQR.add(qrCode.getCode());
								circle.setEnabled(true);
								enabled = false;
							}
						}

						double[] distances = new double[qrCodes.size()];
						String[] qrNames = new String[qrCodes.size()];

						for(int i = 0; iterator.hasNext(); i++){
							//Her kan i fï¿½ fat i QR koderne
							qrCode = iterator.next();

							qrNames[i] = qrCode.getCode();

							//hent data fra deres getMetoder
							distances[i] = qrCode.getDistance();

						}

					} else {
						System.out.println("QR ALREADY DETECTED");
					}	
				}
			}
		}
	}

	public void findQRCodes(BufferedImage image){
		pixel = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		Mat frame = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		frame.put(0, 0, pixel);		

		try {
			qrCodes = qr.findQRCodes(frame);
		} catch (Exception e) {
			e.printStackTrace();
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

	public boolean isCircle(String code){
		return code.startsWith("P");
	}

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}

	public int getTimer() {
		return timer;
	}

	public void incrementTimer() {
		timer = timer+1;
	}

	public void positionQR(BufferedImage image){
		findQRCodes(image);
		setQrImage(qr.getQrImage());

		ListIterator<QRCode> iterator = qrCodes.listIterator();
		while(iterator.hasNext()){

			//Her kan i få fat i QR koderne
			QRCode qrCode = iterator.next();

			if(isCircle(qrCode.getCode())){
				timer=0;

				double distanceAC = qrCode.getDistanceAC();
				double distanceBD = qrCode.getDistanceBD();
				double distanceAB = qrCode.getDistanceAB();

				double centerX = distanceAB/2 + qrCode.getCorners()[0].x;

				int margin = 1;
				int marginSpin = 5;
				double difference;
				if(distanceAC > distanceBD){
					difference = distanceAC - distanceBD;
				} else {
					difference = distanceBD - distanceAC;
				}

				System.out.println("difference = " + difference);

				int centrum = image.getWidth()/2;


				if(difference > margin && (centerX > centrum + marginSpin)){
					commander.newCommand(command.MoveRightQR);
				} else if(difference > margin && (centerX < centrum-marginSpin)){
					commander.newCommand(command.MoveLeftQR);
				}
			}
		}
	}
}