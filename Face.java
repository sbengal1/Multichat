package Final;

//face.java

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;




/**
* 
* @author Chih-Feng, Sonaki Bengali
*
*/
public class Face extends Frame {
      /**
       *
       */
      private static final long serialVersionUID = 1L;
      Button clientBtn, serverBtn,sendMessage,closeConnection;
      JLabel serverIP,serverPort;
      JPanel southPanel,northPanel,westPanel,eastPanel,centerPanel;
      TextArea ta,tftype;      
      JList userListBlock;
      DefaultListModel model;      
      TextField tfaddress, tfport;
      int port,reverseFlag;
      int screenWidth,screenHeight,frameWidth,frameHeight;
      Client client;
      Server server;
      boolean iamserver;
      static Face frm;
      String userName;
      String defaultUserList="";

      
//    add new user in the list
      public void addUserToList(String[] list) {
    	  
    	 model.removeAllElements();
    	 for (int i = 0; i < list.length; i++) {
    		 model.addElement(list[i]);
         }
    	 this.userListBlock.setModel(model);
      }// end method addUserToList
      
//    add message
      public void addMessage(String msg){
      	this.ta.append(msg);
      }//end method addMessage       
      
//    return server object
      public Server getServer(){
      	return this.server;
      }//end method getServer  
      
      
//		constructor
      public Face() {
      	this.southPanel=new JPanel(new BorderLayout());
      	this.northPanel=new JPanel(new GridLayout(1,7)); // 1 row 7 clos 
      	this.westPanel=new JPanel(new BorderLayout());
      	this.eastPanel=new JPanel(new BorderLayout());
      	this.centerPanel=new JPanel(new BorderLayout());
          clientBtn = new Button("Client");
          serverBtn = new Button("Server");            
          this.closeConnection=new Button("Disconnect");
          this.closeConnection.setEnabled(false);
          this.serverIP=new JLabel("Server IP",JLabel.CENTER);
          this.serverPort=new JLabel("Port",JLabel.CENTER);
          ta = new TextArea("", 10, 50, TextArea.SCROLLBARS_BOTH); //msg received and show block
          tfaddress = new TextField("127.0.0.1", 20);
          tfport = new TextField("2000");
          tftype = new TextArea("", 1, 70, TextArea.SCROLLBARS_BOTH);
          this.sendMessage=new Button("Send");
          this.userName="";
          
          
//        add key listener
          tftype.addKeyListener(new TFListener()); //add listener to msg typing block
          ta.setEditable(false);  //set the user can't editable
          
//        add Jlist  
          this.model = new DefaultListModel();
          this.model.addElement(defaultUserList);
          this.userListBlock = new JList(model);
          this.userListBlock.setFixedCellHeight(15);
          this.userListBlock.setFixedCellWidth(100);
          userListBlock.setEnabled(false);
          
//		  set layout          
          this.setLayout(new BorderLayout());
          
//		  east panel
          this.eastPanel.add(this.userListBlock,BorderLayout.CENTER);
          this.eastPanel.setBorder(new TitledBorder("User List"));
          this.add(this.eastPanel,BorderLayout.EAST);
          
//        center panel
          this.centerPanel.add(this.ta,BorderLayout.CENTER);
          this.centerPanel.setBorder(new TitledBorder("Text Block"));
          this.add(this.centerPanel,BorderLayout.CENTER);            
          
//        northPanel
          this.northPanel.add(this.serverIP);
          this.northPanel.add(this.tfaddress);
          this.northPanel.add(this.serverPort);
          this.northPanel.add(this.tfport);
          this.northPanel.add(this.clientBtn);
          this.northPanel.add(this.serverBtn);
          this.northPanel.add(this.closeConnection);
          this.add(this.northPanel,BorderLayout.NORTH);
          
//        south panel
          this.southPanel.add(this.tftype,BorderLayout.WEST);//msg typing area
          this.southPanel.add(this.sendMessage,BorderLayout.EAST);//send button
          this.southPanel.setBorder(new TitledBorder("Enter block"));// name the south title border
          this.add(this.southPanel,BorderLayout.SOUTH);
          
//		  set Window frame          
          this.frameWidth=600;
          this.frameHeight=600;
          setSize(this.frameWidth,this.frameHeight);            
          setTitle("My Chatting room");
          
//		  set visible          
          this.setVisible(true);
   
//        add listener for Jlist selection 
          this.userListBlock.addMouseListener(new MouseAdapter() {
        	  public void mouseClicked(MouseEvent mouseEvent) {
        		  int privateOption;
  				  int reverseOption;
        		  if (mouseEvent.getClickCount() == 1) {
        	          int index = userListBlock.locationToIndex(mouseEvent.getPoint());
        	          if (index >= 0) {
        	            if (!iamserver && client.getUserName().equals(userListBlock.getSelectedValue())) {
    						return;
    					}

    					if (userListBlock.getSelectedValue() == null || userListBlock.getSelectedValue() == " " ) { //
    						return;
    					}		
    						privateOption = JOptionPane.showConfirmDialog(null,"Do you want to send private msg to : "
    									+userListBlock.getSelectedValue() +" ?","Private communication confirmation",JOptionPane.YES_NO_OPTION);
    					if (privateOption==0) {
    						reverseOption = JOptionPane.showConfirmDialog(null,"Do you want to send message in reverse style?","Using Reverse string",JOptionPane.YES_NO_OPTION);
    						tftype.setText("** To User " +userListBlock.getSelectedValue()+" ** : ");
    						if (reverseOption==0) {
    							reverseFlag=0;
    						}else {
    							reverseFlag=1;
    						}
    					}else {
    						System.out.println("NO");	
    					}
        	          }
        		  }
        	  }// end method mouseClicked 	  
		});//end addMouseListener
          
//         add listener for sendButton
          this.sendMessage.addActionListener(new ActionListener(){	  	
				@Override
				public void actionPerformed(ActionEvent e) {
					 String[] strArray = tftype.getText().split(" ");					 
//		            	  Check whether already chosen the type 
		            	  if(clientBtn.isEnabled()==true || serverBtn.isEnabled()==true){
		            		  JOptionPane.showMessageDialog(null, "plz decide to be client or server", "ERROR", JOptionPane.ERROR_MESSAGE);
		            		  return;
		            	  }
		            	  
//		            	  Private communication ,server side
		            	  if (iamserver){
		                	  if (strArray[0].equals("**") && strArray[1].equals("To") && strArray[2].equals("User")  && strArray[4].equals("**") && strArray[5].equals(":") ){ //  
		                		 
		                		  /* if (strArray[6]=="**close**") {  
		                			  tftype.setText("");
		                			  return;
		                		  }*/
		                		  
//		                		  send private message to certain client
		                		  String[] textArray = new String[strArray.length-6]; 	
		                		  System.arraycopy(strArray, 6, textArray, 0, strArray.length-6);
		                		  StringBuilder textArrayinString = new StringBuilder();
		          					for(String s : textArray) {
		          						textArrayinString.append(s+" ");
		          					}
		          					
		          					if (server.sendPrivateMsg(strArray[3].toString(),textArrayinString.toString(),reverseFlag)== true) {
		                				ta.append("You sent secret message to User [" +strArray[3]+"]: "+textArrayinString.toString() + "\n");
		                				tftype.setText("** To User "+strArray[3]+" ** : ");//continue private communication setting
		          					}else{
		          						ta.append("User ["+strArray[3] + "] not found \n");
		          					}                  		  
		     
//		    				  server send message to all clients and server                	  
		                	  }else {
		                		  ta.append(userName+": " + tftype.getText() + "\n");
		                		  server.dataout(tftype.getText());
		                	  }
		                	  
//		                Private communication ,client side
		                  }else if(!iamserver){
		                	 
		                	  if (strArray[0].equals("**") && strArray[1].equals("To") && strArray[2].equals("User")  && strArray[4].equals("**") && strArray[5].equals(":") ) {
		                		
		                		  /* if (strArray[6]=="**close**") {
		                			  tftype.setText("");
		                			  return;
		                		  }*/
		                		  
//		                		  send private message to certain client
		                		  if (strArray[3].equals(client.getUserName())) {
		                			  ta.append("Wrong User to Privately Communicate");
		                		  }else {
		                			  String[] textArray = new String[strArray.length-6]; 	
		                			  System.arraycopy(strArray, 6, textArray, 0, strArray.length-6);
		          					  StringBuilder textArrayinString = new StringBuilder();
		          					  	for(String s : textArray) {
		          					  	textArrayinString.append(s+" ");
		          					  	}
		                			  if (client.privateOut(strArray[3].toString(),textArrayinString.toString(),reverseFlag)== true) {
		                				  ta.append("You sent secret message to User [" +strArray[3]+"]: "+textArrayinString.toString() + "\n");
		                				  tftype.setText("** To User "+strArray[3]+" ** : ");//continue private communication setting
		                			  }else{
		                				  ta.append("User ["+strArray[3] + "] not found \n");
		                			  }                  		  
		                		  }
//							  client send message to all clients and server
		                	  }else {
		                		  ta.append(userName+": " + tftype.getText() + "\n");
		                		  client.dataout(tftype.getText());
		                	  }
		                  }          
	            }//end actionPerformed function	
          });//end actionListener
          
//          add action listener for client button
          clientBtn.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
//					get the port
                  port = Integer.parseInt(tfport.getText());
//                create new client
                  client = new Client(tfaddress.getText(), port, frm);                    
                 
//                get user name
                  while(true){
                  	userName=JOptionPane.showInputDialog(null, "Plz enter user name", "login", JOptionPane.QUESTION_MESSAGE);
                  	if(userName==null || userName.equals("")){
                  		JOptionPane.showMessageDialog(null, "can't be blank", "ERROR", JOptionPane.ERROR_MESSAGE);
                  		continue;
                  	}
                  	if(userName.equals("Server")){
                  		JOptionPane.showMessageDialog(null, "user can't be server", "ERROR", JOptionPane.ERROR_MESSAGE);
                  		userName=null;
                  		continue;
                  	}
                  	if(userName != null)
                  		break;
                  }//end while loop
            		client.setUserName(userName);
//      	        start the client process 
                    client.start();     
                    setTitle(userName+"'s chatting room");
//           	    become a client
                    iamserver=false;
                    tfaddress.setText("become client");
                    
//           	    enable corresponding buttons
                    userListBlock.setEnabled(true);
                    tfaddress.setEnabled(false);
                    tfport.setEnabled(false);
                    serverBtn.setEnabled(false);
                    clientBtn.setEnabled(false);
                    closeConnection.setEnabled(true);                
                 
              }
          });//end client button action listener
          
