package de.yadrone.apps.paperchase;

import de.yadrone.apps.controlcenter.plugins.speed.SpeedPanel;
import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.AdcFrame;
import de.yadrone.base.navdata.AdcListener;
import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;
import de.yadrone.base.navdata.AttitudeListener;
import de.yadrone.base.navdata.ControlState;
import de.yadrone.base.navdata.DroneState;
import de.yadrone.base.navdata.GyroListener;
import de.yadrone.base.navdata.GyroPhysData;
import de.yadrone.base.navdata.GyroRawData;
import de.yadrone.base.navdata.MagnetoData;
import de.yadrone.base.navdata.MagnetoListener;
import de.yadrone.base.navdata.StateListener;
import de.yadrone.base.navdata.VelocityListener;

public class StateController {

	private IARDrone drone;
	private boolean ready = true;
	private boolean videoReady = false;

	public StateController(IARDrone drone) {
		this.drone = drone;
		
		drone.getNavDataManager().addAttitudeListener(new AttitudeListener() {

			public void attitudeUpdated(float pitch, float roll, float yaw)
			{
		    	System.out.println("Pitch: " + pitch + " Roll: " + roll + " Yaw: " + yaw);
			}

			@Override
			public void attitudeUpdated(float pitch, float roll) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windCompensation(float pitch, float roll) {
				// TODO Auto-generated method stub
				
			}
		});

		
		drone.getNavDataManager().addMagnetoListener(new MagnetoListener() {

			@Override
			public void received(MagnetoData d) {
				// TODO Auto-generated method stub
//				System.out.println("Heading fusion : " + d.getHeadingFusionUnwrapped());
//				System.out.println("Heading gyro : " + d.getHeadingGyroUnwrapped());
//				System.out.println("Heading unwrapped" + d.getHeadingUnwrapped());
//				System.out.println("GYRO RADIUS : " + d.getRadius());
//				
			}
			
		});
		
		drone.getNavDataManager().addGyroListener(new GyroListener() {
			@Override
			public void receivedPhysData(GyroPhysData d) {
//				System.out.println("Gyro Temp : " + d.getGyroTemp());
//				System.out.println("Gyro VrefEpson : " + d.getVrefEpson());
//				System.out.println("Gyro VrefIDG : " + d.getVrefIDG());
//				System.out.println("Gyro Phys : " + d.getPhysGyros());
			}
			
			@Override
			public void receivedRawData(GyroRawData d) {
//				// TODO Auto-generated method stub
//				System.out.println("raw Gyro: " + d.getRawGyros());
//				System.out.println("raw gyro110 : " + d.getRawGyros110());
			}
			
			@Override
			public void receivedOffsets(float[] offset_g) {
				// TODO Auto-generated method stub
				
			}
		});

		drone.getNavDataManager().addAltitudeListener(new AltitudeListener() {
			@Override
			public void receivedAltitude(int altitude) {
				//System.out.println("Altitude : " + altitude);
			}
			
			@Override
			public void receivedExtendedAltitude(Altitude d) {
				// TODO Auto-generated method stub
				
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
