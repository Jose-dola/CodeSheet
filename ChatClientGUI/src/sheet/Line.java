/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import java.util.ArrayList;
import macro.ExecutionMacro;
import macro.CodeString;

/**
 *
 * @author akhrain
 */
public class Line implements Solvable
{
    private SheetFile file = null;
    private ArrayList<LineParameter> parameters = null;
   
    public Line(SheetFile f, String parametersString, CodeSheet codeSheet)
    {
        file = f;
        parameters = lineToParameters(parametersString, codeSheet);
    }    
    
    public double solve(CodeSheet cs, ExecutionMacro em) 
    {
        double  sum   = 0;
        for(LineParameter lp : parameters)
        {
            sum += lp.solve(cs,em,false);
        }
        return sum;
    }
    
    public double solve(CodeSheet cs, ExecutionMacro em, AttributeTable table) 
    {
        double  sum   = 0;
        double  f     = 0;
        String  label = null;
        boolean makeTable = true;
        if(table == null) makeTable = false;
        for(LineParameter lp : parameters)
        {
            f = lp.solve(cs,em,makeTable);
            if(lp.getLabel() != null) label = lp.getLabel().CodeStringToString(cs, em);
            else label = null;
            if(label != null && table != null)
                table.attributeTableAdd(label,new Double(f));
            sum += f;
        }
        return sum;
    }
    
    public SheetFile getFile() {return file;}
    public ArrayList<LineParameter> getLineParameters() {return parameters;}
    
    public static ArrayList<LineParameter> lineToParameters(String l, CodeSheet cs)
    {
        ArrayList<LineParameter> p = new ArrayList<LineParameter>();
        LineParameter lineParameter;
        boolean sign = true; /* true is positive and false is negative */
        int i   = 0;
        int j   = 0;
        int len = l.length();
        while(i < len)
        {
            if     (l.charAt(i) == '-') {i++; sign = false;}
            else if(l.charAt(i) == '+') {i++; sign = true;}            
            i = jumpBlanks(l,i,len);
            j = nextOperator(l,i,len);
            if(j > i) 
            {
                lineParameter = toLineParameter(l,i,j,sign,cs);
                if(lineParameter == null) 
                    cs.printError("\nFILE: "+
                                cs.getReadingFile().getAbsolutePath()+
                                "\nERROR: bad Parameter Declaration in Line "+
                                cs.getReadingNumberLine()+":"+"\n"+cs.getReadingLine()+"\n"+
                                "with the parameter: "+l.substring(i, j)+"\n");
                else
                    p.add(lineParameter);
            }
            i = j;
        }

        return p;
    }
    
    public static LineParameter toLineParameter(String l, int i, int j, boolean sign, CodeSheet cs)
    {
        LineParameter lineParameter = null;
        Solvable      param         = null;
        CodeString    toPrint       = null;
        String        variableName  = null;
        int k = 0;
        int t;
        boolean printLabels = false;
        
        if(i>=j) return null;
        
        if(l.charAt(i) == '?') { printLabels = true; i++; }
        else printLabels = false;
        
        if(i>=j) return null;
        
        /* get variable's name to export to the executed macro */
        t = -1;
        k = Line.getCharJumpingBrackets('{', l, i, j);
        if(k<j-1) t = Line.getCharJumpingBrackets('}', l, k, j);
        if(k<t-1)
        {
            if(l.charAt(k) == '{' && l.charAt(t) == '}') { variableName = l.substring(k+1, t); j=k; }
            else return null;
        }
        else { if(k<j-1) return null; }
        /*******************************/

        /* get parameter's text to show */
        t = -1;
        k = Line.getCharJumpingBrackets('[', l, i, j);
        if(k<j-1) t = Line.getCharJumpingBrackets(']', l, k, j);
        if(k<t-1)
        {
            if(l.charAt(k) == '[' && l.charAt(t) == ']') { toPrint = new CodeString(l.substring(k+1, t), cs); j=k; }
            else {System.out.println("dentro del if");return null;}
        }
        else { if(k<j-1) return null; }
        /*******************************/
//        char[] array = {'*','/'};
//        int ProductORQuotientIndex = getCoincidenceJumpingBrackets(array,l,i,j);
//        if (ProductORQuotientIndex < j)
//        {
//            if(l.charAt(ProductORQuotientIndex) == '*')
//                param = toProduct(l,ProductORQuotientIndex,i,j,cs);
//            else if(l.charAt(ProductORQuotientIndex) == '/')
//                param = toQuotient(l,ProductORQuotientIndex,i,j,cs);
//        }
        char[] array = {'/','*'};
        int[] indexQuotientANDproduct = getIndexCoincidencesJumpingBrackets(array, l, i, j);
        if (indexQuotientANDproduct[0] != -1)
            param = toQuotient(l,indexQuotientANDproduct[0],i,j,cs);
        else if (indexQuotientANDproduct[1] != -1)
            param = toProduct(l,indexQuotientANDproduct[1],i,j,cs);
        else if (isNumberCheck(l.charAt(i)))
            param = new Number(l,i,j);
        else if (l.charAt(i) == '(')
            param = getLine(l,i,j,cs);
        else if (l.charAt(i) == '!')
            param = new Variable(l,i+1,j,cs);
        else if (l.charAt(i) == '%')
            param = new Roll(l,i+1,j,cs);
        else if (l.charAt(i) == '&' && i+1 < j)
        {
            if (l.charAt(i+1) == '(') param = new InstructionsParameter(l,i+2,j,cs);
            else                      param = new MacroParameter(l,i+1,j,cs);
        }
        else if (l.charAt(i) == '\\')
            param = Line.slashToParameter(l, i+1, j, cs);
        else 
            param = Attribute.toAttribute(l,i,j,cs);
        
        if (param != null) lineParameter = new LineParameter(param, sign, toPrint, variableName, printLabels);
        return lineParameter;
    }
    