//          add action listener for server button
          serverBtn.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) { 
//              	get the port
                  port = Integer.parseInt(tfport.getText());
//                create new server
                  server = new Server(port, frm);
//                start the server process
                  server.start();                  
                  userName="Server";
                  
//                become a server
                  iamserver = true;
                  tfaddress.setText("become server");
                  setTitle(userName+"'s chatting room");
                  
//                enable corresponding buttons    
                  userListBlock.setEnabled(true);
                  tfaddress.setEnabled(false);
                  tfport.setEnabled(false);
                  serverBtn.setEnabled(false);
                  clientBtn.setEnabled(false);
                  closeConnection.setEnabled(true);
              }
          });//end server button listener
          
          
//        add action listener to close connection
          this.closeConnection.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					
					try {
						closeConnection();
					} catch (IOException e1) {
						
						e1.printStackTrace();
					}
					
					model.removeAllElements();
					model.addElement(defaultUserList);
					userListBlock.setModel(model);
					userListBlock.setEnabled(false);
					ta.setText(" ");
					clientBtn.setEnabled(true);
					serverBtn.setEnabled(true);
					closeConnection.setEnabled(false);
					tftype.setText(null);
					tfaddress.setText("127.0.0.1");
					tfaddress.setEnabled(true);
					tfaddress.setEditable(true);
					tfport.setText("2000");
					tfport.setEnabled(true);
					tfport.setEditable(true);
					
				}//end method action performed
          });//end action listener
          

