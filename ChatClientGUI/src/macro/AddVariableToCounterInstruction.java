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
public class AddVariableToCounterInstruction extends Instruction
{
    private CodeString counter_  = null;
    private CodeString variable_ = null;
    
    public AddVariableToCounterInstruction(String c, String v, CodeSheet cs)
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        counter_  = new CodeString(c, cs);
        variable_ = new CodeString(v, cs);
    }
    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String counter  = Line.onlyFirstToken(counter_.CodeStringToString(cs, em));
        String variable = Line.onlyFirstToken(variable_.CodeStringToString(cs, em));
        int check = em.variableToCounter(counter, variable);
        if(check == -1)
        {
            cs.printError("\nFILE: "+
                this.getFile().getAbsolutePath()+
                "\nERROR: Problems with a Counter Instruction in Line "+
                this.getLine()+":"+
                "\nIt is likely that the variable does not exist \n");
        }
        if(check == -2)
        {
            cs.printError("\nFILE: "+
                this.getFile().getAbsolutePath()+
                "\nERROR: Problems with a Counter Instruction in Line "+
                this.getLine()+":"+
                "\nIt is likely that the variable "+variable+" is already linked to the counter "+counter+" \n");
        }
    }
}
