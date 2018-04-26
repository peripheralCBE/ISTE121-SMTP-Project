import java.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

class	SMTP_Server	extends JFrame	implements ActionListener
{
   //Socket and server thread declaration
   ServerSocket ss =	null;
   Socket socket = null;
   ServerThread st =	null;

   //Declaring printerWriter and bufferedReader
   PrintWriter	out =	null;
   BufferedReader	in	= null;

   //Input output variables
   String clientInput =	null;
   String serverOutput = null;
   boolean clientConnected = true;

   //GUI Components
   JButton jbStartStop = new JButton("Start");
   JPanel jpNorth	= new	JPanel();
   JTextArea jtaLog = new JTextArea();
   JScrollPane	scrollPane = new JScrollPane(jtaLog);

   public static void main(String args[])
   {//Start	of	main
      new SMTP_Server();
   }//End of main

   public SMTP_Server()
   {//start	of	SMTP_Server	Contructor
   
   	//Window	Settings
      this.setTitle("SMTP Server");
      this.setSize(500,300);
      this.setLocation(300,300);
      this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
      
   	//Adding	Components
      jpNorth.add(jbStartStop);
      this.add(jpNorth,	BorderLayout.NORTH);
      this.add(scrollPane);
   
   
   	//Setting ActionListener
      jbStartStop.addActionListener(this);
   
      this.setVisible(true);
   
   
   }//End of SMTP_Server Constructor

   public void	actionPerformed(ActionEvent ae)
   {
   	//What to do when	button is pressed
      switch(ae.getActionCommand())
      {  
         //Code for Start button
         case "Start" :
            jtaLog.append("Starting Server \n \n");
            jbStartStop.setText("Stop");
            runThread();
            break;
      
         //Code for Stop button
         case "Stop"	:
            jtaLog.append("Stopping Server \n \n");
            jbStartStop.setText("Start");
            stopThread();
            break;
      
      
      }//End of Switch
   
   }//End of ActionPerformed Method

   //Stops Thread
   public void	stopThread()
   {
      try
      {
         ss.close();
         st.stop();
      }//End of try
      catch(Exception e){e.printStackTrace();}
   
   
   
   } //End of stopThread


   //Starts listen thread
   public void	runThread()
   {
      ListenThread lt = new ListenThread(jtaLog, clientConnected);
      lt.start();
      
   
   
   }//End of runThread


   class	ServerThread extends	Thread
   {
      //declared variables
      boolean clientConnected;//Not used
      Socket socket = null;
      JTextArea jtaLog = new JTextArea();
      String userName = null;
      String rcpName = null;
      int qNum = 0;//Not used
      int setQNum = 0;//Not used
      
       
      
   
      public ServerThread(JTextArea	jtaLog, Socket socket, boolean clientConnected, int qNum)
      {
         this.jtaLog	= jtaLog;
         this.socket = socket;
         this.clientConnected = clientConnected;
         this.qNum = qNum;
      }//End of contructor
   
