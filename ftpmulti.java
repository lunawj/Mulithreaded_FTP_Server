import java.net.*;
import java.io.*;

/******************************************************************************
 @author: Justin Von Kulajta Winn and Wesley Luna
 @Version: 1.0
  * FTPMulti allows the FTPserver to handle multiple clients at once.
 *****************************************************************************/
public class ftpmulti {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;
	ftpserver w;

        try {
            serverSocket = new ServerSocket(10123);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 10123.");
            System.exit(-1);
        }

	while (listening)
	{
	    
	    w = new ftpserver(serverSocket.accept());
	    Thread t = new Thread(w);
	    t.start();
	   
	   
	}

       
    }
}

