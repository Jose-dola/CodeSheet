/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;


import macro.MacroCode;
import macro.Macro;
import chat.ChatClient;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import macro.ExecutionMacro;

/**
 *
 * @author akhrain
 */
public class CodeSheet 
{
    private File rootFolder = null;
    private ChatClient chatClient = null;
    private SheetFrame sheetFrame =  null;
    private ArrayList<SheetFile> sheetFiles = null;
    private boolean lockLineIteration = false;
    private boolean lockActualLineIteration = false;
    private File   currentReadingFile       = null;
    private int    currentReadingNumberLine = -1;
    private int    currentActualNumberLine  = -1;
    private String currentReadingLine       = null;
    private ArrayList<SheetFile> rootFiles;
    private HashMap<String, Attribute> attributes;
    private HashMap<String, Macro> macros;
    private HashMap<String, Table> tables;
//    private ArrayList<TableOperator> tableOperators;
    private HashMap<String,ArrayList<Macro>> macroFolders = null;
//    private MacroCodesComparator macroComparator = null;
    private String toprint = null;
    /* AttributeTable Attributes */
//    private String            attributeTableName    = null;
//    private ArrayList<Pair<String,Double>> attributeTableArray = null;
    /*******************************/
    
    public CodeSheet(File folder, ChatClient chatClient, SheetFrame sheetFrame)
    {
        this.chatClient = chatClient;
        this.sheetFrame = sheetFrame;
        rootFolder      = folder;
        toprint         = "";
        macros          = new HashMap<String, Macro>();
        macroFolders    = new HashMap<String,ArrayList<Macro>>();
        //macroComparator = new MacroCodesComparator();
        sheetFiles      = new ArrayList<SheetFile>();
        attributes      = new HashMap<String, Attribute>();
        tables          = new HashMap<String, Table>();
//        tableOperators  = new ArrayList<TableOperator>();
        rootFiles =  new ArrayList<SheetFile>();
        File[] fs = folder.listFiles();
        for(File f : fs)
        {
            setReadingFile(f);
            SheetFile sf = new SheetFile(f,null,this);
            if(sf.getType() == SheetFile.CSHEET || sf.getType() == SheetFile.CTABLE || sf.getType() == SheetFile.FOLDER) 
                rootFiles.add(sf);
        }
        SheetFile.sortFiles(rootFiles);
        sortMacroFolders();
        setReadingFile(null);
        setReadingLine("---");
        setReadingNumberLine(0);
        setActualNumberLine(0);
        /* set table operators */
//        for(TableOperator to : tableOperators) to.setTable(this);
//        tableOperators = null;
        /* sort macro codes */
//        for(Macro m : macros.values()) {m.getMacroCodes().sort(macroComparator);}
//        for(Macro m : macros.values()) 
//        {
//            m.getMacroCodes().sort(new Comparator<MacroCode>()
//            {    
//                public int compare(MacroCode o1, MacroCode o2) 
//                {
//                    return o2.getOrder() - o1.getOrder();
//                }
//            });
//        }
        

/*        
for(Attribute a : attributes.values())
{
    System.out.println(a.getName());
    for(Line l : a.getLines())
    {
        System.out.print("\t");
        for(Line.LineParameter lp : l.getLineParameters())
        {
            if(lp.getParameter() instanceof Attribute)
            {
                System.out.print(((Attribute)lp.getParameter()).getName()+" ");
            }
            else if(lp.getParameter() instanceof Number)
            {
                System.out.print(((Number)lp.getParameter()).solve(this)+" ");
            }
        }
        System.out.print("\n");
    }
}
*/
/*
while(true)
{
    System.out.print("Dame un atributo: ");
    BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
    String name;
    try
    {    
        name = bf.readLine();
        System.out.println(this.getAttribute(name).solve(this));
    }
    catch(IOException ex){return;}
}
*/

    }
    
