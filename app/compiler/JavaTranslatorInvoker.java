/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import controllers.CompilerSocket;
import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.data.MetaFile;
import java.net.URLEncoder;
import java.util.HashMap;
import play.mvc.Http.Outbound;
//import webui.core.UserEvent;

/**
 *
 * @author Parker
 */
public class JavaTranslatorInvoker {
    
    private String args[];
    private ResolveCompiler r;
    private Outbound myOutbound;
    
    public JavaTranslatorInvoker(ResolveCompiler c, String a[], Outbound outbound)
    {
        args = a;
        r = c;
        myOutbound = outbound;
    }
    
    public StringBuffer generateJava()
    {
        StringBuffer opBuffer = new StringBuffer();
        String syntaxError = "false";
        
        //opBuffer.append("{\"compile\":{");
        opBuffer.append("{\"results\":{");
        opBuffer.append("\"compilerOutput\":");
        String msg = "";
        //Run the compiler
        try{
            r.compile(args);
            
        }
        catch(Exception ex){
            //obviously not too concerned about this situation ever happening
        }
        CompileReport cr = r.getReport();
        boolean errors = false;
        if(cr.hasErrors()){
            errors = true;
            msg = "{\"status\":\"error\",\"type\":\"error\",\"errors\":[{";
            msg += cr.getErrors();
            msg += "}]}";
            myOutbound.send(msg);
        }
        if(cr.hasBugReports()){
            errors = true;
            msg = "{\"status\":\"error\",\"type\":\"bug\",\"bugs\":[{";
            msg += cr.getBugReports();
            msg += "}]}";
            myOutbound.send(msg);
        }
        if(!errors){
            msg = "{\"status\":\"complete\",\"code\":\"";
            msg += CompilerSocket.encode(cr.getOutput());
            msg += "\"}";
            myOutbound.send(msg);
        }
        return opBuffer;
    }
    
}
