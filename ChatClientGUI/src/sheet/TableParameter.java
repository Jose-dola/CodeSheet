/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import java.io.File;
import macro.ExecutionMacro;

/**
 *
 * @author akhrain
 */
public class TableParameter implements Solvable 
{
    private File   file       =  null;
    private String stringLine =  null;
    private Line   line       =  null;
    private int    numberLine =  -1;
    private String tableName  =  null;
//    private Table  table      =  null;
    
    public TableParameter(String s, CodeSheet cs) 
    {
        file = cs.getReadingFile();
        stringLine = cs.getReadingLine();
        numberLine = cs.getReadingNumberLine();
        
        int len = s.length();
        int j   = Line.getCharJumpingBrackets(',', s, 0, len);
        if(j>0 && j<len-1)
        {
            tableName = Line.onlyFirstToken(s.substring(j+1));
            line      = new Line(null, s.substring(0, j),cs);
        }
        else
        {
            tableName = null;
            cs.printError("\nFILE: "+
                           cs.getReadingFile().getAbsolutePath()+
                           "\nERROR: There is a problem with a table operator definition in Line "+
                           cs.getReadingNumberLine()+":"+"\n"+cs.getReadingLine()+"\n");
        }
    }
    
    public String getTableName() {return tableName;}
    
    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable attributeTable)
    {
        Table table = cs.findTable(tableName);
        if(table == null)
        {
            cs.printError("\nFILE: "+
                           file.getAbsolutePath()+
                           "\nERROR: We do not find the table "+tableName+" in Line "+
                           numberLine+":"+"\n"+stringLine+"\n");
            return 0;
        }
        Line output = table.getMatch(line.solve(cs,em),cs,em);
        if(output == null) return 0;
        else               return output.solve(cs,em);
    }

/*    
    public void setTable(CodeSheet cs)
    {
        table = cs.findTable(tableName);
        if(table == null)
        {
            cs.printError("\nFILE: "+
                           file.getAbsolutePath()+
                           "\nERROR: We do not find the table "+tableName+" in Line "+
                           numberLine+":"+"\n"+line+"\n");
        }
        line = null;
        tableName = null;
    }
*/
}
