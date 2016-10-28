package server_test;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

public class JavaServer {

    private static int PORT = 3000;

    private static HashSet<String> names = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
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
	                       break;
	                   }
	               }
	           }
	           
	           //add writer to set of writers
	           out.println("NAMEACCEPTED");
	           writers.add(out);
	
	           while (true) {
	        	   String input = in.readLine();
	               if (input == null) {
	            	   return;
	               }
	               else if(input.startsWith("option_1")){
	            	   for(PrintWriter writer : writers){
	            		   writer.println(input);
	            	   }
	               }
	               
	               for (PrintWriter writer : writers) {
	            	   writer.println("MESSAGE " + name + ": " + input);
	               }
	           }
	       }catch (IOException e) {
	    	   System.out.println(e);
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
	   
	   /*public void getDataURL() throws Exception{
			String url = "https://web.njit.edu/~edm8/cs491/database.php";
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add request header
			con.setRequestMethod("POST");
			//con.setRequestProperty("User-Agent", USER_AGENT);
			//con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
					
			String urlParameters = "code=getPlayerOneImage&stmnt=SELECT image FROM playerOneTempImage WHERE username='temp'";

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());
			
			String dataURL = response.toString();
			makeImageFile(dataURL);
	   }*/
	}
}
