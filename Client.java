package Final;

//Client.java

import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;
  
/**
* 
* @author Chih-Feng, Sonaki Bengali
*
*/
class Client extends Thread {
      Socket skt;
      InetAddress host;
      int port;
      BufferedReader theInputStream;
      PrintStream theOutputStream;
      String readin,userName;
      Face chat;
      String[] clientlist;
      
//    client constructor
	public Client(String ip, int p, Face chat) {
		try {
			host = InetAddress.getByName(ip);
			port = p;
			this.chat = chat;
		} catch (IOException e) {
			chat.ta.append(e.toString());
		}
	}//end constructor
      
//  	return user name
  	public String getUserName(){
  		return this.userName;
  	}
  	
//  	set user name
  	public void setUserName(String userName){
  		this.userName=userName; 
  	}
//  	return socket
  	public Socket getSocket(){
  		return this.skt;    				
  	}
  	
//  	get output stream
  	public PrintStream getOutputStream(){
  		return this.theOutputStream;
  	}
  	
//  	get input stream
  	public BufferedReader getInputStream(){
  		return this.theInputStream;
  	}

  	/*
  		Thread run method
  	*/
      @SuppressWarnings("deprecation")
	public void run() {  
    	  try {	
      		
    		  this.skt=new Socket(this.host,this.port);
    		  InputStream inputStream=this.skt.getInputStream();
    		  this.theInputStream=new BufferedReader(new InputStreamReader(inputStream));
    		  OutputStream outputStream=this.skt.getOutputStream();
    		  this.theOutputStream=new PrintStream(outputStream);
    		  
//			Send userName to Server
    		  this.theOutputStream.print(this.userName+"\n");
    		  this.theOutputStream.flush();
      			
    	  } catch (IOException e) {
    		  JOptionPane.showMessageDialog(null, "Can't find server", "ERROR", JOptionPane.ERROR_MESSAGE);//jump out a window show no server
    		  this.stop();
    	  }
			System.err.println("in client run");
			
//  		Read received messages sent from ClientThread/Server
			try {   
				while((this.readin=this.theInputStream.readLine()).equals("EXIT")==false){
					String[] receiveString = this.readin.split(",");
  				
					if (receiveString[0].equals("!List!")) {
//  	  				update list
						clientlist = new String[receiveString.length-1]; 
						System.arraycopy(receiveString, 1, clientlist, 0, receiveString.length-1);
						this.chat.addUserToList(clientlist);
  					}else {
//						add message into face
  						this.chat.addMessage(this.readin+"\n");
					}			
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
//  		close the established connection
			finally{
				if(this.skt!=null && this!=null)
  				this.dataout("is offline ; Connection closed from "+this.skt.getInetAddress().getHostName()+"......");
				
				try {
					if(this.theInputStream != null)
						this.theInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(this.theOutputStream!=null){
					this.theOutputStream.close();    				
				}
				try {
					if(this.skt!=null)
						this.skt.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
  		}
      }//end method run
      
      
      
      public void notifyAllOffline() {
    	  this.theOutputStream.print("**delete**,"+this.userName);
    	  this.theOutputStream.flush();
      }
//	  Send the private message to ClientThread to handle the specific client can receive this message      
      public boolean privateOut(String receiverName, String text, int reverseFlag) {
    	  String reverseText="";
          int j;
    	  if(text==null)
    		  return true;
//		  reverse private message style 
    	  if (reverseFlag==0) {
    		  for(j=text.length()-1;j!=-1;j--){
    			  reverseText +=text.charAt(j);
    		  }
    		  for (int i = 0; i < clientlist.length; i++) {
    			  if (clientlist[i].equals(receiverName)) {
    				  this.getOutputStream().print("!PrivateCommunication!,"+clientlist[i]+","+reverseText+"\n");
    				  this.getOutputStream().flush();
    				  return true;
    			  }
    		  }
//		  normal private message style  
    	  }else {
    		  for (int i = 0; i < clientlist.length; i++) {
    			  if (clientlist[i].equals(receiverName)) {
    				  this.getOutputStream().print("!PrivateCommunication!,"+clientlist[i]+","+text+"\n");
    				  this.getOutputStream().flush();
    				  return true;
    			  }
    		  }
    	  }  
    	  return false; 
	}// end method privateOut
      
//    Send the message to ClientThread to handle  
      public void dataout(String text) {
      	if(text==null)
      		return;   	
      	this.theOutputStream.print(this.userName+": "+text+"\n");
      	this.theOutputStream.flush();
      }//end function dataout
}//end class Client
