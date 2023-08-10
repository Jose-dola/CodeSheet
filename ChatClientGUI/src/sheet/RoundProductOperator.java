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
public class RoundProductOperator extends Operator
{
    public RoundProductOperator(Line first, Line second) 
    {
        super(first, second);
    }
   
    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table)
    {
        return (double)Math.round( this.getFirstVar().solve(cs,em)*getSecondVar().solve(cs,em) );
    }
}
