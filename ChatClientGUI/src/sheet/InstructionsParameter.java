/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import java.io.File;
import macro.ExecutionMacro;
import macro.MacroCode;

/**
 *
 * @author akhrain
 */
public class InstructionsParameter implements Solvable
{
    private File   file       =  null;
    private String stringLine =  null;
    private int    numberLine =  -1;
    private MacroCode macroCode = null;
    
    public InstructionsParameter(String l, int i, int j, CodeSheet cs)
    {
        file = cs.getReadingFile();
        stringLine = cs.getReadingLine();
        numberLine = cs.getReadingNumberLine();
        int k = Operator.findFinalParenthesis(l, i, j);
        if(k<j) macroCode = new MacroCode(l.substring(i, k), cs);
        else    macroCode = null;
    }
    
    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table) 
    {
        if(macroCode == null)
        {
            cs.printError("\nFILE: "+
                           file.getAbsolutePath()+
                           "\nERROR: Bad Macro Parameter declaration in Line "+
                           numberLine+":"+"\n"+stringLine+"\n");
            return 0;            
        }
        
        ExecutionMacro newExecMacro = new ExecutionMacro(em);
        macroCode.execute(cs, newExecMacro);
        
        if(newExecMacro.getExit()) return newExecMacro.getReturnedValue();
        else
        {
            cs.printError("\nFILE: "+
                   file.getAbsolutePath()+
                   "\nERROR: Bad Macro Parameter declaration in Line "+
                   numberLine+":"+"\n"+stringLine+
                   "\nThere is no returned value via exit instruction\n");
            return 0;
        }    
    }    
}
