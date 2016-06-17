package de.yadrone.apps.paperchase;
import java.util.ArrayList;
import java.util.ListIterator;

import de.yadrone.base.IARDrone;

public class Commander extends Thread {


	private IARDrone drone;
	private ArrayList<command> queue = new ArrayList<>();
	private ListIterator<command> iterator;


	private double distance;
	private int index;


	public void newCommand(command command) {
		iterator.add(command);
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public enum command{
		CircleUp,CircleDown,CircleSpinLeft,CircleSpinRight,CircleSpinRightClose,CircleSpinLeftClose,
		GoThroughCircle,CircleForward,
		MoveLeftQR,MoveRightQR, SpinLeftQR, SpinRightQR, UpToCircle,
		Landing
	}

	public Commander(IARDrone drone) {
		this.drone = drone;
	}

	@Override
	public void run(){
		while(true){
			iterator = queue.listIterator();
			if(iterator.hasNext()){
				index = iterator.nextIndex();
				command command = iterator.next();
				doCommand(command);
				queue.remove(index);
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void doCommand(command command){
		switch(command){
		case CircleUp: CircleUp();
		break;
		case CircleDown: CircleDown();
		break;
		case CircleSpinLeft: CircleSpinLeft();
		break;
		case CircleSpinRight: CircleSpinRight();
		break;

		case CircleSpinLeftClose: CircleSpinLeftClose();
		break;

		case CircleSpinRightClose: CircleSpinRightClose();
		break;

		case CircleForward: CircleForward();
		break;

		case GoThroughCircle: GoThroughCircle(distance);
		break;

		case MoveLeftQR: MoveLeftQR();
		break;

		case MoveRightQR: MoveRightQR();
		break;

		case SpinLeftQR: SpinLeftQR();
		break;

		case SpinRightQR: SpinRightQR();
		break;

		case UpToCircle: UptoCircle();
		break;

		case Landing: Land();
		break;

		}
	}

	public void CircleUp() {
		System.out.println("UP");
		drone.getCommandManager().up(20).doFor(30);
		Hover();
	}

	public void UptoCircle() {
		System.out.println("UP to circle");
		drone.getCommandManager().up(20).doFor(1000);
		Hover();

	}

	public void CircleDown() {
		System.out.println("Down");
		drone.getCommandManager().down(20).doFor(30);
		Hover();

	}

	public void CircleSpinLeft() {
		System.out.println("Spin Left");
		drone.getCommandManager().spinLeft(30).doFor(30);
		Hover();
	}

	public void CircleSpinRight() {
		System.out.println("Spin right");
		drone.getCommandManager().spinRight(30).doFor(30);
		Hover();
	}

	public void CircleSpinRightClose() {
		System.out.println("Spin right");
		drone.getCommandManager().spinRight(10).doFor(30);
		Hover();
	}

	public void CircleSpinLeftClose() {
		System.out.println("Spin left");
		drone.getCommandManager().spinLeft(10).doFor(30);
		Hover();
	}

	public void CircleForward() {
		System.out.println("Forward");
		drone.getCommandManager().forward(20).doFor(800).backward(20).doFor(200);
		Hover();
	}

	public void GoThroughCircle(double distance) {
		final int doFor = (int) distance+300;
		System.out.println("Go Through Circle " +doFor);
		drone.getCommandManager().forward(20).doFor(doFor).backward(20).doFor(200);
		Hover();
	}

	public void MoveRightQR() {
		System.out.println("Move Right");
		drone.getCommandManager().goRight(10).doFor(30);
		Hover();
	}

	public void MoveLeftQR() {
		System.out.println("Move Left");
		drone.getCommandManager().goLeft(10).doFor(30);
		Hover();
	}

	public void SpinLeftQR() {
		System.out.println("Spin Left");
		drone.getCommandManager().spinLeft(10).doFor(30);
		Hover();
	}

	public void SpinRightQR() {
		System.out.println("Spin Right");
		drone.getCommandManager().spinRight(10).doFor(30);
		Hover();
	}

	public void Hover() {
		drone.getCommandManager().hover();
	}

	public void Land() {
		System.out.println("LANDING");
		drone.getCommandManager().landing();
	}
}


