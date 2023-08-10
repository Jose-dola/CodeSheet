/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.util.Iterator;
import sheet.CodeSheet;

/**
 *
 * @author akhrain
 */
public class WhileInstruction extends Instruction
{
    private MacroCode macroCode = null;
    private ConditionBlocksDividedByOR blocks = null;
    
    public WhileInstruction(String conditions, Iterator<String> fileLinesIterator,CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        macroCode = new MacroCode(fileLinesIterator, cs);
        blocks    = new ConditionBlocksDividedByOR(conditions, cs);
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        while(blocks.check(cs,em)) 
        {
            if(em.getExit()) return; 
            else macroCode.execute(cs,em);
        }    
    }
}
