package de.yadrone.apps.paperchase;

import de.yadrone.apps.paperchase.controller.PaperChaseAbstractController;
import de.yadrone.apps.paperchase.controller.PaperChaseAutoController;
import de.yadrone.apps.paperchase.controller.PaperChaseKeyboardController;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.navdata.BatteryListener;

public class PaperChase 
{
	public final static int IMAGE_WIDTH = 640; // 640 or 1280
	public final static int IMAGE_HEIGHT = 360; // 360 or 720
	
	public final static int TOLERANCE = 80;
	
	private IARDrone drone = null;
	private PaperChaseAbstractController autoController;
	private QRCodeScanner scanner = null;
	
	public PaperChase()
	{
		
		
		drone = new ARDrone();
		drone.getCommandManager().setMinAltitude(1000);
		drone.getCommandManager().setMaxAltitude(2000);
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		
		PaperChaseGUI gui = new PaperChaseGUI(drone, this);
		
		
		// keyboard controller is always enabled and cannot be disabled (for safety reasons)
		PaperChaseKeyboardController keyboardController = new PaperChaseKeyboardController(drone);
		keyboardController.start();
		
		// auto controller is instantiated, but not started
		autoController = new PaperChaseAutoController(drone);
		
		
//		scanner = new QRCodeScanner();
//		scanner.addListener(gui);
//		
		Thread t = new Thread(){
		public void run(){
		CircleDetection objectdetection = new CircleDetection(drone);
		drone.getVideoManager().addImageListener(objectdetection);
		}}; 
		t.start();
		
		drone.getVideoManager().addImageListener(gui);
		//drone.getVideoManager().addImageListener(scanner);
		drone.takeOff();
		drone.getCommandManager().hover().doFor(10000);
		drone.getCommandManager().up(30).doFor(3000);
		drone.getCommandManager().hover().doFor(3000);
		
	}
	
	public void enableAutoControl(boolean enable)
	{
		if (enable)
		{
			scanner.addListener(autoController);
			autoController.start();
		}
		else
		{
			autoController.stopController();
			scanner.removeListener(autoController); // only auto autoController registers as TagListener
		}
	}
	
	
	
	public static void main(String[] args)
	{
		new PaperChase();
	}
	
}
