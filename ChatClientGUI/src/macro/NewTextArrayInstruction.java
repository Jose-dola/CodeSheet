/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.io.File;
import sheet.CodeSheet;
import sheet.Line;

/**
 *
 * @author akhrain
 */
public class NewTextArrayInstruction extends Instruction
{
    private CodeString name_ = null;

    public NewTextArrayInstruction(String arrayname, CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        name_ = new CodeString(arrayname, cs);
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String name = Line.onlyFirstToken(name_.CodeStringToString(cs, em));
        em.newTextArray(name, cs);
    }
    
}
