package de.yadrone.apps.paperchase;

import de.yadrone.apps.controlcenter.plugins.speed.SpeedPanel;
import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.ControlState;
import de.yadrone.base.navdata.DroneState;
import de.yadrone.base.navdata.StateListener;
import de.yadrone.base.navdata.VelocityListener;

public class StateController {

	private IARDrone drone;
	private boolean ready = true;
	private boolean videoReady = false;

	public StateController(IARDrone drone) {
		this.drone = drone;

		drone.getNavDataManager().addStateListener(new StateListener() {

			public void stateChanged(DroneState state) {
				if(state.isCameraReady()){
					videoReady = true;
				}
			}

			public void controlStateChanged(ControlState state) {
				if(state != null){
				if (state == ControlState.HOVERING) {
					ready = true;
				} else {
					ready = false;
				}
			}
			}
		});
		
//		drone.getNavDataManager().addVelocityListener(new VelocityListener() {
//
//			@Override
//			public void velocityChanged(float vx, float vy, float vz) {
//				System.out.println("speedx: " + vx);
//				System.out.println("speedy: " + vy);
//				System.out.println("speedz: " + vz);
//			}
//		});
	}

	public boolean isReady() {
		return ready;
	}
	
	public boolean isVideoReady() {
		return videoReady;
	}

}
