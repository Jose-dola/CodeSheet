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
public class MaxOrMin implements Solvable
{
    public static int MAX = 1;
    public static int MIN = -1;
    
    private int maxORmin = 0;
    private String attributeName = null;
    
    public MaxOrMin (int type, String attribute)
    {
        maxORmin = type;
        attributeName = Line.onlyFirstToken(attribute);
    }
    
    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table) 
    {
        Attribute a = cs.getAttribute(attributeName);
        if(a == null) return 0;
        else
        {
            if     (maxORmin == MAX) return a.solveMax(cs, em);
            else if(maxORmin == MIN) return a.solveMin(cs, em);
            else return 0;
        }
    }
    
}
