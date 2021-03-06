package cdioProjekt.Gruppe14;

import java.util.ArrayList;

import de.yadrone.base.IARDrone;

public class DroneAI {

	private IARDrone drone;
	private Commander commander;
	private StateController state;
	private ArrayList<QRCode> qrCodes;

	private boolean searching;

	public DroneAI(IARDrone drone, Commander commander, StateController state, QRCodeScanner scanner){
		this.drone = drone;
		this.commander = commander;
		this.state = state;
		qrCodes = new ArrayList<>();
	}

	private void findStartPosition(){
		if(state.isReady()){
			while(qrCodes.size() < 2) {
				commander.CircleSpinLeftClose();
			}

		}
	}

	public void addQRCode(QRCode code) {
		qrCodes.add(code);
	}

	private void searchForQR(){

		commander.Search();

	}
	public boolean isSearching() {
		return searching;
	}

	public void setSearching(boolean searching) {
		this.searching = searching;
	}

}
