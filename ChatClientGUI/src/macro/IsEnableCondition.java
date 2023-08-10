/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import sheet.CodeSheet;
import sheet.Line;

/**
 *
 * @author akhrain
 */
public class IsEnableCondition extends Condition
{
    private CodeString pattern_ = null;
    
    public IsEnableCondition(boolean negation, String file, CodeSheet cs)
    {
        super(negation);
        pattern_ = new CodeString(file, cs);
    }

    public boolean check(CodeSheet cs, ExecutionMacro em) 
    {
        String pattern = Line.onlyFirstToken(pattern_.CodeStringToString(cs, em));
        if(this.getNegation()) return !cs.isEnable(pattern);
        else                   return cs.isEnable(pattern);
    }
}
