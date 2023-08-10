/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tool | Templates
 * and open the template in the editor.
 */
package macro;

import chat.ChatClient;
import java.util.ArrayList;
import java.util.Iterator;
import sheet.CodeSheet;
import sheet.Line;
import sheet.SheetFile;

/**
 *
 * @author akhrain
 */
public class MacroCode 
{
    /* Attributes */
    private Macro macro  = null;
    private SheetFile sf = null; /* if sf is 'null' then this MacroCode is the input of a Macro */
    private ArrayList<Instruction> instructions = null;
    private Line order   = null;
    
    public MacroCode(String s, CodeSheet cs)
    {
        cs.lockLineIteration();
        cs.lockActualLineIteration();        
//        cs.setReadingFile(null);
        String[] ls = s.split( ";" );
        ArrayList<String> lines = new ArrayList<String>();
        for(String line : ls) lines.add(line);
        Iterator<String> linesIterator = lines.iterator();
        sf = null;
        instructions = new ArrayList<Instruction>();
        int i;
        int len;
        String line = null;
        while(linesIterator.hasNext())
        {
            line = linesIterator.next();
            len = line.length();
            i   = Line.jumpBlanks(line, 0, len);
            if      (i == len)                   { /*empty line*/     }
            else if (line.charAt(i) == '#')      {  /*commented line*/ }
            else addInstructionByString(line.substring(i),linesIterator,cs);
        }
        cs.unlockLineIteration();
        cs.unlockActualLineIteration();        
    }
    