    public static Solvable getLine(String l, int i, int j, CodeSheet cs)
    {
        int k;
        for(k=j-1; k>i; k--)
        {
            if(l.charAt(k) == ')') break;
        }
        if(k <= i+1)
        {
            cs.printError("\nFILE: "+
                                cs.getReadingFile().getAbsolutePath()+
                                "\nERROR: bad \"Parameter between brackets\" Declaration in Line "+
                                cs.getReadingNumberLine()+":"+"\n"+cs.getReadingLine()+"\n"+
                                "with the parameter: "+l.substring(i, j)+"\n"+
                                "probably, there are some problems with the bracekts\n");
            return null;
        }
        else
            return new Line(null, l.substring(i+1,k), cs);
    }
    
    public static Solvable toProduct(String l, int symbolIndex, int i, int j, CodeSheet cs)
    {
        if(symbolIndex+3<j)
        {
            if (l.charAt(symbolIndex+1) == 'r' && l.charAt(symbolIndex+2) == ':')
                return new RoundProductOperator(new Line(null,l.substring(i, symbolIndex),cs), new Line(null,l.substring(symbolIndex+3,j),cs) );
            else if(l.charAt(symbolIndex+1) == 't' && l.charAt(symbolIndex+2) == ':')
                return new TruncatedProductOperator(new Line(null,l.substring(i, symbolIndex),cs), new Line(null,l.substring(symbolIndex+3,j),cs) );
        }
        if(i+1<j) 
            return new ProductOperator(new Line(null,l.substring(i, symbolIndex),cs), new Line(null,l.substring(symbolIndex+1,j),cs) );
        else return null;
    }
    
    public static Solvable toQuotient(String l, int symbolIndex, int i, int j, CodeSheet cs)
    {
        if(symbolIndex+3<j)
        {
            if (l.charAt(symbolIndex+1) == 'r' && l.charAt(symbolIndex+2) == ':')
                return new RoundQuotientOperator(new Line(null,l.substring(i, symbolIndex),cs), new Line(null,l.substring(symbolIndex+3,j),cs) );
            else if(l.charAt(symbolIndex+1) == 't' && l.charAt(symbolIndex+2) == ':')
                return new TruncatedQuotientOperator(new Line(null,l.substring(i, symbolIndex),cs), new Line(null,l.substring(symbolIndex+3,j),cs) );
        }
        if(i+1<j) 
            return new QuotientOperator(new Line(null,l.substring(i, symbolIndex),cs), new Line(null,l.substring(symbolIndex+1,j),cs) );
        else return null;
    }
    