    public int rollDice(int sides)
    {
        return DiceRoll.rollDice(sides, chatClient);
    }
    
//    public void    setCurrentMacro(ExecutionMacro em) {currentMacro = em;}
//    public ExecutionMacro getCurrentMacro()           {return currentMacro;}
    public Collection<Attribute> getAttributes() {return attributes.values();}
    public void lockLineIteration()   {lockLineIteration = true;}
    public void unlockLineIteration() {lockLineIteration = false;}
    public void lockActualLineIteration()   {lockActualLineIteration = true;}
    public void unlockActualLineIteration() {lockActualLineIteration = false;}    
    public void setReadingFile(File f)      
    {
        if(lockLineIteration) return;
        if(f==null) currentReadingFile = rootFolder;
        else        currentReadingFile = f;
    }
    public File    getRootFolder()             {return rootFolder;}
    public void    setReadingNumberLine(int i) 
    {
        if(lockLineIteration) return;
        currentReadingNumberLine = i;
    }
    public void    setActualNumberLine(int i) 
    {
        if(lockActualLineIteration) return;
        currentActualNumberLine = i;
    }    
    public void    setReadingLine(String s)    
    {
        if(lockLineIteration) return;        
        currentReadingLine = s;
    }
    public File    getReadingFile()            {return currentReadingFile;}
    public int     getReadingNumberLine()      {return currentReadingNumberLine;}
    public int     getActualNumberLine()       {return currentActualNumberLine;}
    public String  getReadingLine()            {return currentReadingLine;}
    
    public SheetFrame getSheetFrame() {return sheetFrame;}
  
    public void removeAttribute(String key) {attributes.remove(key);}
    
    public ArrayList<SheetFile> getRootFiles() {return rootFiles;}
    
    public ChatClient getChatClient() {return chatClient;}
    
    public void toPrint(String s)
    {
        if(s != null) toprint = toprint.concat(s);
    }
    
    public void printError(String error) 
    {
        chatClient.printsheeterror(sheetFrame.getName()+" - SHEET ERROR: "
                +error+ChatClient.NEW_LINE_STRING);
    }
    
    public void printText(String s)
    {
        chatClient.print(s);
    }
    
    public void print()
    {
        chatClient.print(toprint);
        toprint="";
    }
    
    public void printRollTable(String s)
    {
        chatClient.printRoll(s);
    }
    
    public void printRoll(int n, int s, int result, String label)
    {
        if(label != null) chatClient.printRoll(label+":"+n+"<font color=\"#33ff00\">d</font>"+s+"<font color=\"#33ff00\"> -> </font>"+result);
        else chatClient.printRoll(n+"<font color=\"#33ff00\">d</font>"+s+"<font color=\"#33ff00\"> -> </font>"+result);
    }
    
    public ArrayList<SheetFile> getSheetFiles() {return sheetFiles;}
    
    public SheetFile addSheetFile(SheetFile f)
    {
        sheetFiles.add(f);
        return f;
    }
    
    public SheetFile getSheetFile(String s)
    {
        for(SheetFile sf : sheetFiles)
        {
            if(sf.checkName(s)) return sf;
        }
        return null;
    }
    
    public ArrayList<SheetFile> searchCSheetByPattern(String s)
    {
        ArrayList<SheetFile> files = new ArrayList<SheetFile>();
        for(SheetFile sf : sheetFiles)
        {
            if(sf.checkName(s)) files.add(sf);
        }
        return files;
    }
    
    public Attribute addAttribute(String a)
    {
        Attribute attribute = attributes.get(a);
        if(attribute == null)
        {
            attribute = new Attribute(a);
            attributes.put(a, attribute);
            return attribute;
        }
        else{ return attribute; }
    }
    public Attribute getAttribute(String s)
    {
        if(s ==  null) return null;
        else return attributes.get(s);
    }
     
    public Table addTable(String name, Table t)
    {
        if(tables.containsKey(name)) return null;
        else
        {
            tables.put(name,t);
            return t;
        }
    }
    public Table findTable(String name) { return tables.get(name);}
    
