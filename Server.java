package Final;

//Server.java

import java.net.*;
import java.util.ArrayList;
import java.io.*;

/**
* 
* @author Chih-Feng, Sonaki Bengali
*
*/
public class Server extends Thread {
      private ServerSocket skt;
      protected ArrayList<ClientThread> clientThread;
      private int port;
      private PrintStream theOutputStream;
      private BufferedReader theInputStream;
      private String readin;
      private Face chat;
      private String[] defaultList ={""};

//    server constructor  
      public Server(int port, Face chat) {	  
    	  try {
              this.port = port;
              this.clientThread=new ArrayList<ClientThread>();
              this.clientThread.clear();
              skt = new ServerSocket(port,100);
              this.chat = chat;
          } catch (IOException e) {
              chat.ta.append(e.toString());
          }
      }//end constructor
      
     
//    return client thread
      public ArrayList<ClientThread> getClients(){
      	return this.clientThread;
      }
      
      public ServerSocket getServerSocket(){
      	return this.skt;
      }

//    return output stream
      public PrintStream getOutputStream(){
      	return this.theOutputStream;
      }
      
//    return input stream
      public BufferedReader getInputStream(){
      	return this.theInputStream;
      }
      
      public String[] AllUsers() {
    	  String[] clientNameList = new String[getClients().size()];
    	  int count =0;
    	  for (int i = 0; i < getClients().size() ; i++) {
    		  while (!clientThread.get(i).getSocket().isClosed()) {	   
    			  clientNameList[count]=getClients().get(i).getUserName();
    			  count++;
    			  break;
    		  }
    	  } 
    	  return clientNameList;
      }
      
      
    
    /*  
    	Thread run method
    */  
    @SuppressWarnings("deprecation")
	
    public void run() {
  	//build connection
      while(true){
    	  try{		  
    		  Socket client;
    		  client=this.skt.accept();
    		  ClientThread newClient=new ClientThread(client,this.chat,this);
    		  newClient.start();
    		  this.clientThread.add(newClient);
    		  this.chat.addMessage("Server: New connection from "+client.getInetAddress().getHostName()+"......"+"\n");	//show in server's face
    		  this.dataout("New connection from "+client.getInetAddress().getHostName()+"......");						//send to all clients to notify new user join
    		  sleep(500); // wait new client thread create everything well(or AllUser() might get "null" name)
    		  String[] clientlist = AllUsers();
    		  
//    		  update server's list
    		  if (this.getClients().size() == 0) {
    			  System.out.println("no user in list");
    			  this.chat.addUserToList(defaultList);
    		  }else if (this.getClients().size() > 0) {
    			  this.chat.addUserToList(clientlist);
    		  }
    		  this.userListOut(clientlist);//send userlist to all client
    		 
    		  System.err.println("in server run");	 
    	  }
    	  catch(Exception e){
    		  try {
    			  if(this.skt!=null)
    				  this.skt.close();
    		  } catch (IOException e1) {
    			  e1.printStackTrace();
    		  }
    		  this.stop();
    	  }

      }//end while loop
   }//end method run 
	
	public void userListOut(String[] userList){
		StringBuilder userListinString = new StringBuilder();
		for(String s : userList) {
			userListinString.append(","+s);
		}
		for(int i=0;i<this.clientThread.size();i++){
  		  	this.clientThread.get(i).getOutputStream().print("!List!"+userListinString+"\n");
  		  	this.clientThread.get(i).getOutputStream().flush();
      	}
	}//end of userListOut
    
	public boolean sendPrivateMsg(String receiverName, String text, int reverseFlag) {
		String reverseText="";
        int j;
        	if(text==null)
        		return true;
//  		reverse private message style 
        	if (reverseFlag==0) {
        		for(j=text.length()-1;j!=-1;j--){
        			reverseText +=text.charAt(j);
        		}
        		for (int i = 0; i < clientThread.size(); i++) {
        			if (clientThread.get(i).getUserName().equals(receiverName)) {
        				clientThread.get(i).getOutputStream().print("[Server]: Talk To You Secretly : "+reverseText+"\n");
        				clientThread.get(i).getOutputStream().flush();
        				return true;
        			}
        		}
//      	normal private message style 
        	}else {
        		for (int i = 0; i < clientThread.size(); i++) {
        			if (clientThread.get(i).getUserName().equals(receiverName)) {
        				clientThread.get(i).getOutputStream().print("[Server]: Talk To You Secretly : "+text+"\n");
        				clientThread.get(i).getOutputStream().flush();
        				return true;
        			}
        		}
        	}
        	return false;     	  	
	}
	
	public void dataout(String text) {
		if(text==null)
			return;
//		send message to all clients
		for(int i=0;i<this.clientThread.size();i++){
			this.clientThread.get(i).getOutputStream().println("Server: "+text);
			this.clientThread.get(i).getOutputStream().flush();
		}
	}//end of dataout
}//end class Server


