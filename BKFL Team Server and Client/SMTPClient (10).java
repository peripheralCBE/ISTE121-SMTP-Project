import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Client of SMTP Project
 * @author Jamo Reif & Will Kneeland
 * @version 4/x/2018
 */
public class SMTPClient extends JFrame implements ActionListener {
   // GUI components
   private JTextField jtfIP = new JTextField(20);
   private JButton jbConnect = new JButton("Connect");
  // private JTextArea jtaFrom = new JTextArea(1, 20);
  // private JTextArea jtaTo = new JTextArea(1, 20);
   private JTextField jtfFrom = new JTextField(15);
   private JTextField jtfTo = new JTextField(15);
   private JTextArea jtaSendMessage = new JTextArea(20, 20);
   private JButton jbSend = new JButton("Send Message");
   private JTextArea jtaReceiveLog = new JTextArea(20, 20);
   private JButton jbRetrieve = new JButton("Retrieve Messages");
   private JButton jbEncrypt = new JButton("Encrypt Message");
   private int shift = 3; //shifts letters by 3
   
   Socket soc = null;
   PrintWriter pw = null;
   BufferedReader br = null;
   private String host = null;
   private String serverMsg = null;
   private String message = null;
   private final int PORT_NUMBER = 42069;
   
   /** main program */
   public static void main(String[] args) {
      new SMTPClient();
   }//end of main method
   
   /** constructor */
   public SMTPClient() {
      // setup window
      this.setTitle("Client");
      this.setSize(700, 550);
      this.setLocationRelativeTo(null);
      this.setDefaultCloseOperation(EXIT_ON_CLOSE);
      
      // IP and connect added
      JPanel jpNorth = new JPanel();
         jpNorth.add(new JLabel("IP: "));
         jpNorth.add(jtfIP);
         jpNorth.add(jbConnect);
       
         this.add(jpNorth, BorderLayout.NORTH);
      
      JPanel jpFrom = new JPanel (new FlowLayout(FlowLayout.LEFT));//new GridLayout(1,1));
         //MyFlowLayout(); {
         jpFrom.add(new JLabel("  From: "));
         jpFrom.add(jtfFrom);
         
      JPanel jpTo = new JPanel (new FlowLayout(FlowLayout.LEFT));
         jpTo.add(new JLabel("  To:     "));
         jpTo.add(jtfTo);
         //}
      JPanel jpFromTo = new JPanel (new GridLayout(2,1));
         jpFromTo.add(jpFrom);
         jpFromTo.add(jpTo);
      // Two text areas and from/to in the center
      
      JPanel jpNorthWest = new JPanel();
         jpNorthWest.add(jpFromTo);
      
      JPanel jpWest = new JPanel();
         jpWest.add(jpNorthWest, BorderLayout.NORTH);
         
      JPanel jpCenter = new JPanel(new GridLayout(1, 3, 8, 0));
         jpCenter.add(jpWest);
         jpCenter.add(new JScrollPane(jtaSendMessage));
         jpCenter.add(new JScrollPane(jtaReceiveLog));
         this.add(jpCenter);
         
         // Use monospaced font
         Font font = jtaSendMessage.getFont();
         Font newFont = new Font(Font.MONOSPACED, font.getStyle(), font.getSize());
         jtaSendMessage.setFont(newFont);
         jtaReceiveLog.setFont(newFont);
         
         // Scroll text areas
         DefaultCaret caretSend = (DefaultCaret)jtaSendMessage.getCaret();
         caretSend.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
         DefaultCaret caretRetrieve = (DefaultCaret)jtaReceiveLog.getCaret();
         caretRetrieve.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
         this.add(jpCenter);
      
      // buttons in south
      JPanel jpSouth = new JPanel(new GridLayout(1, 3, 8, 0));
         jpSouth.add(jbEncrypt);
         jpSouth.add(jbSend);
         jpSouth.add(jbRetrieve);
         this.add(jpSouth, BorderLayout.SOUTH);
         
         jbEncrypt.addActionListener(this);
         jbConnect.addActionListener(this);
         jbSend.addActionListener(this);
         /*jbRetrieve.addActionListener(this);*/
      
      this.setVisible(true);
   }//end of constructor
      
   public void clientSocket() //listenSocket method
   {
      try
      {  //establishes host
         host = jtfIP.getText();
         soc = new Socket(host, PORT_NUMBER);
         pw = new PrintWriter(new OutputStreamWriter( soc.getOutputStream()));
         br = new BufferedReader(new InputStreamReader( soc.getInputStream()));
         //smtpProtocol();
         
      }
      catch(UnknownHostException uke)
      {
         System.out.println("Error!!! Unknown host: " + host);
         System.exit(1);
      }
      catch(IOException ioe)
      {
         System.out.println("Error!!! I/O Exception has been detected");
         System.exit(1);
      }
      catch(Exception e)
      {
         System.out.println("Error!!! Error unknown");
         System.exit(1);
      }
   }//end of clientSocket method
   
 /*public String doEncrypt(String clientMsg, String reply, int shift) { //shifting method
         reply = "";
         int len = clientMsg.length();
         for(int x = 0; x < len; x++) {
             char alphabet = (char)(clientMsg.charAt(x));
             if(Character.isLetter(alphabet)) {
                 if(Character.isLowerCase(alphabet)) { //validates if letter is in alphabet
                     if((alphabet <= 'z' - shift)) {
                         reply += (char)(clientMsg.charAt(x) + shift); //shifts letters
                     } else {
                         reply += (char)(clientMsg.charAt(x) - (26 - shift));
                     }
                 } else {
                     if((alphabet <= 'Z' - shift)) {
                         reply += (char)(clientMsg.charAt(x) + shift);
                     } else {
                         reply += (char)(clientMsg.charAt(x) - (26 - shift));
                     }
                 }
             } else {
                 reply += (char)(clientMsg.charAt(x));
             }
         }
         return reply;
    }*/

