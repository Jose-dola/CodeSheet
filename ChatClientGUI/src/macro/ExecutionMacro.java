/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.util.ArrayList;
import java.util.HashMap;
import sheet.CodeSheet;
import sheet.SerialData;

/**
 *
 * @author akhrain
 */
public class ExecutionMacro 
{
    private ExecutionMacro  motherMacro = null;
    private HashMap<String, Double> variables = null;
    private HashMap<String, String> txtvariables = null;
    private HashMap<String, ArrayList<Double>> array =  null;
    private HashMap<String, ArrayList<String>> arraytxt = null;
    private HashMap<String, ArrayList<String>> counters = null;
    private boolean exit = false;
    private double  returned_value = 0;
    
    public ExecutionMacro(ExecutionMacro mother)
    {
        motherMacro    = mother;
        variables      = new HashMap<String,Double>();
        txtvariables   = new HashMap<String,String>();
        array          = new HashMap<String, ArrayList<Double>>();
        arraytxt       = new HashMap<String, ArrayList<String>>();
        counters       = new HashMap<String, ArrayList<String>>();
        exit           = false;
        returned_value = 0;
    }
    
    public ExecutionMacro(SerialData sd)
    {
        motherMacro    = null;
        variables      = sd.getVariablesHashMap();
        txtvariables   = sd.getTxtvariablesHashMap();
        array          = sd.getArrayHashMap();
        arraytxt       = sd.getArraytxtHashMap();
        counters       = sd.getCountersHashMap();
        exit           = false;
        returned_value = 0;
    }
    
    public HashMap<String, Double> getVariablesHashMap()           {return variables;}
    public HashMap<String, String> getTxtvariablesHashMap()        {return txtvariables;}
    public HashMap<String, ArrayList<Double>> getArrayHashMap()    {return array;}
    public HashMap<String, ArrayList<String>> getArraytxtHashMap() {return arraytxt;}
    public HashMap<String, ArrayList<String>> getCountersHashMap() {return counters;}
    
    public void    setExit() {exit = true;}
    public boolean getExit() {return exit;}
    
    public void   setReturnedValue(double d) {returned_value = d;}
    public double getReturnedValue()         {return returned_value;}
    
    public ExecutionMacro getMother() {return motherMacro;}
    
    public void newArray(String s, CodeSheet cs)
    {
        array.put(s,new ArrayList<Double>());
        //else cs.printError("\nFILE: "+
        //                   f.getAbsolutePath()+
        //                   "\nERROR: bad 'new array' Instruction Declaration in Line "+
        //                   line+"\n"+
        //                   "\nThe array"+s+" is already created\n");
    }
    public void copyArray(String s, ArrayList<Double> arr, CodeSheet cs)
    {
        array.put(s,arr);
        //else cs.printError("\nFILE: "+
        //                   f.getAbsolutePath()+
        //                   "\nERROR: bad 'export' Instruction Declaration in Line "+
        //                   line+"\n"+
        //                   "\nThe array"+s+" is already created\n");
    }
    public ArrayList<Double> getArray(String s)
    {
        if(s==null) return null;
        else        return array.get(s);
    }
    
    public void newTextArray(String s, CodeSheet cs)
    {
          arraytxt.put(s,new ArrayList<String>());
//        else cs.printError("\nFILE: "+
          //                 f.getAbsolutePath()+
          //                 "\nERROR: bad 'new text array' Instruction Declaration in Line "+
          //                 line+"\n"+
          //                 "\nThe text array"+s+" is already created\n");
    }
    public void copyTextArray(String s, ArrayList<String> arr, CodeSheet cs)
    {
        arraytxt.put(s,arr);
        //else cs.printError("\nFILE: "+
        //                   f.getAbsolutePath()+
        //                   "\nERROR: bad 'export' Instruction Declaration in Line "+
        //                   line+"\n"+
        //                   "\nThe text array"+s+" is already created\n");
    }
    public ArrayList<String> getTextArray(String s)
    {
        if(s==null) return null;
        else        return arraytxt.get(s);
    }
    
    public String setTextVariable(String v, String txt)
    {
        return txtvariables.put(v,txt);
    }
    public String getTextVariable(String s)
    {
        if(s==null) return null;
        else return txtvariables.get(s);
    }
    
    public Double setVariable(String v,double f)
    {
        return variables.put(v, new Double(f));
    }
    public Double getVariable(String s)
    {
        if(s ==  null) return null;
        else return variables.get(s);
    }
    
    public ArrayList<String> getCounter(String s)
    {
        return counters.get(s);
    }   
    public void copyCounter(String s, ArrayList<String> arr, CodeSheet cs)
    {
        counters.put(s,arr);
    }
    public int variableToCounter(String counter, String variable)
    {
        if(variable == null) return -1;
        if(counter  == null) return -1;
        if(variables.get(variable) == null) return -1;
        ArrayList<String> array = counters.get(counter);
        if(array == null) 
        {
            array = new ArrayList<String>(); 
            array.add(variable);
            counters.put(counter,array);
            return 1;
        }
        if(!array.contains(variable)) {array.add(variable); return 1;}
        else return -2;
    }
    
    public int applyCounter(String counter, double f)
    {
        if(counter == null) return -1;
        ArrayList<String> array = counters.get(counter);
        if(array == null) return -1;
        for(String s : array)
        {
            Double d = getVariable(s);
            if(d != null) setVariable(s, d.doubleValue() + f);
        }
        return 1;
    }
}
