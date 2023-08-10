/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import java.util.ArrayList;

/**
 *
 * @author akhrain
 */
public class AttributeTable 
{
    String name = null;
    ArrayList<StringDoublePair> array = null;
    
    public AttributeTable(String s) 
    {
        name      = s;
        array     = new ArrayList<StringDoublePair>();
    }
        
    public void attributeTableAdd(String label, Double d)
    {
        if(array != null) array.add(new StringDoublePair(label,d));
    }
    
    public void attributeTablePrint(double total, CodeSheet cs)
    {
        if(name != null && array != null)
        {
            cs.toPrint("<table class=\"gtable\"> <tr> <th colspan=\"2\">"+"<font size=\"+1\"> <b>"+name+"</b></font>"+"</th> </tr>");
            for(StringDoublePair pair : array)
            {
                cs.toPrint("<tr>"+"<td>"+pair.getKey()+"</td>"+"<td>"+pair.getValue()+"</td>"+"</tr>");
            }
            cs.toPrint("<tr>"+"<td>"+"<font size=\"+1\">TOTAL</font>"+"</td>"+"<td>"
                    +"<font size=\"+1\"> <b>"+total+"</b></font>"+"</td>"+"</tr>"+"</table>");
        }
    }
    
    public static class StringDoublePair
    {
        String key;
        Double value;
        public StringDoublePair(String k, Double v)
        {
            key   = k;
            value = v;
        }
        public String getKey()   {return key;}
        public Double getValue() {return value;}
    }
}
