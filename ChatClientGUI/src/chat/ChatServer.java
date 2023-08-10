/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.WindowConstants;

/**
 *
 * @author akhrain
 */
public class ChatServer extends javax.swing.JFrame implements Runnable
{
   public  static String ROLLPASSWORD = "PELUDITA1402";
   private static int ROLLPASSWORDLENGTH = ROLLPASSWORD.length();
   private ChatServerThread clients[] = new ChatServerThread[50];
   private ServerSocket server = null;
   private Thread       thread = null;
   private int clientCount = 0;

    /**
     * Creates new form ChatServer
     */
    public ChatServer(String name, int port, ChatClient cc) 
    {
        initComponents();
        this.setTitle("CodeSheet Server");
        /* Server things */
        try
        {  
            println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);  
            println("Server started: " + server);
            start(); 
        }
        catch(IOException ioe)
        {  println("Can not bind to port " + port + ": " + ioe.getMessage()); }
        /* Client things */
        cc.setServerName("localhost");
        cc.setServerPort(port);
        cc.setName(name);
        cc.connect();
        /* GUI things */
        txtServerOutput.setBackground(Color.BLACK);
        txtServerOutput.setForeground(Color.WHITE);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent ev) 
                {
                    exit();
                }
            });
    }
    
    public void println(String s)
    {
        txtServerOutput.append(s+"\n");
    }

       
   public void run()
   {  
      while (thread != null)
      {  
         try
         {  println("Waiting for a client ..."); 
            addThread(server.accept()); }
         catch(IOException ioe)
         {  println("Server accept error: " + ioe); stop(); }
      }
   }
   
   public void start()  
   {
      if (thread == null)
      {  
         thread = new Thread(this); 
         thread.start();
      }
   }
   
   public void stop()   
   {  
      if (thread != null)
      {  
         thread.stop(); 
         thread = null;
      }
   }
   
   private int findClient(int ID)
   {  
      for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }
   
   public synchronized void handle(String name, int ID, String input)
   {  
      int i = 0;
      boolean rollCheck = true;
      
      if (input.equals(".bye"))
      {  
         clients[findClient(ID)].send(".bye");
         remove(ID); 
      }
      else
      {
         if(input != null)
         {
             if(input.length() >= ROLLPASSWORDLENGTH)
             {
                 for(i=0; i<ROLLPASSWORDLENGTH; i++) 
                     if(input.charAt(i) != ROLLPASSWORD.charAt(i)) { rollCheck = false; break; }
             }
             else rollCheck = false;
         }
         else rollCheck = false;
         if(rollCheck)
         {
             input = input.substring(ROLLPASSWORDLENGTH);
             for (i = 0; i < clientCount; i++)
                clients[i].send("#"+name + ": " + input);             
         }
         else
         {
             for (i = 0; i < clientCount; i++)
                clients[i].send(name + ": " + input);   
         }
      }
   }
   
   public synchronized void remove(int ID)
   {  
      int pos = findClient(ID);
      if (pos >= 0)
      {  ChatServerThread toTerminate = clients[pos];
         println("Removing client thread " + ID + " at " + pos);
         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
               clients[i-1] = clients[i];
         clientCount--;
         try
         {  toTerminate.close(); }
         catch(IOException ioe)
         {  println("Error closing thread: " + ioe); }
         toTerminate.stop(); }
   }
   
   private void addThread(Socket socket)
   {  
      if (clientCount < clients.length)
      {  
         println("Client accepted: " + socket);
         clients[clientCount] = new ChatServerThread(this, socket);
         try
         {  clients[clientCount].open(); 
            clients[clientCount].start();
            clientCount++; }
         catch(IOException ioe)
         {  println("Error opening thread: " + ioe); } 
      }
      else
         println("Client refused: maximum " + clients.length + " reached.");
   }
   
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnClose = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtServerOutput = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnClose.setText("Close the server");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        txtServerOutput.setColumns(20);
        txtServerOutput.setRows(5);
        jScrollPane1.setViewportView(txtServerOutput);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.exit();
    }//GEN-LAST:event_btnCloseActionPerformed

    public void exit()
    {
        for(ChatServerThread t : clients) 
        {
            if (t != null)
            {  
                try
                {  t.close(); }
                catch(IOException ioe)
                {  println("Error closing thread: " + ioe); }
                t.stop(); 
                t = null;
            }
        }
        this.stop();
        this.dispose();
    } 
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtServerOutput;
    // End of variables declaration//GEN-END:variables
}
