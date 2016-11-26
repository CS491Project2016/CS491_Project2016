import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.AttributedCharacterIterator;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.text.Caret;
import javax.xml.bind.DatatypeConverter;

public class ClientOne {
	
	boolean endGame = false, p1Wins = false, p2Wins = false;
	volatile boolean drawing = false;
	static ClientOne client;
	static StringBuilder code = new StringBuilder();
	String id = "p1", image; //change
	static int imageId = 0;
	float percentage;
	Timer timer;
    BufferedReader in;
    Border lineBorder;
    PrintWriter out;
    JFrame frame;
    JPanel mainPanel, container, topPanel, bottomPanel, timePanel, imagePanel_One, imagePanel_Two, redrawBtnPanel;
    JLabel label_1, label_2, label_3, willBegin, rules, tempLabel, timeLabel, percentageLabel, practice, logoLabel, 
    	resultsImageLabel, masterImageLabel, playerImageLabel, differenceImageLabel, winOrLoseLabel, opponentImage, opponentImageText, opponentDiffImage, opponentDiffImageText, opponentDiffPercentLabel;
    ImageIcon image_2 = new ImageIcon(), logoImage, resultsImage, blankCanvasIcon;
    JTextArea textArea;
    JScrollPane scrollPane;
    JButton redrawBtn, help, newGameBtn, exitBtn;
    Font willBeginFont, timeLabelFont, percentageLabelFont, redrawBtnFont, textAreaFont, resultScreenFont;
    String IPAddress, winner = null, p2FinalData = null; //change p2FinalData
    String[] resultPacket;
    int port, time, constraint;
    float p1Percent, p2Percent;
    static String masterImage, finalImage;
    SpringLayout springLayout;
    File frameIcon;
    BufferedImage redrawBtnIcon;
    DrawImage drawImage;

    public ClientOne() {
    	
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
    	
        /***** Create Initial GUI *****/
    	buildGUI();
    }
    
