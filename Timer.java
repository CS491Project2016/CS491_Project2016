package server_test;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class Timer extends SwingWorker implements ActionListener{

	long endTime = 0, startTime = 0, timeInterval = 8;
	String params;
	JFrame frame;
	JLabel timeLabel;
	
	public Timer(JFrame inFrame, JLabel inTimeLabel){
		frame = inFrame;
		timeLabel = inTimeLabel;
		
		/********** Code that prevents the space bar from closing the window. **********/
		/*InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
		im.put(KeyStroke.getKeyStroke("pressed SPACE"), "none");
		im.put(KeyStroke.getKeyStroke("released SPACE"), "none");*/
	}

	@Override
	protected Object doInBackground() throws Exception {
		// TODO Auto-generated method stub
		
		while(endTime < timeInterval ){
				if(startTime == 0){
					startTime = System.nanoTime();
				}
				endTime = (System.nanoTime() - startTime)/1000000000;
				timeLabel.setText(Long.toString(endTime));
				//System.out.println(endTime);
		}
		
		timeLabel.setText("Times Up");
		params = ClientOne.getImageData();
		//System.out.println(params);
		Desktop.getDesktop().browse(new URI("https://web.njit.edu/~edm8/cs491/processResults.php?" + params));
		//frame.dispose();
		
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
