/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.io.File;
import java.util.ArrayList;
import sheet.CodeSheet;
import sheet.Line;
import sheet.Operator;

/**
 *
 * @author akhrain
 */
public class TextVariableToPrint implements Printable
{
    private String variable   = null;
    private Line   indexarray = null;
    private int    line       = 0;
    private File   file       = null;
    
    public TextVariableToPrint(String s, CodeSheet cs, File f, int l)
    {
        line = l;
        file = f;
        int len = s.length();
        int i = Line.jumpBlanks(s, 0, len);
        int j = 0;
        if(i<len)
        {
            if(s.charAt(i) == '(') /* it is an array position */
            {
                j = Operator.findFinalParenthesis(s, i+1, len);
                if(j+1<len)
                {
                    indexarray = new Line(null,s.substring(i+1, j),cs);
                    variable   = Line.onlyFirstToken(s.substring(j+1));
                }
                else
                {
                    cs.printError("\nFILE: "+f.getAbsolutePath()
                                  +"\nERROR: bad 'print text array position' Definition in Line "+l
                                  +"\nWe will not print anything about this definition\n");
                    variable = null;
                }
            }
            else /* it is only a text variable */
            {
                indexarray = null;
                variable   = Line.onlyFirstToken(s.substring(i));
            }
        }
        else
        {
            cs.printError("\nFILE: "+f.getAbsolutePath()
                                  +"\nERROR: bad 'print text variable' Definition in Line "+l
                                  +"\nWe will not print anything about this definition\n");
            variable = null;
        }
    }
    
    public void print(CodeSheet cs, ExecutionMacro em) 
    {
        cs.toPrint(this.printableToString(cs,em)); 
/*      
        if(variable == null) return;
        
        if(indexarray == null) 
        {
            String txt = em.getTextVariable(variable);
            if(txt != null)
            {
                cs.toPrint(txt);
            }
            else
                cs.printError("\nFILE: "+
                                file.getAbsolutePath()+
                                "\nERROR: You try to use the text variable "+variable+" without initialization"+
                                "in line "+line+"\n"+
                                "We ignore this instruction\n");
        }
        else 
        {
            ArrayList<String> array = em.getTextArray(variable);
            if(array != null)
            {
                int index = (int)Math.round(indexarray.solve(cs, em));
                if(index < array.size())
                {
                    cs.toPrint(array.get(index));
                }
                else
                    cs.printError("\nFILE: "+
                                    file.getAbsolutePath()+
                                    "\nERROR: Text array "+variable+" out of range "+
                                    "in line "+line+"\n"+
                                    "We ignore this instruction\n");
            }
            else
                cs.printError("\nFILE: "+
                                file.getAbsolutePath()+
                                "\nERROR: You try to use the text variable "+variable+" without initialization"+
                                "in line "+line+"\n"+
                                "We ignore this instruction\n");
        
        }
*/
    }
    
    public String printableToString(CodeSheet cs, ExecutionMacro em)
    {
        if(variable == null) return "";
        
        if(indexarray == null) /* it is a text variable */
        {
            String txt = em.getTextVariable(variable);
            if(txt != null)
            {
                return txt;
            }
            else
            {
                cs.printError("\nFILE: "+
                                file.getAbsolutePath()+
                                "\nERROR: You try to use the text variable "+variable+" without initialization"+
                                "in line "+line+"\n"+
                                "We ignore this instruction\n");
                return "";
            }
        }
        else /* it is an array position */
        {
            ArrayList<String> array = em.getTextArray(variable);
            if(array != null)
            {
                int index = (int)Math.round(indexarray.solve(cs, em));
                if(index < array.size())
                {
                    return array.get(index);
                }
                else
                {
                    cs.printError("\nFILE: "+
                                    file.getAbsolutePath()+
                                    "\nERROR: Text array "+variable+" out of range "+
                                    "in line "+line+"\n"+
                                    "We ignore this instruction\n");
                    return "";
                }
            }
            else
            {
                cs.printError("\nFILE: "+
                                file.getAbsolutePath()+
                                "\nERROR: You try to use the text variable "+variable+" without initialization"+
                                "in line "+line+"\n"+
                                "We ignore this instruction\n");
                return "";
            }
        } 
    }
}
