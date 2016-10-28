package server_test;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.bind.DatatypeConverter;

public class ClientOne {

	StringBuilder code = new StringBuilder();
	String id = "ClientOne";
	int imageId = 0;
	Timer timer;
    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("ClientOne");
    JPanel mainPanel, bottomPanel;
    JLabel label_1, label_3, willBegin, tempLabel;
    JLabel label_2 = new JLabel();
    ImageIcon image_2 = new ImageIcon();
    JPanel imagePanel_One, imagePanel_Two;
    JTextArea textArea;
    JButton redrawBtn;
    String IPAddress;
    static String masterImage, finalImage;

    public ClientOne() {
    	
    	//String IPAddress = (String)JOptionPane.showInputDialog(frame, "Enter the IP address of the host.", JOptionPane.PLAIN_MESSAGE);
    	
        /***** Layout *****/
    	buildGUI();

        /*textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });*/
    }
    
    public static void main(String[] args) throws Exception {
        ClientOne client = new ClientOne();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }

   /* private String getName() {
        return JOptionPane.showInputDialog(frame, "Choose a screen name:", "Screen name selection", JOptionPane.PLAIN_MESSAGE);
    }*/

    private void run() throws IOException {

        // Make connection and initialize streams
        String serverAddress = "localhost";
        Socket socket = new Socket(serverAddress, 3000);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            if (line.startsWith("GETID")) {
                out.println("PLAYERONE");
            }
            else if(line.startsWith("option_1")){
            	masterImage = line + ".png";
            	startGame(line);
            }
        }
    }
    
    /*
     * Builds initial GUI settings
     */
    private void buildGUI(){	
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        
        willBegin = new JLabel("Your game will begin shortly...");
        
        mainPanel = new JPanel();
        mainPanel.add(willBegin);
        mainPanel.setLayout(new GridLayout(0, 2, 10, 10));
        
        bottomPanel = new JPanel();
        
        frame.add(mainPanel, BorderLayout.PAGE_START);
        frame.add(bottomPanel, BorderLayout.PAGE_END);
    }
    
    /*
     * Builds remaining GUI elements 
     * Calls listener for redraw
     */
    private void startGame(String image){
    	frame.setTitle(image);
    
    	mainPanel.remove(willBegin);
    	
        label_1 = new JLabel();
        ImageIcon image_1 = new ImageIcon("C:/Users/danm5/workspace Java/server_test/" + image + ".png");
        label_1.setIcon(image_1);
        
        imagePanel_One = new JPanel();
        imagePanel_One.add(label_1);
        
        imagePanel_Two = new JPanel();
        image_2 = new ImageIcon("option_2.png");
        label_2.setIcon(image_2);
        imagePanel_Two.add(label_2);
        
        textArea = new JTextArea(20, 30);
        
        redrawBtn = new JButton("Redraw");
        
		JPanel timePanel = new JPanel();
		JLabel timeLabel = new JLabel("Time goes here.");
		timePanel.add(timeLabel);
        
        mainPanel.add(imagePanel_One);
        mainPanel.add(imagePanel_Two);
        mainPanel.add(textArea);
        mainPanel.add(redrawBtn);
        
        bottomPanel.add(timePanel, "CENTER");
    	
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        timer = new Timer(frame, timeLabel);
        timer.execute();
        
        redrawBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				redrawImage(code.append(textArea.getText()));
			}
        	
        });
    }
    
    /*
     * User image redraw process
     */
    private void redrawImage(StringBuilder code){
    	//creates the ProcessingDraw.java file that draws the players image
    	createJavaFile();
    	//builds and runs the ProcessingDraw.java file
    	runJavaFile();
    	
    	File newFile = new File("playerOneTemp" + imageId + ".png");
    	while(!newFile.exists()){
    		//System.out.println("image does not exist.");
    	}
    	try{
    		//sleep: waits for Processing to release playerOneTemp.png
    		TimeUnit.SECONDS.sleep(1);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	
        label_2.setIcon(new ImageIcon("playerOneTemp" + imageId + ".png"));
        
        frame.revalidate();
        
        finalImage = "playerOneTemp" + imageId + ".png";
        imageId++;
    }
    
    private void createJavaFile(){
    	int index;
    	String partA = "import processing.core.PApplet;public class ProcessingDraw extends PApplet{public static void main(String[] args) {PApplet.main(\"ProcessingDraw\");}"
				+ "public void settings(){size(300,300);}public void setup(){fill(125);}";
    	String partB = "}";
    	index = code.lastIndexOf("}");
    	code.insert(index-1, "save(\"playerOneTemp" + imageId + ".png\"); System.exit(1);");
    	
		try{
			PrintWriter writer = new PrintWriter("ProcessingDraw.java", "UTF-8");
			writer.println(partA + code + partB);
			writer.close();
			code.setLength(0); //clear the StringBuilder
		}
		catch(Exception e){
			e.printStackTrace();
		}
    }
    
    private void runJavaFile(){
    	try{
    		Runtime runtime = Runtime.getRuntime();
    		Process process = runtime.exec("javac ProcessingDraw.java");
    		
    		File classFile = new File("ProcessingDraw.class");
    		while(!classFile.exists()){
    			System.out.println(".class Does not exists");
    		}
    		process = runtime.exec("java ProcessingDraw");
    		try{
        		//sleep: waits for ProcessingDraw.class to finish before deletion
        		TimeUnit.SECONDS.sleep(1);
        	}
        	catch(Exception e){
        		e.printStackTrace();
        	}
    		while(classFile.exists()){
    			System.out.println("HERE");
    			classFile.delete();
    		}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    /*
     * Sends master image/final user image data back to Timer instance to send to webpage.
     */
    public static String getImageData() throws Exception{
    	BufferedImage image1;
    	String params;
    
    	File imageFile1 = new File(masterImage);
    	image1 = ImageIO.read(imageFile1);
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ImageIO.write(image1, "png", baos1);
        String imageString1 = "data:image/png;base64," +
        		DatatypeConverter.printBase64Binary(baos1.toByteArray());
        	
    	File imageFile2 = new File(finalImage);
    	image1 = ImageIO.read(imageFile1);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        ImageIO.write(image1, "png", baos2);
        String imageString2 = "data:image/png;base64," +
        		DatatypeConverter.printBase64Binary(baos2.toByteArray());
        
        System.out.println(URLEncoder.encode(imageString1, "UTF-8"));
        	
        params = "image1=" + URLEncoder.encode(imageString1, "UTF-8") + "&image2=" + URLEncoder.encode(imageString2, "UTF-8");
        
    	return params;
    }
}
