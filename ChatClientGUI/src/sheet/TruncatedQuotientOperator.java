/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import macro.ExecutionMacro;

/**
 *
 * @author akhrain
 */
public class TruncatedQuotientOperator extends Operator
{
    public TruncatedQuotientOperator(Line first, Line second) 
    {
        super(first, second);
    }
   
    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table)
    {
        double p = this.getFirstVar().solve(cs,em)/getSecondVar().solve(cs,em);
        if(p<0) return Math.ceil(p);
        else    return Math.floor(p);
    }
}
