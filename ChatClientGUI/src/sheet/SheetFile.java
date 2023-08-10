/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author akhrain
 */
public class SheetFile
{
    private String    name      = null;       
    private CodeSheet codeSheet = null;
    private File      file      = null;
    private SheetFile parent    = null;
    /* the SheetFile can be a folder and files are the files in this folder */
    private ArrayList<SheetFile> files = null;
    private boolean isActive = true;
    private boolean alwaysActive = false;
    private int     type     = NO_CODE_SHEET_FILE;
    
    /* code sheet file types */
    static int FOLDER = 0;
    static int CSHEET = 1;
    static int CTABLE = 2;
    static int NO_CODE_SHEET_FILE = -1;

    public SheetFile(File f, SheetFile _parent, CodeSheet cs)
    {
        if(f == null) return;
        codeSheet = cs;
        file = f;
        name = file.getName();
        File[] filesvector = f.listFiles();
        if(filesvector == null) 
        {
            analize();
        }
        else    
        {
            parent = _parent;
            type  = SheetFile.FOLDER;
            files = new ArrayList<SheetFile>();
            for(File fofvector : filesvector)
            {
                cs.setReadingFile(fofvector);
                SheetFile sf = new SheetFile(fofvector, this, cs);
                if(sf.getType() == SheetFile.CSHEET || sf.getType() == SheetFile.CTABLE || sf.getType() == SheetFile.FOLDER) 
                    files.add(sf);
            }
            sortFiles(files);
        }
    }
    
    public SheetFile getParent() {return parent;}
    public String getName() {return name;}
    public ArrayList<SheetFile> getFiles() {return files;}
    public File getFile() {return file;}
    public int  getType() {return type;}
    public List<String> getLines()
    {
        try{ return Files.readAllLines(getFile().toPath()); }
        catch( IOException e ) 
        { 
            codeSheet.printError("\nFILE: "+
               getFile().getAbsolutePath()+
               "\nERROR READING THE FILE: "+e.getMessage()+
               "\nMaybe this file is not using UTF-8 encoding format"+
               "\nYou have to use UTF-8 enconding format for CodeSheet files\n");
        }
        return null;
    }
    
    public void setEnable()   {isActive=true;}
    public void setDisable()  {isActive=false;}
    public boolean isEnable() {return alwaysActive || isActive;}
    
    private void analize()
    {
        int len     = name.length();
        if(len < 7) return;
        String extension = name.substring(len-7);
        if(extension.equals(".csheet"))
        {
            type = SheetFile.CSHEET;
            analizeCSHEET();
            return;
        }
        else if(extension.equals(".ctable"))
        {
            type = SheetFile.CTABLE;
            if(codeSheet.addTable( name.substring(0,len-7), new Table(this,codeSheet) ) == null)
            {
                codeSheet.printError("\nFILE: "+
                                   getFile().getAbsolutePath()+
                                   "\nERROR: There is other table with this name\n");
            }
            return;
        }
        type = SheetFile.NO_CODE_SHEET_FILE;
    }
    
    private void analizeCSHEET()
    {
//        name = s;
        codeSheet.addSheetFile(this);
/*
        System.out.println("###################"+file.getName());
        int i = 1;
        try
        {
          for(String line : getLines())
          {
            System.out.println(i+" "+line);
            i++;
          }
          System.out.println("\n\n\n");
        }
        catch(IOException ex) {}
*/
        String auxString;
        String[] brokenLine;
        char firstChar = '#';
        int i   = 0;
        int j   = 0;
        int len = 0;
        codeSheet.setReadingNumberLine(0);
        codeSheet.setActualNumberLine(0);
        Iterator<String> linesIterator = getLines().iterator(); 
        if(linesIterator == null) return;
        // get first line
        String line = getLine(linesIterator, codeSheet);
        while(line != null)
        {
            len = line.length();
            i   = Line.jumpBlanks(line, 0, len);
            if(i < len)
            {
                firstChar = line.charAt(i);
                /* attribute declaration */
                if(firstChar != ' ' && firstChar != '\t' && firstChar != '#' && firstChar != '/' && firstChar != '\\'
                   && firstChar != '@' && !Line.isNumberCheck(firstChar) && firstChar != '+' && firstChar != '-')
                {
                        brokenLine = line.split(":",2);
                        if(brokenLine.length == 2 && brokenLine[0] != null && brokenLine[1] != null)
                        {
                            j = Line.jumpNoBlanks(brokenLine[0], i, brokenLine[0].length());
                            auxString = line.substring(i, j);
                            addLineToSheet(auxString, brokenLine[1]);
                        }
                        else
                            codeSheet.printError("\nFILE: "+
                                               getFile().getAbsolutePath()+
                                               "\nERROR: bad Attribute Declaration in Line "+
                                               codeSheet.getReadingNumberLine()+":"+"\n"+line+"\n");
                }
                else if(firstChar == '\\')
                {
                    setSpecialLine(line, i, linesIterator);
                }
                else if(firstChar == '@')
                {
                    i++;
                    brokenLine = line.substring(i).split(":",2);
                    auxString  = Line.onlyFirstToken(brokenLine[0]);
                    Attribute attribute = codeSheet.addAttribute(auxString);
                    if(brokenLine.length > 1) attribute.setNameToShow(brokenLine[1]);
                }
            }
            // get new line
            line = getLine(linesIterator, codeSheet);
        }
    }
    
