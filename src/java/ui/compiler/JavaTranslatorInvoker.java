/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.compiler;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.data.MetaFile;
import java.util.HashMap;
//import webui.core.UserEvent;

/**
 *
 * @author Parker
 */
public class JavaTranslatorInvoker {
    
    private String args[];
    private ResolveCompiler r;
    
    public JavaTranslatorInvoker(ResolveCompiler c, String a[])
    {
        args = a;
        r = c;
    }
    
    public StringBuffer generateJava()
    {
        StringBuffer opBuffer = new StringBuffer();
        String syntaxError = "false";
        
        opBuffer.append("<compile>");
        opBuffer.append("<genCodeResults>");
        opBuffer.append("<compilerOutput>");

        //Run the compiler
        try{
            r.compile(args);
            if(r.hasError()){
                syntaxError = "true";
            }
        }
        catch(Exception ex){
            //obviously not too concerned about this situation ever happening
        }
        
        opBuffer.append("{\"results\":[{");
        
        
        
        //generate the report then handle it depending out bugs/errors
        CompileReport cr = r.getReport();
        if(cr.hasErrors()){
            opBuffer.append(cr.getErrors());
            //event.setMyCompileSuccess(false);
            //event.setMyCompilerErrors(cr.getErrors());
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

        opBuffer.append("<code>");
        String success = "false";
        if(cr.translateSuccess()){
            opBuffer.append(cr.getOutput());
            success = "true";
        }
        opBuffer.append("</code>");
        opBuffer.append("<translateSuccess>" + success + "</translateSuccess>");

        opBuffer.append("<javaFile>");
        opBuffer.append("<concept></concept>");
        opBuffer.append("<name></name>");
        opBuffer.append("<userName></userName>");
        opBuffer.append("</javaFile>");

        opBuffer.append("</genCodeResults>");
        opBuffer.append("</compile>");

        return opBuffer;
    }
    
}
