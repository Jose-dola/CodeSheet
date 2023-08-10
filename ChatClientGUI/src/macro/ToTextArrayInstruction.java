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
public class ToTextArrayInstruction extends Instruction
{
    private CodeString arrayname_ = null;
    private CodeString txt = null;
    
    public ToTextArrayInstruction(String name,CodeString t,CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        arrayname_ = new CodeString(name, cs);
        txt        = t;
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String arrayname = Line.onlyFirstToken(arrayname_.CodeStringToString(cs, em));
        ArrayList<String> textarray = em.getTextArray(arrayname);
        if(textarray != null)
        {
            textarray.add(txt.CodeStringToString(cs, em));
        }
        else cs.printError("\nFILE: "+
                        getFile().getAbsolutePath()+
                        "\nERROR: bad text-array-add Declaration in Line "+
                        getLine()+"\n"+
                        "\nthe text array "+arrayname+" is not defined\n");
    }
}
