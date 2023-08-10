/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import macro.CodeString;
import macro.ExecutionMacro;

/**
 *
 * @author akhrain
 */
public class WindowOptionList implements Solvable
{
    CodeString text = null;
    String[] options = null;
    Line[]   values  = null;
    
    public WindowOptionList(String s, CodeSheet cs)
    {
        int len = s.length();
        int j;
        int i = s.indexOf(',');
        if(i>0 && i+1<len)
        {
            text = new CodeString(s.substring(0, i), cs);
        }
        else
        {
            cs.printError("\nFILE: "+
                cs.getReadingFile().getAbsolutePath()+
                "\nERROR: bad 'WindowOption' Declaration in Line "+
                cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                "\nWe are going to ignore this parameter\n");
            text = null;
            return;
        }
//        String[] commasSplit = s.substring(i+1).split(",");
        String[] commasSplit = commaSplit(s.substring(i+1));
        int len_split = commasSplit.length;
        options = new String[len_split];
        values  = new Line[len_split];
        int k = 0;
        for(String spair : commasSplit)
        {
            j = spair.indexOf(':');
            if(j>0 && j+1<spair.length())
            {
                options[k] = spair.substring(0, j);
                values[k]  = new Line(null,spair.substring(j+1),cs);
            }
            else
            {
                cs.printError("\nFILE: "+
                    cs.getReadingFile().getAbsolutePath()+
                    "\nERROR: bad 'WindowOption' Declaration in Line "+
                    cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+
                    "\nWe are going to ignore this parameter\n");
                 text = null;
                 return;
            }
            k++;
        }
    }
    
    public String[] commaSplit(String s)
    {
        ArrayList<String> arrayL = new ArrayList<String>();
        int len = s.length();
        int i = 0;
        int j;
        while(i<len)
        {
            j = Line.getCharJumpingBrackets(',', s, i, len);
            arrayL.add(s.substring(i, j));
            i=j+1;
        }
        String[] array = new String[arrayL.size()];
        for(i=0; i<arrayL.size(); i++) array[i] = arrayL.get(i);
        return array;
    }
    
    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table) 
    {
        String s = (String)JOptionPane.showInputDialog(null, text.CodeStringToString(cs, em), "Choose an option", JOptionPane.DEFAULT_OPTION, null, options, options[0]);
        int i;
        for(i=0; i<options.length; i++)
        {
            if(options[i].equals(s)) return values[i].solve(cs, em);
        }
        return 0;
    }
}