      public void	run()
      {
      
         try
         {  
         
            String hostName = this.socket.getInetAddress().getHostName();
            	//Logic goes here!!!!!
            jtaLog.append("Client Connected: " +	hostName + "\n \n");
            out =	new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            in	= new	BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            
         
            //Login Method
           /* out.println("Login Required");
            out.flush();
            userName = in.readLine();
            jtaLog.append("Username Receieved: " + userName + "\n \n");*/
                        
            //Sends 220
            out.println("220");
            out.flush();
            jtaLog.append("Sent 220 acknowledgement to " + hostName + "\n \n");
            
            //Waiting for HELO   
            clientInput = in.readLine();
            if(clientInput.substring(0,4).equals("HELO"))
            
            {
               
               setQNum = qNum; //TEMP FIFO QUEUE TeST
               jtaLog.append(hostName + ": recieved HELO from client \n \n");
            
               //Sending 250
               out.println("250");
               out.flush();
               jtaLog.append("Server sent 250 to " + hostName + "\n \n");
               
               //Getting username //POSSIBLY CHANGE HERE
               clientInput = in.readLine();
               userName = clientInput.substring(11,clientInput.length()-1);
               
               
               //Sending acknowledgement 250
               while(userName.length() == 0){userName = "guest";}
               if(userName.length() > 0)
               {
                  jtaLog.append("Username: " + userName + "\n \n");
                  out.println("250");
                  out.flush();
                  jtaLog.append("Sent 250 acknowledged \n \n");
               
                  //Recieving recipient name   
                  clientInput = in.readLine();
                  rcpName = clientInput.substring(9,clientInput.length()-1);
                  jtaLog.append("Recipient: " + rcpName + "\n \n");
                  
                  //Checking for recipient name
                  if(rcpName.length() == 0)//MAY CHANGE THIS
                  {
                     System.out.println(rcpName.length());
                     out.println("ERROR");
                     out.flush();
                  }
                  
                  
                  if(rcpName.length() > 0) //MAY CHANGE THIS
                  {  
                     //Sending 250 acknowledgement
                     out.println("250");
                     out.flush();
                     jtaLog.append("Sent 250 acknowledged \n \n");
                     clientInput = in.readLine();
                     
                  
                     if(clientInput.equals("DATA"))
                     {
                        jtaLog.append("Received DATA from: " + hostName + "\n \n");
                     //Sending 354 to client
                        out.println("354");
                        out.flush();
                        jtaLog.append("Sent 354 to " + hostName + "\n \n");
                        
                        //Creates mailbox folder as well as mailbox text file and stores data
                        File file = new File("mailbox");
                        file.mkdir();//Makes dir
                        FileWriter fw = new FileWriter("mailbox/" + rcpName + ".txt", true);//Create text file
                        PrintWriter mailBoxWriter = new PrintWriter(fw);
                        
                        while((clientInput = in.readLine()) != null)//Reads for message and looks for . to indicate end of message
                        {
                           if(clientInput.equals(".")){
                              break;
                           }//Breaks out of while if single . is found
                        
                           mailBoxWriter.println(clientInput);//Appends data to PrintWriter
                           mailBoxWriter.flush();//Flushes data to file
                           mailBoxWriter.close();
                           
                        }//End of message while
                        
                        out.println("Sending 250 Ok: queued as " + setQNum);//TEST QUEUE MESSAGE
                        out.flush();
                        jtaLog.append("250 Ok: queued as: " + setQNum);
                        
                        if((clientInput = in.readLine()).equals("QUIT"))
                        {
                           out.println("221 Bye");
                           out.close();
                           in.close();
                           socket.close();
                        }//End of QUIT if
                        
                        
                     }//end of DATA if
                     else{jtaLog.append("Did not recieved DATA ack");}
                     
                  }//end of Recipient if check
                  else{jtaLog.append("Recipient name not received \n \n");}
                  
               }//End of username check if
               else{
                  jtaLog.append("Username not recieved, exiting connection. \n \n");}
               
            }//End of HELO if
            else{jtaLog.append("Did not recieve HELO from client \n \n");
               
            }//End of HELO else                     
            
            
         
         }//end of try
         catch(BindException be){jtaLog.append("Failed to bind to port. The port is already in use. \n");}
         catch(Exception e){e.printStackTrace();}
      
      
         
      
      }//End of run
   
   }//End of ServerThread class
   
   class ListenThread extends Thread 
   {
      boolean clientConnected;
      JTextArea jtaLog = null;
      int qNum = 0;
      public ListenThread(JTextArea jtaLog, boolean clientConnected)
      {
         this.jtaLog = jtaLog;
         this.clientConnected = clientConnected;
      }//End of contructor
      
      public void run()
      {
         try{
            ss	= new	ServerSocket(42069);
         
            //LISTENS for client connections forever
            while(true){
               socket =	ss.accept();
               qNum++;//TEST FIFO QUEUE
               st	= new	ServerThread(jtaLog, socket, clientConnected, qNum);
               st.start();
            }//end of while
            
         }//End of try
         catch(Exception e){e.printStackTrace();}
      
      }//End of run
   
   }//End of Start thread


}//End of SMTP	Server Class


