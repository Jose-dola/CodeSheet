/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import sheet.SheetFrame;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import sheet.DiceRoll;
import sheet.Line;

/**
 *
 * @author akhrain
 */
public class ChatClient extends javax.swing.JFrame {
  public static int CHATCLIENTWIDTH = 350;
  public static int CHATCLIENTHEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
//   public static String HTML_HEAD = "<head>" +
//                        "<style>" +
//                        "body {" +
//                        "        color: #FFFFFF;" +
//                        "}" +
//                        "table {" +
//                        "    border:1px solid #ffffff;"+
//                        "}" +
//                        "td{" +
//                        "    border: 1px solid #ffffff;" +
//                        "    text-align: left;" +
//                        "}" +
//                        "th {" +
//                        "    border: 1px solid #ffffff;" +
//                        "    text-align: center;" +
//                        "}" +
//                        "</style>" +
//                        "</head>";
//   public static String HTML_HEAD = "<head>" +
//                        "<style>" +
//                        "body {" +
//                          "color: #FFFFFF;" +
//                          "font-family: arial, sans-serif;"+
//                        "}" +
//                        "table {"+
//                          "border: 1px solid;"+
//                          "width: 100%;"+
//                        "}"+
//                        "th {"+                          
//                          "border: 1px solid;"+
//                          "text-align: center;"+
//                        "}"+
//                        "td {"+
//                          "border: 1px solid;"+
//                          "text-align: left;"+
//                        "}"+
//                        "</style>" +
//                        "</head>";
   public static String HTML_HEAD = "<head>" +
                        "<style>" +
                        "body {" +
                          "color: #FFFFFF;" +
                          "font-family: arial, sans-serif;"+
                        "}" +
                        "table.gtable {"+
                          "border: 1px solid;"+
                          "width: 100%;"+
                        "}"+
                        "table.gtable th {"+                          
                          "border: 1px solid;"+
                          "text-align: center;"+
                        "}"+
                        "table.gtable td {"+
                          "border: 1px solid;"+
                          "text-align: left;"+
                        "}"+
                        "</style>" +
                        "</head>";
   public static String HTML_WELCOME = "<p><body>" + 
                        "##################<br/>" +
                        "Hello to CodeSheet <br/>" +
                        "##################<br/>" +
                        "</body></p>";
   
   public static String NEW_LINE_STRING = "<br>";
   
   public static int MAXIMUM_NUMBER_OF_MESSAGES = 40;
   
   /* Attributes */
   private ArrayList<SheetFrame> sheetFrames = null;
   private Random random              = null;
   private String name                = "";
   private Socket socket              = null;
   private DataOutputStream streamOut = null;
   private ChatClientThread client    = null;
   private String    serverName   = "localhost";
   private int       serverPort   = 1992;
   private HTMLEditorKit   kit    = null;
//   private HTMLDocument    docHTML =  null;
   private Document  doc    = null;
//   private Style           style  = null;
   private String[]  inputs       = null;
   private int       inputs_index = 0;
   private int       inputs_looking_index = 0;
   private String[]  inputs_dice_expression = null;
   private int       inputs_dice_expression_index = 0;
   private int       inputs_dice_expression_looking_index = 0;
   private int       autoconnection = 0;
   private int nMessages = 0;
   private LinkedList<Integer> printLengths = null;
   private boolean removeFirstMessage = false;

