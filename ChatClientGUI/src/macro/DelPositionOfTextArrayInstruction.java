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
public class DelPositionOfTextArrayInstruction extends Instruction
{
    CodeString arrayname_ = null;
    Line       line       = null;
    
    public DelPositionOfTextArrayInstruction(String name,String l,CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        arrayname_ = new CodeString(name, cs);
        line       = new Line(null,l,cs);
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String arrayname = Line.onlyFirstToken(arrayname_.CodeStringToString(cs, em));
        ArrayList<String> array = em.getTextArray(arrayname);
        if(array != null)
        {
            int pos = (int)Math.round(line.solve(cs,em));
            if(pos >= 0 && pos < array.size())
            {
                array.remove(pos);
            }
            else
                cs.printError("\nFILE: "+
                        getFile().getAbsolutePath()+
                        "\nERROR: Error in text-array-del Declaration in Line "+
                        getLine()+
                        "\nInvalid position: "+pos+", in the array "+arrayname+"\n");
        }
        else cs.printError("\nFILE: "+
                        getFile().getAbsolutePath()+
                        "\nERROR: bad text-array-del Declaration in Line "+
                        getLine()+
                        "\nthe array "+arrayname+" is not defined\n");
    }
}
