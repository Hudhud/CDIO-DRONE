package de.yadrone.apps.paperchase;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.Paper;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import de.yadrone.apps.tutorial.TutorialAttitudeListener;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CalibrationCommand;
import de.yadrone.base.command.Device;
import de.yadrone.base.navdata.BatteryListener;
import de.yadrone.base.navdata.ControlState;
import de.yadrone.base.navdata.DroneState;
import de.yadrone.base.navdata.StateListener;
import de.yadrone.base.video.ImageListener;

public class PaperChaseGUI extends JFrame implements ImageListener, TagListener, ActionListener 
{
	private PaperChase main;
	private IARDrone drone;

	private BufferedImage image = null;
	private BufferedImage qrImage = null;
	private Result result;
	private Result[] multiResult;
	private String orientation;
	private String[] orientations;

	private String[] qrToFind = new String[] {"P.00", "P.01"};
	private boolean[] qrFound = new boolean[] {false, false};

	//QR CODES TO FIND
	private String[] circlesToFind = new String[] {"W1.03","", "", ""};//??
	private boolean[] circlePassed = new boolean[] {false, false};
	private String[] rightWall = new String[] {"","","",""};


	//

	private JPanel videoPanel;
	private JPanel qrPanel;
	private JPanel container;
	private JButton startknap, stopknap;
	private int batterypercentage;
	private JScrollPane jsp = new JScrollPane();

	private Timer timer = new Timer();
	private long gameStartTimestamp = System.currentTimeMillis();
	private String gameTime = "0:00";

	private boolean gameOver = false;

	private QRCodeScanner scanner;

