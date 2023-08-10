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
public class CounterActionInstruction extends Instruction
{
    private CodeString counter_  = null;
    private Line   line                 = null;
    
    public CounterActionInstruction(String c, Line l, CodeSheet cs)
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        counter_  = new CodeString(c, cs);
        line     = l;
    }
    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String counter = Line.onlyFirstToken(counter_.CodeStringToString(cs, em));
        int check = em.applyCounter(counter, line.solve(cs, em));
        if(check < 0)
        {
            cs.printError("\nFILE: "+
                this.getFile().getAbsolutePath()+
                "\nERROR: Problems with a Counter Instruction in Line "+
                this.getLine()+":"+
                "\nIt is likely that the counter does not exist \n");
        }
    }    
}
