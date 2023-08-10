/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.io.File;
import sheet.CodeSheet;

/**
 *
 * @author akhrain
 */
public abstract class Instruction 
{
    int line;
    File file;
    public Instruction(int l, File f){line=l; file=f;}
    public int  getLine() {return line;}
    public File getFile() {return file;}
    public abstract void execute(CodeSheet cs, ExecutionMacro em);
}
