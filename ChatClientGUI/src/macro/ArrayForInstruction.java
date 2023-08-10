/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.util.ArrayList;
import java.util.Iterator;
import sheet.CodeSheet;
import sheet.Line;

/**
 *
 * @author akhrain
 */
public class ArrayForInstruction extends Instruction
{
    private MacroCode macroCode = null;
    private CodeString variable_ = null;
    private CodeString array_    = null;

    public ArrayForInstruction(String var, String arrayString, Iterator<String> fileLinesIterator, CodeSheet cs)
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        variable_ = new CodeString(var, cs);
        array_    = new CodeString(arrayString, cs);
        macroCode = new MacroCode(fileLinesIterator, cs);
    }
    
    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String variable = Line.onlyFirstToken(variable_.CodeStringToString(cs, em));
        String array    = Line.onlyFirstToken(array_.CodeStringToString(cs, em));
        ArrayList<Double> arr = em.getArray(array);
        if(arr != null)
        {
            for(Double d : arr)
            {
                em.setVariable(variable, d.doubleValue());
                if(em.getExit()) return;
                else macroCode.execute(cs,em);
            }
            return;
        }
        ArrayList<String> arrText = em.getTextArray(array);
        if(arrText != null)
        {
            for(String s : arrText)
            {
                em.setTextVariable(variable, s);
                if(em.getExit()) return;
                else macroCode.execute(cs,em);
            }
            return;
        }
        
        cs.printError("\nFILE: "+
                this.getFile().getAbsolutePath()+
                "\nERROR: bad 'for' Declaration in Line "+
                this.getLine()+":"+
                "\nWe have not found any array or text-array named "+array+"\n");
    }
    
}
