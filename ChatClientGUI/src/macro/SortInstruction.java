/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.util.ArrayList;
import java.util.Comparator;
import sheet.CodeSheet;
import sheet.Line;
import sheet.Table;

/**
 *
 * @author akhrain
 */
public class SortInstruction extends Instruction
{
    private CodeString array_ = null;
    private CodeString variable_ = null;
    
    public SortInstruction(String var, String arr,CodeSheet cs) 
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
                "\nERROR: Problem with fuction 'max' in line "+
                this.getLine()+
                "\nWe have not found the array "+array+"\n");
            return;
        }
        if(arr.size()<1)
        {
            cs.printError("\nFILE: "+
                this.getFile().getAbsolutePath()+
                "\nERROR: Problem with fuction 'max' in line "+
                this.getLine()+
                "\nThe array "+array+" does not have elements\n");
            return;
        }
        
        ArrayList<Double> arrSorted = new ArrayList<Double>(arr);
        arrSorted.sort(new Comparator<Double>()
        {
            public int compare(Double o1, Double o2) 
            {
                double f = o1.doubleValue() - o2.doubleValue();
                if(f>-Table.TOL &&  f<Table.TOL) return 0;
                else                             return (int)f;
            }
        });
        em.copyArray(variable, arrSorted, cs);
    }    
}