    private void setSpecialLine(String line, int i, Iterator<String> linesIterator)
    {
        String auxString;
        String[] brokenLine;
        i++;
        if(i+14 <= line.length())
        {
            if(line.substring(i, i+14).equals("ALWAYS ENABLED")) 
            {
                alwaysActive = true;
                return;
            }
        }
        
        brokenLine = line.substring(i).split(":",2);
        if(brokenLine.length == 2 && brokenLine[0] != null && brokenLine[1] != null)
        {
            auxString = Line.onlyFirstToken(brokenLine[1]);
            if(brokenLine[0].equals("MACRO"))
            {
                if(auxString != null)
                    codeSheet.addMacroCode(auxString, linesIterator,this,codeSheet);
                else
                    codeSheet.printError("\nFILE: "+
                                   getFile().getAbsolutePath()+
                                   "\nERROR: bad Macro Declaration in Line "+
                                   codeSheet.getReadingNumberLine()+":"+"\n"+line+"\n");
            }
            else if(brokenLine[0].equals("TABLE"))
            {
                if(auxString != null)
                    codeSheet.addTable(auxString, new Table(linesIterator,codeSheet));
                else
                    codeSheet.printError("\nFILE: "+
                                   getFile().getAbsolutePath()+
                                   "\nERROR: bad Table Declaration in Line "+
                                   codeSheet.getReadingNumberLine()+":"+"\n"+line+"\n");
            }
            return;
        }
    }
    
    static public String getLine(Iterator<String> linesIterator, CodeSheet codeSheet)
    {
        int i;
        if(!linesIterator.hasNext()) return null;
        String returnedLine = null;
        String line = linesIterator.next();
        codeSheet.setReadingNumberLine(codeSheet.getActualNumberLine() + 1);
        codeSheet.setActualNumberLine(codeSheet.getActualNumberLine() + 1);
        for(i = line.length()-1; i>0; i--)
            if(line.charAt(i) != ' ' && line.charAt(i) != '\t' ) break;
        if(i<1) 
        {
            codeSheet.setReadingLine(line);
            return line;
        }
        if(line.charAt(i) == '\\' && line.charAt(i-1) == '\\')
        {
            codeSheet.lockLineIteration();
            String line2 = getLine(linesIterator, codeSheet);
            codeSheet.unlockLineIteration();
            // return two joined lines
            if(line2 != null)
                returnedLine = line.substring(0, i-1).concat(line2.substring( Line.jumpBlanks(line2, 0, line2.length()) ));
            else 
                returnedLine = line.substring(0, i-1);
            codeSheet.setReadingLine(returnedLine);
            return returnedLine;
        }
        codeSheet.setReadingLine(line);
        return line;
    }
    
//    static private String AuxGetLine(Iterator<String> linesIterator, CodeSheet codeSheet)
//    {
//        int i;
//        if(!linesIterator.hasNext()) return null;
//        String line = linesIterator.next();
//        codeSheet.setActualNumberLine(codeSheet.getActualNumberLine() + 1);
//        for(i = line.length()-1; i>0; i--)
//            if(line.charAt(i) != ' ' && line.charAt(i) != '\t' ) break;
//        if(i<1) 
//            return line;
//        if(line.charAt(i) == '\\' && line.charAt(i-1) == '\\')
//        {
//            String line2 = AuxGetLine(linesIterator, codeSheet);
//            // return two joined lines
//            if(line2 != null)
//                return line.substring(0, i-1).concat(line2.substring( Line.jumpBlanks(line2, 0, line2.length()) ));
//            else 
//                return line.substring(0, i-1);
//        }
//        return line;
//    }
    
    public boolean checkName(String s)
    {
        if(name == null) return false;
        if(s    == null) return false;
        if(name.indexOf(s) >= 0) return true;
        else                     return false;
    }
    
    public void addLineToSheet(String attributeString, String parametersString)
    {
        if( attributeString.length()>0 && parametersString.length()>0 )
        {
            Attribute attribute = codeSheet.addAttribute(attributeString);
            attribute.addLine(new Line(this,parametersString,codeSheet));
        }
    }
    
    public static void sortFiles(ArrayList<SheetFile> _files)
    {
        _files.sort(new Comparator<SheetFile>()
            {    
                public int compare(SheetFile o1, SheetFile o2) 
                {
                    if(o1.getType() == SheetFile.FOLDER)
                    {
                        if(o2.getType() == SheetFile.FOLDER) return o1.getName().compareTo(o2.getName());
                        else return -1;
                    }
                    else if(o1.getType() == SheetFile.CSHEET || o1.getType() == SheetFile.CTABLE)
                    {
                        if(o2.getType() == SheetFile.CSHEET || o1.getType() == SheetFile.CTABLE) return o1.getName().compareTo(o2.getName());
                        else if(o2.getType() == SheetFile.FOLDER) return 1;
                        else return -1;
                    }
                    return 1;
                }
            });
    }
    
    public String getText() 
    {
        List<String> lines = getLines();
        if(lines == null) return null;
        String s = "";
        for(String line : lines) s += line + System.lineSeparator();
        return s;
    }
    
    public String getTextWithNumberLines() 
    {
        List<String> lines = getLines();
        if(lines == null) return null;
        String s = "";
        int i = 1;
        for(String line : lines) 
        {
            String nline = ""+i;
            if(nline.length() == 1) nline = "00".concat(nline);
            if(nline.length() == 2) nline = "0".concat(nline);
            s += nline + "   " + line + System.lineSeparator();
            i++;
        }
        return s;
    }
    
    public void overwrite(String s) throws IOException 
    {
        FileOutputStream writer = new FileOutputStream(this.getFile().toPath().toString());
        writer.write(s.getBytes("UTF-8"));
        writer.close();
    }
    
    @Override
    public String toString()
    {
//        if(type == SheetFile.FOLDER) return "DIR: ".concat(name);
        return name;
    }
}
