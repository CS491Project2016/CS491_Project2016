import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class DrawImage extends SwingWorker {
	StringBuilder code;
	int imageId;
	float percentage, constraint;
	boolean drawing;
	JFrame frame;
	JLabel percentageLabel, label_2;
	String masterImage, finalImage;
	PrintWriter out;
	ClientOne clientOne = null;
	ClientTwo clientTwo = null;
	PrintWriter writer;
	ProcessBuilder process;
	File classFile, newImageFile, oldImageFile;
	
	public DrawImage(ClientOne client, StringBuilder code, float percentage, JLabel percentageLabel, String masterImage, String finalImage, 
									int imageId, float constraint, PrintWriter out, JLabel label_2, JFrame frame, boolean drawing){
		
		this.clientOne = client;
		this.code = code;
	    this.percentage = percentage;
	    this.percentageLabel = percentageLabel;
	    this.masterImage = masterImage;
	    this.finalImage = finalImage;
	    this.constraint = constraint;
	    this.out = out;
	    this.imageId = imageId;
	    this.label_2 = label_2;
	    this.frame = frame;
	    this.drawing = drawing;
	}
	
	public DrawImage(ClientTwo client, StringBuilder code, float percentage, JLabel percentageLabel, String masterImage, String finalImage, 
			int imageId, float constraint, PrintWriter out, JLabel label_2, JFrame frame, boolean drawing){

		this.clientTwo = client;
		this.code = code;
		this.percentage = percentage;
		this.percentageLabel = percentageLabel;
		this.masterImage = masterImage;
		this.finalImage = finalImage;
		this.constraint = constraint;
		this.out = out;
		this.imageId = imageId;
		this.label_2 = label_2;
		this.frame = frame;
		this.drawing = drawing;
	}

	@Override
	protected Object doInBackground() throws Exception {
		drawImage(code);
	    
		return true;
	}
	
	protected void drawImage(StringBuilder code){
		System.out.println("drawImage");
		
		if(clientTwo == null){
			clientOne.label_2.setIcon(new ImageIcon("gui_images/redrawing.png"));
		}
		else{
			clientTwo.label_2.setIcon(new ImageIcon("gui_images/redrawing.png"));
		}
    	//creates the ProcessingDraw.java file that draws the players image
    	createJavaFile();
    	//builds and runs the ProcessingDraw.java file
    	runJavaFile();

    	if(clientTwo == null){
    		clientOne.percentage = getComparePercentage(new File("master_images/" + clientOne.masterImage), new File(clientOne.finalImage));
    		clientOne.percentage = 100-clientOne.percentage;
    		if(clientOne.percentageLabel != null){
    			clientOne.percentageLabel.setText("Difference: " + Float.toString(clientOne.percentage) + "%");
    		}
    		System.out.println("Percentage = " + clientOne.percentage + " clientOne.contraint = " + clientOne.constraint);
    		if(clientOne.percentage == (float)clientOne.constraint || clientOne.percentage < clientOne.constraint){
    		//if(percentage == clientOne.constraint){
    			System.out.println("p1 constraint met");
    			clientOne.setDrawingFalse();
    			//clientOne.endGame();
    			clientOne.out.println("P1WINS");
    		}
    	}
    	else{
    		clientTwo.percentage = getComparePercentage(new File("master_images/" + clientTwo.masterImage), new File(clientTwo.finalImage));
    		clientTwo.percentage = 100-clientTwo.percentage;
    		if(clientTwo.percentageLabel != null){
    			clientTwo.percentageLabel.setText("Difference: " + Float.toString(clientTwo.percentage) + "%");
    		}
    		System.out.println("Percentage = " + clientTwo.percentage + " clientTwo.contraint = " + clientTwo.constraint);
    		if(clientTwo.percentage == (float)clientTwo.constraint || clientTwo.percentage < clientTwo.constraint){
    		//if(percentage == clientTwo.constraint || percentage < clientTwo.constraint){
    			System.out.println("p2 constraint met");
    			clientTwo.setDrawingFalse();
    			//clientTwo.endGame();
    			clientTwo.out.println("P2WINS");
    		}
    	}

    	if(clientTwo == null){
    		clientOne.setDrawingFalse();
    	}
    	else{
    		clientTwo.setDrawingFalse();
    	}
    }
    
    private void createJavaFile(){
    	int index;
    	//change x2
    	String partA;
		if (clientTwo == null) {
			partA = "import processing.core.PApplet;public class ProcessingDrawOne extends PApplet{public static void main(String[] args) {PApplet.main(\"ProcessingDrawOne\");}"
					+ "public void settings(){size(300,300);}public void setup(){fill(125);}";
		}
		else{
			partA = "import processing.core.PApplet;public class ProcessingDrawTwo extends PApplet{public static void main(String[] args) {PApplet.main(\"ProcessingDrawTwo\");}"
					+ "public void settings(){size(300,300);}public void setup(){fill(125);}";
		}
    	String partB = "}";
    	index = code.lastIndexOf("}");
    	//change
    	if (clientTwo == null) {
    		code.insert(index, "save(\"playerOneTemp" + imageId + ".png\"); System.exit(1);");
    	}
    	else{
    		code.insert(index, "save(\"playerTwoTemp" + imageId + ".png\"); System.exit(1);");
    	}
    	
		try{
			//change
			if (clientTwo == null) {
				writer = new PrintWriter("ProcessingDrawOne.java", "UTF-8");
			}
			else{
				writer = new PrintWriter("ProcessingDrawTwo.java", "UTF-8");
			}
			
			writer.println(partA + code + partB);
			writer.close();
			code.setLength(0); //clear the StringBuilder
		}
		catch(Exception e){
			e.printStackTrace();
		}
    }
    
    private void runJavaFile(){
    	System.out.println("pointA");
    	try{
    		/***** Compiles .java file *****/
    		Runtime runtime = Runtime.getRuntime();
    		//change (ProcessingDraw2.java)
    		if (clientTwo == null) {
    			process = new ProcessBuilder("javac", "ProcessingDrawOne.java");
    		}
    		else{
    			process = new ProcessBuilder("javac", "ProcessingDrawTwo.java");
    		}
    		
    		Process p = process.start();
    		p.waitFor();
    		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    		String line;
    		StringBuilder error = new StringBuilder();
    		
    		while((line = reader.readLine()) != null){
    			error.append(line);
    		}
    		
    		if(error.length() > 0){
    			System.out.println("Error in code: " + error.toString());
    		}
    		//change
    		if (clientTwo == null) {
    			classFile = new File("ProcessingDrawOne.class");
    		}
    		else{
    			classFile = new File("ProcessingDrawTwo.class");
    		}
    		
    		if(error.length() == 0){
    			/***** Runs java file *****/
    			//change
    			if (clientTwo == null) {
    				process = new ProcessBuilder("java", "ProcessingDrawOne");
    			}
    			else{
    				process = new ProcessBuilder("java", "ProcessingDrawTwo");
    			}
	 
	    		p = process.start();
	    		p.waitFor();
	    		classFile.delete();
	    		
	    		/***** Inserts new image to player image panel *****/
	    		//change
	    		if (clientTwo == null) {
	    			newImageFile = new File("playerOneTemp" + imageId + ".png");
	    		}
	    		else{
	    			newImageFile = new File("playerTwoTemp" + imageId + ".png");
	    		}
	    	  	
	        	while(!newImageFile.exists()){
	        		//System.out.println("image does not exist.");
	        	}
	        	try{
	        		//sleep: waits for Processing to release playerOneTemp.png
	        		TimeUnit.SECONDS.sleep(1);
	        	}
	        	catch(Exception e){
	        		e.printStackTrace();
	        	}
	        	System.out.println("pointB");
	        	
	        	//change
	        	if (clientTwo == null) {
	        		label_2.setIcon(new ImageIcon("playerOneTemp" + imageId + ".png"));
	        	}
	        	else{
	        		label_2.setIcon(new ImageIcon("playerTwoTemp" + imageId + ".png"));
	        	}
	       
	            //System.out.println("setting label_2: " + "playerOneTemp" + imageId + ".png" + ", imageId = " + imageId);
	            
	            if(imageId > 0){
	            	//change
	            	if (clientTwo == null) {
	            		oldImageFile = new File("playerOneTemp" + (imageId-1) + ".png");
	            	}
	            	else{
	            		oldImageFile = new File("playerTwoTemp" + (imageId-1) + ".png");
	            	}
	       
	            	oldImageFile.delete();
	            }
	            /*else{
	            	File imageFile = new File("playerOneTemp0.png");
	            	imageFile.delete();
	            }*/
	            
	            frame.revalidate();
	            
	            //change
	            if (clientTwo == null) {
	            	clientOne.finalImage = "playerOneTemp" + imageId + ".png";
	            }
	            else{
	            	clientTwo.finalImage = "playerTwoTemp" + imageId + ".png";
	            }
	            
	        	if(clientTwo == null){
	        		clientOne.imageId++;
	        	}
	        	else{
	        		clientTwo.imageId++;
	        	}
	            
	            System.out.println("P1 at post increment of imageId");
    		}
    		else{
    	 		JOptionPane.showMessageDialog(
            			new JFrame(), 
            			"The code you have written did not compile.", 
            			"Code Error", 
            			JOptionPane.ERROR_MESSAGE
            			);
    		}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public float getComparePercentage(File fileA, File fileB) {

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
}
