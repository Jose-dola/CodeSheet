/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.text.NumberFormat;
import sheet.CodeSheet;
import sheet.Line;

/**
 *
 * @author akhrain
 */
public class LineToPrint implements Printable
{
    private Line line = null;
    private NumberFormat nf = null;
    
    public LineToPrint(String s, CodeSheet cs)
    {
        int i = s.indexOf('$');
        if(i>0)
        {
            int decimals = Integer.parseInt(s.substring(0, i));
            line = new Line(null,s.substring(i+1),cs);
            nf = NumberFormat.getInstance();
//            nf.setGroupingUsed(false); /* to avoid the commas in large numbers*/
            nf.setMaximumFractionDigits(decimals);
            nf.setMinimumFractionDigits(decimals);
        }
        else
        {
            line = new Line(null,s,cs);
            nf = NumberFormat.getInstance();
//            nf.setGroupingUsed(false);
            nf.setMaximumFractionDigits(0);
            nf.setMinimumFractionDigits(0);
        }
    }
    
    public void print(CodeSheet cs, ExecutionMacro em) 
    {
        cs.toPrint( nf.format(line.solve(cs,em)) );
    }
    
    public String printableToString(CodeSheet cs, ExecutionMacro em) 
    {
        return nf.format(line.solve(cs, em));
    }
}
