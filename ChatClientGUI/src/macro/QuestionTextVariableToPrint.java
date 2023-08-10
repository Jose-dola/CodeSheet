/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.io.File;
import javax.swing.JOptionPane;
import sheet.CodeSheet;

/**
 *
 * @author akhrain
 */
public class QuestionTextVariableToPrint implements Printable
{
    private CodeString message_ = null;
    
    public QuestionTextVariableToPrint(String s, CodeSheet cs, File f, int l)
    {
        message_ = new CodeString(s, cs);
    }    

    public void print(CodeSheet cs, ExecutionMacro em) 
    {
        cs.toPrint(this.printableToString(cs, em));
    }

    public String printableToString(CodeSheet cs, ExecutionMacro em) 
    {
        String message = message_.CodeStringToString(cs, em);
        String s = JOptionPane.showInputDialog(null,message,"Input",JOptionPane.PLAIN_MESSAGE);
        if(s != null) return s;
        else          return "";
    }
}
