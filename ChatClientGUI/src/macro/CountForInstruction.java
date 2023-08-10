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
public class CountForInstruction extends Instruction
{
    private MacroCode macroCode  = null;
    private CodeString countVar_ = null;
    private Line from       = null;
    private Line to         = null;
    
    public CountForInstruction(String var, String fromString, String toString, Iterator<String> fileLinesIterator, CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        countVar_ = new CodeString(var, cs);
        from      = new Line(null, fromString, cs);
        to        = new Line(null, toString, cs);
        macroCode = new MacroCode(fileLinesIterator, cs);
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String countVar = Line.onlyFirstToken(countVar_.CodeStringToString(cs, em));
        int f = (int)Math.round(from.solve(cs, em));
        int t = (int)Math.round(  to.solve(cs, em));
        int i;
        for(i=f; i<=t; i++)
        {
            em.setVariable(countVar,(double)i);
            if(em.getExit()) return;
            else macroCode.execute(cs,em);
        }
    }
}
