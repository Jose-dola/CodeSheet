/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import macro.ConditionBlocksDividedByOR;
import macro.ExecutionMacro;

/**
 *
 * @author akhrain
 */
public class If implements Solvable 
{
    private Line line = null;
    private ConditionBlocksDividedByOR conditions = null;
    
    public If(String cond, String l, CodeSheet cs)
    {
        line = new Line(null,l,cs);
        conditions = new ConditionBlocksDividedByOR(cond, cs);
    }

    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table) 
    {
        if(conditions.check(cs, em)) return line.solve(cs, em, table);
        else                         return 0;
    }
    
}
