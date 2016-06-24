package cdioProjekt.Gruppe14;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

public class QRCodeScan {

	private BufferedImage image;
	private BufferedImage qrImage;
	private ArrayList<QRCode> qrList = new ArrayList<QRCode>();
	private double distanceToQr;
	private int counter = 0;

	public ArrayList<QRCode> findQRCodes(Mat newImage) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		if(counter == 0)
			qrList.removeAll(qrList);

		Mat grey = new Mat(newImage.size(), CvType.makeType(newImage.depth(), 1));
		Mat qr = new Mat();
		Mat qr_raw = new Mat();
		Mat qr_gray = new Mat();
		Mat qr_thres = new Mat();

		if (!newImage.empty()) {


			qr = Mat.zeros(400, 400, CvType.CV_8UC3);
			qr_raw = Mat.zeros(400, 400, CvType.CV_8UC3);
			qr_gray = Mat.zeros(400, 400, CvType.CV_8UC1);
			qr_thres = Mat.zeros(400, 400, CvType.CV_8UC1);

			ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			MatOfInt4 hierarchy = new MatOfInt4();

			Imgproc.cvtColor(newImage, grey, Imgproc.COLOR_BGR2GRAY);
			Imgproc.Canny(grey, grey, 70, 210, 3, false);
			Imgproc.findContours(grey, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);


			Moments[] moments = new Moments[contours.size()];

			int found = 0;
			int count = 0;

			ArrayList<Integer> squares = new ArrayList<Integer>();

			for (int i = 0; i < hierarchy.rows(); i++) {
				for (int j = 0; j < hierarchy.cols(); j++) {
					double[] hieararchyFound = hierarchy.get(i, j);
					if (hieararchyFound[1] != -1) {
						count = 0;
					}

					count++;
					if (count == 5) {
						count = 0;
						found = j - 4;
						squares.add(found);
					}
				}
			}

			Point[] points = new Point[contours.size()];
			for (int i = 0; i < contours.size(); i++) {
				moments[i] = Imgproc.moments(contours.get(i), false);
				points[i] = new Point((moments[i].get_m10() / moments[i].get_m00()), (moments[i].get_m01() / moments[i].get_m00()));

			}


			List<Point[]> pointList = new ArrayList<>();
			MatOfPoint2f foundPoints = new MatOfPoint2f();

			for (int i = 0; i < squares.size(); i++) {
				foundPoints = corners(contours, squares.get(i), 1, foundPoints);
				pointList.add(foundPoints.toArray());

			}
			List<Point[]> squareList = new ArrayList<>();

			double area = 0;
			double maxArea = 0;
			for(int i = 0; i<pointList.size();i++){
				area = distance(pointList.get(i)[0],pointList.get(i)[1])*distance(pointList.get(i)[0],pointList.get(i)[3]);

				if(maxArea == 0){
					maxArea = area;
				}


				else if(area > maxArea*2 || area > maxArea*0.8){
					squareList.add(pointList.get(i));
				}


			}


			MatOfPoint2f tempList = new MatOfPoint2f();
			//
			Mat shifting = new Mat();

			List<Point> pointsFound = new ArrayList<Point>(tempList.toList());

			pointsFound.add(new Point(0, 0));
			pointsFound.add(new Point(qr.cols(), 0));
			pointsFound.add(new Point(qr.cols(), qr.rows()));
			pointsFound.add(new Point(0, qr.rows()));

			Point[] tempPointArray = pointsFound.toArray(new Point[pointsFound.size()]);
			for (int i = 0; i < pointList.size(); i++) {
				Imgproc.drawContours(newImage, contours, squares.get(i), new Scalar(100,50,255), 3, 8, hierarchy, 0, new Point(-1,-1));

				MatOfPoint2f source = new MatOfPoint2f(pointList.get(i));
				MatOfPoint2f newPoint = new MatOfPoint2f(tempPointArray);

				if (source.total() == 4 && newPoint.total() == 4) {
					shifting = Imgproc.getPerspectiveTransform(source, newPoint);

					Imgproc.warpPerspective(newImage, qr_raw, shifting, new Size(qr.cols(), qr.rows()));

					Imgproc.cvtColor(qr_raw, qr_gray, Imgproc.COLOR_RGB2GRAY);
					Imgproc.threshold(qr_gray, qr_thres, 127, 255, Imgproc.THRESH_BINARY);
					image = new BufferedImage(qr_gray.width(), qr_gray.height(), BufferedImage.TYPE_BYTE_GRAY);

					byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

					qr_gray.get(0, 0, data);
					String code = readQR(image);

					if (code != null) {
						distanceToQr = 4.45 * 400 * 360/ ((distance(pointList.get(i)[0], pointList.get(i)[3]))*3.17);
						qrList.add(new QRCode());
						Point[] corners = pointList.get(i);
						qrList.get(counter).setCorners(corners);
						qrList.get(counter).setDistanceAC(distance(corners[0], corners[3]));
						qrList.get(counter).setDistanceBD(distance(corners[1], corners[2]));
						qrList.get(counter).setDistanceAB(distance(corners[0], corners[1]));
						qrList.get(counter).setCode(code);

						qrList.get(counter).setDistance(distanceToQr);
						counter++;
						System.out.println(code);
						System.out.println(distanceToQr);

					}
				}

			}
			setQrImage(mat2img(newImage));

			counter = 0;
			return qrList;
		}

