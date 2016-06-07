package de.yadrone.apps.tutorial;


import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.IExceptionListener;

public class TutorialMain
{
	private static IARDrone drone = null;
	
	public static void main(String[] args)
	{

		try
		{
			// Tutorial Section 1
			drone = new ARDrone();
			drone.addExceptionListener(new IExceptionListener() {
				public void exeptionOccurred(ARDroneException exc)
				{
					exc.printStackTrace();
				}
			});
			drone.getCommandManager().setMinAltitude(2500);
			drone.getCommandManager().setMaxAltitude(3000);
			drone.setSpeed(100);
			drone.start();

			
			// Tutorial Section 2
			new TutorialAttitudeListener(drone);
																									
			// Tutorial Section 3
		//	new TutorialVideoListener(drone);
			
			Thread t = new Thread() {
				public void run() {
					TutorialVideoListener video = new TutorialVideoListener(drone);
					drone.getVideoManager().addImageListener(video.getvideo());
				}
			};
			t.start();
			
			//Tutorial Section 4
			TutorialCommander commander = new TutorialCommander(drone);
			commander.animateLEDs();
			commander.takeOffAndLand();
//			commander.leftRightForwardBackward();
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
		}
		finally
		{
			if (drone != null)
				drone.stop();

			System.exit(0);
		}
	}
}
