/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import chat.ChatClient;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import macro.ExecutionMacro;
import macro.Macro;
import macro.MacroCode;

/**
 *
 * @author akhrain
 */
public class SheetFrame extends javax.swing.JFrame 
{
    public static Color      red   = new Color(255, 100, 100);
    public static Color      green = new Color(100,255,100);
    public static Color      yellow = new Color(255,255,100); 
    public static int        editorFontSize = 10;
    
    private String name = null;
    private ExecutionMacro rootMacro = null;
    private ArrayList<Attribute> attributes = null;
    private ArrayList<Attribute> showingAttributes = null;
    private ArrayList<String>    showingNumVar = null;
    private ArrayList<String>    showingTxtVar = null;
    private ArrayList<Macro>     macros = null;
    private ArrayList<Macro> showingMacros = null;
    private ArrayList<SheetFile> arrayFilesShowing = null;
    private ArrayList<SheetFile> arrayFilesEnabled = null;
    private ArrayList<SheetFile> arrayFilesDisabled = null;
    private SheetFile  actualFolderFiles = null;
    private SheetFile  actualFolderFilesEditor = null;
    private SheetFile  actualFileInEditor = null;
    private ArrayList<Macro> actualFolderMacros = null;
    private ChatClient cclient = null;
    private CodeSheet  csheet  = null;
    private String[]  inputs       = null;
    private String[]  inputs_dice_expression = null;
    private int       inputs_index = 0;
    private int       inputs_looking_index = 0;
    private JPopupMenu popupMacros = null;
    private JMenuItem  execute     = null;
    private JMenuItem  executeWithDependentVariables = null;
    private JPopupMenu popupMacrosFolder = null;
    private JMenuItem  executeF     = null;
    private JMenuItem  executeWithDependentVariablesF = null;
    
