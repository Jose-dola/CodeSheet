/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import sheet.CodeSheet;

/**
 *
 * @author akhrain
 */
public abstract class Condition 
{
    private boolean negation = false;
    
    public Condition(boolean neg)
    {
        negation = neg;
    }
    
    public boolean getNegation() {return negation;}
    public abstract boolean check(CodeSheet cs, ExecutionMacro em);
}
