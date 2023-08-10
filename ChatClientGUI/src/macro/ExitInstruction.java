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
public class ExitInstruction extends Instruction
{
    private Line returned_value = null;

    public ExitInstruction(String l, CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        if(l != null) returned_value = new Line(null,l,cs);
        else          returned_value = null;
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        if(em.getMother() == null)
        {
           // cs.printError("\nFILE: "
           //         + this.getFile().getAbsolutePath()
           //         + "\nERROR: bad 'exit' Declaration in Line "
           //         + this.getLine() + ":"
           //         + "<br> You cannot leave the root macro <br>");
            if(returned_value != null) em.setReturnedValue(returned_value.solve(cs, em));
            return;
        }
        if(returned_value != null) em.setReturnedValue(returned_value.solve(cs, em));
        em.setExit();
    }
}
