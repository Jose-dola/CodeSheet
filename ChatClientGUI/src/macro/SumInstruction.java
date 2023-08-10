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
public class SumInstruction extends Instruction
{
    private CodeString array_ = null;
    private CodeString variable_ = null;
    
    public SumInstruction(String var, String arr,CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        array_ = new CodeString(arr, cs);
        variable_ = new CodeString(var, cs);
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String array = Line.onlyFirstToken(array_.CodeStringToString(cs, em));
        String variable = Line.onlyFirstToken(variable_.CodeStringToString(cs, em));
        ArrayList<Double> arr = em.getArray(array);
        if(arr == null)
        {
            cs.printError("\nFILE: "+
                this.getFile().getAbsolutePath()+
                "\nERROR: Problem with fuction 'sum' in line "+
                this.getLine()+
                "\nWe have not found the array "+array+"\n");
            return;
        }
        
        double sum = 0;
        for(double d : arr) sum+=d;
        em.setVariable(variable, sum);
    }    
}
