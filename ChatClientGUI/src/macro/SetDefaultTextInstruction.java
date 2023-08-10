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
public class SetDefaultTextInstruction extends Instruction
{
    private CodeString variable_ = null;
    private CodeString txt_      = null;

    public SetDefaultTextInstruction(String v, String t, CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        variable_ = new CodeString(v, cs);
        txt_      = new CodeString(t, cs);
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String variable = Line.onlyFirstToken(variable_.CodeStringToString(cs, em));
        String txt      = txt_.CodeStringToString(cs, em);
        if(em.getTextVariable(variable) == null) 
        {
            em.setTextVariable(variable,txt);
        }
    }
}
