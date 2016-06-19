package de.yadrone.apps.paperchase;

import org.opencv.core.Core;

import de.yadrone.apps.paperchase.controller.PaperChaseAbstractController;
import de.yadrone.apps.paperchase.controller.PaperChaseAutoController;
import de.yadrone.apps.paperchase.controller.PaperChaseKeyboardController;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;

public class PaperChase 
{
	public final static int IMAGE_WIDTH = 1280; // 640 or 1280
	public final static int IMAGE_HEIGHT = 720; // 360 or 720
	
	public final static int TOLERANCE = 80;
	
	private IARDrone drone = null;
	private PaperChaseAbstractController autoController;
	private QRCodeScanner scanner = null;
	private StateController state;
	private CircleDetection circle = null;
	private Commander commander;
	public PaperChase()
	{
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		drone = new ARDrone();
		drone.getCommandManager().setMinAltitude(1000);
		drone.getCommandManager().setMaxAltitude(2000);
		drone.getCommandManager().setMaxVz(2000);
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		drone.getCommandManager().setVideoCodec(VideoCodec.H264_360P);
//		drone.getCommandManager().setVideoCodec(VideoCodec.H264_720P);
	
		
		// keyboard controller is always enabled and cannot be disabled (for safety reasons)
		PaperChaseKeyboardController keyboardController = new PaperChaseKeyboardController(drone);
		keyboardController.start();
	
		
		
		// auto controller is instantiated, but not started
		autoController = new PaperChaseAutoController(drone);
		
		//state controller
		state = new StateController(drone);
		
		
		//commander = new DroneCommander(drone);
		commander = new Commander(drone);
		commander.start();
		circle = new CircleDetection(state, commander);
		scanner = new QRCodeScanner(commander, state, circle);
		
//		circle = new CircleDetection(state, commander);
//		scanner = new QRCodeScanner(commander, state, circle);
		
		PaperChaseGUI gui = new PaperChaseGUI(drone, this, scanner);
//		scanner.addListener(gui);
		drone.getCommandManager().setMaxVideoBitrate(4000);
		drone.getCommandManager().setVideoBitrate(1400);
		drone.getCommandManager().setVideoCodecFps(30);
		drone.getVideoManager().addImageListener(gui);
		
		
		if(state.isVideoReady()){
		Thread t = new Thread(){
		public void run(){

		drone.getVideoManager().addImageListener(circle);
		}}; 
		t.start();

		
		Thread u = new Thread(){
		public void run(){
			drone.getVideoManager().addImageListener(scanner);
		}}; 
		u.start();
		
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
