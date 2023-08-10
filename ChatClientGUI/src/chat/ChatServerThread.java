package chat;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author akhrain
 */
import java.net.*;
import java.io.*;

public class ChatServerThread extends Thread
{  
   private ChatServer       server    = null;
   private Socket           socket    = null;
   private int              ID        = -1;
   private String           name      = null;
   private DataInputStream  streamIn  = null;
   private DataOutputStream streamOut = null;

   public ChatServerThread(ChatServer _server, Socket _socket)
   {  
      super();
      server = _server;
      socket = _socket;
      ID     = socket.getPort();
   }
   
   public void send(String msg)
   {   
       try
       {  
          streamOut.writeUTF(msg);
          streamOut.flush();
       }
       catch(IOException ioe)
       {  
          server.println(ID + " ERROR sending: " + ioe.getMessage());
          server.remove(ID);
          stop();
       }
   }
   
   public int getID()
   {  
      return ID;
   }
   
   public void run()
   {  
      try
      {
         name = streamIn.readUTF();
      }
      catch(IOException ioe)
      {  
         server.println(ID + " ERROR reading: " + ioe.getMessage());
         server.remove(ID);
         stop();
      }
      server.println("Server Thread " + ID + " running.");
      while (true)
      {  
         try
         {  
            server.handle(name,ID, streamIn.readUTF());
         }
         catch(IOException ioe)
         {  
            server.println(ID + " ERROR reading: " + ioe.getMessage());
            server.remove(ID);
            stop();
         }
      }
   }
   
   public void open() throws IOException
   {  
      streamIn = new DataInputStream(new 
                        BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
   }
   
   public void close() throws IOException
   {  
      if (socket != null)    socket.close();
      if (streamIn != null)  streamIn.close();
      if (streamOut != null) streamOut.close();
   }
}
