/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import sheet.Attribute;
import sheet.AttributeTable;
import sheet.CodeSheet;
import sheet.Line;

/**
 *
 * @author akhrain
 */
public class AttributeTableToPrintInstruction extends Instruction
{
    CodeString attributeName = null;
    CodeString variable      = null;
    
    public AttributeTableToPrintInstruction(String s, String var, CodeSheet cs)
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        attributeName = new CodeString(s, cs);
        if(var == null) variable = null;
        else            variable = new CodeString(var, cs);
              
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String name = Line.onlyFirstToken(attributeName.CodeStringToString(cs, em));
        Attribute a = cs.getAttribute(name);
        double d = 0;
        if(a != null) d = a.solve(cs, em, new AttributeTable(a.getNameToShow()));
        else
        {
            cs.printError("\nFILE: "+
            this.getFile().getAbsolutePath()+
            "\nERROR: Problems making an attribute table in Line "+
            this.getLine()+":"+
            "\nIt is likely that the attribute "+name+" does not exist \n");
            return;
        }
        if(variable != null) 
        {
            String variableName = Line.onlyFirstToken(variable.CodeStringToString(cs, em));
            em.setVariable(variableName, d);
        }
    }
}
