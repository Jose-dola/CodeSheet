/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.util.ArrayList;
import java.util.Comparator;
import sheet.CodeSheet;
import sheet.Line;

/**
 *
 * @author akhrain
 */
public class Macro 
{
    private ArrayList<MacroCode> macroCodes = null;
    private String name;
    
    public Macro(String name)
    {
        this.name = name;
        macroCodes = new ArrayList<MacroCode>();
    }
    
    public String getName() {return name;}
    
    public ArrayList<MacroCode> getMacroCodes() {return macroCodes;}
    
    public void addMacroCode(MacroCode mc) {macroCodes.add(mc);}
    
    public void sortMacroCodes(final CodeSheet cs, final ExecutionMacro em)
    {
        macroCodes.sort(new Comparator<MacroCode>()
            {    
                public int compare(MacroCode o1, MacroCode o2) 
                {
                    Line l1 = o2.getLineOrder();
                    Line l2 = o1.getLineOrder();
                    int v1 = 0;
                    int v2 = 0;
                    if(l1 != null) v1 = (int)Math.round(l1.solve(cs,em));
                    if(l2 != null) v2 = (int)Math.round(l2.solve(cs,em));
                    return v2 - v1;
                }
            });
    }
    
    public void execute(CodeSheet cs, ExecutionMacro em, MacroCode input)
    {
        if(input != null) input.execute(cs,em);
        sortMacroCodes(cs,em);
        for(MacroCode mc : macroCodes) mc.execute(cs,em);
    }
    
    public boolean checkName(String s)
    {
        if(name == null) return false;
        if(s    == null) return false;
        if(name.indexOf(s) >= 0) return true;
        else                     return false;
    }
    
    public boolean isEnabled()
    {
        for(MacroCode macroCode : macroCodes)
        {
            if(macroCode.getFile().isEnable())
                return true;
        }
        return false;
    }
    
    public String toString() { return name; }
}
