package de.yadrone.apps.paperchase;

import de.yadrone.apps.paperchase.controller.PaperChaseAbstractController;
import de.yadrone.apps.paperchase.controller.PaperChaseAutoController;
import de.yadrone.apps.paperchase.controller.PaperChaseKeyboardController;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;
import de.yadrone.base.navdata.BatteryListener;

public class PaperChase 
{
	public final static int IMAGE_WIDTH = 1280; // 640 or 1280
	public final static int IMAGE_HEIGHT = 720; // 360 or 720
	
	public final static int TOLERANCE = 80;
	
	private IARDrone drone = null;
	private PaperChaseAbstractController autoController;
	private QRCodeScanner scanner = null;
	private StateController state;
	
	public PaperChase()
	{
		
		
		drone = new ARDrone();
		drone.getCommandManager().setMinAltitude(1000);
		drone.getCommandManager().setMaxAltitude(1500);
		drone.getCommandManager().setMaxVz(2000);
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		drone.getCommandManager().setVideoCodec(VideoCodec.H264_720P);
		PaperChaseGUI gui = new PaperChaseGUI(drone, this);
		
		// keyboard controller is always enabled and cannot be disabled (for safety reasons)
		PaperChaseKeyboardController keyboardController = new PaperChaseKeyboardController(drone);
		keyboardController.start();
		
		// auto controller is instantiated, but not started
		autoController = new PaperChaseAutoController(drone);
		
		//state controller
		state = new StateController(drone);
		
		scanner = new QRCodeScanner();
		scanner.addListener(gui);
		
		if(state.isVideoReady()){
		
		Thread t = new Thread(){
		public void run(){
		CircleDetection objectdetection = new CircleDetection(drone, state);
		drone.getVideoManager().addImageListener(objectdetection);
		}}; 
		t.start();
		
		drone.getVideoManager().addImageListener(gui);
		drone.getVideoManager().addImageListener(scanner);
		}
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
