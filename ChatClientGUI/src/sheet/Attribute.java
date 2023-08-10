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
public class Attribute implements Solvable
{
    private String attributeName;
    private ArrayList<Line> lines;
    private String nameToShow;
    
    public Attribute(String name) 
    {
        attributeName = name;
        nameToShow    = name;
        lines = new ArrayList<Line>();
    }
    
    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table)
    {
        double sum = 0;
        for(Line l : lines)
            if(l.getFile().isEnable()) sum += l.solve(cs,em, table);
        if(table != null) table.attributeTablePrint(sum,cs);
        return sum;
    }
    
    public double solveMax(CodeSheet cs, ExecutionMacro em)
    {
        double max = Double.MIN_VALUE;
        double d = 0;
        for(Line l : lines)
        {
            if (l.getFile().isEnable()) 
            { 
                d = l.solve(cs, em);
                if(d > max) max = d;
            }
        }
        if(max == Double.MIN_VALUE) return 0;
        else                        return max;
    }
    
    public double solveMin(CodeSheet cs, ExecutionMacro em)
    {
        double min = Double.MAX_VALUE;
        double d = 0;
        for(Line l : lines)
        {
            if(l.getFile().isEnable())
            {
                d = l.solve(cs, em);
                if(d < min) min = d;
            }
        }
        if(min == Double.MAX_VALUE) return 0;
        else                        return min;
    }
    
    public void setNameToShow(String s) {nameToShow = s;}
    public String getNameToShow() {return nameToShow;}
    public String getName() {return attributeName;}
    public ArrayList<Line> getLines() {return lines;}
    
    public void addLine(Line l) {lines.add(l);}
    
    public static Attribute toAttribute(String l, int i, int j, CodeSheet cs)
    {
        String attributeName =  null;
        int end = Line.jumpNoBlanks(l, i, j);
        try
        {
            if (end > i) attributeName = l.substring(i,end);
            else         attributeName = null;
        }
        catch(IndexOutOfBoundsException ex) {attributeName = null;}
        return cs.addAttribute(attributeName);
    }
    
    public boolean checkName(String s)
    {
        if(attributeName == null) return false;
        if(s    == null)          return false;
        if(attributeName.indexOf(s) >= 0) return true;
        else                              return false;
    }
    
    public String toString()
    {
        if(attributeName.equals(nameToShow)) return attributeName;
        else return new String(attributeName).concat(" / "+nameToShow);
    }
}
