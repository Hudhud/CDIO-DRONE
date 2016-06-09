package de.yadrone.apps.paperchase;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import de.yadrone.base.IARDrone;
import de.yadrone.base.video.ImageListener;
import de.yadrone.apps.paperchase.PaperChaseGUI;

public class CircleDetection implements ImageListener{

	Point pt;
	double distanceToObject;
	boolean forward = false;
	int time;
	private final IARDrone drone;
	private long imageCount = 0;
	PaperChaseGUI gui;
	byte[] pixel = new byte[16];
	int margin;

	public CircleDetection(final IARDrone drone){
		super();

		this.drone = drone;
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	public void imageUpdated(final BufferedImage image)
	{
		//		if ((++imageCount % 2) == 0){
		//			return;
		//		}


		pixel = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		Mat frame = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		Mat gray = new Mat();
		frame.put(0, 0, pixel);		

		Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY );
		Imgproc.GaussianBlur(gray, gray ,new Size(9, 9), 2, 2 );
		Imgproc.equalizeHist(gray, gray);

		Mat circles = new Mat();
		Imgproc.HoughCircles(gray, circles, Imgproc.CV_HOUGH_GRADIENT, 1, gray.rows()/8, 200, 100, 0, 0);
		if (circles.cols() > 0){

			System.out.println("Circle found");
			for (int x = 0; x < circles.cols(); x++) 
			{
				double vCircle[] = circles.get(0,x);

				if (vCircle == null)
					break;

				pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
				int radius = (int)Math.round(vCircle[2]);

				//					Imgproc.circle(frame, pt, radius, new Scalar(0,255,0), 1);
				//					Imgproc.circle(frame, pt, 3, new Scalar(0,0,255), 1);
				//					
				distanceToObject = 4.45 * 750 * frame.height()/ ((radius*2)*3.17);
				time = (int)distanceToObject/3;

				//					if(!forward){
				//					if(distanceToObject > 500){
				//						forward = true;
				//						drone.getCommandManager().forward(100).doFor(time+2000);
				//						drone.getCommandManager().hover().doFor(2000);
				//						drone.getCommandManager().landing();
				//						forward = false;
				//					} else {
				//						drone.getCommandManager().landing();
				//					}
				//					}
				System.out.println("Distance: " + distanceToObject + " Frame Middle: "+ frame.width()/2 + " Center of Circle: " + pt.x);

			}

			margin = (int) (15*(distanceToObject/1000));

			if(frame.height()/2 + margin < pt.y){
				System.out.println("down");
				drone.getCommandManager().down(20).doFor(100);
				drone.getCommandManager().hover();
			}
			else if(frame.height()/2 - margin > pt.y){
				System.out.println("up");
				drone.getCommandManager().up(20).doFor(100);
				drone.getCommandManager().hover();
			}
			else if(frame.width()/2+margin < pt.x){
				drone.getCommandManager().spinRight(30).doFor(33);
				drone.getCommandManager().hover();
				System.out.println("RIGHT");

			}
			else if(frame.width()/2-margin > pt.x){
				drone.getCommandManager().spinLeft(30).doFor(33);
				drone.getCommandManager().hover();
				System.out.println("LEFT");
			}

			else if(frame.width()/2+margin > pt.x && frame.width()/2-margin<pt.x){
				System.out.println("GO");
				drone.getCommandManager().forward(30).doFor(time+2000);
				drone.getCommandManager().hover();
			}

			else{
				drone.getCommandManager().hover();
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

}
