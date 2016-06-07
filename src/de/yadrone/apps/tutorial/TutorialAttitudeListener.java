package de.yadrone.apps.tutorial;

import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.AttitudeListener;
import de.yadrone.base.navdata.BatteryListener;
import de.yadrone.base.navdata.VelocityListener;

public class TutorialAttitudeListener
{

	public TutorialAttitudeListener(IARDrone drone)
	{
		drone.getNavDataManager().addAttitudeListener(new AttitudeListener() {

			public void attitudeUpdated(float pitch, float roll, float yaw)
			{
				//		    	System.out.println("Pitch: " + pitch + " Roll: " + roll + " Yaw: " + yaw*0.001);
			}

			public void attitudeUpdated(float pitch, float roll) { }
			public void windCompensation(float pitch, float roll) { }
		});

		drone.getNavDataManager().addBatteryListener(new BatteryListener() {

			public void batteryLevelChanged(int percentage)
			{
				//				System.out.println("Battery: " + percentage + " %");


			}

			public void voltageChanged(int vbat_raw) { }
		});

		drone.getNavDataManager().addVelocityListener(new VelocityListener() {

			@Override
			public void velocityChanged(float vx, float vy, float vz) {
				System.out.println("speedx: " + vx);
				System.out.println("speedy: " + vy);

			}
		});
	}

}
