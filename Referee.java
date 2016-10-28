package server_test;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Referee {

	String id = "referee";
    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Referee");
    JPanel mainPanel;
    JLabel label_1, label_2, label_3;
    JButton btn_1, btn_2, btn_3;
    String IPAddress;

    public Referee() {
    	
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
        Referee client = new Referee();
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
                out.println("REFEREE");
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
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 3, 0, 0));
        
        
        label_1 = new JLabel();
        label_2 = new JLabel();
        label_3 = new JLabel();
        
        ImageIcon image_1 = new ImageIcon("C:/Users/danm5/workspace Java/server_test/option_1.png");
        ImageIcon image_2 = new ImageIcon("C:/Users/danm5/workspace Java/server_test/option_2.png");
        ImageIcon image_3 = new ImageIcon("C:/Users/danm5/workspace Java/server_test/option_3.png");
        
        label_1.setIcon(image_1);
        label_2.setIcon(image_2);
        label_3.setIcon(image_3);
        
        btn_1 = new JButton();
        btn_1.setText("option_1");
        btn_2 = new JButton();
        
        btn_3 = new JButton();
        
        btn_1.add(label_1);
        btn_2.add(label_2);
        btn_3.add(label_3);
        
        mainPanel.add(btn_1);
        mainPanel.add(btn_2);
        mainPanel.add(btn_3);
        
        btn_1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				out.println(e.getActionCommand());
			}
        });
        
        frame.add(mainPanel);
        frame.pack();
        
    }
}
