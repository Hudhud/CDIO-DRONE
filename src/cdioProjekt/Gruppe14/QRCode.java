package cdioProjekt.Gruppe14;

import java.awt.image.BufferedImage;

import org.opencv.core.Point;

public class QRCode {

	private double distanceAC;
	private double distanceBD;
	private double distanceAB;
	private Point[] corners;
	private double x;
	private double y;
	private String code;
	private BufferedImage qrImage;
	private Point center;
	private double distance;

	public void setX(double x){
		this.x = x;
	}
	public void setY(double y){
		this.y = y;
	}
	
	public void setCode(String code){
		this.code = code;
	}
	
	public String getCode(){
		return code;
	}
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	
	public QRCode(){
	}
	
	public void setQRimg(BufferedImage qRimg) {
		qrImage = qRimg;
	}
	public BufferedImage getQRimage() {
		return qrImage;
	}
	
	public void setCenter(Point center) {
		this.center = center;
	}
	public Point getCenter() {
		return center;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public double getDistance() {
		return distance;
	}

	public double getDistanceBD() {
		return distanceBD;
	}
	public void setDistanceBD(double distanceBD) {
		this.distanceBD = distanceBD;
	}
	public double getDistanceAC() {
		return distanceAC;
	}
	public void setDistanceAC(double distanceAC) {
		this.distanceAC = distanceAC;
	}
	public double getDistanceAB() {
		return distanceAB;
	}
	public void setDistanceAB(double distanceAB) {
		this.distanceAB = distanceAB;
	}
	public Point[] getCorners() {
		return corners;
	}
	public void setCorners(Point[] corners) {
		this.corners = corners;
	}
}