/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import javax.swing.JOptionPane;
import sheet.CodeSheet;

/**
 *
 * @author akhrain
 */
public class ShowMessageInstruction extends Instruction
{
    private CodeString text_ = null;
    
    public ShowMessageInstruction(String txt, CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        text_ = new CodeString(txt, cs);
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String text = text_.CodeStringToString(cs, em);
        JOptionPane.showMessageDialog(null, text, "macro message", JOptionPane.INFORMATION_MESSAGE);
    }
}
