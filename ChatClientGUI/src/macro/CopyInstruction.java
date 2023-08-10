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
public class CopyInstruction extends Instruction
{
    private CodeString name_ = null;
    private CodeString variableToCopy_ = null;
    
    public CopyInstruction(String s, String v,CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(), cs.getReadingFile());
        name_ = new CodeString(s, cs);
        variableToCopy_ = new CodeString(v, cs);
    }

    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String name = Line.onlyFirstToken(name_.CodeStringToString(cs, em));
        String variableToCopy = Line.onlyFirstToken(variableToCopy_.CodeStringToString(cs, em));

        boolean check = true;
        
        Double f = em.getVariable(variableToCopy);
        if (f != null)
        {
            em.setVariable(name, f.doubleValue());
            check = false;
        }
        String s = em.getTextVariable(variableToCopy);
        if (s != null)
        {
            em.setTextVariable(name, new String(s));
            check = false;
        }
        ArrayList<Double> array = em.getArray(variableToCopy);
        if (array != null)
        {
            em.copyArray(name, new ArrayList<Double>(array), cs);
            check = false;
        }
        ArrayList<String> stringArray = em.getTextArray(variableToCopy);
        if (stringArray != null)
        {
            em.copyTextArray(name, new ArrayList<String>(stringArray), cs);
            check = false;
        }
        if(check)
        {
            cs.printError("\nFILE: "+
                this.getFile().getAbsolutePath()+
                "\nERROR: bad 'copy' Declaration in Line "+
                this.getLine()+":"+
                "\nWe have not found any variable named "+variableToCopy+" to copy \n");
        }
    }
}
