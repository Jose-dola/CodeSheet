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
public class SetDefaultValueInstruction extends Instruction
{
    private CodeString variable_ = null;
    private Line   line          = null;
    
    public SetDefaultValueInstruction(String v, Line l, CodeSheet cs)
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());        
        variable_ = new CodeString(v, cs);
        line      = l;
    }
    
    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String variable = Line.onlyFirstToken(variable_.CodeStringToString(cs, em));
        //if(cs.getAttribute(variable) == null)
        //{
            if(em.getVariable(variable) == null) 
            {
                em.setVariable(variable,line.solve(cs,em));
            }
       // }
       // else cs.printError("\nERROR: You use the variable "+variable+" but there is an attribute with the same name\n"+
       //                    "We ignore this variable\n");
    }
}