    public MacroCode(Iterator<String> fileLinesIterator,CodeSheet cs)
    {
        sf    = null;
        instructions = new ArrayList<Instruction>();
        boolean check = false;
        int i;
        int len;
        // read first line
        String line = SheetFile.getLine(fileLinesIterator, cs);
        while(line != null)
        {
            len = line.length();
            i   = Line.jumpBlanks(line, 0, len);
            if      (i == len)                   { /*empty line*/     }
            else if (line.charAt(i) == '#')      {  /*commented line*/ }
            else if ( Line.onlyFirstToken(line).equals("\\end")) {check=true; break;}
            else addInstructionByString(line.substring(i),fileLinesIterator,cs);
            // read new line
            line = SheetFile.getLine(fileLinesIterator, cs);
        }
        if(!check)
            cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad Macro input in Line "+
                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                        "\nWe have not found the '\\end'\n");
    }
    
    public MacroCode(Iterator<String> fileLinesIterator, Macro m, SheetFile sheetfile,CodeSheet cs)
    {
        macro = m;
        sf    = sheetfile;
        instructions = new ArrayList<Instruction>();
        boolean check = false;
        int i;
        int len;
        // get first line
        String line = SheetFile.getLine(fileLinesIterator, cs);
        while(line != null)
        {
            len = line.length();
            i   = Line.jumpBlanks(line, 0, len);
            if      (i == len)                   { /*empty line*/     }
            else if (line.charAt(i) == '#')      { /*commented line*/ }
            else if ( sf!=null && Line.onlyFirstToken(line).equals("\\ENDMACRO") ) {check=true; break;}
            else addInstructionByString(line.substring(i),fileLinesIterator,cs);
            // get new line
            line = SheetFile.getLine(fileLinesIterator, cs);
        }
        if(!check)
            cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad Macro Declaration in Line "+
                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                        "\nWe have not found the \\ENDMACRO\n");
    }
    
    private void addInstructionByString(String s, Iterator<String> fileLinesIterator, CodeSheet cs)
    {
        int i;
        int len = s.length();
        
        if(len>0)
        {
            if(s.charAt(0) == '@')
            {
                /* position is null if it is a text-variable 
                or is a Line representing the position if it is a position of a text-array*/
                Line position = null; 
                int j = 0;
                if(s.charAt(1) == '(')
                {
                    j = s.indexOf(')');
                    if(j>0) 
                    {
                        position = new Line(null, s.substring(2, j), cs);
                        i        = s.indexOf('=');
                    }
                    else
                    {
                        cs.printError("\nFILE: "+
                                        cs.getReadingFile().getAbsolutePath()+
                                        "\nERROR: bad text-array position Declaration in Line "+
                                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                        "\nProblems to found the character ')' in the position's definition\n");
                        return;
                    }    
                }
                else i = s.indexOf('=');
                if(i>0 && i<len-1)
                {
                    addInstruction(new SetTextVariableInstruction(s.substring(j+1,i), position, new CodeString(s.substring(i+1),cs), cs));
                    return;
                }
                else
                    cs.printError("\nFILE: "+
                                        cs.getReadingFile().getAbsolutePath()+
                                        "\nERROR: Problems with the setting of a text-array position in Line "+
                                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                        "\nWe have not found the character '=' in the declaration\n");                
            }
            if(s.charAt(0) == '!')
            {
                /* position is null if it is a variable 
                or is a Line representing the position if it is a position of an array*/
                Line position = null; 
                int j = 0;
                if(s.charAt(1) == '(')
                {
                    j = s.indexOf(')');
                    if(j>0) 
                    {
                        position = new Line(null, s.substring(2, j), cs);
                        i        = s.indexOf('=');
                    }
                    else
                    {
                        cs.printError("\nFILE: "+
                                        cs.getReadingFile().getAbsolutePath()+
                                        "\nERROR: bad array position Declaration in Line "+
                                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                        "\nProblems to found the character ')' in the position's definition\n");
                        return;
                    }    
                }
                else i = s.indexOf('=');
                if(i>0 && i<len-1)
                {
                    addInstruction(new SetVariableInstruction(s.substring(j+1,i), position, new Line(null,s.substring(i+1),cs),cs));
                    return;
                }
                else
                    cs.printError("\nFILE: "+
                                        cs.getReadingFile().getAbsolutePath()+
                                        "\nERROR: Problems with the setting of an array position in Line "+
                                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                        "\nWe have not found the character '=' in the declaration\n");
            }
            if(s.charAt(0) == '*')
            {
                i = s.indexOf('=');
                if(i>0 && i<len-1)
                {
                    addInstruction(new CounterActionInstruction(s.substring(1,i) , new Line(null,s.substring(i+1),cs) , cs));
                    return;
                }                
                i = s.indexOf(':');
                if(i>0 && i<len-1)
                {
                    addInstruction(new AddVariableToCounterInstruction(s.substring(1,i) , s.substring(i+1) , cs));
                    return;
                }
                cs.printError("\nFILE: "+
                                        cs.getReadingFile().getAbsolutePath()+
                                        "\nERROR: Bad definition of Counter instruction in Line "+
                                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                        "\n");
            }
            if(s.charAt(0) == '?')
            {
                    i = s.indexOf("\\to ", 1);
                    if(i>0 && i+4<len)
                    {
                        addInstruction(new AttributeTableToPrintInstruction(s.substring(1,i),s.substring(i+4),cs));
                        return;
                    }
                    else
                    {
                        addInstruction(new AttributeTableToPrintInstruction(s.substring(1),null,cs));
                        return;
                    }
            }
            if(10<len)
            {
                if(s.substring(0, 10).equals("set order:"))
                {
                    order = new Line(null,s.substring(10),cs);
                    return;
                }
            }
            if(11<len)
            {
                if(s.substring(0, 11).equals("set folder:"))
                {
                    setFolder(Line.onlyFirstToken(s.substring(11)) , cs);
                    return;
                }
            }
            if(7<len)
            {
                if(s.substring(0, 7).equals("enable "))
                {
                    addInstruction(new EnableInstruction(s.substring(7),cs));
                    return;
                }
            }
            if(8<len)
            {
                if(s.substring(0, 8).equals("disable "))
                {
                    addInstruction(new DisableInstruction(s.substring(8),cs));
                    return;
                }
            }            
            if(11<=len)
            {
                if(s.substring(0,10).equals("new array "))
                {
                        String arrayname = s.substring(10);
                        if(arrayname.length()>0)
                        {
                            addInstruction(new NewArrayInstruction(arrayname,cs));
                            return;
                        }
                }
            } 
            if(11<=len)
            {
                if(s.substring(0,10).equals("array-add "))
                {
                        i = s.indexOf("\\to ", 10);
                        if(i>0 && i+4<len)
                        {
                            addInstruction(new ToArrayInstruction(s.substring(i+4),s.substring(10,i),cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad array-add Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\to' or the array name is not defined\n");
                }
            } 
            if(11<=len)
            {
                if(s.substring(0,10).equals("array-del "))
                {
                        i = s.indexOf("\\of ", 10);
                        if(i>0 && i+4<len)
                        {
                            addInstruction(new DelPositionOfArrayInstruction(s.substring(i+4),s.substring(10,i),cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad array-del Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\of' or the array name is not defined\n");
                }
            }  
            if(13<=len)
            {
                if(s.substring(0,12).equals("counter-del "))
                {
                        i = s.indexOf("\\of ", 12);
                        if(i>0 && i+4<len)
                        {
                            addInstruction(new DelVariableOfCounter(s.substring(i+4),s.substring(12,i),cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad counter-del Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\of' or the counter name is not defined\n");
                }
            } 
            if(16<=len)
            {
                if(s.substring(0,15).equals("new text-array "))
                {
                        String arrayname = s.substring(15);
                        if(arrayname.length()>0)
                        {
                            addInstruction(new NewTextArrayInstruction(arrayname,cs));
                            return;
                        }
                }
            }
            if(16<=len)
            {
                if(s.substring(0,15).equals("text-array-add "))
                {
                        i = s.indexOf("\\to ", 15);
                        if(i>0 && i+4<len)
                        {
                            addInstruction(new ToTextArrayInstruction(s.substring(i+4),new CodeString(s.substring(15,i), cs), cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad text-array-add Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\to' or the text-array name is not defined\n");
                }
            }
            if(16<=len)
            {
                if(s.substring(0,15).equals("text-array-del "))
                {
                        i = s.indexOf("\\of ", 15);
                        if(i>0 && i+4<len)
                        {
                            addInstruction(new DelPositionOfTextArrayInstruction(s.substring(i+4),s.substring(15,i),cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad text-array-del Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\of' or the array name is not defined\n");
                }
            }  
            if(9<=len)
            {
                if(s.substring(0,8).equals("execute "))
                {
                        i = s.indexOf("\\with dependent variables", 8);
                        if(i>0)
                        {
                            addInstruction(new ExecuteMacroInMotherInstruction( s.substring(8) , null ,cs));
                            return;
                        }
                        i = s.indexOf("\\with", 8);
                        if(i>0)
                        {
                            addInstruction(new ExecuteMacroInstruction( s.substring(8) , new MacroCode(fileLinesIterator,cs) ,cs));
                            return;
                        }
                        else
                        {
                            addInstruction(new ExecuteMacroInstruction( s.substring(8) , null ,cs));
                            return;
                        }
                }
            }
            if(8<=len)
            {
                if(s.substring(0,7).equals("export "))
                {
                        addInstruction(new ExportInstruction( s.substring(7) ,cs));
                        return;
                }
            }  
            if(8<=len)
            {
                if(s.substring(0,7).equals("import "))
                {
                        addInstruction(new ImportInstruction( s.substring(7) ,cs));
                        return;
                }
            } 
            if(6<=len)
            {
                if(s.substring(0,5).equals("copy "))
                {
                        i = s.indexOf("\\in ", 5);
                        if(i>0 && i+4<len)
                        {
                            addInstruction(new CopyInstruction(s.substring(i+4),s.substring(5,i),cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad 'copy' Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\in' or the array name is not defined\n");
                }
            }
            if(5<=len)
            {
                if(s.substring(0,4).equals("max "))
                {
                        i = s.indexOf("\\to ", 4);
                        if(i>0 && i+4<len)
                        {
                            addInstruction(new MaxInstruction(s.substring(i+4),s.substring(4,i),cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad 'max' Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\to' or the array name is not defined\n");
                }
            }
            if(9<=len)
            {
                if(s.substring(0,8).equals("max-pos "))
                {
                        i = s.indexOf("\\to ", 8);
                        if(i>0 && i+4<len)
                        {
                            addInstruction(new MaxPosInstruction(s.substring(i+4),s.substring(8,i),cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad 'max-pos' Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\to' or the array name is not defined\n");
                }
            }
            if(9<=len)
            {
                if(s.substring(0,8).equals("min-pos "))
                {
                        i = s.indexOf("\\to ", 8);
                        if(i>0 && i+4<len)
                        {
                            addInstruction(new MinPosInstruction(s.substring(i+4),s.substring(8,i),cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad 'min-pos' Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\to' or the array name is not defined\n");
                }
            } 
            if(5<=len)
            {
                if(s.substring(0,4).equals("min "))
                {
                        i = s.indexOf("\\to ", 4);
                        if(i>0 && i+4<len)
                        {
                            addInstruction(new MinInstruction(s.substring(i+4),s.substring(4,i),cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad 'min' Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\to' or the array name is not defined\n");
                }
            } 
            if(5<=len)
            {
                if(s.substring(0,4).equals("sum "))
                {
                        i = s.indexOf("\\to ", 4);
                        if(i>0 && i+4<len)
                        {
                            addInstruction(new SumInstruction(s.substring(i+4),s.substring(4,i),cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad 'sum' Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\to' or the array name is not defined\n");
                }
            }
            if(11<=len)
            {
                if(s.substring(0,10).equals("length of "))
                {
                        i = s.indexOf("\\to ", 10);
                        if(i>0 && i+4<len)
                        {
                            addInstruction(new LengthInstruction(s.substring(i+4),s.substring(10,i),cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad 'lenght' Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\to' or the array name is not defined\n");
                }
            }
            if(6<=len)
            {
                if(s.substring(0,5).equals("sort "))
                {
                        i = s.indexOf("\\to ", 5);
                        if(i>0 && i+4<len)
                        {
                            addInstruction(new SortInstruction(s.substring(i+4),s.substring(5,i),cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad 'sort' Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\to' or the array name is not defined\n");
                }
            }
            if(21<=len)
            {
                if(s.substring(0,21).equals("set default value of "))
                {
                    i = s.indexOf(':', 21);
                    if(i>0)
                    {
                        String variable = s.substring(21, i);
                        if(variable.length()>0 && i+1<len)
                        {
                            addInstruction(new SetDefaultValueInstruction(variable, new Line(null,s.substring(i+1),cs),cs));
                            return;
                        }

                    }
                }
            }
            if(20<=len)
            {
                if(s.substring(0,20).equals("set default text of "))
                {
                    i = s.indexOf(':', 20);
                    if(i>0)
                    {
                        String variable = s.substring(20, i);
                        if(variable.length()>0 && i+1<len)
                        {
                            addInstruction(new SetDefaultTextInstruction(variable, s.substring(i+1), cs));
                            return;
                        }

                    }
                }
            }
            if(5<=len)
            {
                if(s.substring(0,5).equals("print"))
                {
                    addInstruction( new PrintInstruction(cs.getReadingNumberLine(),cs.getReadingFile()) );
                    return;
                }
            }
            if(10<=len)
            {
                if(s.substring(0,9).equals("to print:"))
                {
                    addInstruction( new ToprintInstruction(s.substring(9), cs) );
                    return;
                }
            }
            if(14<=len)
            {
                if(s.substring(0,14).equals("to print lines"))
                {
                    String line = null;
                    int    lineNumber = 0;
                    boolean check = false;
                    String linesToPrint = "";
                    while(fileLinesIterator.hasNext())
                    {
                        line = fileLinesIterator.next();
                        cs.setActualNumberLine(cs.getActualNumberLine()+1);
                        lineNumber++;
                        len = line.length();
                        i = Line.jumpBlanks(line, 0, len);
                        if( 4 <= len - i )
                        {
                            if( line.substring(i,i+4).equals("\\end") ) {check=true; break;}
                        }
                        linesToPrint = linesToPrint.concat(line);
                    }
                    if(check)
                        addInstruction( new ToprintInstruction(linesToPrint, cs) );
                    else
                        cs.printError("\nFILE: "+
                                        cs.getReadingFile().getAbsolutePath()+
                                        "\nERROR: bad 'to print lines' Instruction Declaration in Line "+
                                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                        "\nWe have not found the -> '\\end'\n");

                    cs.setReadingNumberLine(cs.getReadingNumberLine()+lineNumber);
                    if(line != null) cs.setReadingLine(line);

                    return;
                }
            }   
            if(25<=len)
            {
                if(s.substring(0,25).equals("to print with line breaks"))
                {
                    String line = null;
                    int    lineNumber = 0;
                    boolean check = false;
                    String linesToPrint = "";
                    while(fileLinesIterator.hasNext())
                    {
                        line = fileLinesIterator.next();
                        cs.setActualNumberLine(cs.getActualNumberLine()+1);
                        lineNumber++;
                        len = line.length();
                        i = Line.jumpBlanks(line, 0, len);
                        if( 4 <= len - i )
                        {
                            if( line.substring(i,i+4).equals("\\end") ) {check=true; break;}
                        }
                        linesToPrint = linesToPrint.concat(line+ChatClient.NEW_LINE_STRING);
                    }
                    if(check)
                        addInstruction( new ToprintInstruction(linesToPrint, cs) );
                    else
                        cs.printError("\nFILE: "+
                                        cs.getReadingFile().getAbsolutePath()+
                                        "\nERROR: bad 'to print with line breaks' Instruction Declaration in Line "+
                                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                        "\nWe have not found the -> '\\end'\n");

                    cs.setReadingNumberLine(cs.getReadingNumberLine()+lineNumber);
                    if(line != null) cs.setReadingLine(line);

                    return;
                }
            }  
            if(30<=len)
            {
                if(s.substring(0,29).equals("to print files matching with "))
                {
//                    i = s.indexOf('\"',29);
//                    int j = 0;
//                    if(i<len-2) j = s.indexOf('\"',i+1);
//                    if(j>0 && i>0 && i<j && j<len)
                        addInstruction( new ToPrintMatchingFilesInstruction(s.substring(29), cs) );
//                    else
//                        cs.printError("\nFILE: "+
//                                cs.getReadingFile().getAbsolutePath()+
//                                "\nERROR: Error reading the files' pattern in Line "+
//                                cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
//                                "\nProblems with the quotation marks -> \"\n");
                    return;
                }
            }      
            if(14<=len)
            {
                if(s.substring(0,13).equals("show message:"))
                {
                        addInstruction( new ShowMessageInstruction(s.substring(13), cs));
                        return;
                }
            }   
            if(5<=len)
            {
                if(s.substring(0,4).equals("for "))
                {
                    i = s.indexOf("\\from ", 4);
                    if(i>0)
                    {
                        int j = s.indexOf("\\to ",i+6);
                        if(j>0)
                        {
                            int k = s.indexOf("\\do",j+4);
                            if(k>0)
                            {
                                addInstruction(new CountForInstruction( s.substring(4,i), s.substring(i+6,j), s.substring(j+4,k), fileLinesIterator, cs));
                                return;
                            }
                            else
                            {
                                cs.printError("\nFILE: "+
                                        cs.getReadingFile().getAbsolutePath()+
                                        "\nERROR: bad 'for' Instruction Declaration in Line "+
                                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                        "\nWe have not found the '\\do'\n");
                                return;
                            }
                        }
                        else
                        {
                            cs.printError("\nFILE: "+
                                        cs.getReadingFile().getAbsolutePath()+
                                        "\nERROR: bad 'for' Instruction Declaration in Line "+
                                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                        "\nWe have not found the '\\to'\n");
                            return;
                        }
                    }
                    else
                    {
                        i = s.indexOf("\\in ",4);
                        if(i>0 && i+4<len)
                        {
                            int j = s.indexOf("\\do",i+4);
                            if(j>0)
                            {
                                addInstruction(new ArrayForInstruction( s.substring(4,i), s.substring(i+4,j), fileLinesIterator, cs) );
                                return;
                            }
                            else
                            {
                                cs.printError("\nFILE: "+
                                        cs.getReadingFile().getAbsolutePath()+
                                        "\nERROR: bad 'for' Instruction Declaration in Line "+
                                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                        "\nWe have not found the '\\do'\n");
                                return;
                            }
                        }
                        else
                        {
                            cs.printError("\nFILE: "+
                                        cs.getReadingFile().getAbsolutePath()+
                                        "\nERROR: bad 'for' Instruction Declaration in Line "+
                                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                        "\nWe have not found the '\\from' or '/in'\n");
                            return;
                        }
                    }
                }
            }
            if(4<=len)
            {
                if(s.substring(0,3).equals("if "))
                {
                        i = s.indexOf("\\then", 3);
                        if(i>0)
                        {
                            addInstruction(new IfInstruction( s.substring(3,i), fileLinesIterator ,cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad 'if' Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\then' word\n");
                }
            }
            if(7<=len)
            {
                if(s.substring(0,6).equals("while "))
                {
                        i = s.indexOf("\\do", 6);
                        if(i>0)
                        {
                            addInstruction(new WhileInstruction( s.substring(6,i), fileLinesIterator ,cs));
                            return;
                        }
                        else
                            cs.printError("\nFILE: "+
                                            cs.getReadingFile().getAbsolutePath()+
                                            "\nERROR: bad 'while' Declaration in Line "+
                                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                                            "\nWe have not found the '\\do' word\n");
                }
            }
            if(4<=len)
            {
                if(s.substring(0,4).equals("exit"))
                {
                        
                        addInstruction(new ExitInstruction(s.substring(4),cs));
                        return;
                }
            }
        }

        cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad definition of macro instruction in Line "+
                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                        "\n");
        
    }
    
    private void setFolder(String folder, CodeSheet cs)
    {
        if(folder == null || folder.isEmpty()) return;
        ArrayList<Macro> folderMacros = cs.getMacroFolders().get(folder);
        if(folderMacros == null)
        {
            folderMacros = new ArrayList<Macro>();
            folderMacros.add(macro);
            cs.getMacroFolders().put(folder,folderMacros);
        }
        else folderMacros.add(macro);
    }
    
    private void addInstruction(Instruction i) {instructions.add(i);}
    
    public Line getLineOrder() {return order;} 
    
    public SheetFile getFile() {return sf;}
    
    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        if(sf == null)
        {
            for(Instruction i : instructions) 
            {
                if(em.getExit()) return;
                else i.execute(cs, em);
            }
        }
        else if (sf.isEnable())
        {
            for(Instruction i : instructions) 
            {
                if(em.getExit()) return;
                else i.execute(cs, em);
            }    
        }
    }
}
