/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import java.io.File;
import macro.ExecutionMacro;

/**
 *
 * @author akhrain
 */
public class MacroParameter implements Solvable
{
    private File   file       =  null;
    private String stringLine =  null;
    private int    numberLine =  -1;
    private String macroName = null;
    
    public MacroParameter(String l, int i, int j, CodeSheet cs)
    {
        file = cs.getReadingFile();
        stringLine = cs.getReadingLine();
        numberLine = cs.getReadingNumberLine();
        macroName = Line.onlyFirstToken(l.substring(i, j));
    }
    
    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table) 
    {
        ExecutionMacro ExcMacro = cs.executeMacroByName(macroName, em, null);
        if(ExcMacro.getExit()) return ExcMacro.getReturnedValue();
        else
        {
            cs.printError("\nFILE: "+
                   file.getAbsolutePath()+
                   "\nERROR: Problems with Macro Parameter declaration in Line "+
                   numberLine+":"+"\n"+stringLine+
                   "\nThere is no returned value via exit instruction\n");
            return 0;            
        }
    }
    
//    public void RollErr(CodeSheet cs)
//    {
//        cs.printError("\nERROR ROLLING DICE: The number of dice or The number of sides are less or equal to zero, or are bad declared\n");
//    }
}

