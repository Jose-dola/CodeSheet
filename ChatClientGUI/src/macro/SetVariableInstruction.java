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
public class SetVariableInstruction extends Instruction
{
    private CodeString variable_ = null;
    /* if position is null, it is the setting of a variable */
    /* if position is different to null, it is the setting of an array's position */
    private Line   position = null;
    private Line   line     = null;

    public SetVariableInstruction(String v, Line pos, Line l, CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        variable_ = new CodeString(v, cs);
        position = pos;
        line     = l;
    }
    
    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String variable = Line.onlyFirstToken(variable_.CodeStringToString(cs, em));
        if(position == null) em.setVariable(variable,line.solve(cs,em));
        else
        {
            ArrayList<Double> arr = em.getArray(variable);
            if(arr != null)
            {
                int pos = (int)Math.round( position.solve(cs, em) );
                if (pos>=0 && pos<arr.size()) arr.set(pos, line.solve(cs,em));
                else
                    cs.printError("\nFILE: "+
                                        this.getFile().getAbsolutePath()+
                                        "\nERROR: bad array position Declaration in Line "+
                                        this.getLine()+
                                        "\nThe especified position "+pos+" is out of range in the array "+variable+"\n");
            }
            else
                cs.printError("\nFILE: "+
                                        this.getFile().getAbsolutePath()+
                                        "\nERROR: Problems with the setting of an array's position in Line "+
                                        this.getLine()+
                                        "\nWe have not found the array "+variable+"\n");
        }
    }
    
}
