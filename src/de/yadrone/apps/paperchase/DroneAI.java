package de.yadrone.apps.paperchase;

import de.yadrone.base.IARDrone;

public class DroneAI {
	
	private IARDrone drone;
	private DroneCommander commander;
	private StateController state;
	public DroneAI(IARDrone drone, DroneCommander commander, StateController state){
		this.drone = drone;
		this.commander = commander;
		this.state = state;
	}
	
	private void findStartPosition(){
		if(state.isReady()){
			//scan 3 qr codes
			
			//calculate position
		}
	}
	
	//private void 
}
