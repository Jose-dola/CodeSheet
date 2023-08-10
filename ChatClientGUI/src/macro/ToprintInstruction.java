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
public class ToprintInstruction extends Instruction
{
    private CodeString codeString = null;
    
    public ToprintInstruction(String s, CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(),cs.getReadingFile());
        codeString = new CodeString(s, cs);
    }
    
    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        codeString.print(cs, em);
    }
}
