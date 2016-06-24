package cdioProjekt.Gruppe14;

import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.AttitudeListener;
import de.yadrone.base.navdata.ControlState;
import de.yadrone.base.navdata.DroneState;
import de.yadrone.base.navdata.StateListener;

public class StateController {

	private IARDrone drone;
	private boolean ready = true;
	private boolean videoReady = false;
	private float droneYaw;


	public StateController(IARDrone drone) {
		this.drone = drone;

		drone.getNavDataManager().addAttitudeListener(new AttitudeListener() {

			public void attitudeUpdated(float pitch, float roll, float yaw)
			{
				droneYaw = yaw/1000;
			}

			@Override
			public void attitudeUpdated(float pitch, float roll) {
			}

			@Override
			public void windCompensation(float pitch, float roll) {

			}
		});

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

	}

	public boolean isReady() {
		return ready;
	}

	public boolean isVideoReady() {
		return videoReady;
	}
	public float getYaw() {
		return droneYaw;
	}

	public void setYaw(float yaw) {
		this.droneYaw = yaw;
	}
}
