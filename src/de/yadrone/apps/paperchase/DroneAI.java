package de.yadrone.apps.paperchase;

import java.util.ArrayList;

import de.yadrone.base.IARDrone;

public class DroneAI {
	
	private IARDrone drone;
	private Commander commander;
	private StateController state;
	private ArrayList<QRCode> qrCodes;
	private Positioning positioning;
	private int[] startPosition;
	
	public DroneAI(IARDrone drone, Commander commander, StateController state, QRCodeScanner scanner){
		this.drone = drone;
		this.commander = commander;
		this.state = state;
		qrCodes = new ArrayList<>();
		positioning = new Positioning();
	}
	
	private void findStartPosition(){
		if(state.isReady()){
			while(qrCodes.size() < 2) {
				commander.CircleSpinLeftClose();
			}
			
			startPosition = positioning.calculatePosition(qrCodes);
		}
	}
	
	public void addQRCode(QRCode code) {
		qrCodes.add(code);
	}
	
	//private void 
}