    public class SheetListCellRenderer extends javax.swing.DefaultListCellRenderer
    {
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) 
        {  
            Component c = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );  
            if ( value instanceof SheetFrame ) 
            {  
                SheetFrame sf = (SheetFrame) value;
                if(sf.isVisible()) c.setForeground(SheetFrame.green);
                else               c.setForeground(SheetFrame.red);
            }  
            return c;  
        } 
    }
    /**
     * Creates new form ChatClient
     */
    public ChatClient() 
    {
        initComponents();
        this.setTitle("CodeSheet 2.6");
        sheetFrames = new ArrayList<SheetFrame>();
        random = new Random();
        /* radio buttons */
        buttonGroup.add(radiobtnLocal);
        buttonGroup.add(radiobtnShare);
        buttonGroup.setSelected(radiobtnShare.getModel(), true);
        /* display */
        input.setBackground(Color.black);
        input.setForeground(Color.white);
        input.setCaretColor(Color.white);
        /* lists */
        listSheetsMain.setBackground(Color.black);
        listSheetsMain.setForeground(Color.white);
        listSheetsMain.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        listSheetsMain.setCellRenderer(new SheetListCellRenderer());
        
        kit = new HTMLEditorKit();
        display.setEditorKit(kit);
        display.setContentType("text/html");
        doc = display.getDocument();
        printLengths = new LinkedList<Integer>();
        display.setText(HTML_HEAD);
        toDisplay(HTML_WELCOME);
        
    
        display.setBackground(Color.black);
        setBounds(0, 0, this.getWidth(), ChatClient.CHATCLIENTHEIGHT);
        display.setEditable(false);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        inputs = new String[50];
        inputs_dice_expression = new String[50];
        int i;
        for(i=0; i<50; i++) {inputs[i]=""; inputs_dice_expression[i]="";}
        fillListSheets();
        display.setVisible(true);
        
        readConfigFile(getLinesConfigFile());
    }
   
    /* auxiliary methods */
     
    private void readConfigFile(List<String> lines)
    {
        if(lines == null) return;
        for(String line : lines)
        {
            String[] brokenLine = line.split("=",2);
            if(brokenLine.length == 2 && brokenLine[0] != null && brokenLine[1] != null)
            {
                String parameter = Line.onlyFirstToken(brokenLine[0]);
                if(parameter.equals("openCodeSheet"))
                {
                    int i = brokenLine[1].indexOf("\"");
                    int j = brokenLine[1].indexOf("\"", i+1);
                    String codesheetpath = brokenLine[1].substring(i+1, j);
                    Path p = Paths.get(codesheetpath);
                    if(p != null)
                    {
                        File f = p.toFile();
                        if(f != null && f.isDirectory())
                            openCodeSheet(this, f, false);
                    }
                }
                else if(parameter.equals("nickName"))
                {
                    name = Line.onlyFirstToken(brokenLine[1]);
                }
                else if(parameter.equals("host"))
                {
                    serverName = Line.onlyFirstToken(brokenLine[1]);
                }
                else if(parameter.equals("port"))
                {
                    serverPort = Integer.parseInt(Line.onlyFirstToken(brokenLine[1]));
                }
                else if(parameter.equals("autoconnection"))
                {
                    String s = Line.onlyFirstToken(brokenLine[1]);
                    if(s.equals("server")) autoconnection = -1;
                    if(s.equals("client")) autoconnection = 1;
                }
            }
        }
        if(autoconnection == -1)
        {
            this.initServer(name, serverPort);
        }
        else if(autoconnection == 1)
        {
            this.connect();
        }
    }
    private List<String> getLinesConfigFile()
    {
        try
        {
            return Files.readAllLines(Paths.get("codesheet.config").toAbsolutePath());
        }
        catch(IOException ex) {}
        return null;
    }
   
    private void fillListSheets()
    {
    //    actualCodeSheet = null;
    //    fillListMacros();
    //    DefaultListModel listModelCodeSheets  = new DefaultListModel();
        DefaultListModel listModelSheetFrames = new DefaultListModel();
        //ArrayList<CodeSheet> csheets = new ArrayList<CodeSheet>();
        for(SheetFrame sf : sheetFrames) 
        { 
    //        listModelCodeSheets.addElement(sf.getCodeSheet());  
            listModelSheetFrames.addElement(sf);
        }
    //    listSheets.setModel(listModelCodeSheets);
        listSheetsMain.setModel(listModelSheetFrames);
    } 

   public Random getRandom() {return random;} 
   /**
    * Set serverName
    */
   public void setServerName(String s) {serverName = s;}
   /**
    * Set serverPort
    */
   public void setServerPort(int n) {serverPort = n;}
   /**
    * Set name
    */
   public void setName(String s) {name = s;}
   /**
    * get serverName
    */
   public String getServerName() {return serverName;}
   /**
    * get serverPort
    */
   public int    getServerPort() {return serverPort;}
   /**
    * get name
    */
   public String getName()       {return name;}
      
   public void printsheeterror(String msg)
   {
       msg = msg.replaceAll("\n", "<br>");
       toDisplay("<i> <font color=\"#FFFF00\">" + msg + "</i> </font>");
   }
   public void printsystem(String msg)
   {
       toDisplay("<i> <small> <font color=\"#FF0000\">" + msg + "</i> </small> </font>");
   }
   public void printmessage(String msg)
   {
       String[] splitedmsg = msg.split(":",2);
       String name  = splitedmsg[0];
       String start = " ";
       String end   = " ";
       if(name != null)
           if(name.charAt(0) == '#') //ROLL
           {
               name  = "<i><font color=\"#33ff00\">Rolling </font></i>".concat(name.substring(1));
               start = "<small>";
               end   = "</small>";
           }
       toDisplay("<body>" + start + "<b>[" + name + "]</b>:" + splitedmsg[1] + end + "</body>");
   }
   public void print(String txt)
   {
        if(radiobtnShare.getModel().isSelected()) send(txt); 
        else { printmessage("LOCAL: "+txt); }
   }
   public void printRoll(String txt)
   {
        if(radiobtnShare.getModel().isSelected()) send(ChatServer.ROLLPASSWORD + txt); 
        else { printmessage("#LOCAL: "+txt); } //# means that it is a roll
   }
   
   private void toDisplay(String s)
   {
       try
       {
           Reader reader = new StringReader(s);
           int lenBefore = doc.getLength();
           kit.read(reader, doc, lenBefore);
           int lenAfter  = doc.getLength();
           display.setCaretPosition(lenAfter);
           printLengths.add(new Integer(lenAfter-lenBefore));
           if(removeFirstMessage)
           {
               doc.remove(0, printLengths.getFirst().intValue());
               printLengths.removeFirst();
           }
           else
           {
               nMessages++;
               if(nMessages >= ChatClient.MAXIMUM_NUMBER_OF_MESSAGES) removeFirstMessage = true;
           }
       }
       catch(BadLocationException e){}
       catch(IOException e){}
   }
   
   public void hardResetDisplay()
   {
        printLengths = new LinkedList<Integer>();
        display.setText(HTML_HEAD);
        nMessages = 0;
        removeFirstMessage = false;
        toDisplay(HTML_WELCOME);
   }
   
   public void connect()
   {
        if(name.indexOf('#') >=0 || name.indexOf('[') >= 0 || name.indexOf(']') >= 0 || name.indexOf('<') >= 0 || name.indexOf('>') >= 0)
        {
            JOptionPane.showMessageDialog(this, "Your nickname can not include the following characters:\n# [ ] < >", "Nickname Error", JOptionPane.ERROR_MESSAGE, null);
            setName("");
            return;
        }  
        if (socket != null)
        { close(); }
        
              printsystem("Establishing connection. Please wait ...");
        try
        {  
          socket = new Socket(serverName, serverPort);
          printsystem("Connected: " + socket.getRemoteSocketAddress());
          open(); 
        }
        catch(UnknownHostException uhe)
        {  printsystem("Host unknown: " + uhe.getMessage()); }
        catch(IOException ioe)
        {  printsystem("Unexpected exception: " + ioe.getMessage()); }
        
        send(name);
   }
   
   public void initServer(String name, int port)
   {
       if(name == null) return;
        if(name.indexOf('#') >=0 || name.indexOf('[') >= 0 || name.indexOf(']') >= 0 || name.indexOf('<') >= 0 || name.indexOf('>') >= 0)
        {
            JOptionPane.showMessageDialog(this, "Your nickname can not include the following characters:\n# [ ] < >", "Nickname Error", JOptionPane.ERROR_MESSAGE, null);
            return;
        }  
        ChatServer server = new ChatServer(name , port , this);
        server.setBounds(this.getWidth(), 0, server.getWidth(), this.getHeight());
        server.setVisible(true);
   }
      
   public void open()
   {  
      try
      {  
         streamOut = new DataOutputStream(socket.getOutputStream());
         client = new ChatClientThread(this, socket); 
      }
      catch(IOException ioe)
      {  printsystem("Error opening output stream: " + ioe); } 
   }
   
   public void close()
   {  
      try
      {  
         if (streamOut != null)  streamOut.close();
         if (socket    != null)  socket.close(); 
      }
      catch(IOException ioe)
      {  printsystem("Error closing ..."); }
      client.close();  
      client.stop(); 
   }
   
   private void send(String s)
   {  
      if(streamOut == null)
      {
          printsystem("Sending error: you need to connect...");
      }
      else
      {
        try
        {  streamOut.writeUTF(s); streamOut.flush(); }
        catch(IOException ioe)
        {  printsystem("Sending error: " + ioe.getMessage()); close(); } 
      }
   }
   
    private void previousInstruction()
    {
        if(inputs_looking_index > 0)
        {
            inputs_looking_index--;
            input.setText(inputs[inputs_looking_index]);
        }
    }
    
    private void nextInstruction()
    {
        if(inputs_looking_index < inputs_index)
        {
            inputs_looking_index++;
            input.setText(inputs[inputs_looking_index]);
        }
    }
    
    public void exit()
    {
        int confirm = JOptionPane.showOptionDialog(this,
        "All opened code sheets are going to be closed. "
                + "\nAre you sure to close the code sheet application?",
        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (confirm == JOptionPane.YES_OPTION) 
        {
            this.dispose();
            System.exit(0);    
        }
    }
    
    public void removeSheetFrame(SheetFrame sf)
    {
        sheetFrames.remove(sf);
        fillListSheets();
    }
    private void addSheetFrame(SheetFrame sf)
    {
        sheetFrames.add(sf);
        fillListSheets();
    }
    
    private void openCodeSheet(ChatClient chatclient, File csheetFile, boolean visible)
    {
        if(chatclient == null || csheetFile == null) return;
        final SheetFrame sframe = new SheetFrame(chatclient, csheetFile);
        this.addSheetFrame(sframe);
        sframe.initialize();
        sframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        sframe.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) 
            {
                sframe.exit();
            }
        });
        sframe.setBounds(this.getWidth(), 0, sframe.getWidth(), this.getHeight());
        if(visible) sframe.setVisible(true);
    }
   
