import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URLEncoder;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class Timer extends SwingWorker implements ActionListener{

	boolean prematureEnd = false;
	int timeRemaining;
	long endTime = 0, startTime = 0, timeInterval;
	String params, id;
	JFrame frame;
	JLabel timeLabel;
	PrintWriter out;
	ClientOne playerOne;
	ClientTwo playerTwo;
	
	public Timer(ClientOne client, JFrame inFrame, JLabel label, PrintWriter printWriter, String identification, int time){
		frame = inFrame;
		timeLabel = label;
		out = printWriter;
		id = identification;
		//timeInterval = time * 60;
		//timeRemaining = time * 60;
		timeRemaining = 8;
		timeInterval = 8;
		playerOne = client;
		
		System.out.println("ID : " + id);
		
		/********** Code that prevents the space bar from closing the window. **********/
		/*InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
		im.put(KeyStroke.getKeyStroke("pressed SPACE"), "none");
		im.put(KeyStroke.getKeyStroke("released SPACE"), "none");*/
	}
	
	public Timer(ClientTwo client, JFrame inFrame, JLabel label, PrintWriter printWriter, String identification, int time){
		frame = inFrame;
		timeLabel = label;
		out = printWriter;
		id = identification;
		//timeInterval = time * 60;
		//timeRemaining = time * 60;
		timeRemaining = 8;
		timeInterval = 8;
		playerTwo = client;
		
		System.out.println("ID : " + id);
		
		/********** Code that prevents the space bar from closing the window. **********/
		/*InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
		im.put(KeyStroke.getKeyStroke("pressed SPACE"), "none");
		im.put(KeyStroke.getKeyStroke("released SPACE"), "none");*/
	}

	@Override
	protected Object doInBackground() throws Exception {
		
		while(timeRemaining > -1){
				if(startTime == 0){
					startTime = System.nanoTime();
				}
				endTime = (System.nanoTime() - startTime)/1000000000;
				timeRemaining = (int)(timeInterval - endTime);
				if(timeRemaining < timeRemaining*0.25){
					//timeLabel.setText("Time: " + Long.toString(timeInterval - endTime));
					timeLabel.setText("Time: " + timeRemaining);
					timeLabel.setForeground(Color.RED);
				}
				else{
					timeLabel.setText("Time: " + timeRemaining);
				}
				//System.out.println(endTime);
		}
		
		if(prematureEnd == true){
			timeLabel.setText("Ending Game...");
		}
		else{
			timeLabel.setText("Time's Up!");
		}
		
		//signals completion to server
		if(prematureEnd == true){
					
		}
		else{
			if(playerTwo == null){
				playerOne.endGame();
			}
			else{
				playerTwo.endGame();
			}
		}
		
		return null;
	}
	
	protected void stopTimer(){
		prematureEnd = true;
		timeRemaining = -1;
	}

	@Override
	public void actionPerformed(ActionEvent e) {}
}