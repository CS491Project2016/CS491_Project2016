import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HelpBox {
	
	   public HelpBox(){
	    	JFrame helpBox = new JFrame();
	    	helpBox.setSize(600, 800);
	    	helpBox.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    	helpBox.setLocationRelativeTo(null);
	    	
	    	JPanel mainPanel = new JPanel();
	    
	    	String line;
	    	StringBuilder sb = new StringBuilder();
	    	
	    	/*try {
				BufferedReader in = new BufferedReader(new FileReader("help/help.html"));
				
				line = in.readLine();
				
				while(line != null){
					line = in.readLine();
					sb.append(line);
					line = in.readLine();
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
	    	
	    	File helpFile;
	    	File decodedHelpFile;
	    	String helpFilePath, decodedHelpFilePath;
	    	
			//helpFile = new File(ClientOne.class.getClassLoader().getResource("help.html").getFile());
	    	helpFile = new File("help/help.html");
			//helpFilePath = helpFile.getAbsolutePath();
			try {
				//decodedHelpFilePath = URLDecoder.decode(helpFilePath, "UTF-8");
				//decodedHelpFile = new File(decodedHelpFilePath);
				Scanner scanner = new Scanner(helpFile);
				
				while(scanner.hasNextLine()){
					line = scanner.nextLine();
					sb.append(line);
				}
				
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

	    	JLabel title = new JLabel(sb.toString());
	    	
	    	mainPanel.add(title);
	    	
	    	helpBox.add(mainPanel);
	    	helpBox.setVisible(true);
	    	
	 	   helpBox.addWindowListener(new WindowListener(){

			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {
				helpBox.dispose();	
			}

			@Override
			public void windowClosed(WindowEvent e) {}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowDeactivated(WindowEvent e) {}
		   });
	    }
}
