/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.util.ArrayList;
import sheet.CodeSheet;

/**
 *
 * @author akhrain
 */
public class CodeString 
{
    private ArrayList<Printable> prints = null;
    
    public CodeString(String s, CodeSheet cs) 
    {
        prints = new ArrayList<Printable>();

        int i    = 0;
        int last = 0;
        int len  = s.length();
        while(i<len-1)
        {
            if(s.charAt(i) == '$' && s.charAt(i+1) == '$')
            {
                prints.add(new TextToPrint(s.substring(last, i)));
                i    = addPrintLine(s, i, cs);
                last = i;
            }
            else if(s.charAt(i) == '@' && s.charAt(i+1) == '@') 
            {
                prints.add(new TextToPrint(s.substring(last, i)));
                i    = addPrintTextVariable(s, i, cs);
                last = i;
            }
            else if(s.charAt(i) == '@' && s.charAt(i+1) == '?')
            {
                prints.add(new TextToPrint(s.substring(last, i)));
                i    = addPrintQuestionTextVariable(s, i, cs);
                last = i;
            }    
            else i++;
        }
        if(last<len) 
        {
            prints.add(new TextToPrint(s.substring(last,len)));
        }    
    }

    private int addPrintLine(String s, int i, CodeSheet cs)
    {
        i = i + 2;
        int j = s.indexOf("$$", i);
        if(j>i)
        {
            prints.add(new LineToPrint(s.substring(i,j), cs));
            return j+2;
        }
        else
        {
            cs.printError("\nFILE: "+
                            cs.getReadingFile().getAbsolutePath()+
                            "\nERROR: error reading text in Line "+
                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                            "\nWe have not found the $$\n");
            return s.length();
        }
    }
    
    private int addPrintTextVariable(String s, int i, CodeSheet cs)
    {
        i = i + 2;
        int j = s.indexOf("@@",i);
        if(j>i)
        {
            prints.add(new TextVariableToPrint(s.substring(i,j),cs, cs.getReadingFile(),cs.getReadingNumberLine()));
            return j+2;
        }
        else
        {
            cs.printError("\nFILE: "+
                            cs.getReadingFile().getAbsolutePath()+
                            "\nERROR: error reading text in Line "+
                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                            "\nWe have not found the @@\n");
            return s.length();
        }
    }
    
    private int addPrintQuestionTextVariable(String s, int i, CodeSheet cs)
    {
        i = i + 2;
        int j = s.indexOf("@@",i);
        if(j>i)
        {
            prints.add(new QuestionTextVariableToPrint(s.substring(i,j),cs,cs.getReadingFile(),cs.getReadingNumberLine()));
            return j+2;
        }
        else
        {
            cs.printError("\nFILE: "+
                            cs.getReadingFile().getAbsolutePath()+
                            "\nERROR: error reading text in Line "+
                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                            "\nWe have not found the final @@ or the middle @\n");
            return s.length();
        }
    }
    
    public void print(CodeSheet cs, ExecutionMacro em) 
    {
        for(Printable p : prints) p.print(cs,em);
    }
    
    public String CodeStringToString(CodeSheet cs, ExecutionMacro em)
    {
        String s = "";
        for(Printable p : prints) s = s.concat(p.printableToString(cs, em));
        return s;
    }
}
