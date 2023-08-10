/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import sheet.CodeSheet;

/**
 *
 * @author akhrain
 */
public interface Printable 
{
    public void print(CodeSheet cs, ExecutionMacro em);
    public String printableToString(CodeSheet cs, ExecutionMacro em);
}
