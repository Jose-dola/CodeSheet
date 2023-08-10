/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.io.File;
import sheet.CodeSheet;

/**
 *
 * @author akhrain
 */
public class PrintInstruction extends Instruction
{

    public PrintInstruction(int l, File f) 
    {
        super(l,f);
    }
    
    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        cs.print();
    }
    
}
