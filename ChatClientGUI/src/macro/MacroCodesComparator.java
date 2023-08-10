/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.util.Comparator;

/**
 **
 * 
 * 
 * ESTA CLASE EN PRINCIPIO SE PUEDE BORRAR!!!!
 * 
 * 
 * 
 * @author akhrain
 */
public class MacroCodesComparator implements Comparator
{
    public MacroCodesComparator(){}
    
    public int compare(Object o1, Object o2) 
    {
        if(o1 instanceof MacroCode)
        {
            if(o2 instanceof MacroCode)
            {
                MacroCode m1 = (MacroCode)o1;
                MacroCode m2 = (MacroCode)o2;
//                return m2.getOrder() - m1.getOrder();
return 0;
            }
            else return -1;
        }
        else
        {
            if(o2 instanceof MacroCode) return 1;
            else                        return 0;
        }
    }
    
}