	public PaperChaseGUI(final IARDrone drone, PaperChase main, QRCodeScanner scanner)
	{
		super("GRUPPE 14");

		this.main = main;
		this.drone = drone;
		this.scanner = scanner;

		batteryListener();

		setSize(PaperChase.IMAGE_WIDTH, PaperChase.IMAGE_HEIGHT);
		setVisible(true);
		setResizable(false);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				drone.stop();
				System.exit(0);
			}
		});

		setLayout(new GridBagLayout());

		add(createVideoPanel(), new GridBagConstraints(0, 0, 1, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill =  GridBagConstraints.BOTH;
		//		gbc.gridheight = 1000;
		//		gbc.gridwidth = 1000;
		//		gbc.weightx = 1;
		//		gbc.weighty = 1;
		//		gbc.gridx = 1;
		//		gbc.gridy = 1;
		add(createQRPanel(), gbc);
		// add listener to be notified once the drone takes off so that the game timer counter starts
		drone.getNavDataManager().addStateListener(new StateListener() {

			public void stateChanged(DroneState state)
			{
				if (state.isFlying())
				{
					
				}
			}

			public void controlStateChanged(ControlState state) { 
				if(state == ControlState.HOVERING){
					startGameTimeCounter();
					drone.getNavDataManager().removeStateListener(this);
				}
			}
		});



		container = new JPanel();
		container.setLayout(new GridLayout(2,2));
		container.add(videoPanel);
		container.add(qrPanel);

		createStartKnap();

		//		container.add(jsp);

		this.add(container);

		pack();
	}

	private JPanel createQRPanel() {
		qrPanel = new JPanel(){

			public void paint(Graphics g)
			{
				if(scanner.getQrImage() != null)
					qrImage = scanner.getQrImage();
				if (qrImage != null){
					g.drawImage(qrImage, 0, 0, PaperChase.IMAGE_WIDTH/2, PaperChase.IMAGE_HEIGHT/2, null);
				}

			}

		};
		//		
		qrPanel.setSize(PaperChase.IMAGE_WIDTH/2, PaperChase.IMAGE_HEIGHT/2);
		qrPanel.setMinimumSize(new Dimension(PaperChase.IMAGE_WIDTH/2, PaperChase.IMAGE_HEIGHT/2));
		qrPanel.setPreferredSize(new Dimension(PaperChase.IMAGE_WIDTH/2, PaperChase.IMAGE_HEIGHT/2));
		qrPanel.setMaximumSize(new Dimension(PaperChase.IMAGE_WIDTH/2, PaperChase.IMAGE_HEIGHT/2));

		return qrPanel;
	}

	private void batteryListener(){
		drone.getNavDataManager().addBatteryListener(new BatteryListener() {

			public void batteryLevelChanged(int percentage)
			{
				batterypercentage = percentage;
			}

			@Override
			public void voltageChanged(int vbat_raw) {
				// TODO Auto-generated method stub

			}
		});
	}



	private void createStartKnap(){

		startknap = new JButton("START");
		startknap.setFont(new Font("Arial", Font.BOLD, 60));
		startknap.setBackground(Color.GREEN);
		startknap.setVisible(true);
		startknap.addActionListener(this);
		
		stopknap = new JButton("STOP");
		stopknap.setFont(new Font("Arial", Font.BOLD, 60));
		stopknap.setBackground(Color.RED);
		stopknap.setVisible(true);
		stopknap.addActionListener(this);
		
			
		container.add(startknap);
		container.add(stopknap);

	}


	private JPanel createVideoPanel()
	{
		videoPanel = new JPanel() {

			private Font tagFont = new Font("SansSerif", Font.BOLD, 14);
			private Font timeFont = new Font("SansSerif", Font.BOLD, 18);
			private Font gameOverFont = new Font("SansSerif", Font.BOLD, 36);

			public void paint(Graphics g)
			{
				if (image != null)
				{
					// now draw the camera image
					g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);

					if(batterypercentage>50) {
						g.setColor(Color.GREEN);
						g.setFont(tagFont);
					}
					else {
						g.setColor(Color.RED);
						g.setFont(tagFont);
					}

					g.drawString("Battery: "+batterypercentage+"%", 0, 15);

					// draw tolerance field (rectangle)
					g.setColor(Color.RED);

				}
			}
		}; 

		// a click on the video shall toggle the camera (from vertical to horizontal and vice versa)
		videoPanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) 
			{
				drone.toggleCamera();
			}
		});

		videoPanel.setSize(PaperChase.IMAGE_WIDTH/2, PaperChase.IMAGE_HEIGHT/2);
		videoPanel.setMinimumSize(new Dimension(PaperChase.IMAGE_WIDTH/2, PaperChase.IMAGE_HEIGHT/2));
		videoPanel.setPreferredSize(new Dimension(PaperChase.IMAGE_WIDTH/2, PaperChase.IMAGE_HEIGHT/2));
		videoPanel.setMaximumSize(new Dimension(PaperChase.IMAGE_WIDTH/2, PaperChase.IMAGE_HEIGHT/2));

		return videoPanel;
	}

	//	private JPanel createButtonPanel()
	//	{
	//		JPanel buttonPanel = new JPanel();
	//		
	//		JButton toggleCamButton = new JButton("Toggle Camera");
	//		toggleCamButton.addActionListener(new ActionListener() {
	//			
	//			@Override
	//			public void actionPerformed(ActionEvent e)
	//			{
	//				drone.toggleCamera();
	//			}
	//		});
	//		
	//		buttonPanel.add(toggleCamButton);
	//		
	//		return buttonPanel;
	//	}

	private long imageCount = 0;

	public void imageUpdated(BufferedImage newImage)
	{
		if ((++imageCount % 2) == 0)
			return;

		image = newImage;
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				videoPanel.repaint();
				qrPanel.repaint();
			}
		});
	}

	public void onTags(Result[] multiResult, double[] orientation)
	{
		if (multiResult != null)
		{
			this.multiResult = multiResult;
			//this.orientation = orientation + ;
			String[] orientations = new String[multiResult.length];

			// check if that's a tag (shred) which has not be seen before and mark it as 'found'
			for(int i = 0; i < multiResult.length; i++){
				orientations[i] = orientation[i] + "°";
				for (int j=0; j < qrToFind.length; j++)
				{
					if (qrToFind[j].equals(multiResult[i].getText()) && !qrFound[i])
					{
						qrToFind[j] = qrToFind[j] + " - " + gameTime;
						qrFound[j] = true;
					}
				}


				// now check if all shreds have been found and if so, set the gameOver flag
				boolean isGameOver = true;
				for (int j=0; j < qrFound.length; j++)
				{
					if (qrFound[j] == false)
						isGameOver = false;
				}

				if (isGameOver) // all shreds found ?
				{
					gameOver = true;
					stopGameTimeCounter();
				}
			}
			this.orientations = orientations;

		}
	}




	private void startGameTimeCounter()
	{
		gameStartTimestamp = System.currentTimeMillis();

		TimerTask timerTask = new TimerTask() {

			public void run()
			{
				long time = System.currentTimeMillis() - gameStartTimestamp;

				int minutes = (int)(time / (60 * 1000));
				int seconds = (int)((time / 1000) % 60);
				gameTime = String.format("%d:%02d", minutes, seconds);
				scanner.incrementTimer();
			}
		};

		timer.schedule(timerTask, 0, 1000);		
	}

	private void stopGameTimeCounter()
	{
		timer.cancel();
	}

	@Override
	public void onTag(Result result, float orientation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		
		if(action.equals("START")){
			Thread t = new Thread(new Runnable() {
				public void run() {
					drone.getCommandManager().takeOff();
					//						drone.getCommandManager().hover().doFor(2000);
					//						drone.getCommandManager().up(60).doFor(900);
					drone.getCommandManager().hover();
					//						drone.getCommandManager().setCommand(new CalibrationCommand(Device.MAGNETOMETER)).doFor(6000);
					//						drone.getCommandManager().hover().doFor(1000);
					//						drone.getCommandManager().forward(100).doFor(1000);
					//						drone.getCommandManager().backward(60).doFor(400);
					//						drone.getCommandManager().hover().doFor(1500);
					//						drone.getCommandManager().spinLeft(100).doFor(1700);
					//						drone.getCommandManager().hover().doFor(2000);
					//						drone.getCommandManager().forward(100).doFor(1000);
					//						drone.getCommandManager().backward(60).doFor(400);
					//						drone.getCommandManager().hover();
					//						drone.getCommandManager().forward(100).doFor(1500);
					//						drone.getCommandManager().backward(60).doFor(400);
					//						drone.getCommandManager().hover().doFor(1500);
					//						drone.getCommandManager().backward(100).doFor(2000);
					//						drone.getCommandManager().forward(100).doFor(200);
					//						drone.getCommandManager().hover();
		

				}
			});
			t.start();
		} else if(action.equals("STOP")){
			drone.getCommandManager().landing();
			System.exit(0);
		}
	}

}