   //actionListener for buttons and sending messages
   public void actionPerformed(ActionEvent event) {
      //button controls
      switch(event.getActionCommand()) {
         case "Connect":
         jbConnect.setText("Disconnect");
         clientSocket(); //method for connecting
         smtpProtocol();
         /*try {
         jtaReceiveLog.append(br.readLine());
         serverMsg = br.readLine();
         jtaReceiveLog.append(br.readLine());
         if(serverMsg == "220") {
                  jtaReceiveLog.append(serverMsg + "\n");
                  System.out.println(serverMsg);
                  pw.println("HELO");
                  System.out.println(serverMsg);
                  pw.flush();
                  System.out.println(serverMsg);
                    if(serverMsg == "250"){
                    System.out.println(serverMsg);
                     jtaReceiveLog.append(serverMsg + "\n");}
                           

                     
                  }
         }
         catch(Exception e){}*/

         break;
         
         case "Disconnect":
         try{
         pw.println("disconnect");
         pw.flush();
         br.close();
         pw.close();
         soc.close();
         
                 
         jbConnect.setText("Connect");
         }
         catch(IOException ioe) {
            //ioe.printStackTrace();
         }
         break;
         
         case "Send Message":
         if (!(jtaSendMessage.getText().equals(""))) //if the user enters a message
         {
            message = jtaSendMessage.getText(); //shows sent message
            pw.println(message); //gets texts that is sent over
            pw.flush();
            pw.println(".");
            pw.flush();
           // pw.println(".");
            System.out.println("Sending " + message + "\n" + ".");
               try{ //displays server's message
               serverMsg=br.readLine();
               jtaReceiveLog.append(serverMsg);
               System.out.println(serverMsg);
                                                
               			
			// close the connection
			//soc.close();
		         }
               
		      catch(IOException ioe) {
			      ioe.printStackTrace();
			      return;
		      }
            
            catch(Exception e)
            {
               System.out.println("Error!!! Something weird happened...");
            }
         }
         break;
      } //end of switch
      
      Object source = event.getSource();  //object that sends message
      
      if (source == jbSend)
      {
         String clientMsg = jtaSendMessage.getText();
         pw.println(clientMsg);
         
      }
      // try {
//          Scanner scan = new Scanner(System.in);
//          String reply2 = "";
//          String clientMsg = scan.nextLine();
//          if(clientMsg.equalsIgnoreCase("ENCRYPT")) {
//             reply2 = doEncrypt(clientMsg, reply2, shift);
//          }
//          if(reply2.equals("")) {
//             pw.println(reply2); 
//          } else {
//            jtaReceiveLog.append("Reply: " + reply2 + "\n");
//            pw.println(reply2);
//          }
//          pw.flush();
//       }
//       catch(Exception e) {
//          e.printStackTrace();
//       }
   
   
      
         }//end of actionPerformed method
   
   public void smtpProtocol() //method for getting and sending messages
   {
      try
      {
         serverMsg = br.readLine();
         if(serverMsg.substring(0, 3).equals("220")) //if you receive 220
         {
            jtaReceiveLog.append(serverMsg + "\n"); //show message on recieve log
            pw.println("HELO" + soc.getInetAddress());
            pw.flush();
            //repeats for other messages
            serverMsg = br.readLine();
            if(serverMsg.substring(0, 3).equals("250")) 
            {
               jtaReceiveLog.append(serverMsg + "\n");
               pw.println("MAIL FROM:<" + jtfFrom.getText() + ">");
               pw.flush();
               
               serverMsg = br.readLine();
               if(serverMsg.substring(0, 3).equals("250"))
               {
                  jtaReceiveLog.append(serverMsg + "\n");
                  pw.println("RCPT TO:<" + jtfTo.getText() + ">");
                  pw.flush();
               
                  serverMsg = br.readLine();
                  if(serverMsg.substring(0, 3).equals("250"))
                  {
                     jtaReceiveLog.append(serverMsg + "\n");
                     pw.println("DATA");
                     pw.flush();
            
                     serverMsg = br.readLine();
                     if(serverMsg.substring(0, 3).equals("354"))
                     {
                        jtaReceiveLog.append(serverMsg + "\n");
                        System.out.println("Server is waiting for email");
                     }
                     else //doesn't send
                     {
                        pw.flush();
                        br.close();
                        pw.close();
                        soc.close();
                        
                        System.out.println("SMTP Protocol has failed server side, disconnecting. #5");
                     }
                  }
                  else
                  { 
                     
                     pw.flush();
                     br.close();
                     pw.close();
                     soc.close();
                     System.out.println("SMTP Protocol has failed server side, disconnecting. #4");
                  }
               }
               else
               {
                  pw.flush();
                  br.close();
                  pw.close();
                  soc.close();
                  System.out.println("SMTP Protocol has failed server side, disconnecting. #3");
               }
            }
            else
            {  
               
               pw.flush();
               br.close();
               pw.close();
               soc.close();
               System.out.println("SMTP Protocol has failed server side, disconnecting. #2");
            }
         }
         else
         {
            pw.flush();
            br.close();
            pw.close();
            soc.close();
            System.out.println("SMTP Protocol has failed server side, disconnecting. #1");
         }
      }
      catch(IOException ioe)
      {
         System.out.println("Error!!! IOException occured during client server SMTP Protocol");
         System.exit(1);
      }
      catch(Exception e)
      {
         System.out.println("Error!!! An unknown exception occured during client server SMTP Protocol"); 
         e.printStackTrace();
         System.exit(1);
      }
   }//end of smptProtocol method
      
 
}//end of SMTPClient class