//        add window listener for frame
          this.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
//              	exit normally
              	try {
						closeConnection();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
                  System.exit(0);
              }
          });//end window listener
      }//end constructor function

      
/****************************************************************
*      Main Function
*****************************************************************/      
      
      public static void main(String args[]) {
          frm = new Face();    
      }

/****************************************************************
*      Main Function
*****************************************************************/
      
//    close current established connections
      @SuppressWarnings("deprecation")
		public void closeConnection() throws IOException{
      	if(this.iamserver==false && this.client!=null && this.client.getSocket()!=null){
      		this.client.dataout("Connection closed from "+this.client.getSocket().getInetAddress().getHostName()+"......");
      		this.client.notifyAllOffline();
      		this.client.getSocket().close();
      		this.client.getOutputStream().close();        		
      		this.client.getInputStream().close();
      		this.client.stop();  	
      	}
      	else if(this.iamserver==true && this.server!=null && this.server.getServerSocket()!=null){
//      		close all connections for clients
      		for(int i=0;i<this.server.getClients().size();i++){
      			this.server.getClients().get(i).getSocket().close();
      			this.server.getClients().get(i).getOutputStream().close();
      			this.server.getClients().get(i).getInputStream().close();
      			this.server.getClients().get(i).stop();
      		}
//      		close server connection
      		this.server.getServerSocket().close();
      		this.server.stop();
      	}
      }//end method closeConnection
      
