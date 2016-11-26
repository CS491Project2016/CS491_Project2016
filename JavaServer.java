import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class JavaServer {

    private static int port = 3000;
    static ServerSocket listener;

    private static HashSet<String> names = new HashSet<String>();
    private static HashSet<String> finished = new HashSet<String>();
    private static HashSet<String> playersOnline = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
    private static PrintWriter refereeOut;
    
    static boolean constraintWinner = false;
    static JFrame frame;
    static JPanel mainPanel, topPanel, bottomPanel, logoPanel;
    static JLabel runningLabel, logoLabel, ipLabel, portLabel;
    static JButton btn;
    static ImageIcon logoImage;
    static Font serverFont;
    static float p1Percent = 200, p2Percent = 200;
    static String winner = null, p1FinalData = null, p2FinalData= null, p1DiffData = null, p2DiffData = null;
    static File frameIcon;

    public static void main(String[] args) throws Exception {
    	
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
        
        buildGUI();
    	
        System.out.println("The server is running.");
        
        try{
        	listener = new ServerSocket(port);
        }
        catch(Exception e){
			   JOptionPane.showMessageDialog(new JFrame(),
					    "Socket in use.",
					    "Socker Error",
					    JOptionPane.ERROR_MESSAGE
			   );
			   System.exit(0);
        }
        
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }
    
	private static void buildGUI(){
		frame = new JFrame();
		frame.setSize(1000,700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		//frameIcon = new File("gui_images/frame_icon.png");
		try{
			//BufferedImage icon = ImageIO.read(frameIcon);
			BufferedImage icon = ImageIO.read(JavaServer.class.getClassLoader().getResource("frame_icon.png"));;
			frame.setIconImage(icon);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		mainPanel = new JPanel();
		mainPanel.setBackground(Color.WHITE);
		//logoPanel = new JPanel();
		
		runningLabel = new JLabel();
		runningLabel.setText("<html><font color='black'>Server:</font> <font color='rgb(0, 179, 0)' >RUNNING</font></html>");
		serverFont = new Font("Serif", 1, 20);
		runningLabel.setFont(serverFont);
		runningLabel.setForeground(new Color(0, 179, 0));
		
		
		logoLabel = new JLabel();
		//logoImage = new ImageIcon("gui_images/logo_server.png");
		logoImage = new ImageIcon(JavaServer.class.getClassLoader().getResource("logo_server.png"));
		logoLabel.setIcon(logoImage);
		//logoPanel.add(logoLabel);
		
		ipLabel = new JLabel();
		ipLabel.setForeground(new Color(0, 179, 0));
		ipLabel.setFont(serverFont);
		try{
			ipLabel.setText("<html><font color='black'>IP Address: </font>" + "<font color='rgb(0, 179, 0)'>" + InetAddress.getLocalHost() + "</font></html>");
		}
		catch(Exception e){
			e.printStackTrace();
			ipLabel.setText("Could not determine your IP address.");
			ipLabel.setForeground(new Color(179, 0, 0));
		}
		
		portLabel = new JLabel("<html><font color='black'>Port: </font>" + "<font color= 'rgb(0, 179, 0)'>" + port + "</font></html>");
		portLabel.setForeground(new Color(0, 179, 0));
		portLabel.setFont(serverFont);
		
		//topPanel.add(label);
		
		btn = new JButton("Shutdown");
		btn.setPreferredSize(new Dimension(125, 75));
		btn.setBackground(new Color(160,160,160));
        btn.setForeground(Color.WHITE);
		btn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		
		//bottomPanel.add(btn);
		
		//BorderLayout bl = new BorderLayout();
		SpringLayout springLayout = new SpringLayout();
		mainPanel.setLayout(springLayout);
		mainPanel.add(logoLabel);
		mainPanel.add(runningLabel);
		mainPanel.add(btn);
		mainPanel.add(ipLabel);
		mainPanel.add(portLabel);
		
		springLayout.putConstraint(SpringLayout.WEST, logoLabel, 0 , SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, logoLabel, 0 , SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, runningLabel, 400 , SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, runningLabel, 250 , SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, ipLabel, 400 , SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, ipLabel, 300 , SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, portLabel, 400 , SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, portLabel, 350 , SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, btn, 400 , SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, btn, 500 , SpringLayout.NORTH, mainPanel);
		
		frame.add(mainPanel);
		frame.setVisible(true);
	}
    
	private static class Handler extends Thread {
		private String name;
	    private Socket socket;
	    private BufferedReader in;
	    private PrintWriter out;
	
	   public Handler(Socket socket) {
		   this.socket = socket;
	   }
	
	   public void run() {
		   try {
			   in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	           out = new PrintWriter(socket.getOutputStream(), true);
	
	           //gets name from client and stores it in 'names'
	           while (true) {
	        	   out.println("GETID");
	               name = in.readLine();
	               if (name == null) {
	            	   return;
	               }
	               synchronized (names) {
	            	   if (!names.contains(name)) {
	            		   names.add(name);
	            		   switch(name){
	            		   case "PLAYERONE": 
	            			   playersOnline.add("PLAYERONE");
	            			   if(names.contains("REFEREE")){
	            				   refereeOut.println("P1ONLINE");
	            			   }
	            			   break;
	            		   case "PLAYERTWO": 
	            			   playersOnline.add("PLAYERTWO");
	            			   if(names.contains("REFEREE")){
	            				   refereeOut.println("P2ONLINE");
	            			   }
	            			   break;
	            		   case "REFEREE": 
	            			   refereeOut = new PrintWriter(socket.getOutputStream(), true);
	            			   if(playersOnline.contains("PLAYERONE")){
	            				   refereeOut.println("P1ONLINE");
	            			   }
	            			   if(playersOnline.contains("PLAYERTWO")){
	            				   refereeOut.println("P2ONLINE");
	            			   }
	            			   break;
	            		   default: 
	            			   System.out.println("Name switch statement error.");
	            			   break;
	            		   }
	            		   
	                       break;
	                   }
	               }
	           }
	           
	           //add writer to set of writers
	           out.println(name + " HAS LOGGED IN");
	           writers.add(out);
	
	           //processes incoming protocols
	           while (true) {
	        	   String input = in.readLine();
	               if (input == null) {
	            	   return;
	               }
	               //sends image name to players
	               else if(input.startsWith("option")){
	            	   for(PrintWriter writer : writers){
	            		   writer.println(input);
	            	   }
	               }
	               else if(input.startsWith("PERCENTAGEONE") || input.startsWith("PERCENTAGETWO")){
	            	   System.out.println("At PERCENTAGE");
	            	   if(input.startsWith("PERCENTAGEONE")){
	            		   String substring = input.substring(13);
	            		   System.out.println("At PERCENTAGEONE");
	            		   p1Percent = Float.parseFloat(substring);
	            		   isResultPacketReady();
	            	   }
	            	   else if(input.startsWith("PERCENTAGETWO")){
	            		   String substring = input.substring(13);
	            		   System.out.println("At PERCENTAGETWO");
	            		   p2Percent = Float.parseFloat(substring);
	            		   isResultPacketReady();
	            	   }
	            	   
	            	   if(p1Percent != 200 && p2Percent != 200){
	            		   System.out.println("At PERCENTAGE out");
	            		   if(p2Percent > p1Percent){
	            			   winner = "P1";
	            			   isResultPacketReady();
	            		   }
	            		   else if(p1Percent > p2Percent){
	            			   winner = "P2";
	            			   isResultPacketReady();
	            		   }
	            		   else{
	            			   winner = "TIE";
	            			   isResultPacketReady();
	            		   }
	            	   }
	               }
	               else if(input.startsWith("1data:image")){
	            	   System.out.println("AT 1data:image");
	            	  p1FinalData = input.substring(1);
	            	  isResultPacketReady();
	               }
	               else if(input.startsWith("2data:image")){
	            	  System.out.println("AT 2data:image");
	            	  p2FinalData = input.substring(1);
	            	  isResultPacketReady();
	               }
	               else if(input.startsWith("d1data:image")){
	            	  System.out.println("AT d1data:image");
	            	  p1DiffData = input.substring(2);
	            	  isResultPacketReady();
	               }
	               else if(input.startsWith("d2data:image")){
	            	  System.out.println("AT d2data:image");
	            	  p2DiffData = input.substring(2);
	            	  isResultPacketReady();
	               }
	               //sends protocol to open browser at end of game
	               else if(input.startsWith("p1") || input.startsWith("p2")){
	            	   System.out.println(input + " is finished.");
	            	   finished.add(input);
	               }
	               else if(input.startsWith("P1OFFLINE")){
	            	   refereeOut.println("P1OFFLINE");
	            	   names.remove("PLAYERONE");
	            	   playersOnline.remove("PLAYERONE");
		           	   p1Percent = 200;
		        	  p1FinalData = null;
		        	  p1DiffData = null;
	               }
	               else if(input.startsWith("P2OFFLINE")){
	            	   refereeOut.println("P2OFFLINE");
	            	   names.remove("PLAYERTWO");
	            	   playersOnline.remove("PLAYERTWO");
		           	   p1Percent = 200;
		        	  p1FinalData = null;
		        	  p1DiffData = null;
	               }
	               else if(input.startsWith("P1WINS")){
	            	   if(constraintWinner == false){
	            		   constraintWinner = true;
	            		   for(PrintWriter writer : writers){
	            			   writer.println("P1ENDGAME");
	            		   }
	            	   }
	               }
	               else if(input.startsWith("P2WINS")){
	            	   for(PrintWriter writer : writers){
	            		   writer.println("P2ENDGAME");
	            	   }
	               }
	               
	               for (PrintWriter writer : writers) {
	            	   writer.println("MESSAGE " + name + ": " + input);
	               }
	           }
	       }catch (IOException e) {
	    	   e.printStackTrace();
	       } finally {
	    	   //socket closing procedures
	    	   if (name != null) {
	    		   names.remove(name);
	           }
	           if (out != null) {
	        	   writers.remove(out);
	           }
	           try {
	        	   socket.close();
	           } 
	           catch (IOException e) {
	           }
	         }
	   }
	}
	
	public static float compareImage(File fileA, File fileB) {

	    float percentage = 0;
	    try {
	        // take buffer data from both image files //
	        BufferedImage biA = ImageIO.read(fileA);
	        DataBuffer dbA = biA.getData().getDataBuffer();
	        int sizeA = dbA.getSize();
	        BufferedImage biB = ImageIO.read(fileB);
	        DataBuffer dbB = biB.getData().getDataBuffer();
	        int sizeB = dbB.getSize();
	        int count = 0;
	        // compare data-buffer objects //
	        if (sizeA == sizeB) {

	            for (int i = 0; i < sizeA; i++) {

	                if (dbA.getElem(i) == dbB.getElem(i)) {
	                    count = count + 1;
	                }

	            }
	            percentage = (count * 100) / sizeA;
	        } else {
	            System.out.println("Both the images are not of same size");
	        }

	    } catch (Exception e) {
	        System.out.println("Failed to compare image files ...");
	    }
	    return percentage;
	}
	
	private static void isResultPacketReady(){
		System.out.println("p1Percent is " + p1Percent);
		System.out.println("p2Percent is " + p2Percent);
		System.out.println("winner is " + winner);
		System.out.println("p1FinalData is \n" + p1FinalData);
		System.out.println("p2FinalData is \n" + p2FinalData);
		System.out.println("p1DiffData is \n" + p1DiffData);
		System.out.println("p2DiffData is \n" + p2DiffData);
		
	    if(p1Percent < 200 && p2Percent< 200 && winner != null && p1FinalData != null && p2FinalData != null && p1DiffData != null && p2DiffData != null){
	    	
	    	for(PrintWriter writer : writers){
	    		finished.clear();
	    		writer.println("PACKET" + p1Percent + "SPLIT" + p2Percent + "SPLIT" + winner + "SPLIT" + p1FinalData + "SPLIT" + p2FinalData + "SPLIT" + p1DiffData + "SPLIT" + p2DiffData);
	    	}
	    	
    		p1Percent = 200;
    		p2Percent = 200;
    		winner = null;
    		p1FinalData = null;
    		p2FinalData = null;
    		p1DiffData = null;
    		p2DiffData = null;
	    }
	}
}