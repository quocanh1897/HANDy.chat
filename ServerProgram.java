package serverpacket;
import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class ServerProgram {
	 public static void main(String args[]) throws IOException {
		 ServerSocket listener = null;
		 ArrayList<ServiceThread> clientList = new ArrayList<ServiceThread>();
	     System.out.println("Server is waiting to accept user...");
	     int clientNumber = 0;
	     int port = 5000;
	     try {
	    	 listener = new ServerSocket(port);
	     } catch (IOException e) {
	         System.out.println(e);
	         System.exit(1);
	     }
	     try {
	         while (true) {
	        	 //Accept accessing request from client and receive a socket at server
	             Socket socketOfServer = listener.accept();
	             ServiceThread client = new ServiceThread(socketOfServer,clientNumber++, clientList);
	             clientList.add(client);
	             client.start();
	          }
	       } finally {
	           listener.close();
	       }
	 }

	 private static void log(String message) {
	       System.out.println(message);
	 }
	
	 private static class ServiceThread extends Thread {
		 private int clientNumber;
	     private Socket socketOfServer;
	     private String Username;
	     private String Message;
	     private BufferedWriter os;
	     private BufferedReader is;
	     ArrayList<ServiceThread> clientList = null;
	     public ServiceThread(Socket socketOfServer, int clientNumber, ArrayList<ServiceThread> clientList) {
	    	 this.clientNumber = clientNumber;
	         this.socketOfServer = socketOfServer;
	         this.clientList = clientList;
	         log("New connection with client# " + this.clientNumber + " at " + socketOfServer);
	         try {
				os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
				is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
	     }
	     public void MessageParser(String mess) {
			 String[] messParser =  mess.split(":");
			 this.Username = messParser[0];
        	 this.Message = messParser[1];
		 }
	     public void WriteData(String data) {
	    	 try {
				os.write(data);
				os.newLine();
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
	     }
	     public void SendToAll(String data) {
	    	 for(ServiceThread client : clientList)
	             client.WriteData(data);
	     }
	     @Override
	     public void run() {
	    	 try {
	    		 
	    		 	while (true) {
	            	 MessageParser(is.readLine());
	            	 System.out.println("In Server: "+this.Username+": "+this.Message);
	            	 if (this.Message.equals("QUIT")) {
	            		 os.write("User "+this.Username+" quit !");
	            		 os.newLine();
	            		 os.flush();
	            		 break;
	            	 }
	            	 else {
	            		 this.SendToAll(this.Username+": "+this.Message);
	            	 }
	               }
	             } catch (IOException e) {
	            	 System.out.println(e);
	            	 e.printStackTrace();
	           }
	       }
	   }
}