//    an inherited listener
      private class TFListener implements KeyListener {    	  
    	  int flag = 0; //for control the key release
//      	when press "Enter", send the text to area
          public void keyPressed(KeyEvent e) {            	  	
        	  String[] strArray = tftype.getText().split(" ");
        	  
              if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
            	  
//            	  Check whether already chosen the type 
            	  if(clientBtn.isEnabled()==true || serverBtn.isEnabled()==true){
            		  JOptionPane.showMessageDialog(null, "plz decide to be client or server", "ERROR", JOptionPane.ERROR_MESSAGE);
            		  return;
            	  }
            	  
//            	  Private communication ,server side
            	  if (iamserver){
                	  if (strArray[0].equals("**") && strArray[1].equals("To") && strArray[2].equals("User")  && strArray[4].equals("**") && strArray[5].equals(":") ){ //  
                		 
                		  /* if (strArray[6]=="**close**") {
                			  tftype.setText("");
                			  return;
                		  }*/
                		  
//                		  send private message to certain client
                		  String[] textArray = new String[strArray.length-6]; 	
                		  System.arraycopy(strArray, 6, textArray, 0, strArray.length-6);
                		  StringBuilder textArrayinString = new StringBuilder();
          					for(String s : textArray) {
          						textArrayinString.append(s+" ");
          					}
          					if (server.sendPrivateMsg(strArray[3].toString(),textArrayinString.toString(),reverseFlag)== true) {
          						flag = 1;//control enter key release setting.
                				ta.append("You sent secret message to User [" +strArray[3]+"]: "+textArrayinString.toString() + "\n");  
          					}else{
          						ta.append("User ["+strArray[3] + "] not found \n");
          					}                  		  
     
//    				  server send message to all clients and server                	  
                	  }else {
                		  ta.append(userName+": " + tftype.getText() + "\n");
                		  server.dataout(tftype.getText());
                	  }
                	  
//                Private communication ,client side
                  }else if(!iamserver){
                	 
                	  if (strArray[0].equals("**") && strArray[1].equals("To") && strArray[2].equals("User")  && strArray[4].equals("**") && strArray[5].equals(":") ) {
                		 
                		  /* if (strArray[6]=="**close**") {
                			  tftype.setText("");
                			  return;
                		  }*/
                		  
//                		  send private message to certain client
                		  if (strArray[3].equals(client.getUserName())) {
                			  ta.append("Wrong User to Privately Communicate");
                		  }else {
                			  String[] textArray = new String[strArray.length-6]; 	
                			  System.arraycopy(strArray, 6, textArray, 0, strArray.length-6);
          					  StringBuilder textArrayinString = new StringBuilder();
          					  	for(String s : textArray) {
          					  	textArrayinString.append(s+" ");
          					  	}
                			  if (client.privateOut(strArray[3].toString(),textArrayinString.toString(),reverseFlag)== true) {
                				  flag = 1;//control enter key release setting.
                				  ta.append("You sent secret message to User [" +strArray[3]+"]: "+textArrayinString.toString() + "\n");
                			  }else{
                				  ta.append("User ["+strArray[3] + "] not found \n");
                			  }                  		  
                		  }
//					  client send message to all clients and server
                	  }else {
                		  ta.append(userName+": " + tftype.getText() + "\n");
                		  client.dataout(tftype.getText());
                	  }
                  }
              }
          }//end method keyPressed

//        when the user type a key
          public void keyTyped(KeyEvent e) {
          }
          
//        when user release a key
          public void keyReleased(KeyEvent e) {
        	  if(e.getKeyCode() == KeyEvent.VK_ENTER){
        		  if (flag == 1) {
        			  String[] strArray = tftype.getText().split(" ");
        			  if (strArray[0].equals("**") && strArray[1].equals("To") && strArray[2].equals("User")  && strArray[4].equals("**") && strArray[5].equals(":") ){
        				  tftype.setText("** To User "+strArray[3]+" ** : ");//continue private communication setting
        				  tftype.setCaretPosition(tftype.getText().length());  
        			  } 
        			  flag = 0;
        		  }else {
        			  tftype.setText("");
        			  tftype.setCaretPosition(0);
        		  } 
        	  }
          }//end function keyReleased  
      }//end class TFLister      
  }//end class Face