		else {
			throw new Exception("No image");
		}
	}

	private MatOfPoint2f corners(ArrayList<MatOfPoint> list, int firstPoint, double slope, MatOfPoint2f matrix) {

		Rect rectangle = Imgproc.boundingRect(list.get(firstPoint));

		Point topLeft = new Point(), topRight = new Point(), bottomRight = new Point(), bottomLeft = new Point();

		Point a = new Point(0, 0), b = new Point(0, 0), c = new Point(0, 0), d = new Point(0, 0);

		a = rectangle.tl();

		b.x = rectangle.br().x;

		b.y = rectangle.tl().y;
		c.x = rectangle.tl().x;
		c.y = rectangle.br().y;


		d = rectangle.br();
		d.x = (a.x + b.x) / 2;
		d.y = a.y;

		d.x = b.x;
		d.y = (b.y + d.y) / 2;

		d.x = (d.x + c.x) / 2;
		d.y = d.y;

		d.x = c.x;
		d.y = (c.y + a.y) / 2;

		double[] maximum = new double[4];
		maximum[0] = 0.0;
		maximum[1] = 0.0;
		maximum[2] = 0.0;
		maximum[3] = 0.0;

		double point1 = 0.0;
		double point2 = 0.0;
		Point[] pointArray = list.get(firstPoint).toArray();

		if (slope > 5 || slope < -5) {
			double temp;
			for (int i = 0; i < list.get(firstPoint).total(); i++) {
				point1 = lineCalc(d, a, pointArray[i]);
				point2 = lineCalc(b, c, pointArray[i]);

				if ((point1 >= 0.0) && (point2 > 0.0)) {

					temp = distance(pointArray[i], d);
					if (temp > maximum[1]) {
						maximum[1] = temp;
						topRight = pointArray[i];
					}

				} else if ((point1 > 0.0) && (point2 <= 0.0)) {

					temp = distance(pointArray[i], d);
					if (temp > maximum[2]) {
						maximum[2] = temp;
						bottomRight = pointArray[i];
					}

				} else if ((point1 <= 0.0) && (point2 < 0.0)) {

					temp = distance(pointArray[i], d);
					if (temp > maximum[3]) {
						maximum[3] = temp;
						bottomLeft = pointArray[i];
					}

				} else if ((point1 < 0.0) && (point2 >= 0.0)) {

					temp = distance(pointArray[i], d);

					if (temp > maximum[0]) {
						maximum[0] = temp;
						topLeft = pointArray[i];
					}
				} else
					continue;
			}
		}

		else {
			double temp;
			double middleX = (a.x + b.x) / 2;
			double middleY = (a.y + c.y) / 2;

			for (int i = 0; i < pointArray.length; i++) {
				if ((pointArray[i].x < middleX) && (pointArray[i].y <= middleY)) {

					temp = distance(pointArray[i], d);
					if (temp > maximum[2]) {
						maximum[2] = temp;
						topLeft = pointArray[i];
					}
				} else if ((pointArray[i].x >= middleX) && (pointArray[i].y < middleY)) {

					temp = distance(pointArray[i], c);
					if (temp > maximum[3]) {
						maximum[3] = temp;
						topRight = pointArray[i];
					}
				} else if ((pointArray[i].x > middleX) && (pointArray[i].y >= middleY)) {

					temp = distance(pointArray[i], a);
					if (temp > maximum[0]) {
						maximum[0] = temp;
						bottomRight = pointArray[i];
					}
				} else if ((pointArray[i].x <= middleX) && (pointArray[i].y > middleY)) {

					temp = distance(pointArray[i], b);
					if (temp > maximum[1]) {
						maximum[1] = temp;
						bottomLeft = pointArray[i];
					}
				}
			}
		}

		List<Point> corners = new ArrayList<Point>(matrix.toList());
		corners.add(topLeft);
		corners.add(topRight);
		corners.add(bottomRight);
		corners.add(bottomLeft);

		Point[] tempCorners = corners.toArray(new Point[corners.size()]);
		MatOfPoint2f tempMatrix = new MatOfPoint2f(tempCorners);
		matrix = tempMatrix;
		
		return matrix;
	}

	private double lineCalc(Point d, Point s, Point t) {
		double a, b, c, distance;

		a = -((s.y - d.y) / (s.x - d.x));
		b = 1;
		c = (((s.y - d.y) / (s.x - d.x)) * d.x) - d.y;

		distance = (a * t.x + (b * t.y) + c) / Math.sqrt((a * a) + (b * b));

		return distance;

	}

	private double distance(Point one, Point two) {
		return Math.sqrt(Math.pow(Math.abs(one.x - two.x), 2) + Math.pow(Math.abs(one.y - two.y), 2));
	}

	private String readQR(BufferedImage image) throws IOException {
		QRCodeReader qrReader = new QRCodeReader();

		try {
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			Result result = qrReader.decode(bitmap);

			if (result != null) {
				return result.getText();
			}
		} catch (NotFoundException e) {
		} catch (ChecksumException e) {
		} catch (FormatException e) {
		}

		return null;
	}

	//Den her virker bedst så længe den ik returnerer fejl hvilket er sket før, hvis problemer brug den anden istedet.
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

	
	public BufferedImage matToImage(Mat input){
		BufferedImage img = new BufferedImage(input.width(), input.height(), BufferedImage.TYPE_BYTE_GRAY);
		byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		input.get(0, 0,data);
		return img;
	}

	public BufferedImage getQrImage() {
		return qrImage;
	}

	public void setQrImage(BufferedImage qrImage) {
		this.qrImage = qrImage;
	}

}
