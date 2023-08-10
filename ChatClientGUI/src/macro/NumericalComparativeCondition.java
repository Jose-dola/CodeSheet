/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import sheet.CodeSheet;
import sheet.Line;
import sheet.Table;

/**
 *
 * @author akhrain
 */
public class NumericalComparativeCondition extends Condition
{
    private Line rightTerm = null;
    private Line leftTerm  = null;
    char comparativeOperator = '=';
    
    public NumericalComparativeCondition(boolean negation, String s, CodeSheet cs)
    {
        super(negation);
        int len = s.length();
        int i;

        i = s.indexOf('<');
        if(i>0)
        {
            if(i+1 < len)
            {
                if(s.charAt(i+1) == '=')
                {
                    if(i+2 < len)
                    {
                        leftTerm  = new Line(null, s.substring(0, i), cs);
                        rightTerm = new Line(null, s.substring(i+2), cs);
                        comparativeOperator = Table.LESS_EQUAL;
                        return;
                    }
                    else
                    {
                        cs.printError("\nFILE: "+
                            cs.getReadingFile().getAbsolutePath()+
                            "\nERROR: bad '<=' condition declaration in Line "+
                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+"\n");
                        return;
                    }    
                }
                else
                {
                    leftTerm  = new Line(null, s.substring(0, i), cs);
                    rightTerm = new Line(null, s.substring(i+1), cs);
                    comparativeOperator = Table.LESS;
                    return;
                }
            }
            else
            {
                cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad '<' condition declaration in Line "+
                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+"\n");
                return;
            }
        }    
        i = s.indexOf('>');
        if(i>0)
        {
            if(i+1 < len)
            {
                if(s.charAt(i+1) == '=')
                {
                    if(i+2 < len)
                    {
                        leftTerm  = new Line(null, s.substring(0, i), cs);
                        rightTerm = new Line(null, s.substring(i+2), cs);
                        comparativeOperator = Table.GREATER_EQUAL;
                        return;
                    }
                    else
                    {
                        cs.printError("\nFILE: "+
                            cs.getReadingFile().getAbsolutePath()+
                            "\nERROR: bad '>=' condition declaration in Line "+
                            cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+"\n");
                        return;
                    }    
                }
                else
                {
                    leftTerm  = new Line(null, s.substring(0, i), cs);
                    rightTerm = new Line(null, s.substring(i+1), cs);
                    comparativeOperator = Table.GREATER;
                    return;
                }
            }
            else
            {
                cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad '>' condition declaration in Line "+
                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+"\n");
                return;
            }
        }
        i = s.indexOf('=');
        if(i>0)
        {
            if(i+1 < len)
            {
                leftTerm  = new Line(null, s.substring(0, i), cs);
                rightTerm = new Line(null, s.substring(i+1), cs);
                comparativeOperator = Table.EQUAL;
                return;
            }
            else
            {
                cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad '=' condition declaration in Line "+
                        cs.getReadingNumberLine()+":\n"+cs.getReadingLine()+"\n");
                return;
            }
        }
    }
    
    public Line getRightTerm() {return rightTerm;}
    
    public boolean check(CodeSheet cs, ExecutionMacro em)
    {
        boolean check = _check(cs,em);
        
        if(this.getNegation()) return !check;
        else                   return check;
    }
    public boolean _check(CodeSheet cs, ExecutionMacro em)
    {
        double f = leftTerm.solve(cs,em) - rightTerm.solve(cs,em);
        if(comparativeOperator == Table.EQUAL)
        {
            if(-Table.TOL<f && f<Table.TOL) return true;
            else                            return false;
        }
        else if(comparativeOperator == Table.LESS)
        {
            if(f<-Table.TOL) return true;
            else             return false;
        }
        else if(comparativeOperator == Table.GREATER)
        {
            if(f>Table.TOL) return true;
            else            return false;
        }
        else if(comparativeOperator == Table.LESS_EQUAL)
        {
            if(f<Table.TOL) return true;
            else            return false;
        }
        else if(comparativeOperator == Table.GREATER_EQUAL)
        {
            if(f>-Table.TOL) return true;
            else             return false;
        }
        else return false;            
    }
}
