package Final;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;


/**
 * 
 * @author Chih-Feng, Sonaki Bengali
 *
 */
public class ClientThread extends Thread{
	
	private Socket socket;
	private PrintStream theOutputStream;
    private BufferedReader theInputStream;
    private String readin,message,userName;
    private Face face;
    private Server server;
    private boolean first;
    private String[] defaultList ={""};
    
//	public constructor
	public ClientThread(Socket socket,Face face,Server server) throws IOException{
		this.socket=socket;
		this.face=face;
		this.server=server;
		this.first=true;
		
//		get input stream and output stream
		InputStream inputStream=this.socket.getInputStream();
		this.theInputStream=new BufferedReader(new InputStreamReader(inputStream));
		OutputStream outputStream=this.socket.getOutputStream();
		this.theOutputStream=new PrintStream(outputStream);
		this.theOutputStream.flush();
	}//end constructor
	
//	return user name
	public String getUserName(){
		return this.userName;
	}
	
//	set user name
	public void setUserName(String userName){
		this.userName=userName;
	}
	
//	get output stream object
	public PrintStream getOutputStream(){
		return this.theOutputStream;
	}
	
//	get input stream object
	public BufferedReader getInputStream(){
		return this.theInputStream;
	}
	
//	return socket
	public Socket getSocket(){
		return this.socket;
	}
	
//	rewrite this method
	@SuppressWarnings("deprecation")
	public void run(){
		
//		read the received message sent from client/server
		try {
			while((this.readin=this.theInputStream.readLine()).equalsIgnoreCase("EXIT")==false){
				if(this.first==true){
					this.setUserName(readin);					
					this.first=false;		
					continue;
				}
				
//				update list after delete user
				String[] strArray = this.readin.split(",");
				
				if (strArray[0].equals("**delete**")) {

					String[] clientlist = new String[1]; 
					System.arraycopy(strArray, 1, clientlist, 0, 1);
					for (int i = 0; i < server.getClients().size(); i++) {
						if (server.clientThread.get(i).getUserName().equals(clientlist[0].toString())) {
							server.clientThread.remove(i);
						}
					}
					String[] afterDeleteClientlist = server.AllUsers();
					if (server.getClients().size() == 0) {
						System.out.println("no user in list");
						this.face.addUserToList(defaultList);
		    		}else if (server.getClients().size() > 0) {
		    			this.face.addUserToList(afterDeleteClientlist);
		    		}
					server.userListOut(afterDeleteClientlist);
					
//				Get message from client/sever then output	
				}else if(strArray[0].equals("!PrivateCommunication!")){
					
					String[] privateMsg = new String[2]; 	
					System.arraycopy(strArray, 1, privateMsg, 0, 2);
					this.sendPrivateMsg(privateMsg);
				}else {
					message=this.readin+"\n";
					this.face.addMessage(message); // add msg to own face
					this.sendMessages(message);//send msg to all user
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
			this.stop();
		}
//    	release the resources
    	finally{
    		try {
				this.theInputStream.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
    		this.theOutputStream.close();
    		try {
				this.socket.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
    	}//end finally
	}//end method run

//	send message privately
	public void sendPrivateMsg(String[] privateMsg) {
		ArrayList<ClientThread> clients=this.server.getClients();
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getUserName().equals(privateMsg[0].toString())) {
				clients.get(i).getOutputStream().print("User ["+this.getUserName()+"]: Talk To You Secretly : "+privateMsg[1]+"\n");
				clients.get(i).getOutputStream().flush();
			}
		}//end for loop
	}//end method sendPrivateMsg
	
	
//	send messages to all online users
	public void sendMessages(String message){
		ArrayList<ClientThread> clients=this.server.getClients();
		for(int i=0;i<clients.size();i++){
			String name=clients.get(i).getUserName();
//			check if it is the same user
			if(message.length()>=name.length()  
					&& (message.substring(0, name.length()).equals(name) ==true ))
				continue;
			clients.get(i).getOutputStream().print(message);
			clients.get(i).getOutputStream().flush();
		}//end for loop
	}//end method sendMessages

}//end class ClientThread
