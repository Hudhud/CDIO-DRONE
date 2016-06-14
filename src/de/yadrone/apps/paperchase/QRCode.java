package de.yadrone.apps.paperchase;

import java.awt.image.BufferedImage;

import org.opencv.core.Point;

public class QRCode {

	private int top;
	private int middle;
	private int bottom;
	private Point lastPoint;
	private double x;
	private double y;
	private String message;
	private BufferedImage qrImage;
	private Point center;
	private double distance;
	private Point coordinates;

	public void setX(double x){
		this.x = x;
	}
	public void setY(double y){
		this.y = y;
	}
	
	public void setCode(String s){
		message = s;
		if(message.startsWith("W")){
			setCoordinates();
		}
	}
	
	public String getCode(){
		return message;
	}
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	
	
	public void setTop(int i){
		top = i;
	}
	public void setMid(int i){
		middle = i;
	}
	public void setBot(int i)
	{
		bottom = i;
	}
	public void setLP(Point p){
		lastPoint = p;
	}
	
	public int getTop(){
		return top;
	}
	public int getMid(){
		return middle;
	}
	public int getBot(){
		return bottom;
	}
	public Point getLP(){
		return lastPoint;
	}
	public Point getCoordinates() {
		return coordinates;
	}
	
	public QRCode(int top, int middle , int bottom){
		this.top = top;
		this.middle = middle;
		this.bottom = bottom;
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
	
	private void setCoordinates(){
		switch(this.message){
		
		case "W00.00":
			this.coordinates = new Point(188,1055);
			break;
		case "W00.01":
			this.coordinates = new Point(338,1060);
			break;
		case "W00.02":
			this.coordinates = new Point(515,1055);
			break;
		case "W00.03":
			this.coordinates = new Point(694,1060);
			break;
		case "W00.04":
			this.coordinates = new Point(840,1055);
			break;
		case "W01.00":
			this.coordinates = new Point(926,904);
			break;
		case "W01.01":
			this.coordinates = new Point(926,721);
			break;
		case "W01.02":
			this.coordinates = new Point(926,566);
			break;
		case "W01.03":
			this.coordinates = new Point(926,324);
			break;
		case "W01.04":
			this.coordinates = new Point(926,115);
			break;
		case "W02.00":
			this.coordinates = new Point(847,-10);
			break;
		case "W02.01":
			this.coordinates = new Point(656,-77);
			break;
		case "W02.02":
			this.coordinates = new Point(420,0);
			break;
		case "W02.03":
			this.coordinates = new Point(350,0);
			break;
		case "W02.04":
			this.coordinates = new Point(150,0);
			break;
		case "W03.00":
			this.coordinates = new Point(0,108);
			break;
		case "W03.01":
			this.coordinates = new Point(0,357);
			break;
		case "W03.02":
			this.coordinates = new Point(0,561);
			break;
		case "W03.03":
			this.coordinates = new Point(0,740);
			break;
		case "W03.04":
			this.coordinates = new Point(0,997);
			break;
			
			default:
			break;	
		}
	}
}