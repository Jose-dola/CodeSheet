/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import sheet.CodeSheet;
import sheet.Line;
import sheet.SheetFile;

/**
 *
 * @author akhrain
 */
public class ToPrintMatchingFilesInstruction extends Instruction
{
    private CodeString pattern = null;
    
    public ToPrintMatchingFilesInstruction(String s, CodeSheet cs) 
    {
        super(cs.getReadingNumberLine(),cs.getReadingFile());
        pattern = new CodeString(s, cs);
    }
        
    public void execute(CodeSheet cs, ExecutionMacro em) 
    {
        String s = Line.onlyFirstToken(pattern.CodeStringToString(cs, em));
        String toprint = "<ul>";
        for(SheetFile sf : cs.getSheetFiles())
            if(sf.checkName(s)) 
                toprint += "<li>"+sf.getName()+"</li>";
        toprint += "</ul>";
        cs.toPrint(toprint);
    }
    
}
