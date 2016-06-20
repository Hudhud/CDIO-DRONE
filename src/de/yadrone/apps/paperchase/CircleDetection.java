package de.yadrone.apps.paperchase;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.ControlState;
import de.yadrone.base.navdata.DroneState;
import de.yadrone.base.navdata.StateListener;
import de.yadrone.base.video.ImageListener;
import de.yadrone.apps.paperchase.PaperChaseGUI;
import de.yadrone.apps.paperchase.Commander.command;

public class CircleDetection implements ImageListener{

	Point pt;
	double distanceToObject;
	boolean forward = false;
	int time;
	private long imageCount = 0;
	PaperChaseGUI gui;
	byte[] pixel = new byte[16];
	int margin;
	Mat frame;
	boolean go = false;
	boolean ready = false;
	StateController state;
	int sleep = 40;
	private boolean enabled = false;
	private int counter = 0;
	int closestDistance = 9999;
	private QRCodeScan qr = new QRCodeScan();
	
	private Commander commander;
	private QRCodeScanner scanner;

	public CircleDetection( StateController state, Commander commander){
		super();

		this.state = state;
		this.commander = commander;
	}
	
	public void imageUpdated(BufferedImage image)
	{
		counter++;
		if(!(counter%3 == 0))
			return;
		if(enabled){
			if(state.isReady()){
				pixel = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
				Mat frame = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
				Mat gray = new Mat();
				frame.put(0, 0, pixel);		

				Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY );
//				Imgproc.blur(gray, gray, new Size(5,5));
				//	Imgproc.equalizeHist(gray, gray);
				Mat circles = new Mat();
				Imgproc.HoughCircles(gray, circles, Imgproc.CV_HOUGH_GRADIENT, 1, gray.rows()/8, 225, 100, 50, 160);
				scanner.positionQR(image);
				if (circles.cols() > 0){
					System.out.println("Circles found : " + circles.cols());
					
					for (int x = 0; x < circles.cols(); x++) 
					{
						
						double vCircle[] = circles.get(0,x);
						
						if (vCircle == null)
							break;

						int radius = (int)Math.round(vCircle[2]);
						distanceToObject = 4.45 * 750 * frame.height()/ ((radius*2)*3.17);
						if(distanceToObject < closestDistance){
							
							closestDistance = (int) distanceToObject;
							System.out.println("Closest distance : " + closestDistance);
							pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
							System.out.println("Radius : " + radius);

							
							time = (int)distanceToObject/3;

							System.out.println("Distance: " + distanceToObject + " Frame Middle: "+ frame.width()/2 + " Center of Circle: " + pt.x);
							
						}
						
					}
					
					closestDistance = 9999;

					if(distanceToObject > 3000){
						margin = (int) (25/(distanceToObject/1000));
					} else

						if(distanceToObject > 2000){
							margin = (int) (30/(distanceToObject/1000));
						} else
						//	margin = (int) (35/(distanceToObject/1000));
							margin = (int) (30/(distanceToObject/1000));


					if(frame.height()/2 + margin < pt.y){
						commander.newCommand(command.CircleDown);
						
					}
					else if(frame.height()/2 - margin > pt.y){
						commander.newCommand(command.CircleUp);
					}
					else if(frame.width()/2+margin < pt.x){
						if(distanceToObject > 3000)
							commander.newCommand(command.CircleSpinRight);
						else commander.newCommand(command.CircleSpinRightClose);//commander.CircleSpinRightClose();
					}
					else if(frame.width()/2-margin > pt.x){
						if(distanceToObject > 3000)
							commander.newCommand(command.CircleSpinLeft);
						else commander.newCommand(command.CircleSpinLeftClose);//commander.CircleSpinLeftClose();
					}

					else if(frame.width()/2+margin > pt.x && frame.width()/2-margin<pt.x){
						if(distanceToObject > 2000){
							commander.newCommand(command.CircleForward);
						}
						else{
							commander.setDistance(distanceToObject);
							commander.newCommand(command.GoThroughCircle);
							enabled = false;
							commander.newCommand(command.DownToQR);
							scanner.setEnabled(true);
							
						}
					}
				}
			}
		}
	}

	public Double getDistToObject() {
		return distanceToObject;
	}

	public BufferedImage mat2img(Mat input){
		Mat mat = input;
		byte[] data = new byte[mat.rows()*mat.cols()*(int)(mat.elemSize())];
		mat.get(0, 0, data);
		if (mat.channels() == 3) {
			for (int i = 0; i < data.length; i += 3) {
				byte temp = data[i];
				data[i] = data[i + 2];
				data[i + 2] = temp;
			}
		}
		BufferedImage img = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_3BYTE_BGR);
		img.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);

		return img;	
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setScanner(QRCodeScanner scanner) {
		this.scanner = scanner;
	}

}
