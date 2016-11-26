import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Keymap;
import javax.swing.text.PlainDocument;


public class Referee {

	String id = "referee";
	String[] timeValues = {"1","3","5","10"}, constraintValues = {"1%", "3%","5%","7%","10%", "25%"};
	Border lineBorder;
    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Referee");
    JPanel mainPanel, topPanel, bottomPanel, centerPanel, sidePanel;
    String IPAddress;
    int port;
    List<String> pngFiles = new ArrayList<String>();
    JButton[] buttons;
    JButton playerOneLight, playerTwoLight;
    JLabel topPanelLabel, bottomPanelLabel, timeSelectLabel, constraintSelectLabel;
    JComboBox timeSelect;
    JTextField constraintSelect;
    //JFormattedTextField constraintSelect;
    ImageIcon[] icons;
    ImageIcon logoImage;
    Font topPanelFont, bottomPanelFont, timeSelectLabelFont, invisibleText = new Font("Serif", 0, 0);;
    SpringLayout springLayout;
    File frameIcon;
    NumberFormat percentFormat;

    public Referee() {
    	
    	/***** IP address prompt *****/
    	try{
	    	IPAddress = (String)JOptionPane.showInputDialog(
	    			frame, 
	    			"Enter the IP address of the host:", 
	    			"Host Address",
	    			JOptionPane.WARNING_MESSAGE,
	    			null,
	    			null,
	    			"localhost"
	    			);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		JOptionPane.showInputDialog(
        			new JFrame(), 
        			"Bad IP address.", 
        			"IP Address Error", 
        			JOptionPane.ERROR_MESSAGE
        			);
    		System.exit(0);
    	}
    	
    	/***** Port prompt *****/
    	try{
	    	port = Integer.parseInt((String)JOptionPane.showInputDialog(
	    			frame, 
	    			"Enter the port number:", 
	    			"Port Selection",
	    			JOptionPane.WARNING_MESSAGE,
	    			null,
	    			null,
	    			"3000"
	    			));
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(
        			new JFrame(), 
        			"Cannot use port " + port, 
        			"Port Error", 
        			JOptionPane.ERROR_MESSAGE
        			);
    		System.exit(0);
    	}

        /***** Layout *****/
    	buildGUI();
    }
    
    public static void main(String[] args) throws Exception {
        Referee client = new Referee();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }

