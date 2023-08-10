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
public class Number implements Solvable
{
    double f=0;
    
    public Number(String l, int i, int j) 
    {
        double f = 0;
        int fin = i + 1;
        while(fin < j)
        {
            if(!Line.isNumberCheck(l.charAt(fin))) break;
            fin++;
        }
        try{ f = Double.parseDouble( l.substring(i, fin) ); }
        catch(IndexOutOfBoundsException ex) {f = 0;}
        catch(NullPointerException ex)      {f = 0;} 
        catch(NumberFormatException ex)     {f = 0;}
        set(f);
    }
    
    public void    set(double f)       {this.f = f;}
    public double  solve(CodeSheet cs,ExecutionMacro em, AttributeTable table) { return f; }
  
/*    
    public static Number toNumber(String l, int i, int j)
    {
        double f = 0;
        int fin = i + 1;
        while(fin < j)
        {
            if(!Line.isNumberCheck(l.charAt(fin))) break;
            fin++;
        }
        try{ f = Float.parseFloat( l.substring(i, fin) ); }
        catch(IndexOutOfBoundsException ex) {f = 0;}
        catch(NullPointerException ex)      {f = 0;} 
        catch(NumberFormatException ex)     {f = 0;}
        return new Number(f);
    }
*/
}
