import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

//connect 35.39.165.81 10123
//get: test1.txt
//connect 192.168.217.1 10123
/******************************************************************************
 @author: Justin Von Kulajta Winn and Wesley Luna
 @Version: 1.0
  * FTPClient connects to a server and establishes a FTP connection with it.
 * This is done by creating a control connection and then sending commands over
 * the control connection. It creates data connections to pass data. This
 * includes sending a file, receiving a file, and receiving a list of files.
 *****************************************************************************/
class FTPClient {

    public static void main(String argv[]) throws Exception
    {
        String sentence;
        String modifiedSentence;
        boolean isOpen = true;
        int fileFound = 0;
        int number=1;
        boolean notEnd = true;
        int port1=1221;
        int port = 10133;
        String statusCode;
        boolean clientgo = true;
        Socket ControlSocket;
        System.out.println("Welcome to the simple FTP App   \n     Commands  \nconnect servername port# connects to a specified server \nlist: lists files on server \nget: fileName.txt downloads that text file to your current directory \nstor: fileName.txt Stores the file on the server \nclose terminates the connection to the server");
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);

        while(true)
        {
            if(sentence.startsWith("connect")){
                String serverName = tokens.nextToken(); // pass the connect command
                serverName = tokens.nextToken();
                port1 = Integer.parseInt(tokens.nextToken());
                System.out.println("You are connected to " + serverName);
                ControlSocket = new Socket(serverName, port1);
                break;
            }
            sentence = inFromUser.readLine();
            tokens = new StringTokenizer(sentence);
        }
        while(isOpen && clientgo)
        {

            sentence = inFromUser.readLine();
            DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
            DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));

            if(sentence.equals("list:"))
            {
                port = port +2;
                System.out.println(port);
                ServerSocket welcomeData = new ServerSocket(port);


                System.out.println("\n \n \nThe files on this server are:");
                outToServer.writeBytes (port + " " + sentence + " " + '\n');

                Socket dataSocket =welcomeData.accept();
                DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                while(notEnd)
                {
                    modifiedSentence = inData.readUTF();
                    System.out.println("Not in if");
                    if(modifiedSentence.equals("eof"))
                    {
                        System.out.println("Entered if");
                        break;
                    }
                    System.out.println("	" + modifiedSentence);
                }

                welcomeData.close();
                dataSocket.close();
                System.out.println("\nWhat would you like to do next: \nget: file.txt ||  stor: file.txt  || close");

            }
            else if(sentence.startsWith("get: ") || sentence.startsWith("retr: "))
            {
                port = port + 2;
                System.out.println(port);
                ServerSocket welcomeData = new ServerSocket(port);

                outToServer.writeBytes (port + " " + sentence + " " + '\n');


                Socket dataSocket = welcomeData.accept();
                DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));


                if ((inData.readUTF()).equals("200: ok")){
                    String splits[] = sentence.split(" ", 2);
                    String filename = splits[1];
                    File fin = new File(filename);
                    FileOutputStream fos = new FileOutputStream(fin);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                    while(notEnd)
                    {
                        modifiedSentence = inData.readUTF();
                        System.out.println(modifiedSentence);
                        if(modifiedSentence.equals("eof"))
                            break;
                        bw.write(modifiedSentence);
                        bw.newLine();
                    }
                    fos.close();//close file
                }
                else if ((inData.readUTF()).equals("550: DNE")){
                    System.out.println("File does not exist");
                }
                else {
                    System.out.println("Server connection failed");
                }
                
                welcomeData.close();
                dataSocket.close();
                System.out.println("\nWhat would you like to do next: \nget: file.txt ||  stor: file.txt  || close");

            }else if(sentence.startsWith("stor: "))
            {
                port = port + 2;
                System.out.println(port);
                ServerSocket welcomeData = new ServerSocket(port);

                outToServer.writeBytes (port + " " + sentence + " " + '\n');

                Socket SocketData = welcomeData.accept();
                DataOutputStream  outData = new DataOutputStream(SocketData.getOutputStream());
                String curDir = System.getProperty("user.dir");
                File dir = new File(curDir);
                String filenameArg = tokens.nextToken();
                String[] children = dir.list();
                if (children == null)
                {
                    System.out.println("File Directory is Empty");
                }
                else {
                    for (int i = 0; i < children.length; i++) {
                        String filename = children[i];

                        //if(filename.endsWith(".txt"))
                        System.out.println(filename + " = " + filenameArg + "?");
                        if (filename.equals(filenameArg)) {
                            fileFound = 1;
                            File cFile = new File(curDir + "//" + filename);
                            BufferedReader brFile = new BufferedReader(new FileReader(cFile));
                            String str;
                            //outData.writeUTF("200: ok");
                            while ((str = brFile.readLine()) != null)
                                outData.writeUTF(str);
                        }
                        if (i - 1 == children.length - 2) {
                            outData.writeUTF("eof");
                        }


                    }
                    if (fileFound == 0) {
                        System.out.println("File "+ filenameArg + " not found");
                    }
                }
                fileFound = 0;
                SocketData.close();





                welcomeData.close();

                System.out.println("\nWhat would you like to do next: \nget: file.txt ||  stor: file.txt  || close");

            }else{
                if(sentence.equals("close") || sentence.equals("quit"))
                {
                    clientgo = false;
                    ControlSocket.close();
                }
                System.out.print("No server exists with that name or server not listening on that port try again");
            }
        }
    }
}
