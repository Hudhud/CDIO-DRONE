package de.yadrone.apps.paperchase;

import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.ControlState;
import de.yadrone.base.navdata.DroneState;
import de.yadrone.base.navdata.StateListener;

public class StateController {

	private IARDrone drone;
	private boolean ready = false;
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
				if (state == ControlState.HOVERING) {
					ready = true;
				} else {
					ready = false;
				}
			}
		});

	}

	public boolean isReady() {
		return ready;
	}
	
	public boolean isVideoReady() {
		return videoReady;
	}

}
