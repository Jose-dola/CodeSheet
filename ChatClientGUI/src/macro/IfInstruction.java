/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.util.Iterator;
import sheet.CodeSheet;
import sheet.Line;

/**
 *
 * @author akhrain
 */
public class IfInstruction extends Instruction
{
    private MacroCode macroCode = null;
    private MacroCode macroCodeElse = null;
    private ConditionBlocksDividedByOR blocks = null;
    
    public IfInstruction(String conditions, Iterator<String> fileLinesIterator,CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        macroCodeElse = null;
        macroCode     = new MacroCode(fileLinesIterator, cs);
        /* get 'else' instructions */
        String final_line = cs.getReadingLine();
        int len = final_line.length();
        int i = Line.jumpBlanks(final_line, 0, len);
        i     = Line.jumpNoBlanks(final_line, i, len);
        i     = Line.jumpBlanks(final_line, i, len);
        if(i<len-1)
        {
            if(Line.onlyFirstToken(final_line.substring(i)).equals("else"))
                macroCodeElse = new MacroCode(fileLinesIterator, cs);
        }        
        /*******************************/
        blocks = new ConditionBlocksDividedByOR(conditions, cs);
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        if(blocks.check(cs, em)) macroCode.execute(cs, em);
        else
        {
            if(macroCodeElse != null) macroCodeElse.execute(cs, em);
        }    
    }
}
