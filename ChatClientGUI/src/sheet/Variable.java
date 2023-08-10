/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import java.util.ArrayList;
import macro.ExecutionMacro;

/**
 *
 * @author akhrain
 */
public class Variable implements Solvable
{
    String name;
    Line   arrayindex = null; /* if arrayindex is null then it is not an array position. It is a variable */
    
    public Variable(String l, int i, int j, CodeSheet cs)
    {
        if(i<j)
        {
            if(l.charAt(i)=='(') /* array position */
            {
                int k = Operator.findFinalParenthesis(l, i+1, j);
                if(k<j)
                {
                    arrayindex = new Line(null,l.substring(i+1,k),cs);
                }
                else
                    cs.printError("\nFILE: "+cs.getReadingFile().getAbsolutePath()+
                                  "\nERROR: bad ARRAY POSITION Declaration in Line "+cs.getReadingNumberLine()+
                                  " :\n"+cs.getReadingLine()+
                                  "\nWe do not find the final parenthesis ')' in the array position definition\n");
                i = k+1;
                if(i>=j) {name = null; return;}
            }
            else arrayindex = null; /* it is not an array position, it is a variable */
            int end = Line.jumpNoBlanks(l, i, j);
            try
            {
                if (end > i) name = l.substring(i,end);
                else         name = null;
            }
            catch(IndexOutOfBoundsException ex) {name = null;}
        }
        else name = null;
    }

    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table) 
    {
        if(name == null) return 0;
        else
        {
            if(em == null) 
            {                
                cs.printError("\nERROR: You use the variable "+name+" without initialitation.\n"+
                                        "We asign the value 0.\n");
                return 0;
            }
            else
            {
                if(arrayindex == null) /* it is a variable */
                {
                    Double d = em.getVariable(name);
                    if(d ==  null) 
                    {
                        cs.printError("\nERROR: You use the variable "+name+" without initialitation.\n"+
                                      "We asign the value 0.\n");
                        return 0;
                    }
                    else return d.doubleValue();
                }
                else /* it is an array position */
                {
                    ArrayList<Double> array = em.getArray(name);
                    if(array == null)
                    {
                        cs.printError("\nERROR: You use the array "+name+" without initialitation.\n"+
                                      "We asign the value 0.\n");
                        return 0;
                    }
                    else
                    {
                        int index = (int)Math.round(arrayindex.solve(cs, em));
                        if(index < array.size()) return array.get(index);
                        else
                        {
                            cs.printError("\nERROR: array "+name+" out of range\n"
                                          + "We assign the value 0.\n");
                            return 0;
                        }
                    }
                }
            }
        }   
    }
}
