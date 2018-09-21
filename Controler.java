package application;


import java.util.ResourceBundle;
import java.io.*;
import java.net.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class Controler implements Initializable {
	@FXML
	private TextField txtContMess;
	@FXML
	private ListView listChat;
	@FXML
	private Button btnSendMess;
	@FXML
	private Label lblNotification;
	@FXML
	private Button btnWait;
	@FXML
	private Button btnSetUser;
	@FXML
	private TextField txtUser;
	
	//Set up connection 
	private String Username="kimochi";
	final String serverHost = "localhost";
	int port = 5000;
	Socket socketOfClient = null;
	BufferedWriter os = null;
	BufferedReader is = null;
	SocketReaderThread reader = null;
	
	public void Get_Connection() {
		try {
			socketOfClient = new Socket(serverHost, port);
			os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
			is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
			} 
		catch (UnknownHostException e) {
			System.err.println("Don't know about host " + serverHost);
			return;
	       } catch (IOException e) {
	    	   System.err.println("Couldn't get I/O for the connection to " + serverHost);
	    	   return;
	       }
	
	}
	
	public void SendMessage(String Username, String mess) {
		try {
			os.write(Username+":"+mess);
			os.newLine();
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void ClickToSendMessage(MouseEvent event) {
		SendMessage();
	}
	
	public void ClickToSetUser(MouseEvent event) {
		SetUser();
	}
	
	public void EnterToSendMessage(KeyEvent event) {
		if(event.getCode()==KeyCode.ENTER) {
			SendMessage();
		}
	}
	
	public void SetUser() {
		this.Username = txtUser.getText();
		this.reader = new SocketReaderThread(this,this.Username);
		this.reader.start();
	}
	public void SendMessage() {
		String mess = txtContMess.getText();
		if(mess.length()!=0) {
			this.SendMessage(this.Username,mess);
		}
	}
	public String Terminate_connection() {
		try {
			os.close();
			is.close();
	        socketOfClient.close();
	        return "Username: "+this.Username+" terminate connection !";
		} catch (UnknownHostException e) {
	           System.err.println("Trying to connect to unknown host: " + e);
	        return "Trying to connect to unknown host: " + e;
		} catch (IOException e) {
			e.printStackTrace();
			return "IOException: " + e;
		}
		
	}
	public void ReceiveMessage(String mess) {
		listChat.getItems().add(mess);
		txtContMess.setText(null);
	}
	public void ReceiveNotification(String note) {
		lblNotification.setText("Notification: "+note);
	}
	@Override
    public void initialize(URL url, ResourceBundle rb) {
		Get_Connection();
	}
	class SocketReaderThread extends Thread {
		Controler cont;
		String Username;
		public SocketReaderThread(Controler cont, String username){
			this.cont = cont;
			this.Username = username;
		}
		@Override
		public void run() {
			String responseLine;
			System.out.println("I am wating ... ");
			try {
				while ((responseLine = is.readLine()) != null) {
					OnMessage(this.cont,responseLine);
					if (responseLine.indexOf("User "+this.Username+" quit !") != -1) {
						break;
						}
					}
				
				this.cont.Terminate_connection();
				System.out.println("test terminate !");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void OnMessage(Controler cont,String line) {
			javafx.application.Platform.runLater(new Runnable() {
	            @Override
	            public void run() {
	                cont.ReceiveMessage(line);
	            }
	        });
		}
	}

}