    public Macro addMacroCode(String name, Iterator<String> linesIterator,SheetFile sf,CodeSheet cs)
    {
        Macro macro = macros.get(name);
        if(macro == null)
        {
            macro = new Macro(name);
            macro.addMacroCode(new MacroCode(linesIterator, macro, sf, cs));
            macros.put(name, macro);
            return macro;
        }
        else
        { 
            macro.addMacroCode(new MacroCode(linesIterator,macro,sf,cs)); 
            return macro; 
        }
    }
    public Macro getMacro(String s) {return macros.get(s);}
    
    public HashMap<String, ArrayList<Macro>> getMacroFolders() {return macroFolders;}
    
//    public void addTableOperatorToCheck(TableOperator to) {tableOperators.add(to);}
    
    public ExecutionMacro executeMacroByName(String name, ExecutionMacro mother, MacroCode input)
    {
        Macro macro = getMacro(name);
        if(macro == null) 
        {
            printError("\nWe have not found a macro named: "+name+"\n");
            return null;
        }
        else
        {
            ExecutionMacro em = new ExecutionMacro(mother);
            macro.execute(this, em, input);
            return em;
        }
    }
    
    public void executeMacroByNameInMother(String name, ExecutionMacro mother, MacroCode input)
    {
        Macro macro = getMacro(name);
        if(macro == null) 
        {
            printError("\nWe have not found a macro named: "+name+"\n");
        }
        else
        {
            macro.execute(this, mother, input);
        }
    }
    
    public ExecutionMacro executeMacro(Macro macro, ExecutionMacro mother, MacroCode input)
    {
        if(macro != null)
        {
            ExecutionMacro em = new ExecutionMacro(mother);
            macro.execute(this, em, input);
            return em;
        }
        return null;
    }
    
    public void executeMacroInMother(Macro macro, ExecutionMacro mother, MacroCode input)
    {
        if(macro != null) macro.execute(this, mother, input);
    }
    
    public void enable(String s)
    {
        for(SheetFile sf : sheetFiles)
        {
            if(sf.checkName(s)) sf.setEnable();
        }
    }
    
    public void disable(String s)
    {
        for(SheetFile sf : sheetFiles)
        {
            if(sf.checkName(s)) sf.setDisable();
        }        
    }
    
    public boolean isEnable(String s)
    {
        for(SheetFile sf : sheetFiles)
        {
            if(sf.checkName(s) && sf.isEnable()) return true;
        }
        return false;
    }
    
//    public void AttributeTableNew(String name) 
//    {
//        attributeTableName    = name;
//        attributeTableArray   = new ArrayList<Pair<String,Double>>();
//    }
//    
//    public void AttributeTableAdd(String label, Double d)
//    {
//        if(attributeTableArray != null) attributeTableArray.add(new Pair<String,Double>(label,d));
//    }
//    
//    public void AttributeTablePrint(double total)
//    {
//        if(attributeTableName != null && attributeTableArray != null)
//        {
//            this.toPrint("<table> <tr> <th colspan=\"2\">"+attributeTableName+"</th> </tr>");
//            for(Pair<String,Double> pair : attributeTableArray)
//            {
//                this.toPrint("<tr>"+"<td>"+pair.getKey()+"</td>"+"<td>"+pair.getValue()+"</td>"+"</tr>");
//            }
//            this.toPrint("<tr>"+"<td>"+"TOTAL"+"</td>"+"<td>"+total+"</td>"+"</tr>");
//            this.toPrint("</table>");
//        }
//        attributeTableName  = null;
//        attributeTableArray = null;
//    }
        
    public void sortMacroFolders()
    {
        for(ArrayList<Macro> array : macroFolders.values())
        {
            array.sort(new Comparator<Macro>()
            {    
                public int compare(Macro m1, Macro m2) 
                {
                    return m1.toString().compareTo(m2.toString());
                }
            });
        }
    }
    
    public ArrayList<Macro> getAllMacros()
    {
        ArrayList<Macro> macrosArray = new ArrayList<Macro>();
        for(Macro m : macros.values()) macrosArray.add(m);
        macrosArray.sort(new Comparator<Macro>()
            {    
                public int compare(Macro m1, Macro m2) 
                {
                    return m1.toString().compareTo(m2.toString());
                }
            });
        return macrosArray;
    }
}
