package de.yadrone.apps.paperchase;

import de.yadrone.base.IARDrone;

public class DroneCommander {

	private IARDrone drone;
	//private List<>
	

	public DroneCommander(IARDrone drone) {
		this.drone = drone;
	}

	public void CircleUp() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				System.out.println("UP");
				drone.getCommandManager().up(20).doFor(30).hover();
			//	Hover();
			}
		});
		t.start();
	}

	public void CircleDown() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				System.out.println("Down");
				drone.getCommandManager().down(20).doFor(30).hover();
				//Hover();
			}
		});
		t.start();

	}

	public void CircleSpinLeft() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				System.out.println("Spin Left");
				drone.getCommandManager().spinLeft(50).doFor(30).hover();
				//Hover();
			}
		});
		t.start();
	}

	public void CircleSpinRight() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				System.out.println("Spin right");
				drone.getCommandManager().spinRight(50).doFor(30).hover();
				//Hover();
			}
		});
		t.start();
	}

	public void CircleForward() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				System.out.println("Forward");
				drone.getCommandManager().forward(60).doFor(500).backward(30).doFor(200).hover();
				//Hover();
			}
		});
		t.start();
	}
	
	public void GoThroughCircle(double distance) {
		final int doFor = (int) distance+500;
		Thread t = new Thread(new Runnable() {
			public void run() {
				System.out.println("Go Through Circle");
				drone.getCommandManager().forward(20).doFor(doFor).backward(20).doFor(200).hover();
			}
		});
		t.start();
	}

	public void Hover() {
		drone.getCommandManager().hover();
	}
}
