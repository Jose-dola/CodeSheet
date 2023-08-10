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
public class DelVariableOfCounter extends Instruction
{
    CodeString counter_  = null;
    CodeString variable_ = null;
    
    public DelVariableOfCounter(String name,String var,CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        counter_  = new CodeString(name, cs);
        variable_ = new CodeString(var, cs);
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String counter  = Line.onlyFirstToken(counter_.CodeStringToString(cs, em));
        String variable = Line.onlyFirstToken(variable_.CodeStringToString(cs, em));
        ArrayList<String> array = em.getCounter(counter);
        if(array != null)
        {
            int i;
            for(i=0; i<array.size(); i++)
                if(array.get(i).equals(variable)) { array.remove(i); i--; }
        }
        else cs.printError("\nFILE: "+
                        getFile().getAbsolutePath()+
                        "\nERROR: bad counter-del Declaration in Line "+
                        getLine()+
                        "\nthe counter "+counter+" does not exist\n");
    }
}
