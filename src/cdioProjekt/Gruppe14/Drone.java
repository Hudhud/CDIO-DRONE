package cdioProjekt.Gruppe14;

import org.opencv.core.Core;

import de.yadrone.apps.paperchase.controller.PaperChaseAbstractController;
import de.yadrone.apps.paperchase.controller.PaperChaseAutoController;
import de.yadrone.apps.paperchase.controller.PaperChaseKeyboardController;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;

public class Drone 
{
	public final static int IMAGE_WIDTH = 1280; // 640 or 1280
	public final static int IMAGE_HEIGHT = 720; // 360 or 720
	
	private IARDrone drone = null;
	private QRCodeScanner scanner = null;
	private StateController state;
	private CircleDetection circle = null;
	private Commander commander;
	private DroneAI droneAi;
	public Drone()
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		drone = new ARDrone();
		drone.getCommandManager().setMinAltitude(1000);
		drone.getCommandManager().setMaxAltitude(2000);
		drone.getCommandManager().setMaxVz(2000);
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		drone.getCommandManager().setVideoCodec(VideoCodec.H264_360P);
	
		
		// keyboard controller is always enabled and cannot be disabled (for safety reasons)
		PaperChaseKeyboardController keyboardController = new PaperChaseKeyboardController(drone);
		keyboardController.start();
		
		
		state = new StateController(drone);
		droneAi = new DroneAI(drone, commander, state, scanner);
		commander = new Commander(drone, state);
		commander.start();
		circle = new CircleDetection(state, commander);
		scanner = new QRCodeScanner(commander, state, circle, droneAi);
		
		DroneGUI gui = new DroneGUI(drone, this, scanner);
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
	
	public static void main(String[] args)
	{
		new Drone();
	}
	
}
