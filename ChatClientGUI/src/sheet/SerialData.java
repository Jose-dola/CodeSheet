/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import macro.ExecutionMacro;

/**
 *
 * @author akhrain
 */
public class SerialData implements Serializable
{
    private HashMap<String, Double> variables = null;
    private HashMap<String, String> txtvariables = null;
    private HashMap<String, ArrayList<Double>> array =  null;
    private HashMap<String, ArrayList<String>> arraytxt = null;
    private HashMap<String, ArrayList<String>> counters = null;
    private String notes = null;
    
    public SerialData(ExecutionMacro em, String _notes)
    {
        variables    = em.getVariablesHashMap();
        txtvariables = em.getTxtvariablesHashMap();
        array        = em.getArrayHashMap();
        arraytxt     = em.getArraytxtHashMap();
        counters     = em.getCountersHashMap();
        notes        = _notes;
    }
    
    public HashMap<String, Double> getVariablesHashMap()           {return variables;}
    public HashMap<String, String> getTxtvariablesHashMap()        {return txtvariables;}
    public HashMap<String, ArrayList<Double>> getArrayHashMap()    {return array;}
    public HashMap<String, ArrayList<String>> getArraytxtHashMap() {return arraytxt;}
    public HashMap<String, ArrayList<String>> getCountersHashMap() {return counters;}
    public String getNotes()                                       {return notes;}
    
    public void save(CodeSheet cs) 
    {
        File f = new File(cs.getRootFolder(),"saved_session.system");
        // Stream declarations
        FileOutputStream   fileStream = null;
        ObjectOutputStream os         = null;
        // Write to file
        try
        {
            fileStream = new FileOutputStream(f);
            os         = new ObjectOutputStream(fileStream);
            os.writeObject(this);
        }
        catch(FileNotFoundException ex)
        { cs.printError("ERROR: File error <br> SESSION NOT SAVED <br>"); return;}
        catch(IOException ex)
        { cs.printError("ERROR: Writing error <br> SESSION NOT SAVED <br>"); return; }
        finally
        {
            // Closes file
            try { if(fileStream != null) fileStream.close(); }
            catch(IOException ex)
            { cs.printError("ERROR: Problems closing the file <br> SESSION NOT SAVED CORRECTLY <br>"); return; }
            // Closes buffer 
            try { if(os != null) os.close(); }
            catch(IOException ex)
            { cs.printError("ERROR: Buffer closed <br> SESSION NOT SAVED CORRECTLY <br>"); return; }
        }
    }
    
    public static SerialData recuperate(CodeSheet cs)
    {
        // Stream declarations
        FileInputStream   fileStream = null;
        ObjectInputStream os         = null;
        // Dades object declaration
        SerialData d;
        // Reads from file
        try
        {
            fileStream = new FileInputStream(new File(cs.getRootFolder(),"saved_session.system"));
            os = new ObjectInputStream(fileStream);
            d = (SerialData)os.readObject();
        }
        catch(FileNotFoundException ex)
        { return null; }
        catch(IOException ex)
        { return null; } 
        catch (ClassNotFoundException ex) 
        { return null;}
        finally
        {
            // Close file
            try { if(fileStream != null) fileStream.close(); }
            catch(IOException ex)
            { return null;}
            // Close buffer 
            try { if(os != null) os.close(); }
            catch(IOException ex)
            { return null;}
        }
        
        return d;
    }
    
}
