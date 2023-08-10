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
public abstract class Operator implements Solvable
{
    private Line firstVar  = null;
    private Line secondVar = null;
    
    public Operator(Line first, Line second)
    {
        firstVar  = first;
        secondVar = second;
    }
    
    public abstract double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table);
    
    public void setFirstVar(Line l)  {firstVar = l;}
    public void setSecondVar(Line l) {secondVar = l;}
    public Line getFirstVar()        {return firstVar;}
    public Line getSecondVar()       {return secondVar;}
    
    public static int findInitialParenthesis(String s, int i, int len)
    {
        while(i<len)
        {
            if(s.charAt(i) == '(') return i;
            i++;
        }
        return i;        
    }
    
    public static int findFinalParenthesis(String s, int i, int len) 
    {
        int parenthesisCheck = 0;
        while(i<len)
        {
            if(s.charAt(i) == '(') parenthesisCheck++;
            if(s.charAt(i) == ')')
            {
                if(parenthesisCheck == 0) return i;
                else parenthesisCheck--;
            }
            i++;
        }
        return i;
    }

/*    
    private static String setOperatorString(String s, int i, int j)
    {
        i = Line.jumpBlanks(s, i, j);
        if(i == j) return null;
        int k = Line.jumpNoBlanks(s, i, j);
        return s.substring(i, k);
    }
 
    
    public static Operator toOperator(String l, int i, int j, CodeSheet cs)
    {
        if(j<=i || i<0 || j>l.length()) return null;
        if(l.charAt(i) != '(') return null;
        i++;
        int k = 0;
        Line firstVar  = null;
        Line secondVar = null;
        String OperatorString = null;

        // find first variable
        k = findFinalParenthesis(l, i, j);
        if(k == j || k<=i) return null;
        firstVar = new Line(null, l.substring(i, k), cs);
        // set operator string
        i = k+1;
        k = findInitialParenthesis(l,i,j);
        if(k == j || k<=i) return null;
        OperatorString = setOperatorString(l,i,k);
        // find second variable
        i = k+1;
        k = findFinalParenthesis(l, i, j);
        if(k == j || k<=i) return null;
        secondVar =  new Line(null, l.substring(i,k),cs);
        
        if (OperatorString.equals("*"))
            return new ProductOperator(firstVar,secondVar);
        else if (OperatorString.equals("/"))
            return new QuotientOperator(firstVar,secondVar);
        else if(OperatorString.equals("*t"))
            return new TruncatedProductOperator(firstVar,secondVar);
        else if (OperatorString.equals("/t"))
            return new TruncatedQuotientOperator(firstVar,secondVar);
        else if(OperatorString.equals("*r")) 
            return new RoundProductOperator(firstVar,secondVar);
        else if (OperatorString.equals("/r"))
            return new RoundQuotientOperator(firstVar,secondVar);
        else if (OperatorString.equals("TABLE"))
        {
            TableOperator tb = new TableOperator(firstVar,secondVar,cs);
            if(tb.getTableName() ==  null) return null;
            else                           return tb;
        }
        else 
            return null;
    }
*/    
}
