import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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
	private int imageId;
	private StringBuilder code;
	private ClientOne clientOne = null;
	private ClientTwo clientTwo = null;
	private PrintWriter writer;
	private ProcessBuilder process;
	private File newImageFile, oldImageFile, oldJavaFile, oldClassFile;
	private JFrame frame;
	private JLabel myImageLabel;
	
	public DrawImage(ClientOne client, StringBuilder code, int imageId, JLabel myImageLabel, JFrame frame){
		
		this.clientOne = client;
		this.code = code;
	    this.imageId = imageId;
	    this.myImageLabel = myImageLabel;
	    this.frame = frame;
	}
	
	public DrawImage(ClientTwo client, StringBuilder code, int imageId, JLabel myImageLabel, JFrame frame){

		this.clientTwo = client;
		this.code = code;
		this.imageId = imageId;
		this.myImageLabel = myImageLabel;
		this.frame = frame;
	}

	@Override
	protected Object doInBackground() throws Exception {
		drawImage(code);
	    
		return true;
	}
	
	protected void drawImage(StringBuilder code){
		System.out.println("drawImage");
		
		if(clientTwo == null){
			clientOne.myImageLabel.setIcon(new ImageIcon("gui_images/redrawing.png"));
		}
		else{
			clientTwo.myImageLabel.setIcon(new ImageIcon("gui_images/redrawing.png"));
		}
    	//creates the ProcessingDraw.java file that draws the players image
    	createJavaFile();
    	//builds and runs the ProcessingDraw.java file
    	runJavaFile();

    	if(clientTwo == null){
    		clientOne.percentage = getComparePercentage(new File("master_images/" + clientOne.masterImage), new File(clientOne.finalImage));
    		String percentStringFormatted = String.format("%.2f", clientOne.percentage);
    		clientOne.percentage = Float.parseFloat(percentStringFormatted);
    		if(clientOne.percentageLabel != null){
    			clientOne.percentageLabel.setText("Similarity: " + Float.toString(clientOne.percentage) + "%");
    		}
    		System.out.println("Percentage = " + clientOne.percentage + " clientOne.contraint = " + clientOne.constraint);
    		if(clientOne.percentage == (float)clientOne.constraint || clientOne.percentage < clientOne.constraint){
    			clientOne.setDrawingFalse();
    			clientOne.out.println("P1WINS");
    		}
    	}
    	else{
    		clientTwo.percentage = getComparePercentage(new File("master_images/" + clientTwo.masterImage), new File(clientTwo.finalImage));
    		String percentStringFormatted = String.format("%.2f", clientTwo.percentage);
    		clientTwo.percentage = Float.parseFloat(percentStringFormatted);
    		if(clientTwo.percentageLabel != null){
    			clientTwo.percentageLabel.setText("Similarity: " + Float.toString(clientTwo.percentage) + "%");
    		}
    		System.out.println("Percentage = " + clientTwo.percentage + " clientTwo.contraint = " + clientTwo.constraint);
    		if(clientTwo.percentage == (float)clientTwo.constraint || clientTwo.percentage < clientTwo.constraint){
    			clientTwo.setDrawingFalse();
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
					+ "public void settings(){size(300,300);}public void setup(){background(255);}";
		}
		else{
			partA = "import processing.core.PApplet;public class ProcessingDrawTwo extends PApplet{public static void main(String[] args) {PApplet.main(\"ProcessingDrawTwo\");}"
					+ "public void settings(){size(300,300);}public void setup(){background(255);}";
		}
    	String partB = "}";
    	index = code.lastIndexOf("}");
    	//change
    	if (clientTwo == null) {
    		code.insert(index, "save(\"playerOneTemp" + imageId + ".png\"); System.exit(0);");
    	}
    	else{
    		code.insert(index, "save(\"playerTwoTemp" + imageId + ".png\"); System.exit(0);");
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
    		
    		if(error.length() == 0){
    			/***** Runs java file *****/
    			if (clientTwo == null) {
    				process = new ProcessBuilder("java", "ProcessingDrawOne"); //run file process
    			}
    			else{
    				process = new ProcessBuilder("java", "ProcessingDrawTwo");  //run file process
    			}
	 
	    		p = process.start();
	    		p.waitFor();

	    		/***** Inserts new image to player image panel *****/
	    		//change
	    		/*if (clientTwo == null) {
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
	        	}*/
	        	
	        	//change
	        	if (clientTwo == null) {
	        		//System.out.println("p1 inserting: " + "playerOneTemp" + imageId + ".png");
	        		clientOne.myImageLabel.setIcon(new ImageIcon("playerOneTemp" + imageId + ".png"));
	        	     /*BufferedImage imgA = null;
	        		    BufferedImage imgB = null;
	        	        
	        	        try {
	        				imgA = ImageIO.read(new File("playerOneTemp" + imageId + ".png"));
	        			    imgB = ImageIO.read(new File("gui_images/grid.png"));
	        			} catch (IOException e1) {
	        				// TODO Auto-generated catch block
	        				e1.printStackTrace();
	        			}
	        	        
	        	        float alpha = 0.5f;
	        	        int compositeRule = AlphaComposite.SRC_OVER;
	        	        AlphaComposite ac;
	        	        int imgW = imgA.getWidth();
	        	        int imgH = imgA.getHeight();
	        	        BufferedImage overlay = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
	        	        Graphics2D g = overlay.createGraphics();
	        	        ac = AlphaComposite.getInstance(compositeRule, alpha);
	        	        g.drawImage(imgA,0,0,null);
	        	        g.setComposite(ac);
	        	        g.drawImage(imgB,0,0,null);
	        	        g.setComposite(ac);
	        	        try {
	        				ImageIO.write(overlay, "PNG", new File("gui_images/composite.png"));
	        			} catch (IOException e1) {
	        				// TODO Auto-generated catch block
	        				e1.printStackTrace();
	        			}
	        	        g.dispose();*/
	        	        //clientOne.label_2.setIcon(new ImageIcon("gui_images/composite.png"));
	        		
	        	}
	        	else{
	        		//System.out.println("p2 inserting: " + "playerTwoTemp" + imageId + ".png");
	        		clientTwo.myImageLabel.setIcon(new ImageIcon("playerTwoTemp" + imageId + ".png"));
	        	}
	        	
	        	 frame.revalidate();
	            
	            //change
	            //post image processing procedures for clientOne and clientTwo, respectively
	            if (clientTwo == null) {
	            	if(imageId > 0){
	            		oldImageFile = new File("playerOneTemp" + (imageId-1) + ".png");
	            		oldImageFile.delete(); //deletes previous image file
	            	}
	            	clientOne.finalImage = "playerOneTemp" + imageId + ".png";
            		oldJavaFile = new File("ProcessingDrawOne.java");
            		oldClassFile = new File("ProcessingDrawOne.class");
            		clientOne.imageId++;
	            }
	            else{
	            	if(imageId > 0){
	            		oldImageFile = new File("playerTwoTemp" + (imageId-1) + ".png");
	            		oldImageFile.delete(); //deletes previous image file
	            	}
	            	clientTwo.finalImage = "playerTwoTemp" + imageId + ".png";
            		oldJavaFile = new File("ProcessingDrawTwo.java");
            		oldClassFile = new File("ProcessingDrawTwo.class");
            		clientTwo.imageId++;
	            }
	            
            	oldJavaFile.delete(); //deletes previous .java file
        		oldClassFile.delete(); //deletes previous .class file
    		}
    		else{	
    			System.out.println("Error in code: " + error.toString());
    			error.setLength(0);
    			if(clientTwo == null){
    				clientOne.myImageLabel.setIcon(new ImageIcon("gui_images/comp_error.png"));
    				clientOne.setDrawingFalse();
    			}
    			else{
    				clientTwo.myImageLabel.setIcon(new ImageIcon("gui_images/comp_error.png"));
    				clientTwo.setDrawingFalse();
    			}
    	 		JOptionPane.showMessageDialog(
            			new JFrame(), 
            			"The code did not compile.", 
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
	    int total = 0, r, g, b;
		
	    try {
	        // take buffer data from both image files //
	        BufferedImage biA = ImageIO.read(fileA);
	        int sizeA = (biA.getWidth() * biA.getHeight());
	       
	        BufferedImage biB = ImageIO.read(fileB);
	        int sizeB = (biB.getWidth() * biB.getHeight());
	        
	        float count = 0;
	        
	        // compare data-buffer objects //
	        if (sizeA == sizeB) {
	            for(int x = 0; x < biA.getWidth(); x++){
	            	for(int y = 0; y < biA.getHeight(); y++){
	            		r = ((biA.getRGB(x,y)>>16)&0xFF); //bit-shift for red
	            		g = ((biA.getRGB(x,y)>>8)&0xFF); //bit-shift for green
	            		b = ((biA.getRGB(x,y)>>0)&0xFF); //bit-shift for blue
	  
	            		if(r == 255 && g == 255 && b == 255){
	            			
	            		}
	            		else if(biA.getRGB(x,y) == biB.getRGB(x,y)){
	            			count++;
	            			total++;
	            		}
	            		else{
	            			total++;
	            		}
	            	}
	            }

	            percentage = (float) ((count * 100.0) / total);
	        } else {
	            System.out.println("The images are not of the same size.");
	        }

	    } catch (Exception e) {
	        System.out.println("Could not compare the images.");
	    }
	    return percentage;
	}
}