//   public void handle(String msg)
//   {  
//      if (msg.equals(".bye"))
//      {  printmessage("Good bye. Press RETURN to exit ...");  close(); }
//      else printmessage(msg); 
//   }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        input = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        display = new javax.swing.JEditorPane();
        radiobtnLocal = new javax.swing.JRadioButton();
        radiobtnShare = new javax.swing.JRadioButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        btnD4 = new javax.swing.JButton();
        btnD6 = new javax.swing.JButton();
        btnD2 = new javax.swing.JButton();
        btnD8 = new javax.swing.JButton();
        btnD10 = new javax.swing.JButton();
        btnD12 = new javax.swing.JButton();
        btnD20 = new javax.swing.JButton();
        fieldDiceExpression = new javax.swing.JTextField();
        btnRollExpression = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btnD3 = new javax.swing.JButton();
        btnD100 = new javax.swing.JButton();
        spinnerNside = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        btnRoll = new javax.swing.JButton();
        spinnerNdice = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listSheetsMain = new javax.swing.JList<>();
        btnSetVisible = new javax.swing.JButton();
        btnHide = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuOptions = new javax.swing.JMenu();
        menuNewSheet = new javax.swing.JMenuItem();
        menuOpenSheet = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        menuConnect = new javax.swing.JMenuItem();
        menuDisconnect = new javax.swing.JMenuItem();
        menuInitializeServer = new javax.swing.JMenuItem();
        menuExit = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputActionPerformed(evt);
            }
        });
        input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputKeyPressed(evt);
            }
        });

        jScrollPane1.setViewportView(display);

        radiobtnLocal.setText("Local printing");
        radiobtnLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiobtnLocalActionPerformed(evt);
            }
        });

        radiobtnShare.setText("Share printing");
        radiobtnShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiobtnShareActionPerformed(evt);
            }
        });

        jPanel5.setPreferredSize(new java.awt.Dimension(350, 300));

        btnD4.setText("d4");
        btnD4.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD4.setMaximumSize(new java.awt.Dimension(55, 24));
        btnD4.setMinimumSize(new java.awt.Dimension(55, 24));
        btnD4.setPreferredSize(new java.awt.Dimension(29, 22));
        btnD4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnD4ActionPerformed(evt);
            }
        });

        btnD6.setText("d6");
        btnD6.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD6.setMaximumSize(new java.awt.Dimension(55, 24));
        btnD6.setMinimumSize(new java.awt.Dimension(55, 24));
        btnD6.setPreferredSize(new java.awt.Dimension(29, 22));
        btnD6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnD6ActionPerformed(evt);
            }
        });

        btnD2.setText("d2");
        btnD2.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD2.setMaximumSize(new java.awt.Dimension(55, 24));
        btnD2.setMinimumSize(new java.awt.Dimension(55, 24));
        btnD2.setPreferredSize(new java.awt.Dimension(29, 22));
        btnD2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnD2ActionPerformed(evt);
            }
        });

        btnD8.setText("d8");
        btnD8.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD8.setMaximumSize(new java.awt.Dimension(55, 24));
        btnD8.setMinimumSize(new java.awt.Dimension(55, 24));
        btnD8.setPreferredSize(new java.awt.Dimension(29, 22));
        btnD8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnD8ActionPerformed(evt);
            }
        });

        btnD10.setText("d10");
        btnD10.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnD10ActionPerformed(evt);
            }
        });

        btnD12.setText("d12");
        btnD12.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnD12ActionPerformed(evt);
            }
        });

        btnD20.setText("d20");
        btnD20.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnD20ActionPerformed(evt);
            }
        });

        fieldDiceExpression.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        fieldDiceExpression.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldDiceExpressionActionPerformed(evt);
            }
        });
        fieldDiceExpression.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fieldDiceExpressionKeyPressed(evt);
            }
        });

        btnRollExpression.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        btnRollExpression.setText("ROLL");
        btnRollExpression.setMargin(new java.awt.Insets(1, 2, 1, 2));
        btnRollExpression.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRollExpressionActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel4.setText("Write expression (i.e: 1d20+(2d6*2+1d8)/2+3*5d5*2)");

        btnD3.setText("d3");
        btnD3.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD3.setMaximumSize(new java.awt.Dimension(55, 24));
        btnD3.setMinimumSize(new java.awt.Dimension(55, 24));
        btnD3.setPreferredSize(new java.awt.Dimension(29, 22));
        btnD3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnD3ActionPerformed(evt);
            }
        });

        btnD100.setText("d100");
        btnD100.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnD100.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnD100ActionPerformed(evt);
            }
        });

        spinnerNside.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("d");
        jLabel2.setToolTipText("");

        btnRoll.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        btnRoll.setText("ROLL");
        btnRoll.setToolTipText("");
        btnRoll.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnRoll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRollActionPerformed(evt);
            }
        });

        spinnerNdice.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnD2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnD8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnD10)
                            .addComponent(btnD3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnD12)
                            .addComponent(btnD4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnD20)
                            .addComponent(btnD6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerNdice, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(spinnerNside, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRoll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnD100)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(fieldDiceExpression)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRollExpression))
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnD10, btnD12, btnD2, btnD20, btnD3, btnD4, btnD6, btnD8});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spinnerNdice)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnD2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnD3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnD10)
                                .addComponent(btnD8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(btnD6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnD20))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(btnD4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnD12)))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spinnerNside)
                    .addComponent(btnRoll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnD100, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fieldDiceExpression, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRollExpression, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnD10, btnD12, btnD2, btnD20, btnD3, btnD4, btnD6, btnD8});

        jTabbedPane1.addTab("Dice Roller", jPanel5);

        listSheetsMain.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listSheetsMainMouseClicked(evt);
            }
        });
        listSheetsMain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listSheetsMainKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(listSheetsMain);

        btnSetVisible.setText("show");
        btnSetVisible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetVisibleActionPerformed(evt);
            }
        });

        btnHide.setText("hide");
        btnHide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHideActionPerformed(evt);
            }
        });

        btnClose.setText("close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSetVisible, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnHide, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnSetVisible)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHide)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClose))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab("Sheets", jPanel1);

        menuOptions.setText("Options");

        menuNewSheet.setText("New Code Sheet");
        menuNewSheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewSheetActionPerformed(evt);
            }
        });
        menuOptions.add(menuNewSheet);

        menuOpenSheet.setText("Open Code Sheet");
        menuOpenSheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenSheetActionPerformed(evt);
            }
        });
        menuOptions.add(menuOpenSheet);

        jMenuItem1.setText("Open Code Sheet (hidden)");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menuOptions.add(jMenuItem1);

        menuConnect.setText("Connect");
        menuConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuConnectActionPerformed(evt);
            }
        });
        menuOptions.add(menuConnect);

        menuDisconnect.setText("Disconnect");
        menuDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDisconnectActionPerformed(evt);
            }
        });
        menuOptions.add(menuDisconnect);

        menuInitializeServer.setText("Initialize Server");
        menuInitializeServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuInitializeServerActionPerformed(evt);
            }
        });
        menuOptions.add(menuInitializeServer);

        menuExit.setText("Exit");
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        menuOptions.add(menuExit);

        jMenuBar1.add(menuOptions);

        jMenu1.setText("Display");

        jMenuItem2.setText("Reset Display");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(input, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radiobtnShare)
                .addGap(18, 18, 18)
                .addComponent(radiobtnLocal)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radiobtnLocal)
                    .addComponent(radiobtnShare))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(input, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputActionPerformed
      String txt = input.getText();
      if(inputs_index == 50) inputs_index = 0;
      inputs[inputs_index] = txt;
      inputs_index++;
      inputs_looking_index = inputs_index;
      print(txt);
      input.setText("");
    }//GEN-LAST:event_inputActionPerformed

    private void menuConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuConnectActionPerformed
        ConnectDialog cd = new ConnectDialog(this, true);
        /* Set visible */
        cd.pack();
        cd.setVisible(true);
    }//GEN-LAST:event_menuConnectActionPerformed

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
        this.exit();
    }//GEN-LAST:event_menuExitActionPerformed

    private void menuOpenSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenSheetActionPerformed
        // Create FileChooser
        JFileChooser selection = new JFileChooser();
        selection.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // Show the file chooser window to select a directory
        int result = selection.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            openCodeSheet(this, selection.getSelectedFile(), true);
        }
    }//GEN-LAST:event_menuOpenSheetActionPerformed

    private void menuInitializeServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuInitializeServerActionPerformed
        MakeServerDialog sd = new MakeServerDialog(this,true);
        sd.pack();
        sd.setVisible(true);
    }//GEN-LAST:event_menuInitializeServerActionPerformed

    private void menuDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDisconnectActionPerformed
        close();
        printsystem("You are disconnected");
    }//GEN-LAST:event_menuDisconnectActionPerformed

    private void radiobtnShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiobtnShareActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radiobtnShareActionPerformed

    private void radiobtnLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiobtnLocalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radiobtnLocalActionPerformed

    private void inputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_UP ) previousInstruction();
        else if( evt.getKeyCode() == KeyEvent.VK_DOWN ) nextInstruction();
    }//GEN-LAST:event_inputKeyPressed

    private void menuNewSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewSheetActionPerformed
        String message = "WRITE THE NEW CODESHEET'S NAME\n"
                         + "After that, you should choose the directory where you want to save this CodeSheet\n"
                         + "Then, we are are going to create a directory with the CodeSheet's name\n"
                         + "You must create the CodeSheet files of this sheet there.\n"
                         + "To create/edit the files you can use the editor which is provided by this software or any other\n" 
                         + "plain text editor using UTF-8 encoding format";
        String sheetName = JOptionPane.showInputDialog(this, message, "New CodeSheet", JOptionPane.QUESTION_MESSAGE);
        if(sheetName == null)       return;
        if(sheetName.length() <= 0) return;
        // Create FileChooser
        JFileChooser selection = new JFileChooser();
        selection.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // Show the file chooser window to select a directory
        int result = selection.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File folder         = selection.getSelectedFile();
            File newSheetFolder = new File(folder, sheetName);
            boolean check = newSheetFolder.mkdir();
            if(check) 
            {
                final SheetFrame sframe = new SheetFrame(this, newSheetFolder);
                sframe.initialize();
                sframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                sframe.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent ev) 
                    {
                        sframe.exit();
                    }
                });
                sframe.setBounds(this.getWidth(), 0, sframe.getWidth(), this.getHeight());
                sframe.setVisible(true);
            }
        }
    }//GEN-LAST:event_menuNewSheetActionPerformed

    private void btnD4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD4ActionPerformed
        singleDieRoll(4);
    }//GEN-LAST:event_btnD4ActionPerformed

    private void btnD6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD6ActionPerformed
        singleDieRoll(6);
    }//GEN-LAST:event_btnD6ActionPerformed

    private void btnD2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD2ActionPerformed
        singleDieRoll(2);
    }//GEN-LAST:event_btnD2ActionPerformed

    private void btnD8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD8ActionPerformed
        singleDieRoll(8);
    }//GEN-LAST:event_btnD8ActionPerformed

    private void btnD10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD10ActionPerformed
        singleDieRoll(10);
    }//GEN-LAST:event_btnD10ActionPerformed

    private void btnD12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD12ActionPerformed
        singleDieRoll(12);
    }//GEN-LAST:event_btnD12ActionPerformed

    private void btnD20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD20ActionPerformed
        singleDieRoll(20);
    }//GEN-LAST:event_btnD20ActionPerformed

    private void btnD100ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD100ActionPerformed
        singleDieRoll(100);
    }//GEN-LAST:event_btnD100ActionPerformed

    private void btnRollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRollActionPerformed
        int ndice = ((Integer)spinnerNdice.getValue()).intValue();
        int nside = ((Integer)spinnerNside.getValue()).intValue();
        multipleDiceRoll(ndice, nside);
    }//GEN-LAST:event_btnRollActionPerformed

    private void fieldDiceExpressionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldDiceExpressionActionPerformed
        rollDiceExpression();
    }//GEN-LAST:event_fieldDiceExpressionActionPerformed

    private void fieldDiceExpressionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldDiceExpressionKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_UP ) previousDiceRollInstruction();
        else if( evt.getKeyCode() == KeyEvent.VK_DOWN ) nextDiceRollInstruction();
    }//GEN-LAST:event_fieldDiceExpressionKeyPressed

    private void btnRollExpressionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRollExpressionActionPerformed
        rollDiceExpression();
    }//GEN-LAST:event_btnRollExpressionActionPerformed

    private void btnD3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnD3ActionPerformed
        singleDieRoll(3);
    }//GEN-LAST:event_btnD3ActionPerformed

    private void btnSetVisibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetVisibleActionPerformed
        int i = listSheetsMain.getSelectedIndex();
        if(i<0) return;
        SheetFrame sf = sheetFrames.get(listSheetsMain.getSelectedIndex());
        sf.setVisible(true);
    }//GEN-LAST:event_btnSetVisibleActionPerformed

    private void btnHideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHideActionPerformed
        int i = listSheetsMain.getSelectedIndex();
        if(i<0) return;
        SheetFrame sf = sheetFrames.get(listSheetsMain.getSelectedIndex());
        sf.setVisible(false);
    }//GEN-LAST:event_btnHideActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        int i = listSheetsMain.getSelectedIndex();
        if(i<0) return;
        SheetFrame sf = sheetFrames.get(listSheetsMain.getSelectedIndex());
        sf.exit();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // Create FileChooser
        JFileChooser selection = new JFileChooser();
        selection.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // Show the file chooser window to select a directory
        int result = selection.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            openCodeSheet(this, selection.getSelectedFile(), false);
        }   
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void listSheetsMainKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listSheetsMainKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_ENTER ) showHideSheet();

    }//GEN-LAST:event_listSheetsMainKeyPressed

    private void listSheetsMainMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listSheetsMainMouseClicked
        if(evt.getClickCount() == 2) showHideSheet();
    }//GEN-LAST:event_listSheetsMainMouseClicked

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        hardResetDisplay();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void showHideSheet()
    {
        int i = listSheetsMain.getSelectedIndex();
        if(i<0) return;
        SheetFrame sf = sheetFrames.get(listSheetsMain.getSelectedIndex());
        if(sf.isVisible()) sf.setVisible(false);
        else               sf.setVisible(true);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                // LO HE CAMBIADO DE NIMBUS A METAL
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                final ChatClient cc = new ChatClient();
                cc.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                cc.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent ev) 
                    {
                        cc.exit();
                    }
                });
                cc.setLocation(0, 0);
                cc.setVisible(true);
            }
        });
    }
    
    private void previousDiceRollInstruction()
    {
        if(inputs_dice_expression_looking_index > 0)
        {
            inputs_dice_expression_looking_index--;
            fieldDiceExpression.setText(inputs_dice_expression[inputs_dice_expression_looking_index]);
        }
    }
    
    private void nextDiceRollInstruction()
    {
        if(inputs_dice_expression_looking_index < inputs_dice_expression_index)
        {
            inputs_dice_expression_looking_index++;
            fieldDiceExpression.setText(inputs_dice_expression[inputs_dice_expression_looking_index]);
        }
    }
    
    public void resolveDiceExpression(String s)
    {
        if(s == null) { printsheeterror("DICE ROLLER ERROR <br>"); return; }
        DiceRoll dr = new DiceRoll(s, this);
        String tab = dr.getTable();
        if(tab == null) printsheeterror("<i> <font color=\"#FFFF00\"> DICE ROLLER ERROR: check your expression. There are some errors. <br>"
                                    + "Remember that the system do NOT support multiple fractions, i.e., 50/5/2 <br> </i> </font>");
        else printRoll(tab);
    }
    
    private void rollDiceExpression()
    {
        String txt = fieldDiceExpression.getText();
        resolveDiceExpression(txt);    
        if(inputs_dice_expression_index == 50) inputs_dice_expression_index = 0;
        inputs_dice_expression[inputs_dice_expression_index] = txt;
        inputs_dice_expression_index++;
        inputs_dice_expression_looking_index = inputs_dice_expression_index;
        fieldDiceExpression.setText("");
    }
    
    public void singleDieRoll(int nside)
    {
        int roll = DiceRoll.rollDice(nside, this);
        printRoll("1<font color=\"#33ff00\">d</font>"+nside+"<font color=\"#33ff00\"> -> </font>" + roll);
    }
    
        public void multipleDiceRoll(int ndice, int nside)
    {
        int i;
        int roll;
        int sum = 0;
        String diceResults = new String(" ");
        for(i=0; i<ndice; i++)
        {
            roll = DiceRoll.rollDice(nside, this);
            sum += roll;
            diceResults = diceResults.concat(roll+" ");
        }
        printRoll(ndice+"<font color=\"#33ff00\">d</font>"+nside+"<font color=\"#33ff00\"> : </font>" + diceResults + "<font color=\"#33ff00\"> -> </font>"+sum);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnD10;
    private javax.swing.JButton btnD100;
    private javax.swing.JButton btnD12;
    private javax.swing.JButton btnD2;
    private javax.swing.JButton btnD20;
    private javax.swing.JButton btnD3;
    private javax.swing.JButton btnD4;
    private javax.swing.JButton btnD6;
    private javax.swing.JButton btnD8;
    private javax.swing.JButton btnHide;
    private javax.swing.JButton btnRoll;
    private javax.swing.JButton btnRollExpression;
    private javax.swing.JButton btnSetVisible;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JEditorPane display;
    private javax.swing.JTextField fieldDiceExpression;
    private javax.swing.JTextField input;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JList<String> listSheetsMain;
    private javax.swing.JMenuItem menuConnect;
    private javax.swing.JMenuItem menuDisconnect;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuInitializeServer;
    private javax.swing.JMenuItem menuNewSheet;
    private javax.swing.JMenuItem menuOpenSheet;
    private javax.swing.JMenu menuOptions;
    private javax.swing.JRadioButton radiobtnLocal;
    private javax.swing.JRadioButton radiobtnShare;
    private javax.swing.JSpinner spinnerNdice;
    private javax.swing.JSpinner spinnerNside;
    // End of variables declaration//GEN-END:variables
}