    public class SheetFilesListCellRenderer extends javax.swing.DefaultListCellRenderer
    {
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) 
        {  
            Component c = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );  
            if ( value instanceof SheetFile ) 
            {  
                SheetFile sf = (SheetFile) value;
                if(sf.getType() == SheetFile.CSHEET)
                {
                    if(sf.isEnable()) c.setForeground(SheetFrame.green);
                    else              c.setForeground(SheetFrame.red);
                }
                else if(sf.getType() == SheetFile.CTABLE)
                    c.setForeground(SheetFrame.yellow);
            }  
            return c;  
        } 
    }
    
    public class MacroListCellRenderer extends javax.swing.DefaultListCellRenderer
    {
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) 
        {  
            Component c = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );  
            if ( value instanceof Macro ) 
            {  
                Macro m = (Macro) value;
                if(m.isEnabled()) c.setForeground(SheetFrame.green);
                else              c.setForeground(SheetFrame.red);
            }  
            return c;  
        } 
    }
    
    /**
     * Creates new form sheetFrame
     */
    public SheetFrame(ChatClient cc, File folder) 
    {
        initComponents();
        popupMacros = new JPopupMenu();
        execute = new JMenuItem("execute...");
        executeWithDependentVariables = new JMenuItem("execute with dependent variables...");
        popupMacros.add(execute);
        popupMacros.add(executeWithDependentVariables);
        execute.addActionListener(new ActionListener()
             {  
                 public void actionPerformed(ActionEvent e) 
                 {              
                      listAllMacrosEvent();
                 }  
             });  
        executeWithDependentVariables.addActionListener(new ActionListener()
            {
                 public void actionPerformed(ActionEvent e) 
                 {              
                      listAllMacrosEventDependentVariables();
                 }  
             }); 
        popupMacrosFolder = new JPopupMenu();
        executeF = new JMenuItem("execute...");
        executeWithDependentVariablesF = new JMenuItem("execute with dependent variables...");
        popupMacrosFolder.add(executeF);
        popupMacrosFolder.add(executeWithDependentVariablesF);
        executeF.addActionListener(new ActionListener()
             {  
                 public void actionPerformed(ActionEvent e) 
                 {              
                      listMacroFolderEvent();
                 }  
             });  
        executeWithDependentVariablesF.addActionListener(new ActionListener()
            {
                 public void actionPerformed(ActionEvent e) 
                 {              
                      listMacroFolderEventWithDependentVariables();
                 }  
             }); 
        inputs = new String[50];
        inputs_dice_expression = new String[50];
        int i;
        for(i=0; i<50; i++) {inputs[i]=""; inputs_dice_expression[i]="";}        
        actualFolderFiles = null;
        actualFolderFilesEditor = null;
        cclient = cc;

        input.setBackground(Color.black);
        input.setForeground(Color.white);
        input.setCaretColor(Color.white);
        listFilesEnabled.setBackground(Color.black);
        listFilesEnabled.setForeground(Color.white);
        listFilesDisabled.setBackground(Color.black);
        listFilesDisabled.setForeground(Color.white);
        listNumericalVariables.setBackground(Color.black);
        listNumericalVariables.setForeground(Color.white);
        listNumericalArrayVariables.setBackground(Color.black);
        listNumericalArrayVariables.setForeground(Color.white);
        listTextVariables.setBackground(Color.black);
        listTextVariables.setForeground(Color.white);
        listArrayTextVariables.setBackground(Color.black);
        listArrayTextVariables.setForeground(Color.white);
        listCounters.setBackground(Color.black);
        listCounters.setForeground(Color.white);
        listFiles.setBackground(Color.black);
        listFiles.setForeground(Color.white);
        listFilesEditor.setBackground(Color.black);
        listFilesEditor.setForeground(Color.white);
        txtAreaFileEditor.setBackground(Color.black);
        txtAreaFileEditor.setForeground(Color.white);
        txtAreaFileEditor.setEditable(false);
        txtAreaFileEditor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, editorFontSize));
        listMacroFolders.setBackground(Color.black);
        listMacroFolders.setForeground(Color.white);
        listMacroFolders.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        listAllMacros.setBackground(Color.black);
        listAllMacros.setForeground(Color.white);
        listAllMacros.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        listAttributes.setBackground(Color.black);
        listAttributes.setForeground(Color.white);
        listAttributes.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);

        listFilesEditor.setCellRenderer(new SheetFilesListCellRenderer()); 
        listFiles.setCellRenderer(new SheetFilesListCellRenderer());
        listAllMacros.setCellRenderer(new MacroListCellRenderer());
        listMacroFolders.setCellRenderer(new MacroListCellRenderer());
        csheet  = new CodeSheet(folder,cc,this);
        rootMacro = createRootMacro(csheet);
        name = csheet.getRootFolder().getName();
        this.setTitle(csheet.getRootFolder().getName());
    }
    
    public void initialize()
    {
        btnEdit.setEnabled(false);
        actualFolderFiles = null;
        actualFolderFilesEditor = null;
        actualFolderMacros = null;
        fillListModelMacroFolders();
        fillListModelFilesFolders();
        fillListModelFilesFoldersEditor();
        fillModels(csheet.getSheetFiles());
        fillListModelMacroFolders();
        macros     = csheet.getAllMacros();
        attributes = new ArrayList<Attribute>();
        for(Attribute a : csheet.getAttributes()) attributes.add(a);
        attributes.sort(new Comparator<Attribute>()
        {    
            public int compare(Attribute a1, Attribute a2) { return a1.toString().compareTo(a2.toString()); }
        });
        fillListAttributes();
        fillListModelMacros();
        fillListNumericalVariables();
        fillListNumericalArrayVariables();
        fillListTextVariables();
        fillListTextArrayVariables();
        fillListCounters();
    }
    
    private ExecutionMacro createRootMacro(CodeSheet cs)
    {
        SerialData sd = SerialData.recuperate(cs);
        if(sd == null) return new ExecutionMacro((ExecutionMacro)null);
        else
        {
            String[] options = new String[2];
            options[0]       = "restore old session";
            options[1]       = "discard and delete old session";
            int i = JOptionPane.showOptionDialog(cs.getChatClient(), "We have found an old session. Do you want to restore this session?", "Restore old saved session", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if(i == 0) 
            {
                txtAreaNotes.setText(sd.getNotes());
                return new ExecutionMacro(sd);
            }
            else
            {
                (new File(cs.getRootFolder(),"saved_session.system")).delete();
                return new ExecutionMacro((ExecutionMacro)null);
            }
        }
    }
    
    private void fillListAttributes()
    {
        DefaultListModel listModel = new DefaultListModel();
        showingAttributes = new ArrayList<Attribute>();
        for(Attribute a : attributes) { listModel.addElement(a); showingAttributes.add(a); }
        listAttributes.setModel(listModel);
    }
    private void fillListAttributes(String s)
    {
        DefaultListModel listModel = new DefaultListModel();
        showingAttributes = new ArrayList<Attribute>();
        for(Attribute a : attributes) 
        {
            if(a.checkName(s))
            {
                listModel.addElement(a);
                showingAttributes.add(a);
            }
        }
        listAttributes.setModel(listModel);        
    }
    
    private void fillListModelMacros()
    {
        DefaultListModel listModel = new DefaultListModel();
        showingMacros = new ArrayList<Macro>();
        for(Macro m : macros) 
        { 
            listModel.addElement(m); 
            showingMacros.add(m); 
        }
        listAllMacros.setModel(listModel);
    }
    private void fillListModelMacros(String s)
    {
        DefaultListModel listModel = new DefaultListModel();
        showingMacros = new ArrayList<Macro>();
        for(Macro m : macros) 
        {
            if(m.checkName(s))
            {
                listModel.addElement(m);
                showingMacros.add(m);
            }
        }
        listAllMacros.setModel(listModel);        
    }
    
    private void fillListModelMacroFolders()
    {
        DefaultListModel listModel = new DefaultListModel();
        if(actualFolderMacros != null)
        {
            listModel.addElement("... BACK ...");
            for(Macro m : actualFolderMacros) listModel.addElement(m);
        }
        else
        {
            for(String s : csheet.getMacroFolders().keySet()) listModel.addElement(s);
        }
        listMacroFolders.setModel(listModel);
    }
    
    private void fillListNumericalVariables()
    {
        DefaultListModel listModel = new DefaultListModel();
        showingNumVar = new ArrayList<String>();
        ArrayList<Map.Entry<String,Double>> arrayEntries = new ArrayList<Map.Entry<String,Double>>();
        for(Map.Entry<String,Double> entry : rootMacro.getVariablesHashMap().entrySet()) arrayEntries.add(entry);
        arrayEntries.sort(new Comparator<Map.Entry<String,Double>>()
        {    
            public int compare(Map.Entry<String,Double> e1, Map.Entry<String,Double> e2) 
            {
                if(e1 == null && e2 == null) return 0;
                if(e1 == null) return -1;
                if(e2 == null) return 1;
                String s1 = e1.getKey();
                String s2 = e2.getKey();
                if(s1 == null && s2 == null) return 0;
                if(s1 == null) return -1;
                if(s2 == null) return 1;
                return s1.compareTo(s2); 
            }
        });
        for(Map.Entry<String,Double> entry : arrayEntries) 
        {
            if(entry != null)
                if(entry.getKey() != null && entry.getValue() != null)
                {
                        listModel.addElement(entry.getKey()+" = "+entry.getValue());
                        showingNumVar.add(entry.getKey());
                }
        }
        listNumericalVariables.setModel(listModel);
    }    
    
    private void fillListNumericalArrayVariables()
    {
        DefaultListModel listModel = new DefaultListModel();
        ArrayList<String> listElements = new ArrayList<String>();
        for(Map.Entry<String,ArrayList<Double>> entry : rootMacro.getArrayHashMap().entrySet()) 
        {
            if(entry != null)
                if(entry.getKey() != null && entry.getValue() != null)
                        listElements.add(entry.getKey()+" = "+entry.getValue());
        }
        listElements.sort(new Comparator<String>()
        {    
            public int compare(String s1, String s2) { return s1.compareTo(s2); }
        });
        for(String s : listElements) listModel.addElement(s);
        listNumericalArrayVariables.setModel(listModel);
    }
    
    private void fillListTextVariables()
    {
        DefaultListModel listModel = new DefaultListModel();
        showingTxtVar = new ArrayList<String>();
        ArrayList<Map.Entry<String,String>> arrayEntries = new ArrayList<Map.Entry<String,String>>();
        for(Map.Entry<String,String> entry : rootMacro.getTxtvariablesHashMap().entrySet()) arrayEntries.add(entry);
        arrayEntries.sort(new Comparator<Map.Entry<String,String>>()
        {    
            public int compare(Map.Entry<String,String> e1, Map.Entry<String,String> e2) 
            {
                if(e1 == null && e2 == null) return 0;
                if(e1 == null) return -1;
                if(e2 == null) return 1;
                String s1 = e1.getKey();
                String s2 = e2.getKey();
                if(s1 == null && s2 == null) return 0;
                if(s1 == null) return -1;
                if(s2 == null) return 1;
                return s1.compareTo(s2); 
            }
        });
        for(Map.Entry<String,String> entry : arrayEntries) 
        {
            if(entry != null)
                if(entry.getKey() != null && entry.getValue() != null)
                {
                        listModel.addElement(entry.getKey()+" = \""+entry.getValue()+"\"");
                        showingTxtVar.add(entry.getKey());
                }
        }
        listTextVariables.setModel(listModel);
    }
    
    private void fillListTextArrayVariables()
    {
        DefaultListModel listModel = new DefaultListModel();
        ArrayList<String> listElements = new ArrayList<String>();
        for(Map.Entry<String,ArrayList<String>> entry : rootMacro.getArraytxtHashMap().entrySet()) 
        {
            if(entry != null)
                if(entry.getKey() != null && entry.getValue() != null)
                        listElements.add( entry.getKey()+" = "+textArraytoString(entry.getValue()) );
        }
        listElements.sort(new Comparator<String>()
        {    
            public int compare(String s1, String s2) { return s1.compareTo(s2); }
        });
        for(String s : listElements) listModel.addElement(s);
        listArrayTextVariables.setModel(listModel);
    }
    
    private String textArraytoString(ArrayList<String> array)
    {
        int i;
        int n = array.size();
        String s = "[";
        if(n > 0)
        {
            for(i=0; i<n-1; i++) s = s.concat("\""+array.get(i)+"\" , ");
            s = s.concat("\""+array.get(n-1)+"\"");
        }
        return s.concat("]");
    }
    
    private void fillListCounters()
    {
        DefaultListModel listModel = new DefaultListModel();
        ArrayList<String> listElements = new ArrayList<String>();
        for(Map.Entry<String,ArrayList<String>> entry : rootMacro.getCountersHashMap().entrySet()) 
        {
            if(entry != null)
                if(entry.getKey() != null && entry.getValue() != null)
                        listElements.add( entry.getKey()+" = "+textArraytoString(entry.getValue()) );
        }
        listElements.sort(new Comparator<String>()
        {    
            public int compare(String s1, String s2) { return s1.compareTo(s2); }
        });
        for(String s : listElements) listModel.addElement(s);
        listCounters.setModel(listModel);
    }
    
    
    private void fillModels(ArrayList<SheetFile> array)
    {
        arrayFilesShowing  = new ArrayList<SheetFile>();
        arrayFilesEnabled  = new ArrayList<SheetFile>();
        arrayFilesDisabled = new ArrayList<SheetFile>();
        DefaultListModel listModelEnabled  = new DefaultListModel();
        DefaultListModel listModelDisabled = new DefaultListModel();
        for(SheetFile sf : array ) 
        { 
            arrayFilesShowing.add(sf);
            if(sf.isEnable()) { listModelEnabled.addElement(sf); arrayFilesEnabled.add(sf); }
            else              { listModelDisabled.addElement(sf); arrayFilesDisabled.add(sf); }
        }
        listFilesEnabled.setModel(listModelEnabled);
        listFilesDisabled.setModel(listModelDisabled);
    }
    
//    private void fillListModelMacroFolders()
//    {
//        DefaultListModel listModelFolders = new DefaultListModel();
//        for(String folder : csheet.getMacroFolders().keySet())
//            listModelFolders.addElement(folder); 
//        listFolders.setModel(listModelFolders);
//    }
    
    private void fillListModelFilesFolders()
    {
        DefaultListModel listModelFiles = new DefaultListModel();
        if(actualFolderFiles != null)
        {
            if(actualFolderFiles.getFiles() == null) fillModelFiles(listModelFiles,csheet.getRootFiles());
            else fillModelFiles(listModelFiles,actualFolderFiles.getFiles());
        }
        else fillModelFiles(listModelFiles,csheet.getRootFiles());
        listFiles.setModel(listModelFiles);
    }
    
    private void fillListModelFilesFoldersEditor()
    {
        DefaultListModel listModelFiles = new DefaultListModel();
        if(actualFolderFilesEditor != null)
        {
            if(actualFolderFilesEditor.getFiles() == null) fillModelFiles(listModelFiles,csheet.getRootFiles());
            else fillModelFiles(listModelFiles,actualFolderFilesEditor.getFiles());
        }
        else fillModelFiles(listModelFiles,csheet.getRootFiles());
        listFilesEditor.setModel(listModelFiles);
    }
    
    private void fillModelFiles(DefaultListModel model, ArrayList<SheetFile> array)
    {
        for(SheetFile sf : array ) model.addElement(sf); 
    }
    
    public void setChatClient(ChatClient cc) {cclient = cc;}
    public void setCodeSheet(CodeSheet  cs)  {csheet  = cs;}
    
    public CodeSheet      getCodeSheet()     {return csheet;}
    public ExecutionMacro getRootMacro()     {return rootMacro;}
           
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        input = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        listMacroFolders = new javax.swing.JList<>();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listAllMacros = new javax.swing.JList<>();
        textFieldFilterMacros = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTabbedPane5 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        btnRefreshLists = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        listNumericalVariables = new javax.swing.JList<>();
        jPanel7 = new javax.swing.JPanel();
        btnRefreshLists1 = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        listNumericalArrayVariables = new javax.swing.JList<>();
        jPanel8 = new javax.swing.JPanel();
        btnRefreshLists2 = new javax.swing.JButton();
        jScrollPane11 = new javax.swing.JScrollPane();
        listTextVariables = new javax.swing.JList<>();
        jPanel9 = new javax.swing.JPanel();
        btnRefreshLists3 = new javax.swing.JButton();
        jScrollPane12 = new javax.swing.JScrollPane();
        listArrayTextVariables = new javax.swing.JList<>();
        jPanel10 = new javax.swing.JPanel();
        btnRefreshLists4 = new javax.swing.JButton();
        jScrollPane13 = new javax.swing.JScrollPane();
        listCounters = new javax.swing.JList<>();
        jPanel11 = new javax.swing.JPanel();
        textFieldAttributeSearch = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        listAttributes = new javax.swing.JList<>();
        spinnerDecimalsAttributes = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        txtAreaNotes = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtFieldSearch = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        listFilesEnabled = new javax.swing.JList<>();
        jScrollPane7 = new javax.swing.JScrollPane();
        listFilesDisabled = new javax.swing.JList<>();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listFiles = new javax.swing.JList<>();
        btnPreviousFolder = new javax.swing.JButton();
        btnUpdateByFolder = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        txtAreaFileEditor = new javax.swing.JTextArea();
        jScrollPane15 = new javax.swing.JScrollPane();
        listFilesEditor = new javax.swing.JList<>();
        btnNewCsheet = new javax.swing.JButton();
        btnNewCtable = new javax.swing.JButton();
        btnNewFolder = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnEditorPreviousFolder = new javax.swing.JButton();
        labelFile = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnDelete = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        btnExit = new javax.swing.JButton();
        btnUpdateSheet = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        input.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
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

        jLabel1.setText("Quick instruction:");

        listMacroFolders.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMacroFoldersMouseClicked(evt);
            }
        });
        listMacroFolders.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listMacroFoldersKeyPressed(evt);
            }
        });
        jScrollPane4.setViewportView(listMacroFolders);

        jTabbedPane3.addTab("Macro Folders", jScrollPane4);

        listAllMacros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listAllMacrosMouseClicked(evt);
            }
        });
        listAllMacros.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listAllMacrosKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(listAllMacros);

        textFieldFilterMacros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldFilterMacrosActionPerformed(evt);
            }
        });

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("filter:");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldFilterMacros))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldFilterMacros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("All Macros", jPanel13);

        jTabbedPane4.addTab("Macros", jTabbedPane3);

        btnRefreshLists.setText("Refresh");
        btnRefreshLists.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshListsActionPerformed(evt);
            }
        });

        listNumericalVariables.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listNumericalVariablesMouseClicked(evt);
            }
        });
        listNumericalVariables.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listNumericalVariablesKeyPressed(evt);
            }
        });
        jScrollPane9.setViewportView(listNumericalVariables);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnRefreshLists, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(btnRefreshLists)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Num", jPanel6);

        btnRefreshLists1.setText("Refresh");
        btnRefreshLists1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshLists1ActionPerformed(evt);
            }
        });

        listNumericalArrayVariables.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listNumericalArrayVariablesKeyPressed(evt);
            }
        });
        jScrollPane10.setViewportView(listNumericalArrayVariables);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnRefreshLists1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(btnRefreshLists1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Num Array", jPanel7);

        btnRefreshLists2.setText("Refresh");
        btnRefreshLists2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshLists2ActionPerformed(evt);
            }
        });

        listTextVariables.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listTextVariablesMouseClicked(evt);
            }
        });
        listTextVariables.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listTextVariablesKeyPressed(evt);
            }
        });
        jScrollPane11.setViewportView(listTextVariables);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnRefreshLists2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(btnRefreshLists2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Txt", jPanel8);

        btnRefreshLists3.setText("Refresh");
        btnRefreshLists3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshLists3ActionPerformed(evt);
            }
        });

        listArrayTextVariables.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listArrayTextVariablesKeyPressed(evt);
            }
        });
        jScrollPane12.setViewportView(listArrayTextVariables);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnRefreshLists3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(btnRefreshLists3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Txt Array", jPanel9);

        btnRefreshLists4.setText("Refresh");
        btnRefreshLists4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshLists4ActionPerformed(evt);
            }
        });

        listCounters.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listCountersMouseClicked(evt);
            }
        });
        listCounters.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listCountersKeyPressed(evt);
            }
        });
        jScrollPane13.setViewportView(listCounters);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnRefreshLists4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(btnRefreshLists4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Counters", jPanel10);

        jTabbedPane4.addTab("Variables", jTabbedPane5);

        textFieldAttributeSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldAttributeSearchActionPerformed(evt);
            }
        });

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("filter:");

        listAttributes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listAttributesMouseClicked(evt);
            }
        });
        listAttributes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listAttributesKeyPressed(evt);
            }
        });
        jScrollPane5.setViewportView(listAttributes);

        jLabel6.setText("decimals:");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(textFieldAttributeSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinnerDecimalsAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane5)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldAttributeSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spinnerDecimalsAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Attributes", jPanel11);

        txtAreaNotes.setColumns(20);
        txtAreaNotes.setRows(5);
        jScrollPane8.setViewportView(txtAreaNotes);

        jTabbedPane4.addTab("Notes", jScrollPane8);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(input)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jTabbedPane4))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jTabbedPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(input, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Execution", jPanel1);

        jLabel3.setText("Filter:");

        txtFieldSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFieldSearchActionPerformed(evt);
            }
        });

        listFilesEnabled.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listFilesEnabledMouseClicked(evt);
            }
        });
        listFilesEnabled.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listFilesEnabledKeyPressed(evt);
            }
        });
        jScrollPane6.setViewportView(listFilesEnabled);

        listFilesDisabled.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listFilesDisabledMouseClicked(evt);
            }
        });
        listFilesDisabled.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listFilesDisabledKeyPressed(evt);
            }
        });
        jScrollPane7.setViewportView(listFilesDisabled);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("ENABLED");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("DISABLED");

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(btnUpdate)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFieldSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
                            .addComponent(jScrollPane6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                            .addComponent(jScrollPane7))))
                .addGap(14, 14, 14))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                    .addComponent(jScrollPane6))
                .addContainerGap())
        );

        jTabbedPane2.addTab("All", jPanel4);

        listFiles.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listFiles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listFilesMouseClicked(evt);
            }
        });
        listFiles.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listFilesKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(listFiles);

        btnPreviousFolder.setText("Previous Folder");
        btnPreviousFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousFolderActionPerformed(evt);
            }
        });

        btnUpdateByFolder.setText("Update");
        btnUpdateByFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateByFolderActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 747, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnUpdateByFolder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnPreviousFolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnPreviousFolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUpdateByFolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("By Folder", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jTabbedPane2)
                .addGap(0, 0, 0))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );

        jTabbedPane1.addTab("csheet De/Activator", jPanel3);

        txtAreaFileEditor.setColumns(20);
        txtAreaFileEditor.setRows(5);
        jScrollPane14.setViewportView(txtAreaFileEditor);

        listFilesEditor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listFilesEditorMouseClicked(evt);
            }
        });
        listFilesEditor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listFilesEditorKeyPressed(evt);
            }
        });
        jScrollPane15.setViewportView(listFilesEditor);

        btnNewCsheet.setText("new .csheet");
        btnNewCsheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewCsheetActionPerformed(evt);
            }
        });

        btnNewCtable.setText("new .ctable");
        btnNewCtable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewCtableActionPerformed(evt);
            }
        });

        btnNewFolder.setText("new folder");
        btnNewFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewFolderActionPerformed(evt);
            }
        });

        btnEdit.setText("EDIT");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnEditorPreviousFolder.setText("Previous Folder");
        btnEditorPreviousFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditorPreviousFolderActionPerformed(evt);
            }
        });

        labelFile.setText("---");
        labelFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel11.setText("Select a File:");

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Remember to update your CodeSheet after making file changes if you want that the CodeSheet has all changes");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEditorPreviousFolder, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNewFolder, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNewCtable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNewCsheet, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(labelFile, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane14, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFile, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEdit)
                    .addComponent(btnEditorPreviousFolder))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNewFolder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNewCsheet)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNewCtable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete))
                    .addComponent(jScrollPane14))
                .addContainerGap())
        );

        jTabbedPane1.addTab("File Viewer & Editor", jPanel12);

        btnExit.setText("Close & Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        btnUpdateSheet.setText("Update Sheet");
        btnUpdateSheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateSheetActionPerformed(evt);
            }
        });

        btnSave.setText("Save Session");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnExit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdateSheet)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExit)
                    .addComponent(btnSave)
                    .addComponent(btnUpdateSheet))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("CSheet_Files");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPreviousFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousFolderActionPerformed
        listFilesEventDelete();
    }//GEN-LAST:event_btnPreviousFolderActionPerformed

    private void listFilesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listFilesMouseClicked
        if(evt.getClickCount() == 2) listFilesEventEnter();
    }//GEN-LAST:event_listFilesMouseClicked

    private void listFilesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listFilesKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_ENTER ) listFilesEventEnter();
        if( evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) listFilesEventDelete();
    }//GEN-LAST:event_listFilesKeyPressed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        fillModels(csheet.searchCSheetByPattern(txtFieldSearch.getText()));
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void listFilesDisabledKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listFilesDisabledKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_ENTER ) listFilesDisabledEvent();
    }//GEN-LAST:event_listFilesDisabledKeyPressed

    private void listFilesDisabledMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listFilesDisabledMouseClicked
        if(evt.getClickCount() == 2) listFilesDisabledEvent();
    }//GEN-LAST:event_listFilesDisabledMouseClicked

    private void listFilesEnabledKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listFilesEnabledKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_ENTER ) listFilesEnabledEvent();
    }//GEN-LAST:event_listFilesEnabledKeyPressed

    private void listFilesEnabledMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listFilesEnabledMouseClicked
        if(evt.getClickCount() == 2) listFilesEnabledEvent();
    }//GEN-LAST:event_listFilesEnabledMouseClicked

    private void txtFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFieldSearchActionPerformed
        fillModels(csheet.searchCSheetByPattern(txtFieldSearch.getText()));
    }//GEN-LAST:event_txtFieldSearchActionPerformed

    private void btnUpdateByFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateByFolderActionPerformed
        fillListModelFilesFolders();
    }//GEN-LAST:event_btnUpdateByFolderActionPerformed

    private void btnUpdateSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateSheetActionPerformed
        updateCodeSheet();
    }//GEN-LAST:event_btnUpdateSheetActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        exit();
    }//GEN-LAST:event_btnExitActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void inputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_UP ) previousInstruction();
        else if( evt.getKeyCode() == KeyEvent.VK_DOWN ) nextInstruction();
    }//GEN-LAST:event_inputKeyPressed

    private void inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputActionPerformed
        String txt = input.getText();
        (new MacroCode(txt,csheet)).execute(csheet, rootMacro);
        if(inputs_index == 50) inputs_index = 0;
        inputs[inputs_index] = txt;
        inputs_index++;
        inputs_looking_index = inputs_index;
        input.setText("");
    }//GEN-LAST:event_inputActionPerformed

    private void listFilesEditorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listFilesEditorKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) listFilesEditorEventDelete();
        if( evt.getKeyCode() == KeyEvent.VK_ENTER)      listFilesEditorEventEnter();
    }//GEN-LAST:event_listFilesEditorKeyPressed

    private void btnEditorPreviousFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditorPreviousFolderActionPerformed
        listFilesEditorEventDelete();
    }//GEN-LAST:event_btnEditorPreviousFolderActionPerformed

    private void listFilesEditorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listFilesEditorMouseClicked
        if(evt.getClickCount() == 2) listFilesEditorEventEnter();
    }//GEN-LAST:event_listFilesEditorMouseClicked

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        editActualFile();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnNewFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewFolderActionPerformed
        newFolder();
    }//GEN-LAST:event_btnNewFolderActionPerformed

    private void btnNewCsheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewCsheetActionPerformed
        newCsheet();
    }//GEN-LAST:event_btnNewCsheetActionPerformed

    private void btnNewCtableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewCtableActionPerformed
        newCtable();
    }//GEN-LAST:event_btnNewCtableActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteSelection();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void listAttributesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listAttributesKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_ENTER ) attributeClicked();
    }//GEN-LAST:event_listAttributesKeyPressed

    private void listAttributesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listAttributesMouseClicked
        if(evt.getClickCount() == 2) attributeClicked();
    }//GEN-LAST:event_listAttributesMouseClicked

    private void textFieldAttributeSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldAttributeSearchActionPerformed
        fillListAttributes(textFieldAttributeSearch.getText());
    }//GEN-LAST:event_textFieldAttributeSearchActionPerformed

    private void listCountersKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listCountersKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) deleteCounter();
    }//GEN-LAST:event_listCountersKeyPressed

    private void listCountersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listCountersMouseClicked
        ////////////////////////////////////////////
    }//GEN-LAST:event_listCountersMouseClicked

    private void btnRefreshLists4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshLists4ActionPerformed
        fillLists();
    }//GEN-LAST:event_btnRefreshLists4ActionPerformed

    private void listArrayTextVariablesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listArrayTextVariablesKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) deleteTxtArray();
    }//GEN-LAST:event_listArrayTextVariablesKeyPressed

    private void btnRefreshLists3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshLists3ActionPerformed
        fillLists();
    }//GEN-LAST:event_btnRefreshLists3ActionPerformed

    private void listTextVariablesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listTextVariablesKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_ENTER ) txtVariableClicked();
        else if (evt.getKeyCode() == KeyEvent.VK_DELETE) deleteTxtVariableClicked();
    }//GEN-LAST:event_listTextVariablesKeyPressed

    private void listTextVariablesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listTextVariablesMouseClicked
        if(evt.getClickCount() == 2) txtVariableClicked();
    }//GEN-LAST:event_listTextVariablesMouseClicked

    private void btnRefreshLists2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshLists2ActionPerformed
        fillLists();
    }//GEN-LAST:event_btnRefreshLists2ActionPerformed

    private void listNumericalArrayVariablesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listNumericalArrayVariablesKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) deleteArray();
    }//GEN-LAST:event_listNumericalArrayVariablesKeyPressed

    private void btnRefreshLists1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshLists1ActionPerformed
        fillLists();
    }//GEN-LAST:event_btnRefreshLists1ActionPerformed

    private void listNumericalVariablesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listNumericalVariablesKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_ENTER ) numVariableClicked();
        else if (evt.getKeyCode() == KeyEvent.VK_DELETE) deleteNumVariableClicked();
    }//GEN-LAST:event_listNumericalVariablesKeyPressed

    private void listNumericalVariablesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listNumericalVariablesMouseClicked
        if(evt.getClickCount() == 2) numVariableClicked();
    }//GEN-LAST:event_listNumericalVariablesMouseClicked

    private void btnRefreshListsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshListsActionPerformed
        fillLists();
    }//GEN-LAST:event_btnRefreshListsActionPerformed

    private void textFieldFilterMacrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldFilterMacrosActionPerformed
        fillListModelMacros(textFieldFilterMacros.getText());
    }//GEN-LAST:event_textFieldFilterMacrosActionPerformed

    private void listAllMacrosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listAllMacrosKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_ENTER ) listAllMacrosEvent();
    }//GEN-LAST:event_listAllMacrosKeyPressed

    private void listAllMacrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listAllMacrosMouseClicked
        if(evt.getClickCount() == 2) listAllMacrosEvent();
        if (SwingUtilities.isRightMouseButton(evt)                                  // if right mouse button clicked
            && !listAllMacros.isSelectionEmpty()              // and list selection is not empty
            && listAllMacros.locationToIndex(evt.getPoint())  // and clicked point is
            == listAllMacros.getSelectedIndex())
        {                                                                           // inside selected item bounds
            popupMacros.show(listAllMacros, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_listAllMacrosMouseClicked

    private void listMacroFoldersKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listMacroFoldersKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_ENTER )           listMacroFolderEvent();
        else if( evt.getKeyCode() == KeyEvent.VK_BACK_SPACE ) listMacroFoldersBack();
    }//GEN-LAST:event_listMacroFoldersKeyPressed

    private void listMacroFoldersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMacroFoldersMouseClicked
        if(evt.getClickCount() == 2) listMacroFolderEvent();
        if (SwingUtilities.isRightMouseButton(evt)                                     // if right mouse button clicked
            && !listMacroFolders.isSelectionEmpty()              // and list selection is not empty
            && listMacroFolders.locationToIndex(evt.getPoint())  // and clicked point is
            == listMacroFolders.getSelectedIndex())
        {                                                                              // inside selected item bounds
            popupMacrosFolder.show(listMacroFolders, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_listMacroFoldersMouseClicked
    
    private void deleteSelection()
    {
        ArrayList<SheetFile> array = null;
        ArrayList<SheetFile> arrayToRemove = new ArrayList<SheetFile>();
        if (actualFolderFilesEditor == null) array = csheet.getRootFiles();
        else                                 array = actualFolderFilesEditor.getFiles();
        int[] indices = listFilesEditor.getSelectedIndices();
        for(int i : indices) 
            if(i>=0 && i<array.size())
                arrayToRemove.add(array.get(i));
        for(SheetFile sf : arrayToRemove) deleteFile(sf,array);
        fillListModelFilesFoldersEditor();
    }
    
    private void deleteFile(SheetFile sf, ArrayList<SheetFile> folder)
    {
        int answer = JOptionPane.showConfirmDialog(this, "Do you want to delete the next file/folder?\n"+sf.toString(), "Delete", JOptionPane.YES_NO_OPTION);
        if(answer == 0)
        {
            boolean c;
            folder.remove(sf);
            if(sf.getType() == SheetFile.FOLDER) c = deleteDirectory(sf.getFile());
            else c = sf.getFile().delete();
            if(!c) JOptionPane.showMessageDialog(this, "ERROR: Problems deleting the next file/directory:\n"+sf.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean deleteDirectory(File folder) 
    {
        if (folder.exists()) 
        {
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) 
            {
                if (files[i].isDirectory()) deleteDirectory(files[i]);
                else                        files[i].delete();
            }
        }
        return (folder.delete());
    }
    
    private void newCtable()
    {
        NewFileFrame  nff = new NewFileFrame(actualFolderFilesEditor, SheetFile.CTABLE, this);
        nff.pack();
        nff.setLocationRelativeTo(null);
        nff.setVisible(true);
    }
    
    private void newCsheet()
    {
        NewFileFrame  nff = new NewFileFrame(actualFolderFilesEditor, SheetFile.CSHEET, this);
        nff.pack();
        nff.setLocationRelativeTo(null);
        nff.setVisible(true);
    }
    
    public void newFile(String txt, int type, SheetFile sfParent)
    {
        String[] namePointer = {""};
        boolean check = true;
        File newFile = null;
        File parent = null;
        SheetFile sfParentFinal = null;
        ArrayList<SheetFile> arrayFiles = null;
        if(sfParent == null) 
        { 
            parent = csheet.getRootFolder(); 
            sfParentFinal = null; 
            arrayFiles = csheet.getRootFiles();
        }
        else  
        { 
            parent = sfParent.getFile(); 
            sfParentFinal = sfParent; 
            arrayFiles = sfParent.getFiles();
        }
        while(check)
        {
            NewFileDialog nfd = new NewFileDialog(this, type, namePointer);
            nfd.setLocationRelativeTo(null);
            nfd.pack();
            nfd.setVisible(true);
            if(namePointer == null)
            {
                JOptionPane.showMessageDialog(this, "Problem with the new file's name", "ERROR", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if(namePointer[0] == null) 
            {
                JOptionPane.showMessageDialog(this, "Problem with the new file's name", "ERROR", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            check = false;
            for(SheetFile sf : arrayFiles)
            {
                if(sf.getFile().getName().equals(namePointer[0]))
                {
                    JOptionPane.showMessageDialog(this, "A file with this name already exists", "ERROR", JOptionPane.ERROR_MESSAGE);
                    check = true;
                    break;
                }
            }
        }
        newFile = new File(parent, namePointer[0]);
        try{ Files.write(newFile.toPath(), txt.getBytes("UTF-8"), StandardOpenOption.CREATE); }
        catch(IOException e)
        {
            JOptionPane.showMessageDialog(this, "ERROR WRITING THE FILE: "+e.getMessage(), "ERROR WRITING THE FILE", JOptionPane.ERROR_MESSAGE);
            return;
        }
        arrayFiles.add(new SheetFile(newFile, sfParentFinal, csheet));
        SheetFile.sortFiles(arrayFiles);
        fillListModelFilesFoldersEditor();
    }
    
    private void newFolder()
    {
        String name = JOptionPane.showInputDialog(this, "Write the folder's name", "New folder", JOptionPane.DEFAULT_OPTION);
        if(name == null)     return;
        if(name.length()<=0) return;
        File newFolder = null;
        File parent = null;
        SheetFile sfParent = null;
        ArrayList<SheetFile> arrayFiles = null;
        if(actualFolderFilesEditor == null) 
        { 
            parent = csheet.getRootFolder(); 
            sfParent = null; 
            arrayFiles = csheet.getRootFiles();
        }
        else  
        { 
            parent = actualFolderFilesEditor.getFile(); 
            sfParent = actualFolderFilesEditor; 
            arrayFiles = actualFolderFilesEditor.getFiles();
        }
        newFolder = new File(parent, name);
        boolean check = newFolder.mkdir();
        if(check) 
        {
            arrayFiles.add(new SheetFile(newFolder, sfParent, csheet));
            SheetFile.sortFiles(arrayFiles);
            fillListModelFilesFoldersEditor();
        }
        else JOptionPane.showMessageDialog(this, "ERROR: The folder has not been created", "ERROR", JOptionPane.ERROR_MESSAGE); 
    }
    
    private void editActualFile()
    {
        if(actualFileInEditor == null) return;
        labelFile.setText("---");
        btnEdit.setEnabled(false);
        txtAreaFileEditor.setText("");
        txtAreaFileEditor.setCaretPosition(0);
        EditFileFrame eff = new EditFileFrame(actualFileInEditor, this);
        actualFileInEditor = null;
        eff.setAttributes();
        eff.setLocationRelativeTo(null);
        eff.setVisible(true);
    }
    
    private void deleteCounter()
    {
        String[] options = {"yes","no"};
        String s = "Do you want to remove the following counters: \n";
        ArrayList<String> arrays = new ArrayList<String>();
        for(String array : listCounters.getSelectedValuesList() ) 
        {
            array = Line.onlyFirstToken(array);
            arrays.add(array); 
            s = s.concat(array+"\n");
        }
        int i = JOptionPane.showOptionDialog(null, s, "Remove Counters", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if(i == 1) return;
        for(String array : arrays )
            getRootMacro().getCountersHashMap().remove(array);
        fillListCounters();        
    }
    
    private void deleteTxtArray()
    {
        String[] options = {"yes","no"};
        String s = "Do you want to remove the following text arrays: \n";
        ArrayList<String> arrays = new ArrayList<String>();
        for(String array : listArrayTextVariables.getSelectedValuesList() ) 
        {
            array = Line.onlyFirstToken(array);
            arrays.add(array); 
            s = s.concat(array+"\n");
        }
        int i = JOptionPane.showOptionDialog(null, s, "Remove Text Arrays", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if(i == 1) return;
        for(String array : arrays )
            getRootMacro().getArraytxtHashMap().remove(array);
        fillListTextArrayVariables();
    }
    
    private void deleteArray()
    {
        String[] options = {"yes","no"};
        String s = "Do you want to remove the following arrays: \n";
        ArrayList<String> arrays = new ArrayList<String>();
        for(String array : listNumericalArrayVariables.getSelectedValuesList() ) 
        {
            array = Line.onlyFirstToken(array);
            arrays.add(array); 
            s = s.concat(array+"\n");
        }
        int i = JOptionPane.showOptionDialog(null, s, "Remove Arrays", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if(i == 1) return;
        for(String array : arrays )
            getRootMacro().getArrayHashMap().remove(array);
        fillListNumericalArrayVariables();        
    }
    
    private void txtVariableClicked()
    {
        ModifyTxtVariableDialog m = new ModifyTxtVariableDialog(this, true, showingTxtVar.get( listTextVariables.getSelectedIndex() ));
        /* Set visible */
        m.pack();
        m.setLocationRelativeTo(this);
        m.setVisible(true);
        fillListTextVariables();       
    }
    
    private void deleteNumVariableClicked() 
    {
        String[] options = {"yes","no"};
        String s = "Do you want to remove the following variables: \n";
        for(int pos : listNumericalVariables.getSelectedIndices() ) s = s.concat(showingNumVar.get(pos)+"\n");
        int i = JOptionPane.showOptionDialog(null, s, "Remove Variables", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if(i == 1) return;
        for(int pos : listNumericalVariables.getSelectedIndices() )
            getRootMacro().getVariablesHashMap().remove(showingNumVar.get(pos));
        fillListNumericalVariables();
    }
    
    private void deleteTxtVariableClicked() 
    {
        String[] options = {"yes","no"};
        String s = "Do you want to remove the following text variables: \n";
        for(int pos : listTextVariables.getSelectedIndices() ) s = s.concat(showingTxtVar.get(pos)+"\n");
        int i = JOptionPane.showOptionDialog(null, s, "Remove Text Variables", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if(i == 1) return;
        for(int pos : listTextVariables.getSelectedIndices() ) 
            getRootMacro().getTxtvariablesHashMap().remove(showingTxtVar.get(pos)); 
        fillListTextVariables();
    }
    
    private void numVariableClicked()
    {
        ModifyNumVariableDialog m = new ModifyNumVariableDialog(this, true, showingNumVar.get( listNumericalVariables.getSelectedIndex() ));
        /* Set visible */
        m.pack();
        m.setLocationRelativeTo(this);
        m.setVisible(true);
        fillListNumericalVariables();
    }
    
    private void attributeClicked()
    {
        Attribute a  = showingAttributes.get(listAttributes.getSelectedIndex());
        int decimals = ((Integer)spinnerDecimalsAttributes.getValue()).intValue();
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(decimals);
        nf.setMinimumFractionDigits(decimals);
        a.solve(csheet, rootMacro, new AttributeTable(a.getNameToShow()));
        csheet.print();
    }
    
    private void fillLists()
    {
        fillListNumericalVariables();
        fillListNumericalArrayVariables();
        fillListTextVariables();
        fillListTextArrayVariables();
        fillListCounters();
    }
    
    
    public void save()
    {
        cclient.printsystem(getName() + " - saving your session ...");
        (new SerialData(rootMacro, txtAreaNotes.getText())).save(csheet);
        cclient.printsystem("DONE");
    }
    
    public void exit()
    {
        int confirm = JOptionPane.showOptionDialog(this,
        "If you want to save the session, you should press the button 'Save' first. "
                + "\nAre you sure to close this code sheet?",
        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (confirm == JOptionPane.YES_OPTION) 
        {
            this.dispose();
        }
    }
    
    private void listAllMacrosEvent()
    {
        Macro m = macros.get(listAllMacros.getSelectedIndex());
        if(m != null) csheet.executeMacro(m, rootMacro, null);
    }
    
    private void listAllMacrosEventDependentVariables()
    {
        Macro m = macros.get(listAllMacros.getSelectedIndex());
        if(m != null) csheet.executeMacroInMother(m, rootMacro, null);
    }
    
    private void listMacroFolderEvent()
    {
        if(actualFolderMacros == null)
        {
            actualFolderMacros = csheet.getMacroFolders().get(listMacroFolders.getSelectedValue());
            fillListModelMacroFolders();
        }
        else
        {
            if("... BACK ...".equals(listMacroFolders.getSelectedValue())) listMacroFoldersBack();
            else
            {
                Macro m = actualFolderMacros.get(listMacroFolders.getSelectedIndex() - 1);
                if(m == null) listMacroFoldersBack();
                else
                {
                    csheet.executeMacro(m, rootMacro, null);
                }
            }
        }
    }
    
    private void listMacroFolderEventWithDependentVariables()
    {
        if(actualFolderMacros != null)
        {
            if(!("... BACK ...".equals(listMacroFolders.getSelectedValue())))
            {
                Macro m = actualFolderMacros.get(listMacroFolders.getSelectedIndex() - 1);
                if(m != null) listMacroFoldersBack();
                {
                    csheet.executeMacroInMother(m, rootMacro, null);
                }
            }
        }
    }
    
    private void listMacroFoldersBack() {actualFolderMacros = null; fillListModelMacroFolders();}
    
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
    
    private void listFilesEventEnter()
    {
            int i = listFiles.getSelectedIndex();
            ArrayList<SheetFile> array = null;
            if(actualFolderFiles == null) array = csheet.getRootFiles();
            else                          array = actualFolderFiles.getFiles();
            if(i>=0 && i<array.size())
            {
                SheetFile sf = array.get(i);
                if(sf.getType() == SheetFile.FOLDER) actualFolderFiles = sf;
                else if(sf.getType() == SheetFile.CSHEET)
                {
                    if(sf.isEnable()) sf.setDisable();
                    else              sf.setEnable();
                }
            }
            fillListModelFilesFolders();
            fillModels(arrayFilesShowing);
    }
    
    private void listFilesEventDelete()
    {
            if (actualFolderFiles != null) 
            {
                actualFolderFiles = actualFolderFiles.getParent();
                fillListModelFilesFolders();
            }
    }
    
    private void listFilesEditorEventEnter()
    {
            int i = listFilesEditor.getSelectedIndex();
            ArrayList<SheetFile> array = null;
            if(actualFolderFilesEditor == null) array = csheet.getRootFiles();
            else                          array = actualFolderFilesEditor.getFiles();
            if(i>=0 && i<array.size())
            {
                SheetFile sf = array.get(i);
                if(sf.getType() == SheetFile.FOLDER) actualFolderFilesEditor = sf;
                else if(sf.getType() == SheetFile.CSHEET || sf.getType() == SheetFile.CTABLE)
                {
                    actualFileInEditor = sf;
                    labelFile.setText(sf.getName());
                    btnEdit.setEnabled(true);
                    txtAreaFileEditor.setText(sf.getTextWithNumberLines());
                    txtAreaFileEditor.setCaretPosition(0);
                }
            }
            fillListModelFilesFoldersEditor();
    }
        
    private void listFilesEditorEventDelete()
    {
            if (actualFolderFilesEditor != null) 
            {
                actualFolderFilesEditor = actualFolderFilesEditor.getParent();
                fillListModelFilesFoldersEditor();
            }
    }
    
    private void listFilesEnabledEvent()
    {
            int i = listFilesEnabled.getSelectedIndex();
            if(i>=0 && i<arrayFilesEnabled.size())
            {
                SheetFile sf = arrayFilesEnabled.get(i);
                if(sf.getType() == SheetFile.CSHEET && sf.isEnable()) sf.setDisable();
            }
            fillModels(arrayFilesShowing);
    }
    
    private void listFilesDisabledEvent()
    {
            int i = listFilesDisabled.getSelectedIndex();
            if(i>=0 && i<arrayFilesDisabled.size())
            {
                SheetFile sf = arrayFilesDisabled.get(i);
                if(sf.getType() == SheetFile.CSHEET && !sf.isEnable()) sf.setEnable();
            }
            fillModels(arrayFilesShowing);
    }
    
    public void updateCodeSheet()
    {
        cclient.printsystem(getName() + " - updating sheet...");
        csheet = new CodeSheet(csheet.getRootFolder(),cclient,this);
        this.setTitle(csheet.getRootFolder().getName());
        initialize();
    }
    
    public String getName()
    {
        return name;
    }
    
    public String toString()
    {
        return name;
    }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnEditorPreviousFolder;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnNewCsheet;
    private javax.swing.JButton btnNewCtable;
    private javax.swing.JButton btnNewFolder;
    private javax.swing.JButton btnPreviousFolder;
    private javax.swing.JButton btnRefreshLists;
    private javax.swing.JButton btnRefreshLists1;
    private javax.swing.JButton btnRefreshLists2;
    private javax.swing.JButton btnRefreshLists3;
    private javax.swing.JButton btnRefreshLists4;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpdateByFolder;
    private javax.swing.JButton btnUpdateSheet;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JTextField input;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JLabel labelFile;
    private javax.swing.JList<String> listAllMacros;
    private javax.swing.JList<String> listArrayTextVariables;
    private javax.swing.JList<String> listAttributes;
    private javax.swing.JList<String> listCounters;
    private javax.swing.JList<String> listFiles;
    private javax.swing.JList<String> listFilesDisabled;
    private javax.swing.JList<String> listFilesEditor;
    private javax.swing.JList<String> listFilesEnabled;
    private javax.swing.JList<String> listMacroFolders;
    private javax.swing.JList<String> listNumericalArrayVariables;
    private javax.swing.JList<String> listNumericalVariables;
    private javax.swing.JList<String> listTextVariables;
    private javax.swing.JSpinner spinnerDecimalsAttributes;
    private javax.swing.JTextField textFieldAttributeSearch;
    private javax.swing.JTextField textFieldFilterMacros;
    private javax.swing.JTextArea txtAreaFileEditor;
    private javax.swing.JTextArea txtAreaNotes;
    private javax.swing.JTextField txtFieldSearch;
    // End of variables declaration//GEN-END:variables
}
