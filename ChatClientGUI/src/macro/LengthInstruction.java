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
public class LengthInstruction extends Instruction
{
    private CodeString variable_ = null;
    private CodeString array_    = null;
    
    public LengthInstruction(String var, String arr,CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        array_    = new CodeString(arr, cs);
        variable_ = new CodeString(var, cs);
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String array = Line.onlyFirstToken(array_.CodeStringToString(cs, em));
        String variable = Line.onlyFirstToken(variable_.CodeStringToString(cs, em));
        ArrayList<Double> arr = em.getArray(array);
        if(arr != null)
        {
            em.setVariable(variable, arr.size());
            return;
        }
        ArrayList<String> arrTxt = em.getTextArray(array);
        if(arrTxt != null)
        {
            em.setVariable(variable, arrTxt.size());
            return;
        }
        cs.printError("\nFILE: "+
                this.getFile().getAbsolutePath()+
                "\nERROR: bad 'length' Declaration in Line "+
                this.getLine()+":"+
                "\nWe have not found any array or text-array named "+array+"\n");
    }
}
