/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.compiler;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.compilereport.CompileReport;

/**
 *
 * @author Parker
 */
public class VCGeneratorInvoker {

    private ResolveCompiler r;
    private String[] args;
    
    public VCGeneratorInvoker(ResolveCompiler rc, String[] a)
    {
        r = rc;
        args = a;
    }
    
    
    public StringBuffer generateVcs()
    {
        
        StringBuffer opBuffer = new StringBuffer();
        String syntaxError = "false";

        opBuffer.append("<compile>");
        opBuffer.append("<genVcResults>");
        opBuffer.append("<compilerOutput>");

        try{
            r.compile(args);
            if(r.hasError()){
                syntaxError = "true";
            }
        }catch(Exception ex){
            
        } 

        CompileReport cr = r.getReport();   
        opBuffer.append("{\"results\":[{");
        if(cr.hasErrors()){
            opBuffer.append(cr.getErrors());
        }
        if(cr.hasBugReports()){
            if(cr.hasErrors()){
                opBuffer.append("},{");
            }
            opBuffer.append(cr.getBugReports());
        }
        opBuffer.append("}]}");
        opBuffer.append("</compilerOutput>");
        opBuffer.append("<syntaxError>" + syntaxError + "</syntaxError>");

        String success = "false";
        if(cr.vcSuccess()){
            opBuffer.append("{"+cr.getOutput()+"}");
            success = "true";
        }
        opBuffer.append("<vcSuccess>" + success + "</vcSuccess>");
        opBuffer.append("</genVcResults>");
        opBuffer.append("</compile>");
        return opBuffer;
    }
    
}
