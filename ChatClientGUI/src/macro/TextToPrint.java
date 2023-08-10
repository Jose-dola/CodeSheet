/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import sheet.CodeSheet;

/**
 *
 * @author akhrain
 */
public class TextToPrint implements Printable
{
    String txt;
    
    public TextToPrint(String s)
    {
        txt = s;
    }

    public void print(CodeSheet cs, ExecutionMacro em) 
    {
        if(txt != null) cs.toPrint(txt);
    }
    
    public String printableToString(CodeSheet cs, ExecutionMacro em)
    {
        return txt;
    }
}
