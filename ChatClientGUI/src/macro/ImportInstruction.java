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
public class ImportInstruction extends Instruction 
{
    private CodeString variable_ = null;
    
    public ImportInstruction(String var,CodeSheet cs) 
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
                "\nERROR: bad 'import' Declaration in Line "+
                this.getLine()+":\n"+
                "\nThis macro does not have macro-mother. No mother to import\n");
            return;
        }
        boolean check = true;
        
        Double f = mother.getVariable(variable);
        if (f != null)
        {
            em.setVariable(variable, f.doubleValue());
            check = false;
        }
        String s = mother.getTextVariable(variable);
        if (s != null)
        {
            em.setTextVariable(variable, s);
            check = false;
        }
        ArrayList<Double> array = mother.getArray(variable);
        if (array != null)
        {
            em.copyArray(variable, array, cs);
            check = false;
        }
        ArrayList<String> stringArray = mother.getTextArray(variable);
        if (stringArray != null)
        {
            em.copyTextArray(variable, stringArray, cs);
            check = false;
        }
        stringArray = mother.getCounter(variable);
        if (stringArray != null)
        {
            em.copyCounter(variable, stringArray, cs);
            check = false;
        }
        if(check)
        {
            cs.printError("\nFILE: "+
                this.getFile().getAbsolutePath()+
                "\nERROR: bad 'import' Declaration in Line "+
                this.getLine()+":"+
                "\nWe have not found any variable named "+variable+" to import \n");
        }
    }
}
