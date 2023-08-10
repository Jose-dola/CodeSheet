/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import macro.ExecutionMacro;
import macro.CodeString;

/**
 *
 * @author akhrain
 */
public class Roll implements Solvable
{
    CodeString cstring = null;
    Line sides = null;
    Line numberOfDice = null;
    boolean print = true;
    
    public Roll(String l, int i, int j, CodeSheet cs)
    {
        int k;
        
        print = true;
        if(i<j)
          if(l.charAt(i) == '%') { print = false; i++; }
        
        k = l.indexOf(':',i+1);
        if(k>0 && k<j-1)
        {
            cstring = new CodeString(l.substring(i, k), cs);
            i=k+1;
        }
        else cstring = null;
        i = Line.jumpBlanks(l, i, j);
        if(i<j)
        {
            if(l.charAt(i) == '(')
            {
                k = Operator.findFinalParenthesis(l, i+1, j);
                numberOfDice = new Line(null, l.substring(i+1,k), cs);
                k = l.indexOf('d',k);
                if(k>=j-1 || k<0) { sides = null; err(cs); return; }
                k++;
            }
            else
            {
                k = l.indexOf('d',i);
                numberOfDice = new Line(null, l.substring(i,k), cs);
                if(k>=j-1 || k<0) { sides = null; err(cs); return; }
                k++;
            }
            i = Line.jumpBlanks(l, k, j);
            if(i<j)
            {
                if(l.charAt(i) == '(')
                {
                    k = Operator.findFinalParenthesis(l, i+1, j);
                    if(i+1<k && k<j) sides = new Line(null, l.substring(i+1,k), cs);
                    else             { sides = null; err(cs); }
                    return;
                }
                else
                {
                    if(i<j) sides = new Line(null, l.substring(i,j), cs);
                    else    { sides = null; err(cs); }
                    return;
                }
            }
        }
        sides = null;
        err(cs);        
    }
    
    public void err(CodeSheet cs)
    {
        cs.printError("\nFILE: "+cs.getReadingFile().getAbsolutePath()+
          "\nERROR: bad ROLL DICE Declaration in Line "+cs.getReadingNumberLine()+
          " :\n"+cs.getReadingLine()+"\n");
    }

    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table) 
    {
        if(sides != null && numberOfDice != null)
        {
            int i;
            int s = (int)Math.round(sides.solve(cs,em));
            int n = (int)Math.round(numberOfDice.solve(cs, em));
            if(n>0 && s >0)
            {
                int d = 0;
                for( i=0 ; i<n ; i++ ) d += cs.rollDice(s);
                String label;
                if(cstring == null) label = null;
                else label = cstring.CodeStringToString(cs, em);
                if(print) cs.printRoll(n,s,d,label);
                return (double)d;
            }
            else { RollErr(cs); return 0; }
        }
        else
        {
            RollErr(cs);    
            return 0;
        }
    }
    
    public void RollErr(CodeSheet cs)
    {
        cs.printError("\nERROR ROLLING DICE: The number of dice or The number of sides are less or equal to zero, or are bad declared\n");
    }
}
