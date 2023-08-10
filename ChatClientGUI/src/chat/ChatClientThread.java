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

public class ChatClientThread extends Thread
{  
   private Socket           socket   = null;
   private ChatClient       client   = null;
   private DataInputStream  streamIn = null;

   public ChatClientThread(ChatClient _client, Socket _socket)
   {  
      client   = _client;
      socket   = _socket;
      open();  
      start();
   }
   
   public void open()
   {  
      try
      {  
         streamIn  = new DataInputStream(socket.getInputStream());
      }
      catch(IOException ioe)
      {  
         client.printsystem("Error getting input stream: " + ioe);
         client.close();
      }
   }
   
   public void close()
   {  
      try
      {  
         if (streamIn != null) streamIn.close();
      }
      catch(IOException ioe)
      {  
         client.printsystem("Error closing input stream: " + ioe);
      }
   }
   
   public void run()
   {  
      while (true)
      {  
         try
         {  
            client.printmessage(streamIn.readUTF());
         }
         catch(IOException ioe)
         {  
            client.printsystem("Listening error: " + ioe.getMessage());
            client.close();
         }
      }
   }
}

