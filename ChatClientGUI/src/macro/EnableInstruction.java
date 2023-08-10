/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import sheet.CodeSheet;
import sheet.Line;

/**
 *
 * @author akhrain
 */
public class EnableInstruction extends Instruction
{
    private CodeString text = null;
    
    public EnableInstruction(String s, CodeSheet cs)
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        text = new CodeString(s,cs);
    }
    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        cs.enable(Line.onlyFirstToken(text.CodeStringToString(cs, em)));
    }
}
