/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import javax.swing.JOptionPane;
import macro.CodeString;
import macro.ExecutionMacro;

/**
 *
 * @author akhrain
 */
public class WindowInput implements Solvable
{
    private CodeString text = null;
    
    public WindowInput(String s, CodeSheet cs)
    {
        text = new CodeString(s,cs);
    }
    
    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table) 
    {
        String s = JOptionPane.showInputDialog(null,text.CodeStringToString(cs, em),"Input",JOptionPane.PLAIN_MESSAGE);
        if(s == null) return 0;
        else          return (new Line(null, s, cs)).solve(cs, em);
    }    
}
