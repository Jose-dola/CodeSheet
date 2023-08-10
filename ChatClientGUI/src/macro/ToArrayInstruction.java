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
public class ToArrayInstruction extends Instruction
{
    CodeString arrayname_ = null;
    Line       line       = null;
    
    public ToArrayInstruction(String name,String l,CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        arrayname_ = new CodeString(name, cs);
        line       = new Line(null,l,cs);
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String arrayname = Line.onlyFirstToken(arrayname_.CodeStringToString(cs, em));
        ArrayList<Double> array = em.getArray(arrayname);
        if(array != null)
        {
            array.add(line.solve(cs,em));
        }
        else cs.printError("\nFILE: "+
                        getFile().getAbsolutePath()+
                        "\nERROR: bad array-add Declaration in Line "+
                        getLine()+
                        "\nthe array "+arrayname+" is not defined\n");
    }
}
