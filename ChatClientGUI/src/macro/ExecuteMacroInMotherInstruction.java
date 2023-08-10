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
public class ExecuteMacroInMotherInstruction extends Instruction 
{
    private CodeString macroName_ = null;
    private MacroCode  input      = null;
    
    public ExecuteMacroInMotherInstruction(String name, MacroCode in,CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        macroName_ = new CodeString(name, cs);
        input     = in;
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String macroName = Line.onlyFirstToken(macroName_.CodeStringToString(cs, em));
        cs.executeMacroByNameInMother(macroName,em,input);
    }
}
