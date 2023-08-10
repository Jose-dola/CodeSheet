/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import chat.ChatClient;
import java.util.ArrayList;

/**
 *
 * @author akhrain
 */
public class DiceRoll
{
    String             expression      = null;
    boolean            error           = false;
    boolean            errorFraction   = false;
    double             result          = 0;
    ArrayList<String>  diceExpressions = null;
    ArrayList<String>  diceResults     = null;
    ArrayList<String>  totals          = null;  

    
    public DiceRoll(String input, ChatClient cc)
    {
        error           = false;
        errorFraction   = false;
        diceExpressions = new ArrayList<String>();
        diceResults     = new ArrayList<String>();
        totals          = new ArrayList<String>();
        expression      = input;
        result          = diceExpressionSolver(input, cc);
    }
    
    private double diceExpressionSolver(String input, ChatClient cc)
    {
        int len = input.length();
        double sign = 1;
        int i   = 0;
        int j;
        double sum = 0;
        j = Line.nextOperator(input, i, len);
        if(j == len) return singleAddend(input.substring(i),cc);
        while(i<len)
        {
            j = Line.nextOperator(input, i, len);
            if(j == len) 
            {
                sum += sign*diceExpressionSolver(input.substring(i,j), cc);
                break;
            }
            if(input.charAt(j) == '-') sign = -1; 
            else                       sign =  1;
            sum += diceExpressionSolver(input.substring(i,j), cc);
            i = j+1;
        }
        return sum;
    }
    
    private double singleAddend(String s, ChatClient cc)
    {
        StringPair split = fractionSplit(s);
        if(split.getKey() == null) { errorFraction = true; return 0; }
        else
        {
            if(split.getValue() == null) return onlyProducts(split.getKey(),cc);
            else return onlyProducts(split.getKey(),cc) / onlyProducts(split.getValue(),cc);
        }
    }
    
    private double onlyProducts(String s, ChatClient cc)
    {
        ArrayList<String> factors = productSplit(s);
        double product = 1;
        if(factors.size() < 1) return 0;
        for(String factor : factors) product *= singleFactor(factor, cc);
        return product;
    }
    
    private double singleFactor(String s, ChatClient cc)
    {
        int len = s.length();
        int i = Line.jumpBlanks(s, 0, len);
        int j;
        if(i>=len) 
        {
            error = true;
            return 0;
        }
        if(s.charAt(i) == '(')
        {
            j = len - 1;
            while(j>i)
            {
                if(s.charAt(j) == ')') break;
                j--;
            }
            if(j == i) { error = true; return 0; }
            return diceExpressionSolver(s.substring(i+1,j),cc);
        }
        j = Line.getCharJumpingBrackets('d', s, i, len);
        if(j >= len)
        {
            try { return Double.parseDouble(Line.onlyFirstToken(s.substring(i))); }
            catch(NullPointerException  e) { error = true; return 0; }
            catch(NumberFormatException e) { error = true; return 0; }
        }
        else
        {
            try
            {
                int    ndice   = Integer.parseInt(Line.onlyFirstToken(s.substring(i,j)));
                int    nside   = Integer.parseInt(Line.onlyFirstToken(s.substring(j+1)));
                int    sum     = 0;
                String diceRes = new String(" ");
                for(i=0; i<ndice; i++)
                {
                    j    = DiceRoll.rollDice(nside, cc);
                    sum += j;
                    diceRes = diceRes.concat(j+" ");
                }
                diceExpressions.add(ndice+"d"+nside);
                diceResults.add(diceRes);
                totals.add(Integer.toString(sum));
                return sum;
            }
            catch(NullPointerException  e) { error = true; return 0; }
            catch(NumberFormatException e) { error = true; return 0; }
        }
    }
    
    private ArrayList<String> productSplit(String s)
    {
        ArrayList<String> splited = new ArrayList<String>();
        int len = s.length();
        int i   = 0;
        int j;
        while(i<len)
        {
            j = Line.getCharJumpingBrackets('*', s, i, len);
            splited.add(s.substring(i, j));
            i = j+1;
        }
        return splited;
    }
    
    private DiceRoll.StringPair fractionSplit(String s)
    {
        int len = s.length();
        int i   = 0;
        i = Line.getCharJumpingBrackets('/',s, 0, len);
        if(i == len) return new StringPair(s,null);
        if(Line.getCharJumpingBrackets('/', s, i+1, len) == len) return new StringPair( s.substring(0, i) , s.substring(i+1) );
        return new StringPair(null,null);
    }
    
    public static int rollDice(int sides, ChatClient cc)
    {
        if(sides<=0) return 0;
        return (Math.abs(cc.getRandom().nextInt()) % sides)+1;
    }
    
    public String getTable()
    {
        if(error)         return null; 
        if(errorFraction) return null; 
        
        if(diceExpressions == null || diceResults == null || totals == null) return null; 
        
        int n = diceExpressions.size();
        int i;
        String s = new String("<table> <tr> <th colspan=\"3\"> <small>"+expression+"</small> </th> </tr>");
        for(i = 0; i<n; i++)
        {
            s = s.concat("<tr>"+"<td>"+"<small>"+diceExpressions.get(i)+"<small>"+"</td>"+"<td>"+"<small>"+diceResults.get(i)+"</small>"+"</td>"+"<small>"+totals.get(i)+"</small>"+"</td>"+"</tr>");
        }
        s = s.concat("<tr>"+"<td>"+"<small>"+"RESULT"+"</small>"+"</td>"+"<td colspan=\"2\">"+"<small>"+result+"</small>"+"</td>"+"</tr>");
        s = s.concat("</table>");
        return s;
    }

    private static class StringPair
    {
        String key   = null;
        String value = null;
        
        public StringPair(String k, String v) 
        {
            key   = k;
            value = v;
        }
        
        public String getKey()   {return key;}
        public String getValue() {return value;}
    }
}
