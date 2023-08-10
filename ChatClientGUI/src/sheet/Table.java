/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import macro.ExecutionMacro;

/**
 *
 * @author akhrain
 */
public class Table 
{
    /* tolerance */
    public static double TOL = 1e-14;
    /* Operators */
    public static char EQUAL         = '='; 
    public static char LESS          = '<';
    public static char GREATER       = '>';
    public static char LESS_EQUAL    = '[';
    public static char GREATER_EQUAL = ']';
    
    /* Attributes */
    ArrayList<Rule> rules = null;
    
    public Table(Iterator<String> fileLinesIterator, CodeSheet cs)
    {
        int     lineNumber = cs.getReadingNumberLine();
        boolean check = false;
        List<String> lines = new ArrayList<String>();
        String lineToTable;
        while(fileLinesIterator.hasNext())
        {
            lineToTable = fileLinesIterator.next();
            cs.setReadingNumberLine(cs.getReadingNumberLine()+1);
            cs.setActualNumberLine(cs.getActualNumberLine()+1);
            if( Line.onlyFirstToken(lineToTable).equals("\\ENDTABLE") ) {check=true; break;}
            else lines.add(lineToTable);
        }
        if(check)
            makeTable(lines,lineNumber,cs);
        else
            cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad Table Declaration in Line "+
                        cs.getReadingLine()+": We have not found the \\ENDTABLE");
    }
    
    public Table(SheetFile file, CodeSheet cs)
    {
        List<String> lines = file.getLines();
        cs.setReadingFile(file.getFile());
        cs.setActualNumberLine(0);
        cs.setReadingNumberLine(0);
        if(lines == null) return;
        makeTable(lines, 0, cs);
    }
    
    private void makeTable(List<String> lines, int lineNumber, CodeSheet cs)
    {
        rules = new ArrayList<Rule>();
        int i;
        int len;
        Rule rule;
        
        for(String line : lines) 
        {
            lineNumber++;
            rule = new Rule();
            // add rule
            String[] brokenLine = line.split(":",2);
            if(brokenLine.length == 2 && brokenLine[0] != null && brokenLine[1] != null)
            {
                // add comparative expressions
                String[] comparativeExpressionStrings = brokenLine[0].split("&");
                for(String ces : comparativeExpressionStrings)
                {
                    i   = 0;
                    len = ces.length();
                    i = Line.jumpBlanks(ces, i, len);
                    if(i == len) {printError(line, lineNumber, cs); break;}
                    
                    if(ces.charAt(i) == '=')
                    {
                        i++; 
                        if(i == len) {printError(line, lineNumber, cs); break;}
                        rule.addComparativeExpression(new ComparativeExpression(EQUAL, new Line(null,ces.substring(i),cs)));
                    }
                    else if(ces.charAt(i) == '<')
                    {
                        i++;
                        if(i == len) {printError(line, lineNumber, cs); break;}
                        if(ces.charAt(i) == '=')
                        {
                            i++; 
                            if(i == len) {printError(line, lineNumber, cs); break;}
                            rule.addComparativeExpression(new ComparativeExpression(LESS_EQUAL, new Line(null,ces.substring(i),cs)));
                        }
                        else rule.addComparativeExpression(new ComparativeExpression(LESS, new Line(null,ces.substring(i),cs)));
                    }
                    else if(ces.charAt(i) == '>')
                    {
                        i++;
                        if(i == len) {printError(line, lineNumber, cs); break;}
                        if(ces.charAt(i) == '=')
                        {
                            i++; 
                            if(i == len) {printError(line, lineNumber, cs); break;}
                            rule.addComparativeExpression(new ComparativeExpression(GREATER_EQUAL, new Line(null,ces.substring(i),cs)));
                        }
                        else rule.addComparativeExpression(new ComparativeExpression(GREATER, new Line(null,ces.substring(i),cs)));
                    }
                    else rule.addComparativeExpression(new ComparativeExpression(EQUAL, new Line(null,ces.substring(i),cs)));
                }
                // set Output
                rule.setOutput(new Line(null,brokenLine[1],cs));
            }
            else
                printError(line, lineNumber, cs);
            
            rules.add(rule);
        }        
    }
    
    public void printError(String line, int lineNumber, CodeSheet cs)
    {
        cs.printError("\nFILE: "+
                                cs.getReadingFile().getAbsolutePath()+
                                "\nERROR: bad rule declaration in the table in Line "+
                                lineNumber+":"+"\n"+line+"\n");    
    }
    
    public Line getMatch(double input, CodeSheet cs, ExecutionMacro em)
    {
        for(Rule r : rules)
            if(r.check(input,cs,em)) return r.getOutput();
        return null;
    }
    
    public class Rule
    {
        private ArrayList<ComparativeExpression> comparativeExpressions =  null;
        private Line output =  null;
        
        public Rule() { comparativeExpressions = new ArrayList<ComparativeExpression>(); }
        public void setOutput(Line line) {output = line;}
        public void addComparativeExpression(ComparativeExpression ce) {comparativeExpressions.add(ce);}
        public ArrayList<ComparativeExpression> getComparativeExpressions() {return comparativeExpressions;}
        public Line getOutput() {return output;}
        
        public boolean check(double input, CodeSheet cs, ExecutionMacro em)
        {
            boolean b = true;
            for(ComparativeExpression ce : comparativeExpressions)
            {
                if(!ce.check(input,cs,em)) {b = false; break;}
            }
            return b;
        }
    }
    
    public class ComparativeExpression
    {
        char comparativeOperator;
        Line expression;
        
        public ComparativeExpression(char co, Line ex) {comparativeOperator=co; expression=ex;}
        
        public boolean check(double input, CodeSheet cs, ExecutionMacro em)
        {
            double f = input - expression.solve(cs,em);
            if(comparativeOperator == EQUAL)
            {
                if(-TOL<f && f<TOL) return true;
                else                return false;
            }
            else if(comparativeOperator == LESS)
            {
                if(f<-TOL) return true;
                else    return false;
            }
            else if(comparativeOperator == GREATER)
            {
                if(f>TOL) return true;
                else    return false;
            }
            else if(comparativeOperator == LESS_EQUAL)
            {
                if(f<TOL) return true;
                else      return false;
            }
            else if(comparativeOperator == GREATER_EQUAL)
            {
                if(f>-TOL) return true;
                else       return false;
            }
            else return false;            
        }
    }
}
