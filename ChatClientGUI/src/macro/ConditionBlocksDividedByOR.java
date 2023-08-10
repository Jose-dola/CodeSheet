/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.util.ArrayList;
import java.util.regex.Pattern;
import sheet.CodeSheet;
import sheet.Line;

/**
 *
 * @author akhrain
 */
public class ConditionBlocksDividedByOR 
{
    ArrayList<ConditionsDividedByAND> blocks = null;

    public ConditionBlocksDividedByOR(String conditions, CodeSheet cs)
    {
        blocks = new ArrayList<ConditionsDividedByAND>();
        //String backslash = ((char)92) + "";
        String[] stringBlocks = conditions.split(Pattern.quote("\\OR"));
        for(String stringBlock : stringBlocks)
        {
            ConditionsDividedByAND cdbAND = new ConditionsDividedByAND(stringBlock, cs);
            if(cdbAND.getConditions().size() > 0) blocks.add(cdbAND);
        }
    }

    public ArrayList<ConditionsDividedByAND> getBlocks() {return blocks;}

    public boolean check(CodeSheet cs, ExecutionMacro em)
    {
        for(ConditionsDividedByAND b : blocks) if(b.check(cs, em) == true) return true;
        return false;
    }
    
    public class ConditionsDividedByAND
    {
        ArrayList<Condition> conditions = null;
        
        public ConditionsDividedByAND(String s, CodeSheet cs)
        {
            Condition cond = null;
            conditions = new ArrayList<Condition>();
            String[] cdtns = s.split(Pattern.quote("\\AND"));
            for(String c : cdtns) 
            {
                cond = toCondition(c, cs);
                if(cond == null)
                {
                    cs.printError("\nFILE: "+
                        cs.getReadingFile().getAbsolutePath()+
                        "\nERROR: bad definition of condition:\n" + c + "\nin Line "+
                        cs.getReadingNumberLine());
                }
                else conditions.add(cond);
            }
        }
        
        public ArrayList<Condition> getConditions() {return conditions;}
        
        public boolean check(CodeSheet cs, ExecutionMacro em)
        {
            for(Condition c : conditions) if(c.check(cs, em) == false) return false;
            return true;
        }
        
        private Condition toCondition(String s, CodeSheet cs)
        {
            boolean negation = false;
            int len = s.length();
            int i = Line.jumpBlanks(s, 0, len);
            if(i<len)
            {
                if(s.charAt(i) == '\\') 
                { 
                    if(i+3 < len)
                    {
                        if(s.charAt(i+1) == 'N' && s.charAt(i+2) == 'E' && s.charAt(i+3) == 'G')
                        {
                            negation=true;
                            i=i+4;
                            i=Line.jumpBlanks(s, i, len);
                            if(i>=len) return null;
                        }
                        else negation = false;
                    }
                    else negation = false;
                }
                else negation = false;
                if(i+10 < len)
                {
                    if(s.substring(i, i+9).equals("isEnable("))
                    {
                        int j;
                        j = s.indexOf(')', i+9);
                        if(j>0)
                        {
                            String aux = s.substring(i+9,j);
                            IsEnableCondition iec = new IsEnableCondition(negation, aux, cs);
                            if(aux == null) return null;
                            else return iec;
                        }
                        else return null;
                    }
                    else
                    {
                        NumericalComparativeCondition ncc = new NumericalComparativeCondition(negation, s.substring(i), cs);
                        if(ncc.getRightTerm() == null) return null;
                        else return ncc;
                    }    
                }
                else
                {
                        NumericalComparativeCondition ncc = new NumericalComparativeCondition(negation, s.substring(i), cs);
                        if(ncc.getRightTerm() == null) return null;
                        else return ncc;
                }
            }
            else return null;
        }
    }
}
