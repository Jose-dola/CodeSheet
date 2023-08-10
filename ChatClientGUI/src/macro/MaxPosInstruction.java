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
public class MaxPosInstruction extends Instruction
{
    private CodeString array_ = null;
    private CodeString variable_ = null;
    
    public MaxPosInstruction(String var, String arr,CodeSheet cs) 
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
                "\nERROR: Problem with fuction 'max-pos' in line "+
                this.getLine()+
                "\nWe have not found the array "+array+"\n");
            return;
        }
        if(arr.size()<1)
        {
            cs.printError("\nFILE: "+
                this.getFile().getAbsolutePath()+
                "\nERROR: Problem with fuction 'max-pos' in line "+
                this.getLine()+
                "\nThe array "+array+" does not have elements\n");
            return;
        }
        
        double f = arr.get(0);
        int j = 0;
        double nw;
        int i;
        for(i=1; i<arr.size(); i++)
        {
            nw = arr.get(i);
            if(nw > f)
            {
                f = nw;
                j = i;
            }
        }
        em.setVariable(variable, j);
    }
}
