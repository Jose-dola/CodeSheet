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
public interface Solvable
{
    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table);
}
