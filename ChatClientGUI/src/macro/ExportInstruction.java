/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.util.ArrayList;
import sheet.CodeSheet;
import sheet.Line;

/**
 *
 * @author akhrain
 */
public class ExportInstruction extends Instruction
{
    private CodeString variable_ = null;
    
    public ExportInstruction(String var,CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        variable_ = new CodeString(var, cs);
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String variable = Line.onlyFirstToken(variable_.CodeStringToString(cs, em));
        ExecutionMacro mother = em.getMother();
        if(mother == null)
        {
            cs.printError("\nFILE: "+
                this.getFile().getAbsolutePath()+
                "\nERROR: bad 'export' Declaration in Line "+
                this.getLine()+":"+
                "\nRoot macro -> There is no mother macro to export \n");
            return;
        }
        boolean check = true;
        
        Double f = em.getVariable(variable);
        if (f != null)
        {
            mother.setVariable(variable, f.doubleValue());
            check = false;
        }
        String s = em.getTextVariable(variable);
        if (s != null)
        {
            mother.setTextVariable(variable, s);
            check = false;
        }
        ArrayList<Double> array = em.getArray(variable);
        if (array != null)
        {
            mother.copyArray(variable, array, cs);
            check = false;
        }
        ArrayList<String> stringArray = em.getTextArray(variable);
        if (stringArray != null)
        {
            mother.copyTextArray(variable, stringArray, cs);
            check = false;
        }
        stringArray = em.getCounter(variable);
        if (stringArray != null)
        {
            mother.copyCounter(variable, stringArray, cs);
            check = false;
        }
        if(check)
        {
            cs.printError("\nFILE: "+
                this.getFile().getAbsolutePath()+
                "\nERROR: bad 'export' Declaration in Line "+
                this.getLine()+":"+
                "\nWe have not found any variable named "+variable+" to export \n");
        }
    }
    
}