    private void run() throws IOException {

        // Make connection and initialize streams
        //String serverAddress = "localhost";
        Socket socket = new Socket(IPAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            if (line.startsWith("GETID")) {
                out.println("REFEREE");
            }
            else if(line.startsWith("RESULTS")) {
            	/*try{
            		Desktop.getDesktop().browse(new URI("https://web.njit.edu/~edm8/cs491/playerResults.php?playerId=referee"));
            	}
            	catch(Exception e){
            		e.printStackTrace();
            		JOptionPane.showInputDialog(
                			new JFrame(), 
                			"Could not open browser. Function not supported.", 
                			"Desktop.getDesktop() Error", 
                			JOptionPane.ERROR_MESSAGE
                			);
            		System.exit(0);
            	}*/
            }
            else if(line.startsWith("P1ONLINE")){
            	playerOneLight.setText("Player One ONLINE");
            	playerOneLight.setBackground(new Color(0, 179, 0));
            	playerOneLight.setForeground(Color.BLACK);
            }
            else if(line.startsWith("P2ONLINE")){
            	playerTwoLight.setText("Player Two ONLINE");
            	playerTwoLight.setBackground(new Color(0, 179, 0));
            	playerTwoLight.setForeground(Color.BLACK);
            }
            else if(line.startsWith("P1OFFLINE")){
            	System.out.println("Ref: Player one offline");
            	playerOneLight.setText("Player One OFFLINE");
            	playerOneLight.setBackground(new Color(179, 0, 0));
            	playerOneLight.setForeground(Color.WHITE);
            }
            else if(line.startsWith("P2OFFLINE")){
            	playerTwoLight.setText("Player Two OFFLINE");
            	playerTwoLight.setBackground(new Color(179, 0, 0));
            	playerTwoLight.setForeground(Color.WHITE);
            }
            else{
            	System.out.println(line);
            }
        }
    }
    
    
    /*
     * Builds the GUI 
     */
    private void buildGUI(){
        frame.setSize(1500, 800);
        frame.setLocationRelativeTo(null);
		//frameIcon = new File("gui_images/frame_icon.png");
		try{
			//BufferedImage icon = ImageIO.read(frameIcon);
			BufferedImage icon = ImageIO.read(Referee.class.getClassLoader().getResource("frame_icon.png"));;
			frame.setIconImage(icon);
		}
		catch(Exception e){
			e.printStackTrace();
		}
        
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0, 3, 0, 0));
        
        timeSelect = new JComboBox(timeValues);
        timeSelectLabel = new JLabel("Time: ");
        timeSelectLabelFont = new Font("Serif", 1, 15);
        timeSelectLabel.setFont(timeSelectLabelFont);
        
        //constraintSelect = new JComboBox(constraintValues);
        //percentFormat = NumberFormat.getNumberInstance();
        //percentFormat.setMinimumFractionDigits(2);
        //constraintSelect = new JFormattedTextField(percentFormat);
        constraintSelect = new JTextField(4);
        PlainDocument constraintSelectDoc = (PlainDocument) constraintSelect.getDocument();
        constraintSelectDoc.setDocumentFilter(new IntFilter());

        constraintSelectLabel = new JLabel("Constraint: ");
        constraintSelectLabel.setFont(timeSelectLabelFont);
        
        // Builds right-side panel //
        sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        JPanel sidePanelTop = new JPanel();
        sidePanelTop.setLayout(new GridLayout(0, 2, 0, 0));
        JPanel sidePanelBottom = new JPanel();
        sidePanelBottom.setLayout(new GridLayout(0, 2, 0, 0));
        sidePanel.add(sidePanelTop, BorderLayout.NORTH);
        sidePanel.add(sidePanelBottom, BorderLayout.CENTER);
        playerOneLight = new JButton("Player One OFFLINE");
        playerOneLight.setBackground(new Color(179, 0, 0));
        playerOneLight.setForeground(Color.WHITE);
        playerTwoLight = new JButton("Player Two OFFLINE");
        playerTwoLight.setBackground(new Color(179, 0, 0));
        playerTwoLight.setForeground(Color.WHITE);
      	Color lineBorderColor = new Color(200,100,0);
  		lineBorder = BorderFactory.createLineBorder(lineBorderColor);
      	//lineBorder = BorderFactory.createRaisedBevelBorder();
  		constraintSelectLabel.setBorder(lineBorder);
        timeSelectLabel.setBorder(lineBorder);
        sidePanelTop.add(timeSelectLabel);
        sidePanelTop.add(timeSelect);
        sidePanelTop.add(constraintSelectLabel);
        sidePanelTop.add(constraintSelect);
        sidePanelBottom.add(playerOneLight);
        sidePanelBottom.add(playerTwoLight);
        
        JScrollPane centerPanelScroll = new JScrollPane(centerPanel);
        centerPanelScroll.setPreferredSize(new Dimension(600,600));
        centerPanelScroll.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerPanelScroll.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        
        
        //mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanelScroll, BorderLayout.CENTER);
        mainPanel.add(sidePanel, BorderLayout.LINE_END);
        
        topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        topPanel.setPreferredSize(new Dimension(1500, 95));
        springLayout = new SpringLayout();
        topPanel.setLayout(springLayout);
        
        topPanelLabel = new JLabel();
        topPanelFont = new Font("Serif", 1, 40);
        topPanelLabel.setFont(topPanelFont);
        //logoImage = new ImageIcon("gui_images/logo.png");
        logoImage = new ImageIcon(Referee.class.getClassLoader().getResource("logo.png"));
        topPanelLabel.setIcon(logoImage);

        topPanel.add(topPanelLabel);
        //topPanel.add(timeSelectLabel);
        //topPanel.add(timeSelect);
        springLayout.putConstraint(SpringLayout.WEST, topPanelLabel, 0, SpringLayout.WEST, topPanel);
        springLayout.putConstraint(SpringLayout.NORTH, topPanelLabel, 0, SpringLayout.NORTH, topPanel);
        springLayout.putConstraint(SpringLayout.WEST, timeSelectLabel, 915, SpringLayout.WEST, topPanel);
        springLayout.putConstraint(SpringLayout.NORTH, timeSelectLabel, 15, SpringLayout.NORTH, topPanel);
        springLayout.putConstraint(SpringLayout.WEST, timeSelect, 1130, SpringLayout.WEST, topPanel);
        springLayout.putConstraint(SpringLayout.NORTH, timeSelect, 25, SpringLayout.NORTH, topPanel);
        
        try{
        	
        	File[] files = new File("master_images/").listFiles();
        	
        	for(File file : files){
        		if(file.isFile()){
        			pngFiles.add(file.getName());
        		}
        	}
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        
        buttons = new JButton[pngFiles.size()];
        icons = new ImageIcon[pngFiles.size()];
        for(int i = 0; i < pngFiles.size(); i++){
        	icons[i] = new ImageIcon("master_images/" + pngFiles.get(i));
        	buttons[i] = new JButton(icons[i]);
        	buttons[i].setText(pngFiles.get(i));
        	buttons[i].setFont(invisibleText);
        	buttons[i].setBackground(Color.WHITE);
        	
        	Border lineBorder;
        	Color screenBorderColor = new Color(200,100,0);
    		lineBorder = BorderFactory.createLineBorder(screenBorderColor);
        	lineBorder = BorderFactory.createRaisedBevelBorder();
    		buttons[i].setBorder(lineBorder);
    		int index = i;
    		buttons[i].addMouseListener(new MouseListener(){
				@Override
				public void mouseClicked(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseReleased(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {
					buttons[index].setBackground(Color.BLACK);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					buttons[index].setBackground(new Color(160,160,160));
				}
    			
    		});

        	buttons[i].addActionListener(new ActionListener(){

    			@Override
    			public void actionPerformed(ActionEvent e) {
    				//String constraint = constraintValues[constraintSelect.getSelectedIndex()];
    				String constraint = constraintSelect.getText();
    				if(constraint.isEmpty()){
    					constraint = "0";
    				}
    				try{
    					Float constFloat = Float.parseFloat(constraint);
    					if(constFloat > 100 || constFloat < 0){
    						JOptionPane.showMessageDialog(
    								new JFrame(),
    								"Constraint range is 0-100.",
    								"Invalid Constraint Value",
    								JOptionPane.ERROR_MESSAGE
    								);
    							
    						
    					}
    				}
    				catch(Exception e1){
   						JOptionPane.showMessageDialog(
								new JFrame(),
								"Constraint value must be a decimal or integer.",
								"Invalid Constraint Value",
								JOptionPane.ERROR_MESSAGE
								);
    					e1.printStackTrace();
    				}
    				//int index = constraint.lastIndexOf('%');
    				//constraint = constraint.substring(0, index);
    				out.println(e.getActionCommand() + "&" + timeValues[timeSelect.getSelectedIndex()] + "&" + constraint);
    			}
            });
        	
            centerPanel.add(buttons[i]);
        }
        
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);
        //frame.add(bottomPanel, BorderLayout.SOUTH);
        //frame.setResizable(false);
        frame.pack();
        System.out.println(frame.getWidth());
        System.out.println(frame.getHeight());
        
    }
    
    class IntFilter extends DocumentFilter{

    	@Override
    	public void replace(FilterBypass fb, int offset, int length, String text,
    		AttributeSet attrs) throws BadLocationException {

    	     Document doc = fb.getDocument();
    	     StringBuilder sb = new StringBuilder();
    	     sb.append(doc.getText(0, doc.getLength()));
    	     sb.replace(offset, offset + length, text);

    	     if (test(sb.toString())) {
    	    	 if(sb.length() < 6 && sb.indexOf("f") < 0)
    	    		 super.replace(fb, offset, length, text, attrs);
    	     }
    	     else {
    	    	 System.out.println("Bad input");
    	     }
    	}

    	@Override
    	public void remove(FilterBypass fb, int offset, int length)
    		throws BadLocationException {
    		   
    		super.remove(fb, offset, 1);
    	}
    	
    	private boolean test(String text) {
    		//System.out.println("Test: " + text + ", code = " + code + ", length = " + length + ", offset = " + offset);	
    		try {   	  
    			Float.parseFloat(text);  
    			return true;
    		} 
    		catch (NumberFormatException e) {
    			return false;
    		}
    	}
    }
}