    public static void main(String[] args) throws Exception {
        client = new ClientOne();
        //client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }

   /* private String getName() {
        return JOptionPane.showInputDialog(frame, "Choose a screen name:", "Screen name selection", JOptionPane.PLAIN_MESSAGE);
    }*/

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
            	//change
                out.println("PLAYERONE");
            }
            else if(line.startsWith("option")){
            	//System.out.println("Whole: " + line);
            	String[] argumentArray = new String[3];
            	argumentArray = line.split("&");
            	image = argumentArray[0];
            	time = Integer.parseInt(argumentArray[1]);
            	constraint = Integer.parseInt(argumentArray[2]);
            	if(masterImage == null){
            		out.println("Server message received.");
            		masterImage = image;
            		/*System.out.println("Line: " + line);
            		System.out.println("Image: " + image);
            		System.out.println("Time: " + time);*/
            		startGame(image, time);
            	}
            }
            /*else if(line.startsWith("RESULTS")){
            	System.out.println("Results, drawing =" + drawing);
            	if(drawing == false){
            		System.out.println("drawing =" + drawing);
            		endGame();
            	}
            	else{
            		while(drawing == true){
            			System.out.println("drawing");
            		}
            		
            		endGame();
            	}
            }*/
            else if(line.startsWith("P1ENDGAME")){
            	timer.stopTimer();
            	p1Wins = true;
            	endGame();
            }
            else if(line.startsWith("P2ENDGAME")){
            	timer.stopTimer();
            	p2Wins = true;
            	endGame();
            }
            else if(line.startsWith("PACKET")){
            	line = line.substring(6);
            	int index = 0, i = 0;
            	resultPacket = new String[7];
            	while(line.indexOf("SPLIT") >= 0){
            		//System.out.println(line);
            		index = line.indexOf("SPLIT");
            		//System.out.println(index);
            	    resultPacket[i] = line.substring(0, index);
            	    //System.out.println(resultPacket[i]);
            		line = line.substring(index+5);
            		if(i == 5){
            			resultPacket[i+1] = line;
            		}
            		System.out.println(line);
            		System.out.println("\n");
            		i++;
            	}
            	
            	//change switch indexes
            	differenceImageLabel.setText("Difference: " + resultPacket[0] + "%");
            	opponentDiffImageText.setText("Difference: " + resultPacket[1] + "%");
            	if(p1Wins == true){
            		winOrLoseLabel.setText("Player 1 Wins! Constraint met.");
            	}
            	else if(p1Wins == true){
            		winOrLoseLabel.setText("Player 2 Wins! Constraint met.");
            	}
            	else{
	                if(resultPacket[2].startsWith("P1")){
	                	System.out.println("AT WINNERP1");
	                	winOrLoseLabel.setText("PLAYER 1 WINS!");
	                }
	                else if(resultPacket[2].startsWith("P2")){
	                	System.out.println("AT WINNERP2");
	                	winOrLoseLabel.setText("PLAYER 2 WINS");
	                }
	                else if(resultPacket[2].startsWith("TIE")){
	                	System.out.println("AT WINNERTIE");
	                	//winner = "tie";
	                	winOrLoseLabel.setText("IT'S A TIE!");
	                }
            	}
                
                String p1FinalData = resultPacket[3]; 
                byte[] finalData1 = DatatypeConverter.parseBase64Binary(p1FinalData.substring(p1FinalData.indexOf(",") + 1)); //change
                BufferedImage bufferedImage1 = ImageIO.read(new ByteArrayInputStream(finalData1));
                ImageIO.write(bufferedImage1, "png", new File("p1MyFinalImage.png")); //change
                
                //change flip label_2 and opponentImage
                label_2.setText("");
                label_2.setIcon(new ImageIcon("p1MyFinalImage.png")); //change
                
              	String p2FinalData = resultPacket[4]; 
                byte[] finalData2 = DatatypeConverter.parseBase64Binary(p2FinalData.substring(p2FinalData.indexOf(",") + 1)); //change
                BufferedImage bufferedImage2 = ImageIO.read(new ByteArrayInputStream(finalData2));
                ImageIO.write(bufferedImage2, "png", new File("p1OpponentFinalImage.png")); //change
                
                opponentImage.setText("");
                opponentImage.setIcon(new ImageIcon("p1OpponentFinalImage.png")); //change
                
                String p2DiffData = resultPacket[6]; 
                byte[] diffData1 = DatatypeConverter.parseBase64Binary(p2DiffData.substring(p2DiffData.indexOf(",") + 1)); 
                BufferedImage bufferedImage3 = ImageIO.read(new ByteArrayInputStream(diffData1));
                ImageIO.write(bufferedImage3, "png", new File("p1OpponentDiffImage.png")); //change
                
                //change flip opponentDiffImage and resultsImageLabel
                opponentDiffImage.setText("");
                opponentDiffImage.setIcon(new ImageIcon("p1OpponentDiffImage.png")); //change
                
                String p1DiffData = resultPacket[5]; 
                byte[] diffData2 = DatatypeConverter.parseBase64Binary(p1DiffData.substring(p1DiffData.indexOf(",") + 1)); 
                BufferedImage bufferedImage4 = ImageIO.read(new ByteArrayInputStream(diffData2));
                ImageIO.write(bufferedImage4, "png", new File("p1MyDiffImage.png")); //change
                
                resultsImageLabel.setIcon(new ImageIcon("p1MyDiffImage.png")); //change
                     	
            	frame.revalidate();
            	frame.repaint();
            }
        }
    }
    
    /*
     * Builds initial GUI settings
     */
    private void buildGUI(){
    	if(frame == null){
    		//change
    		frame = new JFrame("Player One");
    	}
        frame.setSize(1500,768);
        frame.setLocationRelativeTo(null);
		//frameIcon = new File("gui_images/frame_icon.png");
		try{
			//BufferedImage icon = ImageIO.read(frameIcon);
			BufferedImage icon = ImageIO.read(ClientOne.class.getClassLoader().getResource("frame_icon.png"));;
			frame.setIconImage(icon);
		}
		catch(Exception e){
			e.printStackTrace();
		}
       
        logoLabel = new JLabel();
        logoLabel.setPreferredSize(new Dimension(1500, 95));
        //logoImage = new ImageIcon("gui_images/logo.png");
        logoImage = new ImageIcon(ClientOne.class.getClassLoader().getResource("logo.png"));
        logoLabel.setIcon(logoImage);
        
        willBegin = new JLabel("Your game will begin shortly...");
        willBeginFont = new Font("Serif", 1, 30);
        willBegin.setFont(willBeginFont);
        
        rules = new JLabel("Try to match the master image as close as you can.");
        rules.setFont(willBeginFont);
        
        practice = new JLabel("Practice Area");
        practice.setFont(willBeginFont);
        
        textArea = new JTextArea(10, 40);
        textArea.setCaretColor(Color.WHITE);

        textArea.setText("public void draw(){fill(255);rect(100,100,50,50);}");//delete
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textAreaFont = new Font("Serif", 1, 25);
        textArea.setFont(textAreaFont);
        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500,430));
        scrollPane.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Border lineBorder2;
    	Color screenBorderColor2 = new Color(200,100,0);
		lineBorder2 = BorderFactory.createLineBorder(screenBorderColor2);
    	//lineBorder = BorderFactory.createRaisedBevelBorder();
        scrollPane.setBorder(lineBorder2);
		textArea.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10 && e.isControlDown() && drawing == false){
					drawing = true;
					DrawImage drawImage = new DrawImage(client, code.append(textArea.getText()), percentage, percentageLabel, masterImage, finalImage, imageId, constraint, out, label_2, frame, drawing);
					drawImage.execute();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {}
		});
      
        label_2 = new JLabel();
        label_2.setOpaque(true);
    	Color lineBorderColor = new Color(200,100,0);
		lineBorder = BorderFactory.createLineBorder(lineBorderColor);
    	//lineBorder = BorderFactory.createRaisedBevelBorder();
        label_2.setBorder(lineBorder);
        blankCanvasIcon = new ImageIcon("gui_images/blank_canvas.png");
        label_2.setIcon(blankCanvasIcon);
        
        help = new JButton("Help");
        help.setPreferredSize(new Dimension(75,50));
        help.setBackground(new Color(160,160,160));
        help.setForeground(Color.WHITE);
        help.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				createHelpBox();
			}
        	
        });
        redrawBtn = new JButton("DRAW");
        redrawBtn.setPreferredSize(new Dimension(125,75));
        redrawBtn.setBackground(new Color(160,160,160));
        //redrawBtn.setContentAreaFilled(false);
        //redrawBtn.setBorderPainted(false);
        redrawBtn.setForeground(Color.WHITE);
        redrawBtnFont = new Font("Serif", 0 , 25);
        redrawBtn.setFont(redrawBtnFont);
        /*redrawBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("At action listener, drawing = " + drawing);
				if(drawing == false){
					//redrawBtn.setEnabled(false);
					System.out.println("DRAW ");
					drawing = true;
					DrawImage drawImage = new DrawImage(client, code.append(textArea.getText()), percentage, percentageLabel, masterImage, finalImage, imageId, constraint, out, label_2, frame, drawing);
					drawImage.execute();
					if(drawImage.isDone()){
						System.out.println("drawImage.isDone() = " + drawImage.isDone());
						drawImage.cancel(true);
					}
					//redrawImage(code.append(textArea.getText()));
					
					//redrawBtn.setEnabled(true);
				}
			}
        });*/
        redrawBtn.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("Drawing = " + drawing);
				if(drawing == false){
					drawing = true;
				    drawImage = new DrawImage(client, code.append(textArea.getText()), percentage, percentageLabel, masterImage, finalImage, imageId, constraint, out, label_2, frame, drawing);
					drawImage.execute();
				}
				else{
					System.out.println("CONSUMED");
					e.consume();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {
				redrawBtn.setBackground(Color.BLACK);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				redrawBtn.setBackground(new Color(160,160,160));
			}
        	
        });
        
        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(255,255, 240)); //Light Yellow
        //mainPanel.setBackground(new Color(244,234, 128)); //Yellow
        //mainPanel.setBackground(new Color(159,96, 96)); //Dark Red
        //mainPanel.setBackground(new Color(90,90, 90)); //Grey
        //mainPanel.setBackground(new Color(255,90,0)); //Orange
        //mainPanel.setPreferredSize(new Dimension(0, 200));
        springLayout = new SpringLayout();
        mainPanel.setLayout(springLayout);
        mainPanel.add(logoLabel);
        mainPanel.add(willBegin);
        mainPanel.add(rules);
        mainPanel.add(practice);
        mainPanel.add(scrollPane);
        mainPanel.add(label_2);
        mainPanel.add(redrawBtn);
        mainPanel.add(help);
        
        springLayout.putConstraint(SpringLayout.WEST, logoLabel, 0, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, logoLabel, 0, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, willBegin, 570, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, willBegin, 110, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, rules, 450, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, rules, 145, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, practice, 220, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, practice, 200, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, scrollPane, 75, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 250, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, label_2, 1110, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, label_2, 250, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, redrawBtn, 780, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, redrawBtn, 400, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, help, 1330, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, help, 600, SpringLayout.NORTH, mainPanel);
        
        frame.add(mainPanel);
        
        frame.addWindowListener(new WindowListener(){
			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {
				if(out != null){
					//change
					out.println("P1OFFLINE");
					System.exit(0);
				}
				else{
					System.exit(0);
				}
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
        
        //startGame("test");
    }
    
    /*
     * Builds remaining GUI elements 
     * Calls listener for redraw
     */
    private void startGame(String image, int time){

    	frame.remove(mainPanel);
    	
    	container = new JPanel();
    	topPanel = new JPanel();
    	topPanel.add(logoLabel);
    	container.setLayout(new BorderLayout());
    	container.add(topPanel, BorderLayout.NORTH);
    	container.add(mainPanel, BorderLayout.CENTER);
    	
    	mainPanel.remove(willBegin);
    	mainPanel.remove(rules);
    	mainPanel.remove(practice);
        mainPanel.remove(scrollPane);
        mainPanel.remove(label_2);
        mainPanel.remove(redrawBtn);
        mainPanel.remove(help);
        SpringLayout springLayout = new SpringLayout();
        mainPanel.setLayout(springLayout);
    	
        label_1 = new JLabel();
        ImageIcon image_1 = new ImageIcon("master_images/" + image);
        label_1.setIcon(image_1);
        
        imagePanel_One = new JPanel();
        imagePanel_One.add(label_1);
        
        imagePanel_Two = new JPanel();
        image_2 = new ImageIcon("gui_images/blank_canvas.png");
        label_2.setIcon(image_2);
        imagePanel_Two.add(label_2);
        
		timeLabel = new JLabel();
		timeLabelFont = new Font("Serif", 1, 22);
		timeLabel.setFont(timeLabelFont);
		
		percentageLabel = new JLabel("Difference: ");
		percentageLabelFont = new Font("Serif", 1, 22);
		percentageLabel.setFont(percentageLabelFont);
        
        mainPanel.add(imagePanel_One);
        mainPanel.add(imagePanel_Two);
        mainPanel.add(scrollPane);
        mainPanel.add(redrawBtn);
        mainPanel.add(timeLabel);
        mainPanel.add(percentageLabel);
        
        springLayout.putConstraint(SpringLayout.WEST, imagePanel_One, 700, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, imagePanel_One, 50, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, imagePanel_Two, 1100, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, imagePanel_Two, 50, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, scrollPane, 75, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 50, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, redrawBtn, 450, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, redrawBtn, 500, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, timeLabel, 1010, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, timeLabel, 400, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, percentageLabel, 1010, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, percentageLabel, 450, SpringLayout.NORTH, mainPanel);
   
        frame.add(container);
        //frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        System.out.println("Frame size: " + frame.getSize());
        
        timer = new Timer(this, frame, timeLabel, out, id, time);
        timer.execute();
    }

    private void createHelpBox(){
    	JFrame helpBox = new JFrame();
    	helpBox.setSize(600, 800);
    	helpBox.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	helpBox.setLocationRelativeTo(null);
    	
    	JPanel mainPanel = new JPanel();
    	SpringLayout springLayout = new SpringLayout();
    	mainPanel.setLayout(springLayout);
    	JLabel title = new JLabel("Basic Operations");
    	JLabel rectangle = new JLabel("Rectangle: rect(x-axis, y-axis, width, height);");

    	springLayout.putConstraint(SpringLayout.WEST, title, 250, SpringLayout.WEST, mainPanel);
    	springLayout.putConstraint(SpringLayout.NORTH, title, 20, SpringLayout.NORTH, mainPanel);
    	
    	springLayout.putConstraint(SpringLayout.WEST, rectangle, 170, SpringLayout.WEST, mainPanel);
    	springLayout.putConstraint(SpringLayout.NORTH, rectangle, 50, SpringLayout.NORTH, mainPanel);
    	
    	mainPanel.add(title);
    	mainPanel.add(rectangle);
    	
    	
    	helpBox.add(mainPanel);
    	helpBox.setVisible(true);
    }

    private void startNewGame(){
       	frame.dispose();
       	masterImage = null;
       	frame = new JFrame();
       	frame.setVisible(true);
       	buildGUI();
    }
    
    private void differenceImage(){
    	System.out.println("P1 in differenceImage");
    	
    	/***** Creates .java file *****/
    	code.insert(0, "import processing.core.PApplet; import processing.core.PImage;"
    			+ "public class differenceImageOne extends PApplet{" //change (differenceImageOne)
    			+ "String masterImage = \"master_images/" + masterImage + "\";String playerImage = \"" + finalImage + "\";"
    			+ "PImage imageOne;" 
    			+ "PImage imageTwo;int pixelYPosition;int pixelXPosition;public static void main(String[] args) {"
    			+ "PApplet.main(\"differenceImageOne\");}public void settings(){imageOne = loadImage(masterImage, \"png\");" //change (differenceImageOne)
    					+ "imageTwo = loadImage(playerImage, \"png\");size(300,300);}public void setup(){fill(125);}public void draw() {loadPixels();imageOne.loadPixels();imageTwo.loadPixels();"
    					+ "for (pixelYPosition = 0; pixelYPosition < imageOne.height; pixelYPosition++) {for (pixelXPosition = 0; pixelXPosition < imageOne.width; pixelXPosition++) {"
    					+ "int loc = pixelXPosition + pixelYPosition * imageOne.width;int loc1 = pixelXPosition + pixelYPosition * imageTwo.width;float r = red(imageOne.pixels[loc]);float g = green(imageOne.pixels[loc]);"
    					+ "float b = blue(imageOne.pixels[loc]);float r1 = red(imageTwo.pixels[loc1]);float r2 = red(imageTwo.pixels[loc1]);float r3 = red(imageTwo.pixels[loc1]);"
    					+ "if (!(r == r1) && (!(g == r2) && (!(b == r3)))) {r = 255;g = 0;b = 0;}pixels[loc] = color(r, g, b);}}updatePixels();"
    					+ "save(\"differenceImageOne.png\"); System.exit(1); }}"); //change (differenceImageOne)
    
		try{
			//change (differenceImageOne)
			PrintWriter writer = new PrintWriter("differenceImageOne.java", "UTF-8");
			writer.println(code);
			writer.close();
			code.setLength(0);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		/***** Compiles .java file *****/
		//change
		ProcessBuilder pb = new ProcessBuilder("javac", "differenceImageOne.java");
		try{
			Process process = pb.start();
			process.waitFor();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		/***** Runs java .class file *****/
		//change
		pb = new ProcessBuilder("java", "differenceImageOne");
		try{
			Process process = pb.start();
			process.waitFor();
		}
		catch(Exception e){
			e.printStackTrace();
		}
    } 
    
    public void buildResultScreen(){
    	System.out.println("P1 in buildResultsScreen, imageId = " + imageId);
    	frame.setSize(1500, 900);
    	
    	// Build container //
    	container.remove(mainPanel);
    	mainPanel = new JPanel();
    	springLayout = new SpringLayout();
    	mainPanel.setLayout(springLayout);
    	container.add(mainPanel, BorderLayout.CENTER);
    	
    	masterImageLabel = new JLabel("Referee");
    	playerImageLabel = new JLabel("You");
    	differenceImageLabel = new JLabel();
    	winOrLoseLabel = new JLabel("Winner? Waiting for results...");
    	resultScreenFont = new Font("Serif", 1, 20);
    	label_2 = new JLabel();
    	opponentImage = new JLabel("Waiting for results...");
    	opponentImageText = new JLabel("Opponent");
    	opponentDiffImage = new JLabel("Waiting for results..."); 
    	opponentDiffImageText = new JLabel(); 
    	masterImageLabel.setFont(resultScreenFont);
    	playerImageLabel.setFont(resultScreenFont);
    	differenceImageLabel.setFont(resultScreenFont);
    	winOrLoseLabel.setFont(resultScreenFont);
    	opponentImageText.setFont(resultScreenFont);
    	opponentDiffImageText.setFont(resultScreenFont);
    	
    	resultsImageLabel = new  JLabel();
    	if(imageId > 0){
    		resultsImage = new ImageIcon("differenceImageOne.png"); //change
    		resultsImageLabel.setIcon(resultsImage);
    		
    		label_2.setIcon(new ImageIcon(finalImage));
    	}
    	else{
    		resultsImageLabel.setText("No image available.");
    		resultsImageLabel.setText("No image available.");
    		resultsImageLabel.setVerticalAlignment(JLabel.CENTER);
    		resultsImageLabel.setPreferredSize(new Dimension(300,300));
    		
    		label_2.setText("No image available.");
    		label_2.setVerticalAlignment(JLabel.CENTER);
    		label_2.setPreferredSize(new Dimension(300,300));
    	}
    	
    	newGameBtn = new JButton("New Game");
        newGameBtn.setPreferredSize(new Dimension(147, 75));
        newGameBtn.setBackground(new Color(160,160,160));
        newGameBtn.setForeground(Color.WHITE);
        newGameBtn.setFont(redrawBtnFont);
        newGameBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				startNewGame();
			}
        	
        });
        
    	exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(125,75));
        exitBtn.setBackground(new Color(160,160,160));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFont(redrawBtnFont);
        exitBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
        	
        });
    	
        mainPanel.add(imagePanel_One);
        mainPanel.setBackground(Color.WHITE);
        //mainPanel.add(imagePanel_Two);
        mainPanel.add(label_2);
        mainPanel.add(resultsImageLabel);
        //mainPanel.add(percentageLabel);
        mainPanel.add(playerImageLabel);
        mainPanel.add(masterImageLabel);
        mainPanel.add(differenceImageLabel);
        mainPanel.add(winOrLoseLabel);
        mainPanel.add(opponentImage);
        mainPanel.add(opponentImageText);
        mainPanel.add(opponentDiffImage); 
        mainPanel.add(opponentDiffImageText); 
        mainPanel.add(newGameBtn);
        mainPanel.add(exitBtn);
        //timeLabel.setFont(timeLabelFont.deriveFont(18f));
        
        springLayout.putConstraint(SpringLayout.WEST, imagePanel_One, 50, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, imagePanel_One, 170, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, label_2, 450, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, label_2, 40, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, resultsImageLabel, 450, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, resultsImageLabel, 390, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, masterImageLabel, 50, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, masterImageLabel, 140, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, playerImageLabel, 450, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, playerImageLabel, 10, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, differenceImageLabel, 450, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, differenceImageLabel, 360, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, opponentImage, 1140, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, opponentImage, 40, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, opponentDiffImage, 1140, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, opponentDiffImage, 390, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, opponentImageText, 1140, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, opponentImageText, 10, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, opponentDiffImageText, 1140, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, opponentDiffImageText, 360, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, winOrLoseLabel, 850, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, winOrLoseLabel, 350, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, newGameBtn, 50, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, newGameBtn, 670, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, exitBtn, 200, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, exitBtn, 670, SpringLayout.NORTH, mainPanel);
        
        mainPanel.revalidate();
        mainPanel.repaint();

        // Begins sequence to send individual information to server //
        String p1FinalData;  //change
        String p1DiffData; //change
        BufferedImage p1Image, p1DiffImage;  //change x2
    	File imageFile1 = new File(finalImage);
    	File imageFile2 = new File("differenceImageOne.png"); //change
    	
    	try {
			p1Image = ImageIO.read(imageFile1);  //change
			ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
			ImageIO.write(p1Image, "png", baos1);  //change
	        p1FinalData = "1data:image/png;base64," +  //change x2 DONT FORGET 2data:image
	        		DatatypeConverter.printBase64Binary(baos1.toByteArray());
	        out.println(p1FinalData);  //change
    	}
    	catch (IOException e) {
 			e.printStackTrace();
 		}
    	
    	try {
			p1DiffImage = ImageIO.read(imageFile2);  //change
			ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
			ImageIO.write(p1DiffImage, "png", baos1);  //change
	        p1DiffData = "d1data:image/png;base64," +  //change DONT FORGET d2data:image
	        		DatatypeConverter.printBase64Binary(baos1.toByteArray());
	        out.println(p1DiffData);  //change
    	}
    	catch (IOException e) {
 			e.printStackTrace();
 		}
        
    	 //change
        out.println("PERCENTAGEONE" + percentage);   
    }
    
    protected void endGame(){
    	System.out.println("HERE at endGame(), drawing = " + drawing + ", imageId = " + imageId);
    	
    	if(drawing == false){
	    	try{
	    		if(imageId > 0){
	    			differenceImage();
	    		}
	    		buildResultScreen();
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
    	}
    	else{
    		//Synch on the variable 'drawing'. When DrawImage finally changes the value of 'drawing', client will be able to see it.
    		synchronized(this){
	    		while(drawing == true){
	    			try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
    		}
	    	try{
	    		if(imageId > 0){
	    			differenceImage();
	    		}
	    		buildResultScreen();
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
    	}
    }
    
    //Synch on 'drawing'
    public void setDrawingFalse(){
    	synchronized(this){
	    	drawing = false;
	    	this.notifyAll();
    	}
    }
}