    public static Solvable slashToParameter(String l, int i, int j, CodeSheet cs)
    {
        if(j-i >= 7)
        {
            if(l.substring(i, i+6).equals("table("))
            {
                int k = Operator.findFinalParenthesis(l, i+6, j);
                if(k<j && k>i+6)
                {
                    return new TableParameter(l.substring(i+6,k),cs);
                }
                else
                {
                    cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad 'windowInput' Declaration in Line "+
                        cs.getReadingNumberLine()+":"+"\n"+cs.getReadingLine()+"\n"+
                        "it is possible that there are some problems with the brackets\n");
                }
            }
        }
        if(j-i >= 4)
        {
            if(l.substring(i, i+3).equals("if("))
            {
                int k = Operator.findFinalParenthesis(l, i+3, j);
                int s = Operator.findInitialParenthesis(l, k+1, j);
                int t = Operator.findFinalParenthesis(l, s+1, j);
                if(t<j)
                {
                    return new If( l.substring(i+3,k), l.substring(s+1, t), cs );
                }
                else
                {
                    cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad 'If' Declaration in Line "+
                        cs.getReadingNumberLine()+":"+"\n"+cs.getReadingLine()+"\n"+
                        "it is possible that there are some problems with the brackets\n");
                }
            }
        }
        if(j-i >= 14)
        {
            if(l.substring(i, i+13).equals("windowOption("))
            {
                int k = Operator.findFinalParenthesis(l, i+13, j);
                if(k<j)
                {
                    return new WindowOption(l.substring(i+13, k), cs);
                }
                else
                {
                    cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad 'windowOption' Declaration in Line "+
                        cs.getReadingNumberLine()+":"+"\n"+cs.getReadingLine()+"\n"+
                        "it is possible that there are some problems with the brackets\n");
                }
            }
        }
        if(j-i >= 18)
        {
            if(l.substring(i, i+17).equals("windowOptionList("))
            {
                int k = Operator.findFinalParenthesis(l, i+17, j);
                if(k<j)
                {
                    return new WindowOptionList(l.substring(i+17, k), cs);
                }
                else
                {
                    cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad 'windowOptionList' Declaration in Line "+
                        cs.getReadingNumberLine()+":"+"\n"+cs.getReadingLine()+"\n"+
                        "it is possible that there are some problems with the brackets\n");
                }
            }
        }
        if(j-i >= 13)
        {
            if(l.substring(i, i+12).equals("windowInput("))
            {
                int k = Operator.findFinalParenthesis(l, i+12, j);
                if(k<j)
                {
                    return new WindowInput( l.substring(i+12,k), cs );
                }
                else
                {
                    cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad 'windowInput' Declaration in Line "+
                        cs.getReadingNumberLine()+":"+"\n"+cs.getReadingLine()+"\n"+
                        "it is possible that there are some problems with the brackets\n");
                }
            }
        }
        if(j-i >= 5)
        {
            if(l.substring(i, i+4).equals("min("))
            {
                int k = Operator.findFinalParenthesis(l, i+4, j);
                if(k<j)
                {
                    return new MaxOrMin( MaxOrMin.MIN, l.substring(i+4,k));
                }
                else
                {
                    cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad 'min' Declaration in Line "+
                        cs.getReadingNumberLine()+":"+"\n"+cs.getReadingLine()+"\n"+
                        "it is possible that there are some problems with the brackets\n");
                }
            }
            if(l.substring(i, i+4).equals("max("))
            {
                int k = Operator.findFinalParenthesis(l, i+4, j);
                if(k<j)
                {
                    return new MaxOrMin( MaxOrMin.MAX, l.substring(i+4,k));
                }
                else
                {
                    cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad 'max' Declaration in Line "+
                        cs.getReadingNumberLine()+":"+"\n"+cs.getReadingLine()+"\n"+
                        "it is possible that there are some problems with the brackets\n");
                }
            }
        }
        return null;
    }
    
    public static boolean isNumberCheck(char c)
    {
        if (c=='0' || c=='1' || c=='2' || c=='3' || c=='4' || c=='5' || c=='6' || c=='7' || c=='8' || c=='9' || c=='.')
            return true;
        else 
            return false;
    }
    
    public static int jumpBlanks(String l, int i, int len)
    {
        while(i < len)
        {
            if(l.charAt(i) != ' ' && l.charAt(i) != '\t') break;
            i++;
        }
        return i;
    }
    
    public static int jumpNoBlanks(String l, int i, int len)
    {
        while(i < len)
        {
            if(l.charAt(i) == ' ' || l.charAt(i) == '\t') break;
            i++;
        }
        return i;
    }
    
    public static int nextOperator(String l, int i, int len)
    {
        int checkParenthesis = 0;
        while(i < len) 
        {
            if      (l.charAt(i) == '(') checkParenthesis++;
            else if (l.charAt(i) == ')') checkParenthesis--;
            else if ( (l.charAt(i) == '+' || l.charAt(i) == '-') && checkParenthesis == 0 ) break;
            i++;
        }
        return i;
    }
    
    public static String onlyFirstToken(String s)
    {
        int len = s.length();
        int i   = jumpBlanks(s, 0, len);
        int j   = jumpNoBlanks(s,i,len);
        if(j>i) return s.substring(i, j);
        else    return null;
    }
    
    public static int[] getIndexCoincidencesJumpingBrackets(char[] array, String l, int i, int len)
    {
        int       j;
        int       checkParenthesis = 0;
        int       ncheck           = 0;
        int       n                = array.length;
        boolean[] notChecked       = new boolean[n];
        int[]     indexArray       = new int[n];
        for(j=0; j<n; j++) { notChecked[j]=true; indexArray[j]=-1; }
        while(i < len) 
        {
            if      (l.charAt(i) == '(') checkParenthesis++;
            else if (l.charAt(i) == ')') checkParenthesis--;
            else if ( checkParenthesis == 0 ) 
            {
                for(j=0; j<n; j++)
                {
                    if(notChecked[j] && l.charAt(i) == array[j])
                    {
                        notChecked[j] = false;
                        indexArray[j] = i;
                        ncheck++;
                    }
                }
                if(ncheck == n) return indexArray;
            }
            i++;
        }
        return indexArray;
    }
    
    public static int getCharJumpingBrackets(char c, String l, int i, int len)
    {
        int checkParenthesis = 0;
        while(i < len) 
        {
            if      (l.charAt(i) == '(') checkParenthesis++;
            else if (l.charAt(i) == ')') checkParenthesis--;
            else if ( (l.charAt(i) == c) && checkParenthesis == 0 ) break;
            i++;
        }
        return i;          
    }
    
    public static int getCoincidenceJumpingBrackets(char[] array, String l, int i, int len)
    {
        int checkParenthesis = 0;
        while(i < len) 
        {
            if      (l.charAt(i) == '(') checkParenthesis++;
            else if (l.charAt(i) == ')') checkParenthesis--;
            else if ( checkCharCoincidence(array,l.charAt(i)) && checkParenthesis == 0 ) break;
            i++;
        }
        return i;  
    }
    
    public static boolean checkCharCoincidence(char[] array, char c)
    {
        for(char ch : array)
        {
            if(c == ch) return true;
        }
        return false;
    }
    
    /* return 0 if there are no '*' or '/'. Return 1 if there is a '*' or -1 if there is a '/' */
    public static int checkProductORQuotient(String l,int i,int len)
    {
        char[] array = {'*','/'};
        int j = getCoincidenceJumpingBrackets(array, l, i, len);
        if(j<len) 
        {
            if(l.charAt(j) == '*') return 1;
            if(l.charAt(j) == '/') return -1;
        }
        return 0;
    } 
    
    private void messageStackOverFlow(CodeSheet cs)
    {
        cs.printError("ERROR: StackOverFlow!!<br>"
            +"The program got stuck, probably because you have an infinite attribute loop.<br>"
            +"For example:<br>"
            +"var1 : var2<br>"
            +"var2 : var3<br>"
            +"var2 : var1<br>"                        
            +"We assign the value 0 to the variables that have problems<br>");
    }
    
    public static class LineParameter
    {
        private Solvable p = null;
        private boolean  s = true;
        private CodeString toPrint    = null;
        private boolean  printLabels  = false;
        private String   variableName = null;
        
        public LineParameter(Solvable parameter, boolean sign, CodeString tp, String vName, boolean pl)
        {
            p            = parameter;
            s            = sign;
            toPrint      = tp;
            printLabels  = pl;
            variableName = vName;
        }
        public CodeString getLabel()    {return toPrint;}
        public boolean getPrintLabels() {return printLabels;}
        public double solve(CodeSheet cs, ExecutionMacro em, boolean makeTable) 
        {
            double d = 0;
            double sign = 1;
            if(!s) sign = -1;
            if(printLabels && makeTable) 
            {
                if(p instanceof Attribute) d = sign*p.solve(cs, em, new AttributeTable(((Attribute)p).getNameToShow()));
                else                       d = sign*p.solve(cs, em, null);
            }
            else d = sign*p.solve(cs, em, null);
            em.setVariable(variableName, d);
            return d;
        }
        public Solvable getParameter() {return p;}     
    }
}
