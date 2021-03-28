import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

    public class ftpserver extends Thread{ 
      private Socket connectionSocket;
      int port;
      int count=1;
    public ftpserver(Socket connectionSocket)  {
	this.connectionSocket = connectionSocket;
    }


      public void run() 
        {
                if(count==1)
                    System.out.println("User connected" + connectionSocket.getInetAddress());
                count++;

	try {
		processRequest();
		
	} catch (Exception e) {
		System.out.println(e);
	}
	 
	}
	
	
	private void processRequest() throws Exception
	{
            String fromClient;
            String clientCommand;
            byte[] data;
            String frstln;
                    
            while(true)
            {
                if(count==1)
                    System.out.println("User connected" + connectionSocket.getInetAddress());
                count++;
         
                DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                fromClient = inFromClient.readLine();
            
      		//System.out.println(fromClient);
                  StringTokenizer tokens = new StringTokenizer(fromClient);
            
                  frstln = tokens.nextToken();
                  port = Integer.parseInt(frstln);
                  clientCommand = tokens.nextToken();
                  //System.out.println(clientCommand);

		
                  if(clientCommand.equals("list:"))
                  { 
                  System.out.println("In server list");
                      String curDir = System.getProperty("user.dir");
       System.out.println("In server list port: " + port + "IP: " + connectionSocket.getInetAddress());
                      Socket dataSocket = new Socket("35.39.165.81", port);
                      //Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                      System.out.println("In server list2");
                      DataOutputStream  dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
                      System.out.println("In server list3");
                      File dir = new File(curDir);
    			System.out.println("In server list4");
                      String[] children = dir.list();
                      if (children == null) 
                      {
                          // Either dir does not exist or is not a directory
                          System.out.println("no children");
                      } 
                      else 
                      {
                      System.out.println("list else");
                          for (int i=0; i<children.length; i++)
                          {
                              // Get filename of file or directory
                              String filename = children[i];

                              if(filename.endsWith(".txt"))
                                dataOutToClient.writeUTF(children[i]);
                             System.out.println(filename);
                             if(i-1==children.length-2)
                             {
                                 dataOutToClient.writeUTF("eof");
                                 System.out.println("eof");
                             }//if(i-1)

     
                          }//for

                           dataSocket.close();
		          System.out.println("Data Socket closed");
                     }//else
        

                }//if list:


                if(clientCommand.equals("get:") || clientCommand.equals("retr:"))
                {
                      String curDir = System.getProperty("user.dir");
       
       		Socket dataSocket = new Socket("35.39.165.81", port);
                      //Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                      DataOutputStream  dataOutToClient = 
                      new DataOutputStream(dataSocket.getOutputStream());
                      File dir = new File(curDir);
    		      String filenameArg = tokens.nextToken();
                      String[] children = dir.list();
                      if (children == null) 
                      {
                          // Either dir does not exist or is not a directory
                      } 
                      else 
                      {
                          for (int i=0; i<children.length; i++)
                          {
                              // Get filename of file or directory
                              String filename = children[i];

                              //if(filename.endsWith(".txt"))
                              if(filename == filenameArg)
                              {
                              //dataOutToClient.writeUTF(children[i]);
                              //open file 
                              File cFile = new File(curDir + filename);
                              BufferedReader brFile = new BufferedReader(new FileReader(cFile));
                              String str;
  			      while ((str = brFile.readLine()) != null)
    					dataOutToClient.writeUTF(str);
                              //read line by line
                              //close file
                              //break
                              }
                             //System.out.println(filename);
                             if(i-1==children.length-2)
                             {
                                 dataOutToClient.writeUTF("eof");
                                 // System.out.println("eof");
                             }//if(i-1)

     
                          }//for

                           dataSocket.close();
		          //System.out.println("Data Socket closed");
                     }//else

		}

            }//main
        }
}
	

