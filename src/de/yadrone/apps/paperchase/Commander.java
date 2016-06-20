package de.yadrone.apps.paperchase;
import java.util.ArrayList;
import java.util.ListIterator;

import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.StateListener;

public class Commander extends Thread {


	private IARDrone drone;
	private ArrayList<command> queue = new ArrayList<>();
	private ListIterator<command> iterator;
	private StateController state;


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
		MoveLeftQR,MoveRightQR, SpinLeftQR, SpinRightQR, UpToCircle, DownToQR, BackFromQR,
		Landing
	}

	public Commander(IARDrone drone, StateController state) {
		this.drone = drone;
		this.state = state;
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
		case DownToQR: DownToQR();
		break;
		case BackFromQR: BackFromQR();
		break;
		case Landing: Land();
		break;
		}
	}

	public void BackFromQR(){
		System.out.println("BackFromQR");
		drone.getCommandManager().backward(20).doFor(700).forward(20).doFor(200);
		Hover();
	}

	public void DownToQR() {
		System.out.println("DownToQR");
		drone.getCommandManager().down(20).doFor(1000);
		Hover();
	}


	public void CircleUp() {
		System.out.println("UP");
		drone.getCommandManager().up(30).doFor(30);
		Hover();
	}

	public void UptoCircle() {
		System.out.println("UP to circle");
		drone.getCommandManager().up(20).doFor(1200);
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
		drone.getCommandManager().forward(20).doFor(600).backward(20).doFor(160);
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
		drone.getCommandManager().goRight(20).doFor(100);
		Hover();
	}

	public void MoveLeftQR() {
		System.out.println("Move Left");
		drone.getCommandManager().goLeft(20).doFor(100);
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

	public void Search(){
		System.out.println("Searching");
		System.out.println("get yaw " + state.getYaw());
		double targetYaw = state.getYaw()+25;
		System.out.println(targetYaw < 360);
		if(targetYaw < 360) {
			System.out.println(targetYaw + " < " +state.getYaw()+180);
			while(targetYaw > state.getYaw()) {
				System.out.println("Spin");
				drone.getCommandManager().spinRight(30).doFor(30).hover();
			}
		} else {
			targetYaw -= 360;
			while(state.getYaw() > 300 && targetYaw > state.getYaw()) {
				System.out.println("Spin");
				drone.getCommandManager().spinRight(30).doFor(30).hover();
			}
		}
	}
}



